/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author User
 */
public class KelolaTransaksi extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(KelolaTransaksi.class.getName());

    private static final String STATUS_DIPESAN = "dipesan";
    private static final String STATUS_DISEWA  = "disewa";
    private static final String STATUS_SELESAI = "selesai";
    private static final String STATUS_BATAL   = "batal";

    private static final String JENIS_MOBIL = "mobil";
    private static final String JENIS_MOTOR = "motor";

    private static final double PERSEN_DENDA_TELAT = 0.10;

    private static final double BIAYA_RUSAK_DEFAULT = 500_000.0;

    private static final String KONDISI_AMBIL_PLACEHOLDER = "-";

    // ====== DATA IN-MEMORY ======
    private static final Map<String, Transaksi> daftarTransaksi = new HashMap<>();
    private static int counterTransaksi = 1;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    /**
     * Model data transaksi — dipetakan 1:1 ke kolom SQL.
     * Catatan pemetaan:
     *   kode            → transaksi.kode_transaksi
     *   nama            → pelanggan.nama (via transaksi.id_pelanggan)
     *   jenis           → kendaraan.jenis
     *   kendaraan       → kendaraan.plat_nomor + merek + model (label gabungan)
     *   kondisiAmbil    → transaksi.kondisi_ambil  (NOT NULL, diisi placeholder saat booking)
     *   kondisiKembali  → pengembalian.kondisi_kembali
     *   deskripsiKerusakan → kerusakan.deskripsi
     *   status          → transaksi.status ('dipesan'/'disewa'/'selesai'/'batal')
     *   tglSewa         → transaksi.tgl_sewa
     *   tglKembali      → transaksi.tgl_kembali_rencana
     *   tglAmbil        → transaksi.tgl_ambil       (NOT NULL, diisi tglSewa saat booking)
     *   tglDikembalikan → pengembalian.tgl_kembali_aktual
     *   hargaSewaPerHari→ transaksi.harga_sewa_per_hari
     *   jumlahHari      → transaksi.jumlah_hari
     *   totalSewa       → transaksi.total_sewa      (estimasi saat booking)
     *   hariTelat       → pengembalian.hari_telat
     *   biayaTelat      → pengembalian.biaya_telat
     *   totalBiayaRusak → pengembalian.total_biaya_rusak
     *   totalBayar      → pengembalian.total_bayar
     *   idKasir         → transaksi.id_kasir & pengembalian.id_kasir
     */
    
    private static class Transaksi {
        
        String kode;
        String nama;
        String jenis;       
        String kendaraan;

         String kondisiAmbil; 
        String kondisiKembali;
        String deskripsiKerusakan;

        String status;

        Date tglSewa;
        Date tglKembali;       
        Date tglAmbil;
        Date tglDikembalikan;  

        double hargaSewaPerHari;
        int    jumlahHari;
        double totalSewa;
        int    hariTelat;
        double biayaTelat;
        double totalBiayaRusak;
        double totalBayar;

        int idPelanggan = 1;
        int idKendaraan = 1;
        int idKasir     = 1;    
    }

    public KelolaTransaksi() {
        initComponents();
        setupForm(); 
        refreshTabel();
    }
    
    private void setupForm() {
    Date today = new Date();
    setupDateSpinner(txtTglSewa, today);
    setupDateSpinner(txtTglKembali, today);
    setupDateSpinner(txtTglAmbil, today);
    setupDateSpinner(txtTglbalik, today);
    setupDateSpinner(spnTglKembaliAktual, today);
    
    txtJenis.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[]{ JENIS_MOBIL, JENIS_MOTOR }));
    
     txtJenis.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            filterKendaraanBerdasarkanJenis();
        }
    });

    // otomatis hitung total di tab Pengembalian
    spnTglKembaliAktual.addChangeListener(e -> hitungTotalPengembalian());
    CbxKeterlambatan.addItemListener(e -> hitungTotalPengembalian());
    cmbKondisiKembali.addItemListener(e -> hitungTotalPengembalian());
    txtKodetransaksi.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override public void focusLost(java.awt.event.FocusEvent e) { 
            hitungTotalPengembalian(); }
    });
    
    filterKendaraanBerdasarkanJenis();
}

