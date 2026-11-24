package Controller;

import Model.transaksi;
import Model.transaksi.BookingResult;
import Model.transaksi.DamageInput;
import Model.transaksi.KendaraanOption;
import Model.transaksi.KerusakanData;
import Model.transaksi.PelangganOption;
import Model.transaksi.ReturnCalculation;
import Model.transaksi.ReturnResult;
import Model.transaksi.TransaksiData;
import Model.interfaces.transaksiInterface;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller untuk alur Booking, Pengambilan, Pengembalian, Riwayat,
 * dan Detail Kerusakan.
 *
 * Controller ini tidak menjalankan query atau perintah SQL. Seluruh akses
 * penyimpanan diteruskan ke Model.transaksi.
 */
public class TransaksiController {

    private final transaksi model;
    private int idKasir;

    /** Membuat controller dengan model transaksi baru. */
    public TransaksiController() {
        this(new transaksi(), 0);
    }

    /**
     * Membuat controller dengan ID kasir yang sedang login.
     *
     * @param idKasir ID admin/kasir dari sesi login; 0 berarti belum diberikan
     */
    public TransaksiController(int idKasir) {
        this(new transaksi(), idKasir);
    }

    /** Constructor untuk dependency injection dan pengujian. */
    public TransaksiController(transaksi model, int idKasir) {
        if (model == null) {
            throw new IllegalArgumentException("Model transaksi tidak boleh null.");
        }
        if (idKasir < 0) {
            throw new IllegalArgumentException("ID kasir tidak boleh negatif.");
        }
        this.model = model;
        this.idKasir = idKasir;
    }

    public void setIdKasir(int idKasir) {
        if (idKasir <= 0) {
            throw new IllegalArgumentException("ID kasir harus lebih dari 0.");
        }
        this.idKasir = idKasir;
    }

    /** Mengembalikan ID kasir; memakai admin aktif pertama sebagai fallback. */
    public int getIdKasirUntukTransaksi() throws SQLException {
        if (idKasir <= 0) {
            idKasir = model.getFirstActiveAdminId();
        }
        return idKasir;
    }


    public List<PelangganOption> getPelangganAktif() throws SQLException {
        return model.getPelangganAktif();
    }

    public List<KendaraanOption> getKendaraanTersedia(String jenis) throws SQLException {
        if (jenis == null || jenis.trim().isEmpty()) {
            throw new IllegalArgumentException("Pilih jenis kendaraan terlebih dahulu.");
        }
        return model.getKendaraanTersedia(jenis);
    }


    public BookingResult createBooking(int idPelanggan, int idKendaraan,
                                       LocalDate tanggalSewa, LocalDate tanggalKembali)
            throws SQLException {
        if (idPelanggan <= 0) {
            throw new IllegalArgumentException("Pelanggan wajib dipilih.");
        }
        if (idKendaraan <= 0) {
            throw new IllegalArgumentException("Kendaraan wajib dipilih.");
        }
        if (tanggalSewa == null || tanggalKembali == null
                || !tanggalKembali.isAfter(tanggalSewa)) {
            throw new IllegalArgumentException("Tanggal kembali harus setelah tanggal sewa.");
        }
        return model.createBooking(idPelanggan, idKendaraan,
                getIdKasirUntukTransaksi(), tanggalSewa, tanggalKembali);
    }


    public TransaksiData getByKode(String kode) throws SQLException {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode transaksi wajib diisi.");
        }
        return model.getByKode(kode.trim());
    }

    public void prosesPengambilan(String kode, LocalDate tanggalAmbil,
                                  LocalDate tanggalKembali, String kondisiAwal)
            throws SQLException {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode transaksi wajib diisi.");
        }
        if (tanggalAmbil == null || tanggalKembali == null
                || !tanggalKembali.isAfter(tanggalAmbil)) {
            throw new IllegalArgumentException("Tanggal kembali harus setelah tanggal pengambilan.");
        }
        if (kondisiAwal == null || kondisiAwal.trim().isEmpty()) {
            throw new IllegalArgumentException("Kondisi awal kendaraan wajib diisi.");
        }
        model.updatePengambilan(kode.trim(), tanggalAmbil, tanggalKembali, kondisiAwal.trim());
    }


    public ReturnCalculation hitungPengembalian(String kode, LocalDate tanggalAktual,
                                                List<DamageInput> kerusakan)
            throws SQLException {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode transaksi wajib diisi.");
        }
        if (tanggalAktual == null) {
            throw new IllegalArgumentException("Tanggal kembali aktual wajib diisi.");
        }
        return model.hitungPengembalian(kode.trim(), tanggalAktual, kerusakan);
    }

    public ReturnResult prosesPengembalian(String kode, LocalDate tanggalAktual,
                                           String kondisiKembali,
                                           List<DamageInput> kerusakan)
            throws SQLException {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode transaksi wajib diisi.");
        }
        if (tanggalAktual == null) {
            throw new IllegalArgumentException("Tanggal kembali aktual wajib diisi.");
        }
        if (kondisiKembali == null || kondisiKembali.trim().isEmpty()) {
            throw new IllegalArgumentException("Kondisi kendaraan saat kembali wajib dipilih.");
        }
        
        return model.simpanPengembalian(kode.trim(), tanggalAktual,
                kondisiKembali.trim(), kerusakan, getIdKasirUntukTransaksi());
    }


    public List<TransaksiData> getRiwayatTransaksi() throws SQLException {
        return model.getAllTransaction();
    }

    public List<transaksiInterface> getRiwayatTransaksi(int limit, int offset)
            throws SQLException {
        return model.getAllTransaction(limit, offset);
    }

    public List<KerusakanData> getDetailKerusakan() throws SQLException {
        return model.getAllKerusakan();
    }

    public void batalkanBooking(String kode) throws SQLException {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode transaksi wajib diisi.");
        }
        model.batalkan(kode.trim());
    }
}
