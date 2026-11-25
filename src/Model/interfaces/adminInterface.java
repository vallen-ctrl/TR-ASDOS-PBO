package Model.interfaces;

import java.time.LocalDate;

/**
 * Tempat data (DTO) untuk satu baris tabel admin.
 *
 * @author ruben
 */
public class adminInterface {
    public int id_user;
    public String nama_lengkap;
    public String username;
    public String password_hash;
    public String status_aktif;
    public LocalDate created_at;

    /**
     * Constructor lama (tetap dipertahankan agar kompatibel).
     */
    public adminInterface(String nama_lengkap, String username, String password_hash, LocalDate created_at) {
        this.nama_lengkap = nama_lengkap;
        this.username = username;
        this.password_hash = password_hash;
        this.created_at = created_at;
    }

    /**
     * Dipakai saat membaca data dari database.
     */
    public adminInterface(int id_user, String nama_lengkap, String username, String password_hash, String status_aktif, LocalDate created_at) {
        this.id_user = id_user;
        this.nama_lengkap = nama_lengkap;
        this.username = username;
        this.password_hash = password_hash;
        this.status_aktif = status_aktif;
        this.created_at = created_at;
    }
}
