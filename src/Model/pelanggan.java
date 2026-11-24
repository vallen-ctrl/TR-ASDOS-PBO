package Model;

import Model.interfaces.pelangganInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

/**
 * Model untuk tabel {@code pelanggan}.
 *
 * Catatan: nama class harus tetap {@code pelanggan} karena base class
 * {@link model} memakai nama class sebagai nama tabel.
 *
 * @author ruben
 */
public class pelanggan extends model {

    /* ===================== READ ===================== */

    /**
     * Mengambil seluruh data pelanggan.
     *
     * @throws SQLException jika query gagal dijalankan
     */
    public List<pelangganInterface> getPelanggan() throws SQLException {
        List<pelangganInterface> daftar = new ArrayList<>();
        try (ResultSet res = this.select().run()) {
            if (res == null) {
                return daftar;
            }
            while (res.next()) {
                java.sql.Date created = res.getDate("created_at");
                daftar.add(new pelangganInterface(
                        res.getInt("id_pelanggan"),
                        res.getString("nama"),
                        res.getString("no_ktp"),
                        res.getString("no_sim"),
                        res.getString("no_hp"),
                        res.getString("alamat"),
                        res.getString("status"),
                        created == null ? null : created.toLocalDate()
                ));
            }
        }
        return daftar;
    }

    /**
     * Mencari satu pelanggan berdasarkan id_pelanggan.
     *
     * @return objek pelanggan, atau null jika tidak ditemukan
     * @throws SQLException jika query gagal dijalankan
     */
    public pelangganInterface getPelangganById(int idPelanggan) throws SQLException {
        if (idPelanggan <= 0) {
            return null;
        }
        try (ResultSet res = this.select()
                .where("id_pelanggan = " + idPelanggan)
                .run()) {
            if (res != null && res.next()) {
                java.sql.Date created = res.getDate("created_at");
                return new pelangganInterface(
                        res.getInt("id_pelanggan"),
                        res.getString("nama"),
                        res.getString("no_ktp"),
                        res.getString("no_sim"),
                        res.getString("no_hp"),
                        res.getString("alamat"),
                        res.getString("status"),
                        created == null ? null : created.toLocalDate()
                );
            }
        }
        return null;
    }

    /**
     * Mencari satu pelanggan berdasarkan no_ktp (unik).
     *
     * @return objek pelanggan, atau null jika tidak ditemukan
     * @throws SQLException jika query gagal dijalankan
     */
    public pelangganInterface getPelangganByNoKtp(String noKtp) throws SQLException {
        if (noKtp == null || noKtp.isBlank()) {
            return null;
        }
        try (ResultSet res = this.select()
                .where("no_ktp = " + esc(noKtp.trim()))
                .run()) {
            if (res != null && res.next()) {
                java.sql.Date created = res.getDate("created_at");
                return new pelangganInterface(
                        res.getInt("id_pelanggan"),
                        res.getString("nama"),
                        res.getString("no_ktp"),
                        res.getString("no_sim"),
                        res.getString("no_hp"),
                        res.getString("alamat"),
                        res.getString("status"),
                        created == null ? null : created.toLocalDate()
                );
            }
        }
        return null;
    }

