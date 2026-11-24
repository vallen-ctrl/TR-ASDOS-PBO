package Model;

import Model.interfaces.kerusakanInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import utils.Format;

/**
 * Model untuk tabel {@code kerusakan}.
 *
 * Catatan: nama class harus tetap {@code kerusakan} karena base class
 * {@link model} memakai nama class sebagai nama tabel.
 *
 * @author ruben
 */
public class kerusakan extends model {

    /* ===================== READ ===================== */

    /**
     * Mengambil seluruh data kerusakan.
     *
     * @throws SQLException jika query gagal dijalankan
     */
    public List<kerusakanInterface> getKerusakan() throws SQLException {
        List<kerusakanInterface> daftar = new ArrayList<>();
        try (ResultSet res = this.select().run()) {
            if (res == null) {
                return daftar;
            }
            while (res.next()) {
                java.sql.Date created = res.getDate("created_at");
                daftar.add(new kerusakanInterface(
                        res.getInt("id_kerusakan"),
                        res.getInt("id_pengembalian"),
                        res.getString("deskripsi"),
                        res.getDouble("biaya"),
                        res.getInt("dicatat_oleh"),
                        created == null ? null : created.toLocalDate()
                ));
            }
        }
        return daftar;
    }

    /**
     * Mencari satu data kerusakan berdasarkan id_kerusakan.
     *
     * @return objek kerusakan, atau null jika tidak ditemukan
     * @throws SQLException jika query gagal dijalankan
     */
    public kerusakanInterface getKerusakanById(int idKerusakan) throws SQLException {
        if (idKerusakan <= 0) {
            return null;
        }
        try (ResultSet res = this.select()
                .where("id_kerusakan = " + idKerusakan)
                .run()) {
            if (res != null && res.next()) {
                java.sql.Date created = res.getDate("created_at");
                return new kerusakanInterface(
                        res.getInt("id_kerusakan"),
                        res.getInt("id_pengembalian"),
                        res.getString("deskripsi"),
                        res.getDouble("biaya"),
                        res.getInt("dicatat_oleh"),
                        created == null ? null : created.toLocalDate()
                );
            }
        }
        return null;
    }

    /**
     * Mengambil semua kerusakan pada satu transaksi pengembalian.
     *
     * @throws SQLException jika query gagal dijalankan
     */
    public List<kerusakanInterface> getKerusakanByIdPengembalian(int idPengembalian) throws SQLException {
        validator.chekIdPositif(idPengembalian, "ID pengembalian");
        List<kerusakanInterface> daftar = new ArrayList<>();
        try (ResultSet res = this.select()
                .where("id_pengembalian = " + idPengembalian)
                .run()) {
            if (res == null) {
                return daftar;
            }
            while (res.next()) {
                java.sql.Date created = res.getDate("created_at");
                daftar.add(new kerusakanInterface(
                        res.getInt("id_kerusakan"),
                        res.getInt("id_pengembalian"),
                        res.getString("deskripsi"),
                        res.getDouble("biaya"),
                        res.getInt("dicatat_oleh"),
                        created == null ? null : created.toLocalDate()
                ));
            }
        }
        return daftar;
    }

    /**
     * Menjumlahkan seluruh biaya kerusakan pada satu pengembalian.
     *
     * @return total biaya (0 jika belum ada data)
     * @throws SQLException jika query gagal dijalankan
     */
    public double totalBiayaKerusakan(int idPengembalian) throws SQLException {
        validator.chekIdPositif(idPengembalian, "ID pengembalian");
        String query = "SELECT COALESCE(SUM(biaya), 0) AS total FROM kerusakan"
                + " WHERE id_pengembalian = " + idPengembalian;
        try (ResultSet res = this.setQuery(query).run()) {
            if (res != null && res.next()) {
                return res.getDouble("total");
            }
        }
        return 0;
    }

    /**
     * Menghitung jumlah baris kerusakan.
     *
     * @throws SQLException jika query gagal dijalankan
     */
    public int jumlahKerusakan() throws SQLException {
        try (ResultSet res = this.setQuery("SELECT COUNT(*) AS jumlah FROM kerusakan").run()) {
            if (res != null && res.next()) {
                return res.getInt("jumlah");
            }
        }
        return 0;
    }

    /* ===================== CREATE ===================== */

    /**
     * Menambah data kerusakan.
     *
     * @return true jika berhasil
     * @throws InputMismatchException jika data tidak lolos validasi
     * @throws SQLException jika query gagal dijalankan
     */
    public boolean tambahKerusakan(int idPengembalian, String deskripsi,
                                   double biaya, int dicatatOleh) throws SQLException {
        validasi(idPengembalian, deskripsi, biaya, dicatatOleh);

        String[] kolom = {"id_pengembalian", "deskripsi", "biaya", "dicatat_oleh"};
        String[] nilai = {
                String.valueOf(idPengembalian),
                deskripsi.trim(),
                Format.decimal(biaya),
                String.valueOf(dicatatOleh)
        };

        return this.insert(kolom, nilai).execute() > 0;
    }

    /* ===================== UPDATE ===================== */

    /**
     * Mengubah data kerusakan.
     *
     * @return true jika berhasil
     * @throws InputMismatchException jika data tidak lolos validasi
     * @throws SQLException jika query gagal dijalankan
     */
    public boolean ubahKerusakan(int idKerusakan, int idPengembalian, String deskripsi,
                                 double biaya, int dicatatOleh) throws SQLException {
        validator.chekIdPositif(idKerusakan, "ID kerusakan");
        validasi(idPengembalian, deskripsi, biaya, dicatatOleh);

        String[] kolom = {"id_pengembalian", "deskripsi", "biaya", "dicatat_oleh"};
        String[] nilai = {
                String.valueOf(idPengembalian),
                deskripsi.trim(),
                Format.decimal(biaya),
                String.valueOf(dicatatOleh)
        };

        return this.update(kolom, nilai)
                .where("id_kerusakan = " + idKerusakan)
                .execute() > 0;
    }

    /* ===================== DELETE ===================== */

    /**
     * Menghapus data kerusakan berdasarkan id_kerusakan.
     *
     * @return true jika ada baris yang terhapus
     * @throws SQLException jika query gagal dijalankan
     */
    public boolean hapusKerusakan(int idKerusakan) throws SQLException {
        validator.chekIdPositif(idKerusakan, "ID kerusakan");
        return this.delete()
                .where("id_kerusakan = " + idKerusakan)
                .execute() > 0;
    }

    /* ===================== helper ===================== */

    private void validasi(int idPengembalian, String deskripsi, double biaya, int dicatatOleh) {
        validator.chekIdPositif(idPengembalian, "ID pengembalian");
        validator.chekDeskripsi(deskripsi);
        validator.chekBiaya(biaya);
        validator.chekIdPositif(dicatatOleh, "ID admin pencatat");
    }
}
