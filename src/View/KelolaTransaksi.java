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

    // ====== DATA IN-MEMORY ======
private static final Map<String, Transaksi> daftarTransaksi = new HashMap<>();
private static int counterTransaksi = 1;
private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

// Model data transaksi
private static class Transaksi {
    String kode, nama, jenis, kendaraan, kondisiAwal, kondisiKembali, deskripsiKerusakan, status;
    Date tglSewa, tglKembali, tglAmbil, tglDikembalikan;
    boolean terlambat;
    double totalHarga;
}
// ============================
    /**
     * Creates new form KelolaTransaksi
     */
    public KelolaTransaksi() {
        initComponents();
        setupForm(); 
    }
    // ====== INIT FORM (date spinner + auto-hitung) ======
private void setupForm() {
    Date today = new Date();
    setupDateSpinner(txtTglSewa, today);
    setupDateSpinner(txtTglKembali, today);
    setupDateSpinner(txtTglAmbil, today);
    setupDateSpinner(txtTglbalik, today);
    setupDateSpinner(spnTglDikembalikan, today);

    // Auto-hitung total di tab Pengembalian
    spnTglDikembalikan.addChangeListener(e -> hitungTotalPengembalian());
    CbxKeterlambatan.addItemListener(e -> hitungTotalPengembalian());
    cmbKondisiKembali.addItemListener(e -> hitungTotalPengembalian());
    txtKodetransaksi.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override public void focusLost(java.awt.event.FocusEvent e) { hitungTotalPengembalian(); }
    });
}

private void setupDateSpinner(JSpinner sp, Date init) {
    sp.setModel(new SpinnerDateModel(init, null, null, java.util.Calendar.DAY_OF_MONTH));
    sp.setEditor(new JSpinner.DateEditor(sp, "dd-MM-yyyy"));
}