private void setupDateSpinner(JSpinner sp, Date init) {
    sp.setModel(new SpinnerDateModel(init, null, null, java.util.Calendar.DAY_OF_MONTH));
    sp.setEditor(new JSpinner.DateEditor(sp, "dd-MM-yyyy"));
}

private void filterKendaraanBerdasarkanJenis() {
    String jenis = txtJenis.getSelectedItem().toString();
    
    txtKendaraanSewa.removeAllItems();

    if (jenis.equalsIgnoreCase(JENIS_MOBIL)) { 
        txtKendaraanSewa.addItem("A 01 Toyota Avanza");
        txtKendaraanSewa.addItem("I 03 Toyota Innova");
        txtKendaraanSewa.addItem("C 04 Honda Civic");
        txtKendaraanSewa.addItem("B 20 Honda Brio");
    } 
    else if (jenis.equalsIgnoreCase(JENIS_MOTOR)) { 
        txtKendaraanSewa.addItem("S 12 Honda Supra X");
        txtKendaraanSewa.addItem("M 32 Yamaha Mio");
        txtKendaraanSewa.addItem("Z 56 Kawasaki Ninja ZX25rr");
        txtKendaraanSewa.addItem("V 13 Tawon Vespa");
    }
}

private long hariAntara(Date a, Date b) {
    java.util.Calendar c1 = java.util.Calendar.getInstance();
    c1.setTime(a);
    c1.set(java.util.Calendar.HOUR_OF_DAY, 0);
    c1.set(java.util.Calendar.MINUTE, 0);
    c1.set(java.util.Calendar.SECOND, 0);
    c1.set(java.util.Calendar.MILLISECOND, 0);
    java.util.Calendar c2 = java.util.Calendar.getInstance();
    c2.setTime(b);
    c2.set(java.util.Calendar.HOUR_OF_DAY, 0);
    c2.set(java.util.Calendar.MINUTE, 0);
    c2.set(java.util.Calendar.SECOND, 0);
    c2.set(java.util.Calendar.MILLISECOND, 0);
    return (c2.getTimeInMillis() - c1.getTimeInMillis()) / (1000L * 60 * 60 * 24);
}

private double getHargaPerHari(String kendaraan) {
    if (kendaraan.contains("Avanza")) return 350_000;
    if (kendaraan.contains("Innova")) return 500_000;
    if (kendaraan.contains("Civic"))  return 450_000;
    if (kendaraan.contains("Brio"))   return 300_000;
    if (kendaraan.contains("Supra"))  return 80_000;
    if (kendaraan.contains("Mio"))    return 75_000;
    if (kendaraan.contains("Ninja"))  return 150_000;
    if (kendaraan.contains("Vespa"))  return 120_000;
    return 100_000;
}

private String formatRupiah(double v) {
    NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
    return "Rp " + nf.format(v);
}

private void tampilkanStruk(String judul, String isi) {
    JTextArea ta = new JTextArea(isi);
    ta.setEditable(false);
    ta.setFont(new java.awt.Font("Monospaced", 0, 12));
    JOptionPane.showMessageDialog(this, ta, judul, JOptionPane.INFORMATION_MESSAGE);
}