    /**
     * Mencari pelanggan berdasarkan potongan nama (LIKE).
     * Jika keyword kosong, mengembalikan semua pelanggan.
     *
     * @throws SQLException jika query gagal dijalankan
     */
    public List<pelangganInterface> cariPelangganByNama(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) {
            return getPelanggan();
        }
        List<pelangganInterface> daftar = new ArrayList<>();
        try (ResultSet res = this.select()
                .where("nama LIKE " + esc("%" + keyword.trim() + "%"))
                .run()) {
            if (res == null) {
                return daftar;
            }
            while (res.next()) {
                java.sql.Date created = res.getDate("created_at");
                daftar.add(new pelangganInterface(
                        res.getInt("id_pelanggan"),
                        res.getString("nama"),
                        res.getString("no_ktp"),
                        res.getString("no_sim"),
                        res.getString("no_hp"),
                        res.getString("alamat"),
                        res.getString("status"),
                        created == null ? null : created.toLocalDate()
                ));
            }
        }
        return daftar;
    }

    /**
     * Menghitung jumlah pelanggan.
     *
     * @throws SQLException jika query gagal dijalankan
     */
    public int jumlahPelanggan() throws SQLException {
        try (ResultSet res = this.setQuery("SELECT COUNT(*) AS jumlah FROM pelanggan").run()) {
            if (res != null && res.next()) {
                return res.getInt("jumlah");
            }
        }
        return 0;
    }

    /* ===================== CREATE ===================== */

    /**
     * Menambah pelanggan baru.
     *
     * @param noSim  boleh null/kosong (opsional)
     * @param alamat boleh null/kosong (opsional)
     * @param status salah satu: aktif, sedang_menyewa, blacklist
     * @return true jika berhasil
     * @throws InputMismatchException jika data tidak lolos validasi
     * @throws SQLException jika query gagal dijalankan
     */
    public boolean tambahPelanggan(String nama, String noKtp, String noSim,
                                   String noHp, String alamat, String status) throws SQLException {
        validasi(nama, noKtp, noSim, noHp, alamat, status);

        if (getPelangganByNoKtp(noKtp) != null) {
            throw new InputMismatchException("No KTP '" + noKtp + "' sudah terdaftar");
        }

        String[] kolom = {"nama", "no_ktp", "no_sim", "no_hp", "alamat", "status"};
        String[] nilai = {
                nama.trim(),
                noKtp.trim(),
                kosongJadiNull(noSim),
                noHp.trim(),
                kosongJadiNull(alamat),
                status
        };

        return this.insert(kolom, nilai).execute() > 0;
    }

    /* ===================== UPDATE ===================== */

    /**
     * Mengubah data pelanggan.
     *
     * @return true jika berhasil
     * @throws InputMismatchException jika data tidak lolos validasi
     * @throws SQLException jika query gagal dijalankan
     */
    public boolean ubahPelanggan(int idPelanggan, String nama, String noKtp, String noSim,
                                 String noHp, String alamat, String status) throws SQLException {
        validator.chekIdPositif(idPelanggan, "ID pelanggan");
        validasi(nama, noKtp, noSim, noHp, alamat, status);

        pelangganInterface lain = getPelangganByNoKtp(noKtp);
        if (lain != null && lain.id_pelanggan != idPelanggan) {
            throw new InputMismatchException("No KTP '" + noKtp + "' sudah dipakai pelanggan lain");
        }

        String[] kolom = {"nama", "no_ktp", "no_sim", "no_hp", "alamat", "status"};
        String[] nilai = {
                nama.trim(),
                noKtp.trim(),
                kosongJadiNull(noSim),
                noHp.trim(),
                kosongJadiNull(alamat),
                status
        };

        return this.update(kolom, nilai)
                .where("id_pelanggan = " + idPelanggan)
                .execute() > 0;
    }

    /* ===================== DELETE ===================== */

    /**
     * Menghapus pelanggan berdasarkan id_pelanggan.
     *
     * @return true jika ada baris yang terhapus
     * @throws SQLException jika query gagal dijalankan
     */
    public boolean hapusPelanggan(int idPelanggan) throws SQLException {
        validator.chekIdPositif(idPelanggan, "ID pelanggan");
        return this.delete()
                .where("id_pelanggan = " + idPelanggan)
                .execute() > 0;
    }

    /* ===================== helper ===================== */

    private void validasi(String nama, String noKtp, String noSim,
                          String noHp, String alamat, String status) {
        validator.chekWajibIsi(nama, "Nama pelanggan");
        validator.chekPanjangNama(nama, 100);
        validator.chekNoKtp(noKtp);
        validator.chekNoSim(noSim);
        validator.chekNomorHp(noHp, 15);
        validator.chekPanjangAlamat(alamat, 255);
        validator.chekStatusPelanggan(status);
    }

    private String kosongJadiNull(String nilai) {
        return (nilai == null || nilai.isBlank()) ? null : nilai.trim();
    }
}