// ====== HELPER ======
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
    if (kode.isEmpty()) { txtHargaTotal.setText(""); return; }
    Transaksi t = daftarTransaksi.get(kode);
    if (t == null) { txtHargaTotal.setText("Kode tidak ditemukan"); return; }
    if (!"DIAMBIL".equals(t.status)) { txtHargaTotal.setText("-"); return; }

    Date tglKembali = (Date) spnTglDikembalikan.getValue();
    long hari = hariAntara(t.tglSewa, tglKembali);
    if (hari < 1) hari = 1;
    double total = getHargaPerHari(t.kendaraan) * hari;

    long late = hariAntara(t.tglKembali, tglKembali);
    if (late > 0 && CbxKeterlambatan.isSelected()) {
        total += getHargaPerHari(t.kendaraan) * late * 0.5;
    }
    if (cmbKondisiKembali.getSelectedItem().toString().contains("Rusak")) {
        total += 500_000;
    }
    txtHargaTotal.setText(formatRupiah(total));
}

    private void refreshTabel() {
        // 1. Buat model tabel baru yang bersih (mengabaikan settingan error dari NetBeans)
        DefaultTableModel m = new DefaultTableModel(
            new Object[][]{},
            new String[]{"No", "Nama Pelanggan", "Kode Transaksi", "Kendaraan", "Tgl Sewa", "Tgl Kembali", "Status", "Total Harga"}
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // Kolom No
                if (columnIndex == 7) return Double.class;  // Kolom Total Harga
                return String.class; // Paksa semua kolom lain jadi String (teks)
            }
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false; // Supaya tabel tidak bisa diedit manual oleh user
            }
        };
        
        // 2. Pasang model baru ini ke tabel TabRiwayat kamu
        TabRiwayat.setModel(m); 

        // 3. Isi datanya dari daftarTransaksi
        List<String> keys = new ArrayList<>(daftarTransaksi.keySet());
        Collections.sort(keys);
        int no = 1;
        for (String k : keys) {
            Transaksi t = daftarTransaksi.get(k);
            m.addRow(new Object[]{
                no++, 
                t.nama, 
                t.kode, 
                t.kendaraan, 
                sdf.format(t.tglSewa), 
                t.tglKembali == null ? "-" : sdf.format(t.tglKembali), 
                t.status, 
                t.totalHarga
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

        jPanel6 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
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
        spnTglDikembalikan = new javax.swing.JSpinner();
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
        jPanel10 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 750, Short.MAX_VALUE)
                .addComponent(btnLogout)
                .addGap(16, 16, 16))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(btnLogout))
                .addGap(0, 0, 0))
        );

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
                .addContainerGap(37, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
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
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
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

        spnTglDikembalikan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

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
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtDeskripsiKerusakan)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtKodetransaksi)
                                .addComponent(spnTglDikembalikan)
                                .addComponent(cmbKondisiKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 807, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13)
                                .addComponent(jLabel12)
                                .addComponent(jLabel11)
                                .addComponent(jLabel10))
                            .addComponent(jLabel17)
                            .addComponent(jLabel16)
                            .addComponent(txtHargaTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(210, 210, 210))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addComponent(btnProsesPengembalian)
                        .addGap(68, 68, 68))))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(28, 28, 28)
                .addComponent(jLabel16)
                .addGap(3, 3, 3)
                .addComponent(txtKodetransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spnTglDikembalikan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(txtHargaTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnProsesPengembalian)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 982, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 930, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
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
                .addGap(15, 15, 15))
        );

        jTabbedPane1.addTab("Riwayat", jPanel7);

        jPanel10.setBackground(new java.awt.Color(204, 255, 204));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1039, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 572, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
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
        String jenis     = txtJenis.getSelectedItem().toString();
        String kendaraan = txtKendaraanSewa.getSelectedItem().toString();
        Date tglSewa     = (Date) txtTglSewa.getValue();
        Date tglKembali  = (Date) txtTglKembali.getValue();

        if (hariAntara(tglSewa, tglKembali) <= 0) {
            JOptionPane.showMessageDialog(this,
                "Tanggal kembali harus setelah tanggal sewa!", "Validasi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String kode = String.format("M%02d", counterTransaksi++);
        Transaksi t = new Transaksi();
        t.kode = kode;
        t.nama = nama;
        t.jenis = jenis;
        t.kendaraan = kendaraan;
        t.tglSewa = tglSewa;
        t.tglKembali = tglKembali;
        t.status = "BOOKING";
        t.totalHarga = 0;
        daftarTransaksi.put(kode, t);

        long hari = hariAntara(tglSewa, tglKembali);
        double estimasi = getHargaPerHari(kendaraan) * hari;

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
            "Estimasi Biaya : " + formatRupiah(estimasi) + "\n" +
            "Status         : BOOKING\n" +
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
        if (!"BOOKING".equals(t.status)) {
            JOptionPane.showMessageDialog(this, "Transaksi bukan status BOOKING!");
            return;
        }
        String kondisi = txtKondisiAwal.getText().trim();
        if (kondisi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kondisi awal wajib diisi!");
            return;
        }
        t.tglAmbil    = (Date) txtTglAmbil.getValue();
        t.tglKembali  = (Date) txtTglbalik.getValue();
        t.kondisiAwal = kondisi;
        t.status      = "DIAMBIL";
        refreshTabel();

        String struk =
            "===== BUKTI PENGAMBILAN =====\n" +
            "Kode Transaksi : " + t.kode + "\n" +
            "Nama           : " + t.nama + "\n" +
            "Kendaraan      : " + t.kendaraan + "\n" +
            "Tgl Diambil    : " + sdf.format(t.tglAmbil) + "\n" +
            "Tgl Kembali    : " + sdf.format(t.tglKembali) + "\n" +
            "Kondisi Awal   : " + t.kondisiAwal + "\n" +
            "Status         : DIAMBIL\n" +
            "=============================";
        tampilkanStruk("Bukti Pengambilan", struk);
    }//GEN-LAST:event_btnProsesPengambilanActionPerformed

    private void btnProsesPengembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProsesPengembalianActionPerformed
        // TODO add your handling code here:
         String kode = txtKodetransaksi.getText().trim();
        if (kode.isEmpty()) { JOptionPane.showMessageDialog(this, "Masukkan kode transaksi!"); return; }
        Transaksi t = daftarTransaksi.get(kode);
        if (t == null) { JOptionPane.showMessageDialog(this, "Kode tidak ditemukan!"); return; }
        if (!"DIAMBIL".equals(t.status)) {
            JOptionPane.showMessageDialog(this, "Transaksi belum diambil / sudah dikembalikan!");
            return;
        }

        String kondisi    = cmbKondisiKembali.getSelectedItem().toString();
        String deskripsi  = txtDeskripsiKerusakan.getText().trim();
        boolean terlambat = CbxKeterlambatan.isSelected();

        if (kondisi.contains("Rusak") && deskripsi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi deskripsi kerusakan!");
            return;
        }

        Date tglKembali = (Date) spnTglDikembalikan.getValue();
        t.tglDikembalikan    = tglKembali;
        t.kondisiKembali     = kondisi;
        t.deskripsiKerusakan = deskripsi;
        t.terlambat          = terlambat;

        long hari = hariAntara(t.tglSewa, tglKembali);
        if (hari < 1) hari = 1;
        double biayaSewa = getHargaPerHari(t.kendaraan) * hari;

        long late = hariAntara(t.tglKembali, tglKembali);
        double denda = (late > 0 && terlambat) ? getHargaPerHari(t.kendaraan) * late * 0.5 : 0;
        double biayaRusak = kondisi.contains("Rusak") ? 500_000 : 0;

        t.totalHarga = biayaSewa + denda + biayaRusak;
        t.status     = "DIKEMBALIKAN";
        txtHargaTotal.setText(formatRupiah(t.totalHarga));
        refreshTabel();

        String struk =
            "===== STRUK PENGEMBALIAN =====\n" +
            "Kode Transaksi    : " + t.kode + "\n" +
            "Nama              : " + t.nama + "\n" +
            "Kendaraan         : " + t.kendaraan + "\n" +
            "Tgl Sewa          : " + sdf.format(t.tglSewa) + "\n" +
            "Tgl Harus Kembali : " + sdf.format(t.tglKembali) + "\n" +
            "Tgl Dikembalikan  : " + sdf.format(tglKembali) + "\n" +
            "Lama Sewa         : " + hari + " hari\n" +
            "Kondisi Kembali   : " + kondisi + "\n" +
            (deskripsi.isEmpty() ? "" : "Kerusakan         : " + deskripsi + "\n") +
            (denda > 0 ? "Denda Terlambat   : " + formatRupiah(denda) + "\n" : "") +
            (biayaRusak > 0 ? "Biaya Kerusakan   : " + formatRupiah(biayaRusak) + "\n" : "") +
            "---------------------------------\n" +
            "TOTAL BAYAR       : " + formatRupiah(t.totalHarga) + "\n" +
            "Status            : DIKEMBALIKAN\n" +
            "===============================";
        tampilkanStruk("Struk Pengembalian", struk);
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
    private javax.swing.JSpinner spnTglDikembalikan;
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
