package utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hash dan cek password pakai PBKDF2 (bawaan JDK, tanpa library tambahan).
 * Format yang disimpan di kolom password_hash: iterasi:salt:hash (base64).
 * Jalankan main() untuk bikin hash yang dimasukkan ke tabel admin.
 */
public class PasswordUtil {

    private static final int ITERASI = 120_000;
    private static final int PANJANG_KEY = 256;

    public static String hash(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] h = pbkdf2(password.toCharArray(), salt, ITERASI);
        return ITERASI + ":"
                + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(h);
    }

    public static boolean verify(String password, String tersimpan) {
        try {
            String[] bagian = tersimpan.split(":");
            if (bagian.length != 3) {
                return false;
            }
            int iterasi = Integer.parseInt(bagian[0]);
            byte[] salt = Base64.getDecoder().decode(bagian[1]);
            byte[] asli = Base64.getDecoder().decode(bagian[2]);
            byte[] coba = pbkdf2(password.toCharArray(), salt, iterasi);
            // MessageDigest.isEqual: perbandingan waktu-konstan
            return java.security.MessageDigest.isEqual(asli, coba);
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterasi) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterasi, PANJANG_KEY);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Gagal menghitung hash password", e);
        }
    }

    /** Pemakaian: java utils.PasswordUtil <password> */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Pemakaian: java utils.PasswordUtil <password>");
            return;
        }
        System.out.println(hash(args[0]));
    }
}