private void hitungTotalPengembalian() {
    String kode = txtKodetransaksi.getText().trim();
    if (kode.isEmpty()) { 
        txtHargaTotal.setText(""); 
        return;
    }
    Transaksi t = daftarTransaksi.get(kode);
    if (t == null) { 
        txtHargaTotal.setText("Kode tidak ditemukan"); 
        return; 
    }
    if (!STATUS_DISEWA.equals(t.status)) {
        txtHargaTotal.setText("-");
        return;
    }

    Date tglAktual = (Date) spnTglKembaliAktual.getValue(); 
    
    long hari = hariAntara(t.tglSewa, tglAktual);
    if (hari < 1) hari = 1;
    double biayaSewa = t.hargaSewaPerHari * hari;
        
     long late = hariAntara(t.tglKembali, tglAktual);
        double biayaTelat = 0;
        if (late > 0 && CbxKeterlambatan.isSelected()) {
            biayaTelat = t.hargaSewaPerHari * late * PERSEN_DENDA_TELAT;
        }

        double biayaRusak = 0;
        if (cmbKondisiKembali.getSelectedItem().toString().contains("Rusak")) {
            biayaRusak = BIAYA_RUSAK_DEFAULT;
        }

        txtHargaTotal.setText(formatRupiah(biayaSewa + biayaTelat + biayaRusak));
    }

    private void refreshTabel() {
    // 1. Buat model tabel yang dipaksa menggunakan tipe data String (kecuali No)
    DefaultTableModel m = new DefaultTableModel(
        new Object[][]{},
        new String[]{"No", "Kode Transaksi", "Pelanggan", "Kendaraan", "Tgl Sewa", "Tgl Kembali", "Status", "Total Harga"}
    ) {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) return Integer.class;
            // Paksa semua kolom selain No menjadi String agar tidak bentrok dengan Boolean bawaan NetBeans
            return String.class; 
        }
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false; 
        }
    };
    
    TabRiwayat.setModel(m); 

    // 3. Isi datanya
    List<String> keys = new ArrayList<>(daftarTransaksi.keySet());
    Collections.sort(keys);
    int no = 1;
    for (String k : keys) {
        Transaksi t = daftarTransaksi.get(k);
        
        String tglSewaStr = (t.tglSewa != null) ? sdf.format(t.tglSewa) : "-";
        
        String tglKembaliStr = "-";
        if (t.tglDikembalikan != null) {
            tglKembaliStr = sdf.format(t.tglDikembalikan);
        } else if (t.tglKembali != null) {
            tglKembaliStr = sdf.format(t.tglKembali);
        }

        double totalTampil = STATUS_SELESAI.equals(t.status) ? t.totalBayar : t.totalSewa;
        
        String totalStr = formatRupiah(totalTampil);

        // Tambahkan status tambahan jika telat
        String statusTampil = t.status;
        if (STATUS_SELESAI.equals(t.status) && t.hariTelat > 0) {
            statusTampil = "selesai (Telat " + t.hariTelat + "h)";
        }

        m.addRow(new Object[]{
            no++,
            t.kode,
            t.nama,
            t.kendaraan,
            tglSewaStr,
            tglKembaliStr,
            statusTampil,
            totalStr 
        });
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel10 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btnBatalBooking = new javax.swing.JToggleButton();
        btnProsesBooking = new javax.swing.JToggleButton();
        jLabel7 = new javax.swing.JLabel();
        txtJenis = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtKendaraanSewa = new javax.swing.JComboBox<>();
        txtTglSewa = new javax.swing.JSpinner();
        txtTglKembali = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        txtNama = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        txtKodeTransaksi = new javax.swing.JTextField();
        txtTglAmbil = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtKondisiAwal = new javax.swing.JTextField();
        btnProsesPengambilan = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtTglbalik = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtKodetransaksi = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        spnTglKembaliAktual = new javax.swing.JSpinner();
        jPanel4 = new javax.swing.JPanel();
        CbxKeterlambatan = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        cmbKondisiKembali = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        txtDeskripsiKerusakan = new javax.swing.JTextField();
        btnProsesPengembalian = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtHargaTotal = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TabRiwayat = new javax.swing.JTable();
        btnBatalBookingRiwayat = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel10.setBackground(new java.awt.Color(204, 255, 204));

        jTabbedPane1.setBackground(new java.awt.Color(217, 255, 204));
        jTabbedPane1.setForeground(new java.awt.Color(0, 102, 51));
        jTabbedPane1.setToolTipText("");
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel5.setForeground(new java.awt.Color(0, 0, 0));

        btnBatalBooking.setBackground(new java.awt.Color(204, 0, 51));
        btnBatalBooking.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBatalBooking.setForeground(new java.awt.Color(255, 255, 255));
        btnBatalBooking.setText("Batal");
        btnBatalBooking.addActionListener(this::btnBatalBookingActionPerformed);

        btnProsesBooking.setBackground(new java.awt.Color(0, 153, 51));
        btnProsesBooking.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnProsesBooking.setForeground(new java.awt.Color(255, 255, 255));
        btnProsesBooking.setText("Proses & Cetak Struk");
        btnProsesBooking.addActionListener(this::btnProsesBookingActionPerformed);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Jenis Kendaraan");

        txtJenis.setBackground(new java.awt.Color(255, 255, 255));
        txtJenis.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtJenis.setForeground(new java.awt.Color(0, 0, 0));
        txtJenis.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mobil", "Motor" }));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Nama Pelanggan");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Kendaraan yang ingin disewa");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Tanggal Sewa");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Tanggal Kembali");

        txtKendaraanSewa.setBackground(new java.awt.Color(255, 255, 255));
        txtKendaraanSewa.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtKendaraanSewa.setForeground(new java.awt.Color(0, 0, 0));
        txtKendaraanSewa.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A 01 Toyota Avanza", "I 03 Toyota Innova", "C 04 Honda Civic", "B 20 Honda Brio", "S 12 Honda Supra X", "M 32  Yamaha Mio", "Z 56 Kawasaki Ninja ZX25rr", "V 13 Tawon Vespa" }));

        txtTglSewa.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        txtTglKembali.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("Booking Rental");

        txtNama.setBackground(new java.awt.Color(255, 255, 255));
        txtNama.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtNama.setForeground(new java.awt.Color(0, 0, 0));
        txtNama.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Felix D A", "Vallen Nathalio M", "Ruben Syema B Y", "Samuel Clement T" }));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtTglSewa, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtJenis, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGap(72, 72, 72)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3)
                                .addComponent(txtTglKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5)
                                .addComponent(txtKendaraanSewa, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(btnBatalBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(34, 34, 34)
                            .addComponent(btnProsesBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jLabel14))
                    .addComponent(jLabel2)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtKendaraanSewa))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtJenis)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTglSewa, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTglKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 95, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProsesBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBatalBooking, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(52, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Booking", jPanel2);

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.setForeground(new java.awt.Color(0, 0, 0));

        txtKodeTransaksi.setBackground(new java.awt.Color(255, 255, 255));
        txtKodeTransaksi.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtKodeTransaksi.addActionListener(this::txtKodeTransaksiActionPerformed);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Tanggal diambil");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Kondisi Awal");

        txtKondisiAwal.setBackground(new java.awt.Color(255, 255, 255));
        txtKondisiAwal.setForeground(new java.awt.Color(153, 153, 153));
        txtKondisiAwal.setToolTipText("");

        btnProsesPengambilan.setBackground(new java.awt.Color(0, 142, 33));
        btnProsesPengambilan.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnProsesPengambilan.setForeground(new java.awt.Color(255, 255, 255));
        btnProsesPengambilan.setText("Proses Pengambilan");
        btnProsesPengambilan.addActionListener(this::btnProsesPengambilanActionPerformed);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Pengambilan Kendaraan");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Tanggal Kembali");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Masukkan Kode Transaksi");

        jButton1.setBackground(new java.awt.Color(153, 0, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Batal");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel1)
                        .addComponent(txtKodeTransaksi)
                        .addComponent(txtTglAmbil)
                        .addComponent(jLabel6)
                        .addComponent(txtKondisiAwal)
                        .addComponent(jLabel9)
                        .addComponent(txtTglbalik, javax.swing.GroupLayout.PREFERRED_SIZE, 801, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel8)
                    .addComponent(jLabel15))
                .addContainerGap(143, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnProsesPengambilan)
                .addGap(51, 51, 51))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel8)
                .addGap(8, 8, 8)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtKodeTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTglAmbil, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTglbalik, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtKondisiAwal, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnProsesPengambilan)
                    .addComponent(jButton1))
                .addGap(40, 40, 40))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Pengambilan", jPanel1);

        jPanel3.setBackground(new java.awt.Color(204, 255, 204));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel9.setForeground(new java.awt.Color(0, 0, 0));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Pengembalian Kendaraan");

        txtKodetransaksi.setBackground(new java.awt.Color(255, 255, 255));
        txtKodetransaksi.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtKodetransaksi.addActionListener(this::txtKodetransaksiActionPerformed);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Tanggal Dikembalikan");

        spnTglKembaliAktual.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 215, 165));

        CbxKeterlambatan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        CbxKeterlambatan.setForeground(new java.awt.Color(0, 0, 0));
        CbxKeterlambatan.setText("Kendaraan Terlambat?");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(CbxKeterlambatan, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CbxKeterlambatan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("Kondisi Kembali");

        cmbKondisiKembali.setBackground(new java.awt.Color(255, 255, 255));
        cmbKondisiKembali.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbKondisiKembali.setForeground(new java.awt.Color(0, 0, 0));
        cmbKondisiKembali.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Baik / Normal", "Rusak / Tidak Normal" }));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("Deskripsi Kerusakan (Jika ada)");

        txtDeskripsiKerusakan.setBackground(new java.awt.Color(255, 255, 255));
        txtDeskripsiKerusakan.setForeground(new java.awt.Color(0, 0, 0));

        btnProsesPengembalian.setBackground(new java.awt.Color(0, 142, 33));
        btnProsesPengembalian.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnProsesPengembalian.setForeground(new java.awt.Color(255, 255, 255));
        btnProsesPengembalian.setText("Proses Pengembalian");
        btnProsesPengembalian.addActionListener(this::btnProsesPengembalianActionPerformed);

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setText("Masukkan kode Transaksi");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setText("Total Harga");

        txtHargaTotal.setBackground(new java.awt.Color(255, 255, 255));
        txtHargaTotal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtHargaTotal.addActionListener(this::txtHargaTotalActionPerformed);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtDeskripsiKerusakan)
                                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtKodetransaksi)
                                        .addComponent(spnTglKembaliAktual)
                                        .addComponent(cmbKondisiKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 807, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel13)
                                        .addComponent(jLabel12)
                                        .addComponent(jLabel11))
                                    .addComponent(jLabel17)
                                    .addComponent(jLabel16))
                                .addGap(204, 204, 204))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(txtHargaTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnProsesPengembalian)
                                .addGap(98, 98, 98))))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(72, 72, 72))))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addGap(3, 3, 3)
                .addComponent(txtKodetransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnTglKembaliAktual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbKondisiKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDeskripsiKerusakan, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHargaTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnProsesPengembalian))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(51, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 982, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Pengembalian", jPanel3);

        jPanel7.setBackground(new java.awt.Color(204, 255, 204));
        jPanel7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setText("Riwayat Transaksi");

        TabRiwayat.setBackground(new java.awt.Color(255, 255, 255));
        TabRiwayat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No", "Kode Transaksi", "Pelanggan", "Kendaraan", "Tgl Sewa", "Tgl Kembali", "Status", "Total Harga"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(TabRiwayat);

        btnBatalBookingRiwayat.setBackground(new java.awt.Color(204, 0, 51));
        btnBatalBookingRiwayat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBatalBookingRiwayat.setForeground(new java.awt.Color(255, 255, 255));
        btnBatalBookingRiwayat.setText("Batalkan Booking");
        btnBatalBookingRiwayat.addActionListener(this::btnBatalBookingRiwayatActionPerformed);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(571, 571, 571)
                        .addComponent(btnBatalBookingRiwayat))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 930, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(btnBatalBookingRiwayat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
        );

        jTabbedPane1.addTab("Riwayat", jPanel7);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(0, 102, 51));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("RENTAL GAS");

        btnLogout.setBackground(new java.awt.Color(255, 255, 255));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(0, 102, 51));
        btnLogout.setText("LOG OUT");
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addGap(16, 16, 16))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(btnLogout))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBatalBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalBookingActionPerformed
        // TODO add your handling code here:
        txtNama.setSelectedIndex(0);
        txtJenis.setSelectedIndex(0);
        txtKendaraanSewa.setSelectedIndex(0);
        txtTglSewa.setValue(new Date());
        txtTglKembali.setValue(new Date());
    }//GEN-LAST:event_btnBatalBookingActionPerformed

    private void btnProsesBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProsesBookingActionPerformed
        // TODO add your handling code here:
         String nama      = txtNama.getSelectedItem().toString();
        String jenis     = txtJenis.getSelectedItem().toString();     // 'mobil' / 'motor'
        String kendaraan = txtKendaraanSewa.getSelectedItem().toString();
        Date tglSewa     = (Date) txtTglSewa.getValue();
        Date tglKembali  = (Date) txtTglKembali.getValue();

        if (hariAntara(tglSewa, tglKembali) <= 0) {
            JOptionPane.showMessageDialog(this,
                "Tanggal kembali harus setelah tanggal sewa!",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String kode = String.format("M%02d", counterTransaksi++);
        double harga = getHargaPerHari(kendaraan);
        long hari = hariAntara(tglSewa, tglKembali);

        Transaksi t = new Transaksi();
        t.kode             = kode;
        t.nama             = nama;
        t.jenis            = jenis;
        t.kendaraan        = kendaraan;
        t.tglSewa          = tglSewa;
        t.tglKembali       = tglKembali;
        t.hargaSewaPerHari = harga;
        t.jumlahHari       = (int) hari;
        t.totalSewa        = harga * hari;
        t.status           = STATUS_DIPESAN;     // ✅ sesuai ENUM SQL

        //untuk mengupdate saat pengambilan)
        t.tglAmbil = tglSewa;
        // transaksi.kondisi_ambil NOT NULL → isi placeholder
        t.kondisiAmbil = KONDISI_AMBIL_PLACEHOLDER;

        daftarTransaksi.put(kode, t);
        refreshTabel();

        String struk =
            "========= STRUK BOOKING =========\n" +
            "Kode Transaksi : " + kode + "\n" +
            "Nama Pelanggan : " + nama + "\n" +
            "Jenis          : " + jenis + "\n" +
            "Kendaraan      : " + kendaraan + "\n" +
            "Tgl Sewa       : " + sdf.format(tglSewa) + "\n" +
            "Tgl Kembali    : " + sdf.format(tglKembali) + "\n" +
            "Lama Sewa      : " + hari + " hari\n" +
            "Estimasi Biaya : " + formatRupiah(t.totalSewa) + "\n" +
            "Status         : " + t.status + "\n" +
            "=================================";
        tampilkanStruk("Struk Booking", struk);
    
    }//GEN-LAST:event_btnProsesBookingActionPerformed

    private void txtKodeTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKodeTransaksiActionPerformed
        // TODO add your handling code here:
         String kode = txtKodeTransaksi.getText().trim();
        Transaksi t = daftarTransaksi.get(kode);
        if (t != null) {
            txtTglbalik.setValue(t.tglKembali);
            JOptionPane.showMessageDialog(this,
                "Ditemukan: " + t.nama + " - " + t.kendaraan + " (Status: " + t.status + ")");
        }
    }//GEN-LAST:event_txtKodeTransaksiActionPerformed

    private void btnProsesPengambilanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProsesPengambilanActionPerformed
        // TODO add your handling code here:
         String kode = txtKodeTransaksi.getText().trim();
        if (kode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan kode transaksi!");
            return;
        }
        Transaksi t = daftarTransaksi.get(kode);
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Kode transaksi tidak ditemukan!");
            return;
        }
        
        if (!STATUS_DIPESAN.equals(t.status)) {
            JOptionPane.showMessageDialog(this, "Transaksi bukan berstatus 'dipesan'!");
            return;
        }
        String kondisi = txtKondisiAwal.getText().trim();
        if (kondisi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kondisi awal wajib diisi!");
            return;
        }

        t.tglAmbil      = (Date) txtTglAmbil.getValue();
        t.tglKembali    = (Date) txtTglbalik.getValue();
        t.tglSewa       = (Date) txtTglAmbil.getValue();
        t.kondisiAmbil  = kondisi;
        t.status        = STATUS_DISEWA;          
        refreshTabel();

        String struk =
            "===== BUKTI PENGAMBILAN =====\n" +
            "Kode Transaksi : " + t.kode + "\n" +
            "Nama           : " + t.nama + "\n" +
            "Kendaraan      : " + t.kendaraan + "\n" +
            "Tgl Diambil    : " + sdf.format(t.tglAmbil) + "\n" +
            "Tgl Kembali    : " + sdf.format(t.tglKembali) + "\n" +
            "Kondisi Awal   : " + t.kondisiAmbil + "\n" +
            "Status         : " + t.status + "\n" +
            "=============================";
        tampilkanStruk("Bukti Pengambilan", struk);
    }//GEN-LAST:event_btnProsesPengambilanActionPerformed

    private void btnProsesPengembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProsesPengembalianActionPerformed
        // TODO add your handling code here:
        String kode = txtKodetransaksi.getText().trim();
    if (kode.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Masukkan kode transaksi!");
        return;
    }
    Transaksi t = daftarTransaksi.get(kode);
    if (t == null) {
        JOptionPane.showMessageDialog(this, "Kode tidak ditemukan!");
        return;
    }
    if (!STATUS_DISEWA.equals(t.status)) {
        JOptionPane.showMessageDialog(this, "Transaksi belum diambil atau sudah dikembalikan!");
        return;
    }

    String kondisi    = cmbKondisiKembali.getSelectedItem().toString();
    String deskripsi  = txtDeskripsiKerusakan.getText().trim();
    boolean terlambat = CbxKeterlambatan.isSelected();

    if (kondisi.contains("Rusak") && deskripsi.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Isi deskripsi kerusakan!");
        return;
    }

    Date tglAktual = (Date) spnTglKembaliAktual.getValue();

    long hari = hariAntara(t.tglSewa, tglAktual);
    if (hari < 1) hari = 1;

    long late = hariAntara(t.tglKembali, tglAktual);
    if (late < 0) late = 0;
    int    hariTelat  = (int) late;
    double biayaTelat = 0;
    if (terlambat && hariTelat > 0) {
        biayaTelat = t.hargaSewaPerHari * hariTelat * PERSEN_DENDA_TELAT;
    }

    double totalBiayaRusak = kondisi.contains("Rusak") ? BIAYA_RUSAK_DEFAULT : 0;

    t.tglDikembalikan = tglAktual; 
    t.kondisiKembali  = kondisi;
    t.deskripsiKerusakan = deskripsi;
    t.hariTelat       = hariTelat;
    t.biayaTelat      = biayaTelat;
    t.totalBiayaRusak = totalBiayaRusak;
    t.totalBayar      = (t.hargaSewaPerHari * hari) + biayaTelat + totalBiayaRusak;
    t.status          = STATUS_SELESAI;

    txtHargaTotal.setText(formatRupiah(t.totalBayar));
    refreshTabel();

    StringBuilder struk = new StringBuilder();
    struk.append("===== STRUK PENGEMBALIAN =====\n")
         .append("Kode Transaksi    : ").append(t.kode).append("\n")
         .append("Nama              : ").append(t.nama).append("\n")
         .append("Kendaraan         : ").append(t.kendaraan).append("\n")
         .append("Tgl Sewa          : ").append(sdf.format(t.tglSewa)).append("\n")
         .append("Tgl Harus Kembali : ").append(sdf.format(t.tglKembali)).append("\n")
         .append("Tgl Dikembalikan  : ").append(sdf.format(tglAktual)).append("\n") // Pakai tglAktual
         .append("Lama Sewa         : ").append(hari).append(" hari\n")
         .append("Kondisi Kembali   : ").append(kondisi).append("\n");
    if (!deskripsi.isEmpty()) {
        struk.append("Kerusakan         : ").append(deskripsi).append("\n");
    }
    if (biayaTelat > 0) {
        struk.append("Hari Telat        : ").append(hariTelat).append(" hari\n")
             .append("Denda Terlambat   : ").append(formatRupiah(biayaTelat)).append("\n");
    }
    if (totalBiayaRusak > 0) {
        struk.append("Biaya Kerusakan   : ").append(formatRupiah(totalBiayaRusak)).append("\n");
    }
    struk.append("---------------------------------\n")
         .append("TOTAL BAYAR       : ").append(formatRupiah(t.totalBayar)).append("\n")
         .append("Status            : ").append(t.status).append("\n")
         .append("===============================");
    tampilkanStruk("Struk Pengembalian", struk.toString());
    }//GEN-LAST:event_btnProsesPengembalianActionPerformed

    private void txtHargaTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHargaTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaTotalActionPerformed

    private void txtKodetransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKodetransaksiActionPerformed
        // TODO add your handling code here:
        hitungTotalPengembalian();
    }//GEN-LAST:event_txtKodetransaksiActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        int pilih = JOptionPane.showConfirmDialog(this,
            "Yakin ingin log out?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (pilih == JOptionPane.YES_OPTION) {
            dispose();
            // new Login().setVisible(true);
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        txtKodeTransaksi.setText("");
        txtKondisiAwal.setText("");
        txtTglAmbil.setValue(new Date());
        txtTglbalik.setValue(new Date());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnBatalBookingRiwayatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalBookingRiwayatActionPerformed
        // TODO add your handling code here:
    int selectedRow = TabRiwayat.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, 
            "Pilih data transaksi di tabel terlebih dahulu!", 
            "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 1. Ambil Kode Transaksi dari kolom ke-1 (indeks 1, karena kolom 0 adalah "No")
    String kode = TabRiwayat.getValueAt(selectedRow, 1).toString();
    Transaksi t = daftarTransaksi.get(kode);

    if (t == null) return;

    // 2. Validasi: Hanya status 'dipesan' (BOOKING) yang boleh dibatalkan
    if (!STATUS_DIPESAN.equals(t.status)) {
        JOptionPane.showMessageDialog(this, 
            "Hanya transaksi berstatus 'dipesan' yang bisa dibatalkan!\n" +
            "Status transaksi ini saat ini: " + t.status.toUpperCase(), 
            "Gagal Membatalkan", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // 3. Tampilkan Konfirmasi
    int konfirmasi = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin membatalkan booking " + kode + "?\n" +
            "Nama: " + t.nama + "\n" +
            "Kendaraan: " + t.kendaraan, 
            "Konfirmasi Pembatalan", 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

    if (konfirmasi == JOptionPane.YES_OPTION) {
        // 4. Ubah status menjadi 'batal'
        t.status = STATUS_BATAL;
        
        refreshTabel();
        
        // 5. Pesan Sukses
        JOptionPane.showMessageDialog(this, 
            "Booking dengan kode " + kode + " berhasil dibatalkan.", 
            "Sukses", JOptionPane.INFORMATION_MESSAGE);
        
        // --- CATATAN UNTUK NANTI SAAT SUDAH PAKAI DATABASE ---
        // Jika nanti sudah tersambung ke MySQL, Anda tinggal memanggil query ini:
        // String sql = "UPDATE transaksi SET status = 'batal' WHERE kode_transaksi = ?";
        // PreparedStatement ps = conn.prepareStatement(sql);
        // ps.setString(1, kode);
        // ps.executeUpdate();
    }
    }//GEN-LAST:event_btnBatalBookingRiwayatActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new KelolaTransaksi().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CbxKeterlambatan;
    private javax.swing.JTable TabRiwayat;
    private javax.swing.JToggleButton btnBatalBooking;
    private javax.swing.JButton btnBatalBookingRiwayat;
    private javax.swing.JButton btnLogout;
    private javax.swing.JToggleButton btnProsesBooking;
    private javax.swing.JButton btnProsesPengambilan;
    private javax.swing.JButton btnProsesPengembalian;
    private javax.swing.JComboBox<String> cmbKondisiKembali;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JSpinner spnTglKembaliAktual;
    private javax.swing.JTextField txtDeskripsiKerusakan;
    private javax.swing.JTextField txtHargaTotal;
    private javax.swing.JComboBox<String> txtJenis;
    private javax.swing.JComboBox<String> txtKendaraanSewa;
    private javax.swing.JTextField txtKodeTransaksi;
    private javax.swing.JTextField txtKodetransaksi;
    private javax.swing.JTextField txtKondisiAwal;
    private javax.swing.JComboBox<String> txtNama;
    private javax.swing.JSpinner txtTglAmbil;
    private javax.swing.JSpinner txtTglKembali;
    private javax.swing.JSpinner txtTglSewa;
    private javax.swing.JSpinner txtTglbalik;
    // End of variables declaration//GEN-END:variables
}
