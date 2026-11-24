package utils;

import java.util.InputMismatchException;

/**
 * Kumpulan aturan validasi yang dipakai oleh Model.
 *
 * Semua method di sini melempar {@link InputMismatchException} jika input
 * tidak valid, sehingga pemanggil (Model/Controller/View) bisa menangkapnya
 * dan menampilkan pesan ke pengguna.
 *
 * @author vallen
 */
public class Validation {

    /* ===================== validasi umum ===================== */

    /**
     * Memastikan sebuah field wajib tidak kosong.
     */
    public void chekWajibIsi(String nilai, String namaField) {
        if (nilai == null || nilai.isBlank()) {
            throw new InputMismatchException(namaField + " wajib diisi");
        }
    }

    /**
     * Memastikan panjang teks tidak melebihi batas kolom database.
     */
    public void chekPanjang(String nilai, int max, String namaField) {
        if (nilai != null && nilai.length() > max) {
            throw new InputMismatchException(namaField + " maksimal " + max + " karakter");
        }
    }

    /**
     * Memastikan sebuah ID bernilai positif.
     */
    public void chekIdPositif(int id, String namaField) {
        if (id <= 0) {
            throw new InputMismatchException(namaField + " harus lebih dari 0");
        }
    }

    /* ===================== validasi admin ===================== */

    public void chekUsername(String username) {
        chekWajibIsi(username, "Username");
        if (!username.matches("[A-Za-z0-9._]{3,50}")) {
            throw new InputMismatchException(
                    "Username 3-50 karakter, hanya huruf, angka, titik (.), dan underscore (_)");
        }
    }

    public void chekPassword(String password) {
        chekWajibIsi(password, "Password");
        if (password.length() < 6 || password.length() > 255) {
            throw new InputMismatchException("Password harus 6-255 karakter");
        }
    }

    /**
     * status_aktif disimpan sebagai tinyint(1): "1" = aktif, "0" = nonaktif.
     */
    public void chekStatusAktif(String status) {
        if (!"1".equals(status) && !"0".equals(status)) {
            throw new InputMismatchException("Status aktif harus 1 (aktif) atau 0 (nonaktif)");
        }
    }

    /* ===================== validasi pelanggan ===================== */

    public void chekNoKtp(String noKtp) {
        chekWajibIsi(noKtp, "No KTP");
        String bersih = noKtp.replaceAll("[\\s-]", "");
        if (!bersih.matches("\\d{16}")) {
            throw new InputMismatchException("No KTP harus tepat 16 digit angka");
        }
    }

    /**
     * No SIM bersifat opsional. Jika diisi, hanya boleh angka maksimal 20 digit.
     */
    public void chekNoSim(String noSim) {
        if (noSim == null || noSim.isBlank()) {
            return;
        }
        String bersih = noSim.replaceAll("[\\s-]", "");
        if (!bersih.matches("\\d{1,20}")) {
            throw new InputMismatchException("No SIM hanya boleh angka, maksimal 20 digit");
        }
    }

    public void chekStatusPelanggan(String status) {
        String[] daftar = {"aktif", "sedang_menyewa", "blacklist"};
        for (String s : daftar) {
            if (s.equals(status)) {
                return;
            }
        }
        throw new InputMismatchException(
                "Status pelanggan harus salah satu dari: aktif, sedang_menyewa, blacklist");
    }

    /* ===================== validasi kerusakan ===================== */

    public void chekDeskripsi(String deskripsi) {
        chekWajibIsi(deskripsi, "Deskripsi");
        chekPanjang(deskripsi, 255, "Deskripsi");
    }

    public void chekBiaya(double biaya) {
        if (Double.isNaN(biaya) || Double.isInfinite(biaya)) {
            throw new InputMismatchException("Biaya tidak valid");
        }
        if (biaya < 0) {
            throw new InputMismatchException("Biaya tidak boleh negatif");
        }
        if (biaya > 9_999_999_999.99) {
            throw new InputMismatchException("Biaya melebihi batas kolom decimal(12,2)");
        }
    }

    /* ============ method lama (tetap dipertahankan) ============ */

    public void chekPanjangKode(String kode, int maxKode) {
        if (kode != null && kode.length() > maxKode) {
            throw new InputMismatchException("Kode maksimal " + maxKode + " karakter");
        }
    }

    public void chekPanjangNama(String Nama, int maxNama) {
        if (Nama != null && Nama.length() > maxNama) {
            throw new InputMismatchException("Nama maksimal " + maxNama + " karakter");
        }
    }

    public void chekNomorHp(String nomorHp, int maxNoHp) {
        if (nomorHp == null || nomorHp.isBlank()) {
            throw new InputMismatchException("Nomor HP tidak boleh kosong");
        }
        String bersih = nomorHp.replaceAll("[\\s-]", "");

        if (!bersih.matches("\\d+")) {
            throw new InputMismatchException("Nomor HP hanya boleh berisi angka");
        }
        if (bersih.length() < 10 || bersih.length() > maxNoHp) {
            throw new InputMismatchException(
                    "Jumlah digit nomor HP harus 10-" + maxNoHp);
        }
        if (!bersih.startsWith("08") && !bersih.startsWith("62")) {
            throw new InputMismatchException("Nomor HP harus diawali 08 atau 62");
        }
    }

    public void chekPanjangAlamat(String Alamat, int maxAlamat) {
        if (Alamat != null && Alamat.length() > maxAlamat) {
            throw new InputMismatchException("Alamat maksimal " + maxAlamat + " karakter");
        }
    }
}
