package Model.interfaces;

import java.time.LocalDate;

/**
 * Tempat data (DTO) untuk satu baris tabel pelanggan.
 *
 * @author ruben
 */
public class pelangganInterface {
    public int id_pelanggan;
    public String nama;
    public String no_ktp;
    public String no_sim;
    public String no_hp;
    public String alamat;
    public String status;
    public LocalDate created_at;

    /**
     * Dipakai saat menambah pelanggan baru (tanpa id dan created_at).
     */
    public pelangganInterface(String nama, String no_ktp, String no_sim, String no_hp,
                              String alamat, String status) {
        this.nama = nama;
        this.no_ktp = no_ktp;
        this.no_sim = no_sim;
        this.no_hp = no_hp;
        this.alamat = alamat;
        this.status = status;
    }

    /**
     * Dipakai saat membaca data dari database.
     */
    public pelangganInterface(int id_pelanggan, String nama, String no_ktp, String no_sim,
                              String no_hp, String alamat, String status, LocalDate created_at) {
        this.id_pelanggan = id_pelanggan;
        this.nama = nama;
        this.no_ktp = no_ktp;
        this.no_sim = no_sim;
        this.no_hp = no_hp;
        this.alamat = alamat;
        this.status = status;
        this.created_at = created_at;
    }
}
