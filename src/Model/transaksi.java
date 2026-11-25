package Model;

import Model.interfaces.transaksiInterface;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class transaksi extends model {
    

    private static final BigDecimal SERATUS = new BigDecimal("100");
    private static final BigDecimal NOL = new BigDecimal("0.00");

   
    public static class PelangganOption {
        public final int idPelanggan;
        public final String nama;

        public PelangganOption(int idPelanggan, String nama) {
            this.idPelanggan = idPelanggan;
            this.nama = nama;
        }

        @Override
        public String toString() {
            return nama;
        }
    }

    public static class KendaraanOption {
        public final int idKendaraan;
        public final String platNomor;
        public final String jenis;
        public final String merek;
        public final String model;
        public final BigDecimal hargaSewa;
        public final BigDecimal persenDendaTelat;

        public KendaraanOption(int idKendaraan, String platNomor, String jenis,
                               String merek, String model, BigDecimal hargaSewa,
                               BigDecimal persenDendaTelat) {
            this.idKendaraan = idKendaraan;
            this.platNomor = platNomor;
            this.jenis = jenis;
            this.merek = merek;
            this.model = model;
            this.hargaSewa = hargaSewa == null ? NOL : hargaSewa;
            this.persenDendaTelat = persenDendaTelat == null
                    ? new BigDecimal("10.00") : persenDendaTelat;
        }

        @Override
        public String toString() {
            return platNomor + " - " + merek + " " + model
                    + " (" + formatRupiah(hargaSewa) + "/hari)";
        }
    }

     public static class TransaksiData extends transaksiInterface {
        public final String nama_pelanggan;
        public final String kendaraan;
        public final String jenis_kendaraan;
        public final String plat_nomor;
        public final String kondisi_ambil_lengkap;
        public final BigDecimal total_bayar;
        public final BigDecimal biaya_telat;
        public final BigDecimal total_biaya_rusak;
        public final BigDecimal persen_denda_telat;
        public final int hari_telat;
        public final LocalDate tgl_kembali_aktual;
        public final String kondisi_kembali;
        public final LocalDateTime tgl_ambil_datetime;

        public TransaksiData(int id, String kodeTransaksi, int idPelanggan,
                             int idKendaraan, int idKasir, LocalDate tglSewa,
                             LocalDate tglKembaliRencana, BigDecimal hargaPerHari,
                             int jumlahHari, BigDecimal totalSewa,
                             LocalDate tglAmbil, String kondisiAmbil,
                             String status, LocalDateTime createdAt,
                             String namaPelanggan, String kendaraan,
                             String jenisKendaraan, String platNomor,
                             BigDecimal totalBayar, BigDecimal biayaTelat,
                             BigDecimal totalBiayaRusak, BigDecimal persenDendaTelat,
                             int hariTelat, LocalDate tglKembaliAktual,
                             String kondisiKembali, LocalDateTime tglAmbilDatetime) {
            super(id, kodeTransaksi, idPelanggan, idKendaraan, idKasir,
                    tglSewa, tglKembaliRencana, hargaPerHari, jumlahHari,
                    totalSewa, tglAmbil, kondisiAmbil, status, createdAt);
            this.nama_pelanggan = namaPelanggan;
            this.kendaraan = kendaraan;
            this.jenis_kendaraan = jenisKendaraan;
            this.plat_nomor = platNomor;
            this.kondisi_ambil_lengkap = kondisiAmbil;
            this.total_bayar = totalBayar == null ? NOL : totalBayar;
            this.biaya_telat = biayaTelat == null ? NOL : biayaTelat;
            this.total_biaya_rusak = totalBiayaRusak == null ? NOL : totalBiayaRusak;
            this.persen_denda_telat = persenDendaTelat == null
                    ? new BigDecimal("10.00") : persenDendaTelat;
            this.hari_telat = hariTelat;
            this.tgl_kembali_aktual = tglKembaliAktual;
            this.kondisi_kembali = kondisiKembali;
            this.tgl_ambil_datetime = tglAmbilDatetime;
        }
    }

    public static class DamageInput {
        public final String deskripsi;
        public final BigDecimal biaya;

        public DamageInput(String deskripsi, BigDecimal biaya) {
            if (deskripsi == null || deskripsi.trim().isEmpty()) {
                throw new IllegalArgumentException("Deskripsi kerusakan wajib diisi.");
            }
            if (biaya == null || biaya.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Biaya kerusakan tidak boleh negatif.");
            }
            this.deskripsi = deskripsi.trim();
            this.biaya = biaya.setScale(2, RoundingMode.HALF_UP);
        }
    }

    public static class BookingResult {
        public final String kodeTransaksi;
        public final BigDecimal hargaPerHari;
        public final int jumlahHari;
        public final BigDecimal totalSewa;

        public BookingResult(String kodeTransaksi, BigDecimal hargaPerHari,
                             int jumlahHari, BigDecimal totalSewa) {
            this.kodeTransaksi = kodeTransaksi;
            this.hargaPerHari = hargaPerHari;
            this.jumlahHari = jumlahHari;
            this.totalSewa = totalSewa;
        }
    }

    public static class ReturnCalculation {
        public final int hariTelat;
        public final BigDecimal totalSewa;
        public final BigDecimal biayaTelat;
        public final BigDecimal totalBiayaRusak;
        public final BigDecimal totalBayar;
        public final BigDecimal dendaTelatPerHari;

        public ReturnCalculation(int hariTelat, BigDecimal totalSewa,
                                 BigDecimal biayaTelat, BigDecimal totalBiayaRusak,
                                 BigDecimal totalBayar, BigDecimal dendaTelatPerHari) {
            this.hariTelat = hariTelat;
            this.totalSewa = totalSewa;
            this.biayaTelat = biayaTelat;
            this.totalBiayaRusak = totalBiayaRusak;
            this.totalBayar = totalBayar;
            this.dendaTelatPerHari = dendaTelatPerHari;
        }
    }

    public static class ReturnResult {
        public final int idPengembalian;
        public final ReturnCalculation perhitungan;

        public ReturnResult(int idPengembalian, ReturnCalculation perhitungan) {
            this.idPengembalian = idPengembalian;
            this.perhitungan = perhitungan;
        }
    }

    public static class KerusakanData {
        public final int idKerusakan;
        public final int idPengembalian;
        public final String kodeTransaksi;
        public final String namaPelanggan;
        public final String kendaraan;
        public final BigDecimal biaya;
        public final BigDecimal totalBayar;
        public final String deskripsi;
        public final LocalDate tanggal;

        public KerusakanData(int idKerusakan, int idPengembalian,
                             String kodeTransaksi, String namaPelanggan,
                             String kendaraan, BigDecimal biaya,
                             BigDecimal totalBayar, String deskripsi,
                             LocalDate tanggal) {
            this.idKerusakan = idKerusakan;
            this.idPengembalian = idPengembalian;
            this.kodeTransaksi = kodeTransaksi;
            this.namaPelanggan = namaPelanggan;
            this.kendaraan = kendaraan;
            this.biaya = biaya == null ? NOL : biaya;
            this.totalBayar = totalBayar == null ? NOL : totalBayar;
            this.deskripsi = deskripsi;
            this.tanggal = tanggal;
        }
    }

   
    private ResultSet jalankanSelect(String sql) throws SQLException {
        ResultSet rs = this.setQuery(sql).run();
        if (rs == null) {
            throw new SQLException("Query SELECT gagal dijalankan.");
        }
        return rs;
    }

    private void jalankanUpdate(String sql, String pesanGagal) throws SQLException {
        int hasil = this.setQuery(sql).execute();
        if (hasil < 0) {
            throw new SQLException(pesanGagal);
        }
    }

    private static String tanggalSql(LocalDate tanggal) {
        return tanggal.toString();
    }

    private static String tanggalWaktuSql(LocalDate tanggal) {
        return tanggal + " 00:00:00";
    }

    private static String angkaSql(BigDecimal angka) {
        return angka.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String formatRupiah(BigDecimal nilai) {
        return "Rp " + java.text.NumberFormat.getNumberInstance(
                java.util.Locale.forLanguageTag("id-ID")).format(nilai);
    }

    private static LocalDate ambilTanggal(ResultSet rs, String kolom) throws SQLException {
        java.sql.Date nilai = rs.getDate(kolom);
        return nilai == null ? null : nilai.toLocalDate();
    }

    private static LocalDateTime ambilTanggalWaktu(ResultSet rs, String kolom) throws SQLException {
        Timestamp nilai = rs.getTimestamp(kolom);
        return nilai == null ? null : nilai.toLocalDateTime();
    }

    private static final String SELECT_TRANSAKSI
            = "SELECT t.id_transaksi, t.kode_transaksi, t.id_pelanggan, t.id_kendaraan, "
            + "t.id_kasir, t.tgl_sewa, t.tgl_kembali_rencana, t.harga_sewa_per_hari, "
            + "t.jumlah_hari, t.total_sewa, t.tgl_ambil, t.kondisi_ambil, t.status, "
            + "t.created_at, p.nama AS nama_pelanggan, "
            + "CONCAT(k.plat_nomor, ' - ', k.merek, ' ', k.model) AS kendaraan, "
            + "k.jenis AS jenis_kendaraan, k.plat_nomor, k.persen_denda_telat, "
            + "COALESCE(pg.total_bayar, t.total_sewa) AS total_bayar, "
            + "COALESCE(pg.biaya_telat, 0) AS biaya_telat, "
            + "COALESCE(pg.total_biaya_rusak, 0) AS total_biaya_rusak, "
            + "COALESCE(pg.hari_telat, 0) AS hari_telat, "
            + "pg.tgl_kembali_aktual, pg.kondisi_kembali "
            + "FROM transaksi t "
            + "INNER JOIN pelanggan p ON p.id_pelanggan = t.id_pelanggan "
            + "INNER JOIN kendaraan k ON k.id_kendaraan = t.id_kendaraan "
            + "LEFT JOIN pengembalian pg ON pg.id_transaksi = t.id_transaksi ";

    private TransaksiData bacaTransaksi(ResultSet rs) throws SQLException {
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp tglAmbilWaktu = rs.getTimestamp("tgl_ambil");
        java.sql.Date tglKembaliAktual = rs.getDate("tgl_kembali_aktual");
        return new TransaksiData(
                rs.getInt("id_transaksi"),
                rs.getString("kode_transaksi"),
                rs.getInt("id_pelanggan"),
                rs.getInt("id_kendaraan"),
                rs.getInt("id_kasir"),
                ambilTanggal(rs, "tgl_sewa"),
                ambilTanggal(rs, "tgl_kembali_rencana"),
                rs.getBigDecimal("harga_sewa_per_hari"),
                rs.getInt("jumlah_hari"),
                rs.getBigDecimal("total_sewa"),
                tglAmbilWaktu == null ? null : tglAmbilWaktu.toLocalDateTime().toLocalDate(),
                rs.getString("kondisi_ambil"),
                rs.getString("status"),
                created == null ? null : created.toLocalDateTime(),
                rs.getString("nama_pelanggan"),
                rs.getString("kendaraan"),
                rs.getString("jenis_kendaraan"),
                rs.getString("plat_nomor"),
                rs.getBigDecimal("total_bayar"),
                rs.getBigDecimal("biaya_telat"),
                rs.getBigDecimal("total_biaya_rusak"),
                rs.getBigDecimal("persen_denda_telat"),
                rs.getInt("hari_telat"),
                tglKembaliAktual == null ? null : tglKembaliAktual.toLocalDate(),
                rs.getString("kondisi_kembali"),
                tglAmbilWaktu == null ? null : tglAmbilWaktu.toLocalDateTime()
        );
    }

    
    public List<PelangganOption> getPelangganAktif() throws SQLException {
        List<PelangganOption> daftar = new ArrayList<>();
        String sql = "SELECT id_pelanggan, nama FROM pelanggan "
                + "WHERE status = 'aktif' ORDER BY nama ASC";
        try (ResultSet rs = jalankanSelect(sql)) {
            while (rs.next()) {
                daftar.add(new PelangganOption(
                        rs.getInt("id_pelanggan"), rs.getString("nama")));
            }
        }
        return daftar;
    }

    public List<KendaraanOption> getKendaraanTersedia(String jenis) throws SQLException {
        if (jenis == null || !(jenis.equalsIgnoreCase("motor") || jenis.equalsIgnoreCase("mobil"))) {
            throw new IllegalArgumentException("Jenis kendaraan harus motor atau mobil.");
        }
        List<KendaraanOption> daftar = new ArrayList<>();
        String sql = "SELECT id_kendaraan, plat_nomor, jenis, merek, model, harga_sewa, "
                + "persen_denda_telat FROM kendaraan WHERE status = 'tersedia' AND jenis = "
                + esc(jenis.toLowerCase()) + " ORDER BY merek, model";
        try (ResultSet rs = jalankanSelect(sql)) {
            while (rs.next()) {
                daftar.add(new KendaraanOption(
                        rs.getInt("id_kendaraan"),
                        rs.getString("plat_nomor"),
                        rs.getString("jenis"),
                        rs.getString("merek"),
                        rs.getString("model"),
                        rs.getBigDecimal("harga_sewa"),
                        rs.getBigDecimal("persen_denda_telat")
                ));
            }
        }
        return daftar;
    }

    
    public int getFirstActiveAdminId() throws SQLException {
        String sql = "SELECT id_user FROM admin WHERE status_aktif = 1 ORDER BY id_user ASC LIMIT 1";
        try (ResultSet rs = jalankanSelect(sql)) {
            if (rs.next()) {
                return rs.getInt("id_user");
            }
        }
        throw new SQLException("Tidak ada akun admin/kasir yang aktif pada tabel admin.");
    }


    public BookingResult createBooking(int idPelanggan, int idKendaraan,
                                       int idKasir, LocalDate tglSewa,
                                       LocalDate tglKembali) throws SQLException {
        if (idPelanggan <= 0 || idKendaraan <= 0 || idKasir <= 0) {
            throw new IllegalArgumentException("Pelanggan, kendaraan, dan kasir wajib dipilih.");
        }
        if (tglSewa == null || tglKembali == null || !tglKembali.isAfter(tglSewa)) {
            throw new IllegalArgumentException("Tanggal kembali harus setelah tanggal sewa.");
        }

        String statusPelanggan = null;
        String sqlPelanggan = "SELECT status FROM pelanggan WHERE id_pelanggan = " + idPelanggan;
        try (ResultSet rs = jalankanSelect(sqlPelanggan)) {
            if (rs.next()) {
                statusPelanggan = rs.getString("status");
            }
        }
        if (statusPelanggan == null) {
            throw new SQLException("Data pelanggan tidak ditemukan.");
        }
        if (!"aktif".equalsIgnoreCase(statusPelanggan)) {
            throw new IllegalStateException("Pelanggan tidak berstatus aktif dan belum dapat melakukan booking.");
        }

        BigDecimal hargaPerHari;
        String statusKendaraan;
        String sqlKendaraan = "SELECT harga_sewa, status FROM kendaraan WHERE id_kendaraan = " + idKendaraan;
        try (ResultSet rs = jalankanSelect(sqlKendaraan)) {
            if (!rs.next()) {
                throw new SQLException("Data kendaraan tidak ditemukan.");
            }
            hargaPerHari = rs.getBigDecimal("harga_sewa");
            statusKendaraan = rs.getString("status");
        }
        if (!"tersedia".equalsIgnoreCase(statusKendaraan)) {
            throw new IllegalStateException("Kendaraan sudah tidak tersedia. Muat ulang daftar kendaraan.");
        }

        int jumlahHari = Math.toIntExact(ChronoUnit.DAYS.between(tglSewa, tglKembali));
        BigDecimal totalSewa = hargaPerHari.multiply(BigDecimal.valueOf(jumlahHari))
                .setScale(2, RoundingMode.HALF_UP);
        
        int urutan = 1;
ResultSet res = this.select().run();
while (res.next()) {
    String k = res.getString("kode_transaksi");
    if (k != null && k.matches("M\\d+")) {
        int nomor = Integer.parseInt(k.substring(1));
        if (nomor >= urutan) {
            urutan = nomor + 1;
        }
    }
}
    String kode = String.format("M%02d", urutan);

        String insert = "INSERT INTO transaksi "
                + "(kode_transaksi, id_pelanggan, id_kendaraan, id_kasir, tgl_sewa, "
                + "tgl_kembali_rencana, harga_sewa_per_hari, jumlah_hari, total_sewa, "
                + "tgl_ambil, kondisi_ambil, status) VALUES ("
                + esc(kode) + ", " + idPelanggan + ", " + idKendaraan + ", " + idKasir + ", "
                + esc(tanggalSql(tglSewa)) + ", " + esc(tanggalSql(tglKembali)) + ", "
                + angkaSql(hargaPerHari) + ", " + jumlahHari + ", " + angkaSql(totalSewa) + ", "
                + "NOW(), 'Belum diambil', 'dipesan')";
        jalankanUpdate(insert, "Gagal menyimpan booking ke tabel transaksi.");

        jalankanUpdate("UPDATE kendaraan SET status = 'disewa' WHERE id_kendaraan = " + idKendaraan,
                "Booking tersimpan, tetapi status kendaraan gagal diperbarui.");
        jalankanUpdate("UPDATE pelanggan SET status = 'sedang_menyewa' WHERE id_pelanggan = " + idPelanggan,
                "Booking tersimpan, tetapi status pelanggan gagal diperbarui.");

                
        return new BookingResult(kode, hargaPerHari, jumlahHari, totalSewa);
    }

    
    public TransaksiData getByKode(String kode) throws SQLException {
        if (kode == null || kode.trim().isEmpty()) {
            return null;
        }
        String sql = SELECT_TRANSAKSI + "WHERE t.kode_transaksi = " + esc(kode.trim()) + " LIMIT 1";
        try (ResultSet rs = jalankanSelect(sql)) {
            return rs.next() ? bacaTransaksi(rs) : null;
        }
    }

    public List<TransaksiData> getAllTransaction() throws SQLException {
        return getAllTransactionData(0, 0);
    }

    private List<TransaksiData> getAllTransactionData(int limit, int offset) throws SQLException {
        List<TransaksiData> daftar = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_TRANSAKSI)
                .append("ORDER BY t.created_at DESC, t.id_transaksi DESC");
        if (limit > 0) {
            sql.append(" LIMIT ").append(limit);
            if (offset > 0) {
                sql.append(" OFFSET ").append(offset);
            }
        }
        try (ResultSet rs = jalankanSelect(sql.toString())) {
            while (rs.next()) {
                daftar.add(bacaTransaksi(rs));
            }
        }
        return daftar;
    }

    public List<transaksiInterface> getAllTransaction(int limit, int offset) throws SQLException {
        List<transaksiInterface> hasil = new ArrayList<>();
        hasil.addAll(getAllTransactionData(Math.max(0, limit), Math.max(0, offset)));
        return hasil;
    }

    
    public void updatePengambilan(String kode, LocalDate tglAmbil,
                                  LocalDate tglKembali, String kondisiAwal) throws SQLException {
        TransaksiData data = getByKode(kode);
        if (data == null) {
            throw new SQLException("Kode transaksi tidak ditemukan.");
        }
        if (!"dipesan".equals(data.status)) {
            throw new IllegalStateException("Pengambilan hanya bisa dilakukan untuk transaksi berstatus dipesan.");
        }
        if (tglAmbil == null || tglKembali == null || !tglKembali.isAfter(tglAmbil)) {
            throw new IllegalArgumentException("Tanggal kembali harus setelah tanggal pengambilan.");
        }
        if (kondisiAwal == null || kondisiAwal.trim().isEmpty()) {
            throw new IllegalArgumentException("Kondisi awal kendaraan wajib diisi.");
        }

        int jumlahHari = Math.toIntExact(ChronoUnit.DAYS.between(tglAmbil, tglKembali));
        BigDecimal totalSewa = data.harga_sewa_perHari
                .multiply(BigDecimal.valueOf(jumlahHari)).setScale(2, RoundingMode.HALF_UP);
        String sql = "UPDATE transaksi SET tgl_ambil = " + esc(tanggalWaktuSql(tglAmbil))
                + ", tgl_kembali_rencana = " + esc(tanggalSql(tglKembali))
                + ", kondisi_ambil = " + esc(kondisiAwal.trim())
                + ", jumlah_hari = " + jumlahHari
                + ", total_sewa = " + angkaSql(totalSewa)
                + ", status = 'disewa' WHERE kode_transaksi = " + esc(kode.trim())
                + " AND status = 'dipesan'";
        jalankanUpdate(sql, "Gagal menyimpan data pengambilan kendaraan.");
    }

    
    public void updatePengambilan(String kode, LocalDate tglAmbil,
                                  String kondisiAwal) throws SQLException {
        TransaksiData data = getByKode(kode);
        if (data == null) {
            throw new SQLException("Kode transaksi tidak ditemukan.");
        }
        updatePengambilan(kode, tglAmbil, data.tgl_kembali_rencana, kondisiAwal);
    }

    
    public ReturnCalculation hitungPengembalian(String kode, LocalDate tglAktual,
                                                 List<DamageInput> daftarKerusakan) throws SQLException {
        TransaksiData data = getByKode(kode);
        if (data == null) {
            throw new SQLException("Kode transaksi tidak ditemukan.");
        }
        if (!"disewa".equals(data.status)) {
            throw new IllegalStateException("Transaksi belum diambil atau sudah selesai dikembalikan.");
        }
        if (tglAktual == null) {
            throw new IllegalArgumentException("Tanggal kembali aktual wajib diisi.");
        }
        if (data.tgl_kembali_rencana == null || data.tgl_ambil == null) {
            throw new SQLException("Tanggal pengambilan/kembali rencana belum lengkap.");
        }
        if (tglAktual.isBefore(data.tgl_ambil)) {
            throw new IllegalArgumentException("Tanggal kembali aktual tidak boleh sebelum tanggal pengambilan.");
        }

        long terlambat = ChronoUnit.DAYS.between(data.tgl_kembali_rencana, tglAktual);
        int hariTelat = (int) Math.max(0, terlambat);
        BigDecimal persen = data.persen_denda_telat == null
                ? new BigDecimal("10.00") : data.persen_denda_telat;
        BigDecimal dendaPerHari = data.harga_sewa_perHari
                .multiply(persen)
                .divide(SERATUS, 2, RoundingMode.HALF_UP);
        BigDecimal biayaTelat = dendaPerHari.multiply(BigDecimal.valueOf(hariTelat))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal biayaRusak = BigDecimal.ZERO;
        if (daftarKerusakan != null) {
            for (DamageInput item : daftarKerusakan) {
                if (item == null) {
                    continue;
                }
                biayaRusak = biayaRusak.add(item.biaya);
            }
        }
        biayaRusak = biayaRusak.setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = data.total_sewa.add(biayaTelat).add(biayaRusak)
                .setScale(2, RoundingMode.HALF_UP);

        return new ReturnCalculation(hariTelat, data.total_sewa, biayaTelat,
                biayaRusak, total, dendaPerHari);
    }

    public ReturnResult simpanPengembalian(String kode, LocalDate tglAktual,
                                            String kondisiKembali,
                                            List<DamageInput> daftarKerusakan,
                                            int idKasir) throws SQLException {
        // Cukup cek dropdown kondisi kembali saja
        if (idKasir <= 0) {
            throw new IllegalArgumentException("ID kasir tidak valid.");
        }
        if (kondisiKembali == null || kondisiKembali.trim().isEmpty()) {
            throw new IllegalArgumentException("Kondisi kendaraan saat kembali wajib dipilih.");
        }

        TransaksiData data = getByKode(kode);
        if (data == null) {
            throw new SQLException("Kode transaksi tidak ditemukan.");
        }
        ReturnCalculation hitung = hitungPengembalian(kode, tglAktual, daftarKerusakan);

        String insertPengembalian = "INSERT INTO pengembalian "
                + "(id_transaksi, id_kasir, tgl_kembali_aktual, kondisi_kembali, "
                + "hari_telat, biaya_telat, total_biaya_rusak, total_bayar) VALUES ("
                + data.id + ", " + idKasir + ", "
                + esc(tanggalWaktuSql(tglAktual)) + ", " + esc(kondisiKembali.trim()) + ", "
                + hitung.hariTelat + ", " + angkaSql(hitung.biayaTelat) + ", "
                + angkaSql(hitung.totalBiayaRusak) + ", " + angkaSql(hitung.totalBayar) + ")";
        jalankanUpdate(insertPengembalian, "Gagal menyimpan data pengembalian.");

        int idPengembalian;
        String sqlId = "SELECT id_pengembalian FROM pengembalian WHERE id_transaksi = "
                + data.id + " LIMIT 1";
        try (ResultSet rs = jalankanSelect(sqlId)) {
            if (!rs.next()) {
                throw new SQLException("Data pengembalian tersimpan, tetapi ID pengembalian tidak ditemukan.");
            }
            idPengembalian = rs.getInt("id_pengembalian");
        }

        if (daftarKerusakan != null) {
            for (DamageInput item : daftarKerusakan) {
                if (item == null) {
                    continue;
                }
                String insertKerusakan = "INSERT INTO kerusakan "
                        + "(id_pengembalian, deskripsi, biaya, dicatat_oleh) VALUES ("
                        + idPengembalian + ", " + esc(item.deskripsi) + ", "
                        + angkaSql(item.biaya) + ", " + idKasir + ")";
                jalankanUpdate(insertKerusakan, "Gagal menyimpan salah satu detail kerusakan.");
            }
        }

        jalankanUpdate("UPDATE transaksi SET status = 'selesai' WHERE id_transaksi = "
                + data.id + " AND status = 'disewa'",
                "Data pengembalian tersimpan, tetapi status transaksi gagal diperbarui.");

        boolean kendaraanRusak = "Rusak / Tidak Normal".equalsIgnoreCase(kondisiKembali.trim());
        String statusKendaraan = kendaraanRusak ? "perawatan" : "tersedia";

        jalankanUpdate("UPDATE kendaraan SET status = " + esc(statusKendaraan)
                + " WHERE id_kendaraan = " + data.id_kendaraan,
                "Status kendaraan gagal diperbarui setelah pengembalian.");
        jalankanUpdate("UPDATE pelanggan SET status = 'aktif' WHERE id_pelanggan = "
                + data.id_pelanggan,
                "Status pelanggan gagal diperbarui setelah pengembalian.");

        return new ReturnResult(idPengembalian, hitung);
    }

    
    public void batalkan(String kode) throws SQLException {
        TransaksiData data = getByKode(kode);
        if (data == null) {
            throw new SQLException("Transaksi tidak ditemukan.");
        }
        if (!"dipesan".equals(data.status)) {
            throw new IllegalStateException("Hanya booking berstatus dipesan yang dapat dibatalkan.");
        }
        jalankanUpdate("UPDATE transaksi SET status = 'batal' WHERE id_transaksi = "
                + data.id + " AND status = 'dipesan'",
                "Gagal membatalkan booking.");
        jalankanUpdate("UPDATE kendaraan SET status = 'tersedia' WHERE id_kendaraan = "
                + data.id_kendaraan,
                "Booking dibatalkan, tetapi status kendaraan gagal dipulihkan.");
        jalankanUpdate("UPDATE pelanggan SET status = 'aktif' WHERE id_pelanggan = "
                + data.id_pelanggan,
                "Booking dibatalkan, tetapi status pelanggan gagal dipulihkan.");
    }


    public List<KerusakanData> getAllKerusakan() throws SQLException {
        List<KerusakanData> daftar = new ArrayList<>();
        String sql = "SELECT kr.id_kerusakan, kr.id_pengembalian, t.kode_transaksi, "
                + "p.nama AS nama_pelanggan, CONCAT(k.plat_nomor, ' - ', k.merek, ' ', k.model) AS kendaraan, "
                + "kr.biaya, pg.total_bayar, kr.deskripsi, kr.created_at "
                + "FROM kerusakan kr "
                + "INNER JOIN pengembalian pg ON pg.id_pengembalian = kr.id_pengembalian "
                + "INNER JOIN transaksi t ON t.id_transaksi = pg.id_transaksi "
                + "INNER JOIN pelanggan p ON p.id_pelanggan = t.id_pelanggan "
                + "INNER JOIN kendaraan k ON k.id_kendaraan = t.id_kendaraan "
                + "ORDER BY kr.created_at DESC, kr.id_kerusakan DESC";
        try (ResultSet rs = jalankanSelect(sql)) {
            while (rs.next()) {
                daftar.add(new KerusakanData(
                        rs.getInt("id_kerusakan"),
                        rs.getInt("id_pengembalian"),
                        rs.getString("kode_transaksi"),
                        rs.getString("nama_pelanggan"),
                        rs.getString("kendaraan"),
                        rs.getBigDecimal("biaya"),
                        rs.getBigDecimal("total_bayar"),
                        rs.getString("deskripsi"),
                        ambilTanggal(rs, "created_at")
                ));
            }
        }
        return daftar;
    }
}
