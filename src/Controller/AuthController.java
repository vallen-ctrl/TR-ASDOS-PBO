package Controller;

import Model.AdminModel;
import java.io.IOException;
import java.sql.SQLException;
import java.util.InputMismatchException;
import utils.PasswordUtil;
import utils.Validation;

/**
 * Penghubung FormLogin dan Model: validasi input, lalu cek akun.
 * Semua pesan error sudah siap ditampilkan ke pengguna lewat getMessage().
 */
public class AuthController {

    private final Validation validator = new Validation();
    private final AdminModel adminModel = new AdminModel();

    /** Gagal login: pesannya aman ditampilkan apa adanya. */
    public static class LoginException extends Exception {
        public LoginException(String message) {
            super(message);
        }
    }

    public AdminModel.Akun login(String username, String password) throws LoginException {
        String user = username == null ? "" : username.trim();

        try {
            validator.chekUsername(user);
            validator.chekPassword(password);
        } catch (InputMismatchException e) {
            throw new LoginException(e.getMessage());
        }

        AdminModel.Kandidat k;
        try {
            k = adminModel.cariByUsername(user);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new LoginException("Tidak bisa terhubung ke database: " + e.getMessage());
        }

        // Pesan sama untuk "username tidak ada" dan "password salah",
        // supaya orang tidak bisa menebak username mana yang terdaftar.
        if (k == null || !PasswordUtil.verify(password, k.passwordHash)) {
            throw new LoginException("Username atau password salah");
        }
        // Status aktif dicek setelah password benar, supaya status akun tidak bocor.
        if (!k.aktif) {
            throw new LoginException("Akun ini sudah dinonaktifkan. Hubungi admin.");
        }
        return k.akun;
    }
}