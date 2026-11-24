package Model;

import Model.interfaces.adminInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

/**
 * Model untuk tabel {@code admin}.
 *
 * Catatan: nama class harus tetap {@code admin} karena base class {@link model}
 * memakai nama class sebagai nama tabel.
 *
 * @author ruben
 */
public class admin extends model {

    /* ===================== READ ===================== */

    /**
     * Mengambil seluruh data admin.
     *
     * @throws SQLException jika query gagal dijalankan
     */
    public List<adminInterface> getAdmin() throws SQLException {
        List<adminInterface> daftar = new ArrayList<>();
        try (ResultSet res = this.select().run()) {
            if (res == null) {
                return daftar;
            }
            while (res.next()) {
                java.sql.Date created = res.getDate("created_at");
                daftar.add(new adminInterface(
                        res.getInt("id_user"),
                        res.getString("nama_lengkap"),
                        res.getString("username"),
                        res.getString("password_hash"),
                        res.getString("status_aktif"),
                        created == null ? null : created.toLocalDate()
                ));
            }
        }
        return daftar;
    }

    /**
     * Mencari satu admin berdasarkan id_user.
     *
     * @return objek admin, atau null jika tidak ditemukan
     * @throws SQLException jika query gagal dijalankan
     */
    public adminInterface getAdminById(int idUser) throws SQLException {
        if (idUser <= 0) {
            return null;
        }
        try (ResultSet res = this.select()
                .where("id_user = " + idUser)
                .run()) {
            if (res != null && res.next()) {
                java.sql.Date created = res.getDate("created_at");
                return new adminInterface(
                        res.getInt("id_user"),
                        res.getString("nama_lengkap"),
                        res.getString("username"),
                        res.getString("password_hash"),
                        res.getString("status_aktif"),
                        created == null ? null : created.toLocalDate()
                );
            }
        }
        return null;
    }

    /**
     * Mencari satu admin berdasarkan username.
     *
     * @return objek admin, atau null jika tidak ditemukan
     * @throws SQLException jika query gagal dijalankan
     */
    public adminInterface getAdminByUsername(String username) throws SQLException {
        if (username == null || username.isBlank()) {
            return null;
        }
        try (ResultSet res = this.select()
                .where("username = " + esc(username.trim()))
                .run()) {
            if (res != null && res.next()) {
                java.sql.Date created = res.getDate("created_at");
                return new adminInterface(
                        res.getInt("id_user"),
                        res.getString("nama_lengkap"),
                        res.getString("username"),
                        res.getString("password_hash"),
                        res.getString("status_aktif"),
                        created == null ? null : created.toLocalDate()
                );
            }
        }
        return null;
    }

    /**
     * Menghitung jumlah admin.
     *
     * @throws SQLException jika query gagal dijalankan
     */
    public int jumlahAdmin() throws SQLException {
        try (ResultSet res = this.setQuery("SELECT COUNT(*) AS jumlah FROM admin").run()) {
            if (res != null && res.next()) {
                return res.getInt("jumlah");
            }
        }
        return 0;
    }

    /* ===================== CREATE ===================== */

    /**
     * Menambah admin baru.
     *
     * @return true jika berhasil
     * @throws InputMismatchException jika data tidak lolos validasi
     * @throws SQLException jika query gagal dijalankan
     */
    public boolean tambahAdmin(String namaLengkap, String username, String password, boolean aktif) throws SQLException {
        validator.chekWajibIsi(namaLengkap, "Nama lengkap");
        validator.chekPanjangNama(namaLengkap, 100);
        validator.chekUsername(username);
        validator.chekPassword(password);

        if (getAdminByUsername(username) != null) {
            throw new InputMismatchException("Username '" + username + "' sudah dipakai");
        }

        String[] kolom = {"nama_lengkap", "username", "password_hash", "status_aktif"};
        String[] nilai = {
                namaLengkap.trim(),
                username.trim(),
                password,
                aktif ? "1" : "0"
        };

        return this.insert(kolom, nilai).execute() > 0;
    }

    /* ===================== UPDATE ===================== */

    /**
     * Mengubah data admin.
     * Password boleh dikosongkan (null/blank) agar password lama tidak diubah.
     *
     * @return true jika berhasil
     * @throws InputMismatchException jika data tidak lolos validasi
     * @throws SQLException jika query gagal dijalankan
     */
    public boolean ubahAdmin(int idUser, String namaLengkap, String username, String password, boolean aktif) throws SQLException {
        validator.chekIdPositif(idUser, "ID admin");
        validator.chekWajibIsi(namaLengkap, "Nama lengkap");
        validator.chekPanjangNama(namaLengkap, 100);
        validator.chekUsername(username);

        adminInterface adminLain = getAdminByUsername(username);
        if (adminLain != null && adminLain.id_user != idUser) {
            throw new InputMismatchException("Username '" + username + "' sudah dipakai admin lain");
        }

        List<String> kolom = new ArrayList<>();
        List<String> nilai = new ArrayList<>();
        kolom.add("nama_lengkap");
        nilai.add(namaLengkap.trim());
        kolom.add("username");
        nilai.add(username.trim());
        kolom.add("status_aktif");
        nilai.add(aktif ? "1" : "0");

        if (password != null && !password.isBlank()) {
            validator.chekPassword(password);
            kolom.add("password_hash");
            nilai.add(password);
        }

        return this.update(kolom.toArray(new String[0]), nilai.toArray(new String[0]))
                .where("id_user = " + idUser)
                .execute() > 0;
    }

    /* ===================== DELETE ===================== */

    /**
     * Menghapus admin berdasarkan id_user.
     *
     * @return true jika ada baris yang terhapus
     * @throws SQLException jika query gagal dijalankan
     */
    public boolean hapusAdmin(int idUser) throws SQLException {
        validator.chekIdPositif(idUser, "ID admin");
        return this.delete()
                .where("id_user = " + idUser)
                .execute() > 0;
    }

    /* ===================== helper ===================== */
}
