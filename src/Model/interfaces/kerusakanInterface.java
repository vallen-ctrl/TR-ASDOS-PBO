package Model.interfaces;

import java.time.LocalDate;

/**
 * Tempat data (DTO) untuk satu baris tabel kerusakan.
 *
 * @author ruben
 */
public class kerusakanInterface {
    public int id_kerusakan;
    public int id_pengembalian;
    public String deskripsi;
    public double biaya;
    public int dicatat_oleh;
    public LocalDate created_at;

    /**
     * Dipakai saat menambah data kerusakan baru (tanpa id dan created_at).
     */
    public kerusakanInterface(int id_pengembalian, String deskripsi, double biaya, int dicatat_oleh) {
        this.id_pengembalian = id_pengembalian;
        this.deskripsi = deskripsi;
        this.biaya = biaya;
        this.dicatat_oleh = dicatat_oleh;
    }

    /**
     * Dipakai saat membaca data dari database.
     */
    public kerusakanInterface(int id_kerusakan, int id_pengembalian, String deskripsi,
                              double biaya, int dicatat_oleh, LocalDate created_at) {
        this.id_kerusakan = id_kerusakan;
        this.id_pengembalian = id_pengembalian;
        this.deskripsi = deskripsi;
        this.biaya = biaya;
        this.dicatat_oleh = dicatat_oleh;
        this.created_at = created_at;
    }
}
