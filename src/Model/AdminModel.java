package Model;

import config.Database;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Akses tabel admin khusus untuk login.
 * Sengaja tidak extends model: where() di model merangkai SQL dari String mentah,
 * jadi input login bisa disuntik (SQL injection). Di sini pakai PreparedStatement.
 */
public class AdminModel {

    /** Data akun yang dikembalikan setelah login berhasil. */
    public static class Akun {
        public final int idUser;
        public final String namaLengkap;
        public final String username;
        public final String role;

        Akun(int idUser, String namaLengkap, String username, String role) {
            this.idUser = idUser;
            this.namaLengkap = namaLengkap;
            this.username = username;
            this.role = role;
        }
    }

    /** Hasil pencarian username: hash + status, belum dianggap login. */
    public static class Kandidat {
        public final Akun akun;
        public final String passwordHash;
        public final boolean aktif;

        Kandidat(Akun akun, String passwordHash, boolean aktif) {
            this.akun = akun;
            this.passwordHash = passwordHash;
            this.aktif = aktif;
        }
    }

    /** @return null kalau username tidak ada */
    public Kandidat cariByUsername(String username)
            throws SQLException, IOException, ClassNotFoundException {
        Database db = new Database();
        try {
            java.sql.Statement stm = db.start();
            String sql = "SELECT id_user, nama_lengkap, username, password_hash, "
                    + "status_aktif, role FROM admin WHERE username = ? LIMIT 1";
            try (PreparedStatement ps = stm.getConnection().prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    Akun akun = new Akun(
                            rs.getInt("id_user"),
                            rs.getString("nama_lengkap"),
                            rs.getString("username"),
                            rs.getString("role"));
                    return new Kandidat(akun,
                            rs.getString("password_hash"),
                            rs.getBoolean("status_aktif"));
                }
            }
        } finally {
            try {
                db.close();
            } catch (SQLException | NullPointerException ignored) {
                // koneksi gagal dibuka -> tidak ada yang perlu ditutup
            }
        }
    }
}