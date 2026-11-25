package Controller;

import Model.interfaces.kendaraanInterface;
import Model.kendaraan;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author vallen
 */
public class KendaraanControler {

    private static final Pattern PLAT = Pattern.compile("^[A-Z]{1,2} \\d{1,4} [A-Z]{1,3}$");
    private static final Set<String> STATUS_VALID = Set.of("tersedia", "disewa", "perawatan");
    private static final int TAHUN_MIN = 1980;

    private final kendaraan model = new kendaraan();

    public boolean tambah(kendaraanInterface k) throws SQLException {
        this.normalisasi(k);
        List<String> errors = this.validasi(k);

        if (errors.isEmpty() && this.model.findByPlat(k.plat_nomor) != null) {
            errors.add("Plat nomor " + k.plat_nomor + " sudah terdaftar");
        }
        this.lemparJikaError(errors);

        return this.model.create(k);
    }

    public List<kendaraanInterface> semua() throws SQLException {
        return this.model.findAll();
    }

    public kendaraanInterface cariById(int id) throws SQLException {
        this.pastikanIdValid(id);
        return this.model.findById(id);
    }

    public kendaraanInterface cariByPlat(String plat) throws SQLException {
        if (plat == null || plat.isBlank()) {
            throw new IllegalArgumentException("Plat nomor tidak boleh kosong");
        }
        return this.model.findByPlat(this.rapikanPlat(plat));
    }

    public boolean ubah(int id, kendaraanInterface k) throws SQLException {
        this.pastikanIdValid(id);
        this.normalisasi(k);
        List<String> errors = this.validasi(k);

        if (this.model.findById(id) == null) {
            errors.add("Kendaraan dengan id " + id + " tidak ditemukan");
        } else if (errors.isEmpty()) {
            kendaraanInterface pemilikPlat = this.model.findByPlat(k.plat_nomor);
            if (pemilikPlat != null && pemilikPlat.id_kendaraan != id) {
                errors.add("Plat nomor " + k.plat_nomor + " sudah dipakai kendaraan lain");
            }
        }
        this.lemparJikaError(errors);

        return this.model.updateData(id, k);
    }

    public boolean ubahStatus(int id, String status) throws SQLException {
        this.pastikanIdValid(id);
        String s = status == null ? "" : status.trim().toLowerCase();
        if (!STATUS_VALID.contains(s)) {
            throw new IllegalArgumentException("Status harus salah satu dari: " + STATUS_VALID);
        }
        if (this.model.findById(id) == null) {
            throw new IllegalArgumentException("Kendaraan dengan id " + id + " tidak ditemukan");
        }
        return this.model.updateStatus(id, s);
    }

    public boolean hapus(int id) throws SQLException {
        this.pastikanIdValid(id);
        kendaraanInterface k = this.model.findById(id);
        if (k == null) {
            throw new IllegalArgumentException("Kendaraan dengan id " + id + " tidak ditemukan");
        }
        if ("disewa".equalsIgnoreCase(k.status)) {
            throw new IllegalArgumentException("Kendaraan sedang disewa, tidak bisa dihapus");
        }
        return this.model.deleteById(id);
    }

    private List<String> validasi(kendaraanInterface k) {
        List<String> e = new ArrayList<>();

        if (this.kosong(k.plat_nomor)) {
            e.add("Plat nomor wajib diisi");
        } else if (!PLAT.matcher(k.plat_nomor).matches()) {
            e.add("Format plat nomor tidak valid (contoh: H 1234 AB)");
        }

        if (this.kosong(k.jenis)) e.add("Jenis wajib diisi");
        if (this.kosong(k.merek)) e.add("Merek wajib diisi");
        if (this.kosong(k.model)) e.add("Model wajib diisi");
        if (this.kosong(k.warna)) e.add("Warna wajib diisi");

        int tahunMax = Year.now().getValue() + 1;
        if (k.Year == null) {
            e.add("Tahun wajib diisi");
        } else if (k.Year < TAHUN_MIN || k.Year > tahunMax) {
            e.add("Tahun harus antara " + TAHUN_MIN + " dan " + tahunMax);
        }

        if (k.Harga_sewa == null || k.Harga_sewa.compareTo(BigDecimal.ZERO) <= 0) {
            e.add("Harga sewa harus lebih dari 0");
        }
        if (k.Harga_denda_telat == null || k.Harga_denda_telat.compareTo(BigDecimal.ZERO) < 0) {
            e.add("Harga denda telat tidak boleh negatif");
        }

        if (this.kosong(k.status)) {
            e.add("Status wajib diisi");
        } else if (!STATUS_VALID.contains(k.status)) {
            e.add("Status harus salah satu dari: " + STATUS_VALID);
        }

        return e;
    }

    private void normalisasi(kendaraanInterface k) {
        if (k == null) {
            throw new IllegalArgumentException("Data kendaraan tidak boleh null");
        }
        k.plat_nomor = k.plat_nomor == null ? null : this.rapikanPlat(k.plat_nomor);
        k.jenis = this.trim(k.jenis);
        k.merek = this.trim(k.merek);
        k.model = this.trim(k.model);
        k.warna = this.trim(k.warna);
        k.status = k.status == null ? null : k.status.trim().toLowerCase();
    }

    private String rapikanPlat(String plat) {
        String p = plat.trim().toUpperCase().replaceAll("\\s+", "");
        java.util.regex.Matcher m = Pattern.compile("^([A-Z]{1,2})(\\d{1,4})([A-Z]{1,3})$").matcher(p);
        return m.matches() ? m.group(1) + " " + m.group(2) + " " + m.group(3) : plat.trim().toUpperCase();
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    private boolean kosong(String s) {
        return s == null || s.isBlank();
    }

    private void pastikanIdValid(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID kendaraan harus lebih dari 0");
        }
    }

    private void lemparJikaError(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n- ", errors).replaceFirst("^", "- "));
        }
    }
}