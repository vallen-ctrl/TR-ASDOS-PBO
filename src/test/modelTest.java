package test;

import Model.admin;
import Model.kerusakan;
import Model.pelanggan;
import Model.interfaces.adminInterface;
import Model.interfaces.kerusakanInterface;
import Model.interfaces.pelangganInterface;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import utils.Format;

public class modelTest {

    private static final Scanner in = new Scanner(System.in);

    private static admin adminModel;
    private static pelanggan pelangganModel;
    private static kerusakan kerusakanModel;

    public static void main(String[] args) throws SQLException {
        try {
            adminModel = new admin();
            pelangganModel = new pelanggan();
            kerusakanModel = new kerusakan();
        } catch (RuntimeException e) {
            System.out.println("Gagal koneksi ke database.");
            System.out.println("Penyebab : " + e.getMessage());
            System.out.println("Pastikan MySQL berjalan dan file .conf sudah benar.");
            return;
        }

        int pilihan;
        do {
            tampilMenuUtama();
            pilihan = bacaInt("Pilih menu: ");
            switch (pilihan) {
                case 1 -> menuAdmin();
                case 2 -> menuPelanggan();
                case 3 -> menuKerusakan();
                case 0 -> System.out.println("Keluar dari program.");
                default -> System.out.println("Pilihan tidak valid.");
            }
        } while (pilihan != 0);
    }

    private static void tampilMenuUtama() {
        System.out.println("\n========================================");
        System.out.println("     SISTEM RENTAL - TEST MODEL");
        System.out.println("========================================");
        System.out.println("1. Kelola Admin");
        System.out.println("2. Kelola Pelanggan");
        System.out.println("3. Kelola Kerusakan");
        System.out.println("0. Keluar");
        System.out.println("========================================");
    }

    private static void menuAdmin() throws SQLException {
        int pilihan;
        do {
            System.out.println("\n--- MENU ADMIN ---");
            System.out.println("1. Lihat semua admin");
            System.out.println("2. Cari admin by ID");
            System.out.println("3. Cari admin by username");
            System.out.println("4. Tambah admin");
            System.out.println("5. Ubah admin");
            System.out.println("6. Hapus admin");
            System.out.println("7. Jumlah admin");
            System.out.println("0. Kembali");
            pilihan = bacaInt("Pilih menu: ");
            switch (pilihan) {
                case 1 -> lihatSemuaAdmin();
                case 2 -> cariAdminById();
                case 3 -> cariAdminByUsername();
                case 4 -> tambahAdmin();
                case 5 -> ubahAdmin();
                case 6 -> hapusAdmin();
                case 7 -> System.out.println("Jumlah admin: " + adminModel.jumlahAdmin());
                case 0 -> {
                }
                default -> System.out.println("Pilihan tidak valid.");
            }
        } while (pilihan != 0);
    }

    private static void lihatSemuaAdmin() throws SQLException {
        System.out.println("\n--- Daftar Admin ---");
        List<adminInterface> daftar = adminModel.getAdmin();
        if (daftar.isEmpty()) {
            System.out.println("(belum ada data)");
            return;
        }
        System.out.printf("%-4s %-30s %-15s %-10s %-12s%n",
                "ID", "Nama", "Username", "Status", "Dibuat");
        System.out.println("------------------------------------------------------------------------------");
        for (adminInterface a : daftar) {
            System.out.printf("%-4d %-30s %-15s %-10s %-12s%n",
                    a.id_user,
                    a.nama_lengkap,
                    a.username,
                    "1".equals(a.status_aktif) ? "aktif" : "nonaktif",
                    a.created_at == null ? "-" : a.created_at.toString());
        }
    }

    private static void cariAdminById() {
        System.out.println("\n--- Cari Admin by ID ---");
        try {
            int id = bacaInt("ID admin: ");
            adminInterface a = adminModel.getAdminById(id);
            if (a == null) {
                System.out.println("Admin dengan ID " + id + " tidak ditemukan.");
            } else {
                tampilAdmin(a);
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void cariAdminByUsername() {
        System.out.println("\n--- Cari Admin by Username ---");
        try {
            String username = bacaText("Username: ");
            adminInterface a = adminModel.getAdminByUsername(username);
            if (a == null) {
                System.out.println("Username '" + username + "' tidak ditemukan.");
            } else {
                tampilAdmin(a);
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void tambahAdmin() {
        System.out.println("\n--- Tambah Admin ---");
        try {
            String nama = bacaText("Nama lengkap : ");
            String username = bacaText("Username     : ");
            String password = bacaText("Password     : ");
            boolean aktif = bacaBoolean("Status aktif (1=aktif, 0=nonaktif): ");

            boolean ok = adminModel.tambahAdmin(nama, username, password, aktif);
            System.out.println(ok ? "Admin berhasil ditambahkan." : "Admin gagal ditambahkan.");
        } catch (InputMismatchException e) {
            System.out.println("Validasi gagal: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void ubahAdmin() {
        System.out.println("\n--- Ubah Admin ---");
        try {
            int id = bacaInt("ID admin yang diubah: ");
            adminInterface lama = adminModel.getAdminById(id);
            if (lama == null) {
                System.out.println("Admin dengan ID " + id + " tidak ditemukan.");
                return;
            }
            System.out.println("(kosongkan input untuk memakai nilai lama)");

            String nama = bacaTextAtau("Nama lengkap [" + lama.nama_lengkap + "]: ", lama.nama_lengkap);
            String username = bacaTextAtau("Username [" + lama.username + "]: ", lama.username);
            String password = bacaText("Password baru (kosongkan jika tidak diubah): ");
            boolean aktif = bacaBooleanAtau(
                    "Status aktif (1/0) [" + ("1".equals(lama.status_aktif) ? "1" : "0") + "]: ",
                    "1".equals(lama.status_aktif));

            boolean ok = adminModel.ubahAdmin(id, nama, username, password, aktif);
            System.out.println(ok ? "Admin berhasil diubah." : "Admin gagal diubah.");
        } catch (InputMismatchException e) {
            System.out.println("Validasi gagal: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void hapusAdmin() {
        System.out.println("\n--- Hapus Admin ---");
        try {
            int id = bacaInt("ID admin yang dihapus: ");
            adminInterface a = adminModel.getAdminById(id);
            if (a == null) {
                System.out.println("Admin dengan ID " + id + " tidak ditemukan.");
                return;
            }
            String konfirmasi = bacaText("Yakin hapus '" + a.nama_lengkap + "'? (y/t): ");
            if (!konfirmasi.equalsIgnoreCase("y")) {
                System.out.println("Penghapusan dibatalkan.");
                return;
            }
            boolean ok = adminModel.hapusAdmin(id);
            System.out.println(ok ? "Admin berhasil dihapus." : "Admin gagal dihapus.");
        } catch (InputMismatchException e) {
            System.out.println("Validasi gagal: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void tampilAdmin(adminInterface a) {
        System.out.println("ID       : " + a.id_user);
        System.out.println("Nama     : " + a.nama_lengkap);
        System.out.println("Username : " + a.username);
        System.out.println("Status   : " + ("1".equals(a.status_aktif) ? "aktif" : "nonaktif"));
        System.out.println("Dibuat   : " + (a.created_at == null ? "-" : a.created_at));
    }

    private static void menuPelanggan() throws SQLException {
        int pilihan;
        do {
            System.out.println("\n--- MENU PELANGGAN ---");
            System.out.println("1. Lihat semua pelanggan");
            System.out.println("2. Cari pelanggan by ID");
            System.out.println("3. Cari pelanggan by No KTP");
            System.out.println("4. Cari pelanggan by nama");
            System.out.println("5. Tambah pelanggan");
            System.out.println("6. Ubah pelanggan");
            System.out.println("7. Hapus pelanggan");
            System.out.println("8. Jumlah pelanggan");
            System.out.println("0. Kembali");
            pilihan = bacaInt("Pilih menu: ");
            switch (pilihan) {
                case 1 -> lihatSemuaPelanggan();
                case 2 -> cariPelangganById();
                case 3 -> cariPelangganByNoKtp();
                case 4 -> cariPelangganByNama();
                case 5 -> tambahPelanggan();
                case 6 -> ubahPelanggan();
                case 7 -> hapusPelanggan();
                case 8 -> System.out.println("Jumlah pelanggan: " + pelangganModel.jumlahPelanggan());
                case 0 -> {
                }
                default -> System.out.println("Pilihan tidak valid.");
            }
        } while (pilihan != 0);
    }

    private static void lihatSemuaPelanggan() throws SQLException {
        System.out.println("\n--- Daftar Pelanggan ---");
        tampilDaftarPelanggan(pelangganModel.getPelanggan());
    }

    private static void cariPelangganById() {
        System.out.println("\n--- Cari Pelanggan by ID ---");
        try {
            int id = bacaInt("ID pelanggan: ");
            pelangganInterface p = pelangganModel.getPelangganById(id);
            if (p == null) {
                System.out.println("Pelanggan dengan ID " + id + " tidak ditemukan.");
            } else {
                tampilPelanggan(p);
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void cariPelangganByNoKtp() {
        System.out.println("\n--- Cari Pelanggan by No KTP ---");
        try {
            String noKtp = bacaText("No KTP: ");
            pelangganInterface p = pelangganModel.getPelangganByNoKtp(noKtp);
            if (p == null) {
                System.out.println("Pelanggan dengan No KTP '" + noKtp + "' tidak ditemukan.");
            } else {
                tampilPelanggan(p);
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void cariPelangganByNama() {
        System.out.println("\n--- Cari Pelanggan by Nama ---");
        try {
            String keyword = bacaText("Potongan nama: ");
            tampilDaftarPelanggan(pelangganModel.cariPelangganByNama(keyword));
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void tambahPelanggan() {
        System.out.println("\n--- Tambah Pelanggan ---");
        try {
            String nama = bacaText("Nama        : ");
            String noKtp = bacaText("No KTP (16) : ");
            String noSim = bacaText("No SIM      : ");
            String noHp = bacaText("No HP       : ");
            String alamat = bacaText("Alamat      : ");
            String status = bacaStatusPelanggan();

            boolean ok = pelangganModel.tambahPelanggan(nama, noKtp, noSim, noHp, alamat, status);
            System.out.println(ok ? "Pelanggan berhasil ditambahkan." : "Pelanggan gagal ditambahkan.");
        } catch (InputMismatchException e) {
            System.out.println("Validasi gagal: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void ubahPelanggan() {
        System.out.println("\n--- Ubah Pelanggan ---");
        try {
            int id = bacaInt("ID pelanggan yang diubah: ");
            pelangganInterface lama = pelangganModel.getPelangganById(id);
            if (lama == null) {
                System.out.println("Pelanggan dengan ID " + id + " tidak ditemukan.");
                return;
            }
            System.out.println("(kosongkan input untuk memakai nilai lama)");

            String nama = bacaTextAtau("Nama [" + lama.nama + "]: ", lama.nama);
            String noKtp = bacaTextAtau("No KTP [" + lama.no_ktp + "]: ", lama.no_ktp);
            String noSimLama = lama.no_sim == null ? "" : lama.no_sim;
            String alamatLama = lama.alamat == null ? "" : lama.alamat;
            String noSim = bacaTextAtau("No SIM [" + teksAtauStrip(lama.no_sim) + "]: ", noSimLama);
            String noHp = bacaTextAtau("No HP [" + lama.no_hp + "]: ", lama.no_hp);
            String alamat = bacaTextAtau("Alamat [" + teksAtauStrip(lama.alamat) + "]: ", alamatLama);
            String status = bacaStatusPelangganAtau(lama.status);

            boolean ok = pelangganModel.ubahPelanggan(id, nama, noKtp, noSim, noHp, alamat, status);
            System.out.println(ok ? "Pelanggan berhasil diubah." : "Pelanggan gagal diubah.");
        } catch (InputMismatchException e) {
            System.out.println("Validasi gagal: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void hapusPelanggan() {
        System.out.println("\n--- Hapus Pelanggan ---");
        try {
            int id = bacaInt("ID pelanggan yang dihapus: ");
            pelangganInterface p = pelangganModel.getPelangganById(id);
            if (p == null) {
                System.out.println("Pelanggan dengan ID " + id + " tidak ditemukan.");
                return;
            }
            String konfirmasi = bacaText("Yakin hapus '" + p.nama + "'? (y/t): ");
            if (!konfirmasi.equalsIgnoreCase("y")) {
                System.out.println("Penghapusan dibatalkan.");
                return;
            }
            boolean ok = pelangganModel.hapusPelanggan(id);
            System.out.println(ok ? "Pelanggan berhasil dihapus." : "Pelanggan gagal dihapus.");
        } catch (InputMismatchException e) {
            System.out.println("Validasi gagal: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void tampilDaftarPelanggan(List<pelangganInterface> daftar) {
        if (daftar.isEmpty()) {
            System.out.println("(belum ada data)");
            return;
        }
        System.out.printf("%-4s %-20s %-18s %-15s %-14s%n",
                "ID", "Nama", "No KTP", "No HP", "Status");
        System.out.println("--------------------------------------------------------------------------");
        for (pelangganInterface p : daftar) {
            System.out.printf("%-4d %-20s %-18s %-15s %-14s%n",
                    p.id_pelanggan,
                    p.nama,
                    p.no_ktp,
                    p.no_hp,
                    p.status);
        }
    }

    private static void tampilPelanggan(pelangganInterface p) {
        System.out.println("ID       : " + p.id_pelanggan);
        System.out.println("Nama     : " + p.nama);
        System.out.println("No KTP   : " + p.no_ktp);
        System.out.println("No SIM   : " + teksAtauStrip(p.no_sim));
        System.out.println("No HP    : " + p.no_hp);
        System.out.println("Alamat   : " + teksAtauStrip(p.alamat));
        System.out.println("Status   : " + p.status);
        System.out.println("Dibuat   : " + (p.created_at == null ? "-" : p.created_at));
    }

    private static void menuKerusakan() throws SQLException {
        int pilihan;
        do {
            System.out.println("\n--- MENU KERUSAKAN ---");
            System.out.println("1. Lihat semua kerusakan");
            System.out.println("2. Cari kerusakan by ID");
            System.out.println("3. Lihat kerusakan by ID pengembalian");
            System.out.println("4. Tambah kerusakan");
            System.out.println("5. Ubah kerusakan");
            System.out.println("6. Hapus kerusakan");
            System.out.println("7. Total biaya per pengembalian");
            System.out.println("8. Jumlah kerusakan");
            System.out.println("0. Kembali");
            pilihan = bacaInt("Pilih menu: ");
            switch (pilihan) {
                case 1 -> lihatSemuaKerusakan();
                case 2 -> cariKerusakanById();
                case 3 -> lihatKerusakanByPengembalian();
                case 4 -> tambahKerusakan();
                case 5 -> ubahKerusakan();
                case 6 -> hapusKerusakan();
                case 7 -> totalBiayaKerusakan();
                case 8 -> System.out.println("Jumlah kerusakan: " + kerusakanModel.jumlahKerusakan());
                case 0 -> {
                }
                default -> System.out.println("Pilihan tidak valid.");
            }
        } while (pilihan != 0);
    }

    private static void lihatSemuaKerusakan() throws SQLException {
        System.out.println("\n--- Daftar Kerusakan ---");
        tampilDaftarKerusakan(kerusakanModel.getKerusakan());
    }

    private static void cariKerusakanById() {
        System.out.println("\n--- Cari Kerusakan by ID ---");
        try {
            int id = bacaInt("ID kerusakan: ");
            kerusakanInterface k = kerusakanModel.getKerusakanById(id);
            if (k == null) {
                System.out.println("Kerusakan dengan ID " + id + " tidak ditemukan.");
            } else {
                tampilKerusakan(k);
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void lihatKerusakanByPengembalian() {
        System.out.println("\n--- Kerusakan per Pengembalian ---");
        try {
            int idPengembalian = bacaInt("ID pengembalian: ");
            tampilDaftarKerusakan(kerusakanModel.getKerusakanByIdPengembalian(idPengembalian));
        } catch (InputMismatchException e) {
            System.out.println("Validasi gagal: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void tambahKerusakan() {
        System.out.println("\n--- Tambah Kerusakan ---");
        try {
            int idPengembalian = bacaInt("ID pengembalian : ");
            String deskripsi = bacaText("Deskripsi       : ");
            double biaya = bacaDouble("Biaya (Rp)      : ");
            int dicatatOleh = bacaInt("ID admin pencatat: ");

            boolean ok = kerusakanModel.tambahKerusakan(idPengembalian, deskripsi, biaya, dicatatOleh);
            System.out.println(ok ? "Kerusakan berhasil ditambahkan." : "Kerusakan gagal ditambahkan.");
        } catch (InputMismatchException e) {
            System.out.println("Validasi gagal: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void ubahKerusakan() {
        System.out.println("\n--- Ubah Kerusakan ---");
        try {
            int id = bacaInt("ID kerusakan yang diubah: ");
            kerusakanInterface lama = kerusakanModel.getKerusakanById(id);
            if (lama == null) {
                System.out.println("Kerusakan dengan ID " + id + " tidak ditemukan.");
                return;
            }
            System.out.println("(kosongkan input untuk memakai nilai lama)");

            int idPengembalian = bacaIntAtau(
                    "ID pengembalian [" + lama.id_pengembalian + "]: ", lama.id_pengembalian);
            String deskripsi = bacaTextAtau("Deskripsi [" + lama.deskripsi + "]: ", lama.deskripsi);
            double biaya = bacaDoubleAtau("Biaya [" + Format.rupiah(lama.biaya) + "]: ", lama.biaya);
            int dicatatOleh = bacaIntAtau(
                    "ID admin pencatat [" + lama.dicatat_oleh + "]: ", lama.dicatat_oleh);

            boolean ok = kerusakanModel.ubahKerusakan(id, idPengembalian, deskripsi, biaya, dicatatOleh);
            System.out.println(ok ? "Kerusakan berhasil diubah." : "Kerusakan gagal diubah.");
        } catch (InputMismatchException e) {
            System.out.println("Validasi gagal: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void hapusKerusakan() {
        System.out.println("\n--- Hapus Kerusakan ---");
        try {
            int id = bacaInt("ID kerusakan yang dihapus: ");
            kerusakanInterface k = kerusakanModel.getKerusakanById(id);
            if (k == null) {
                System.out.println("Kerusakan dengan ID " + id + " tidak ditemukan.");
                return;
            }
            String konfirmasi = bacaText("Yakin hapus data '" + k.deskripsi + "'? (y/t): ");
            if (!konfirmasi.equalsIgnoreCase("y")) {
                System.out.println("Penghapusan dibatalkan.");
                return;
            }
            boolean ok = kerusakanModel.hapusKerusakan(id);
            System.out.println(ok ? "Kerusakan berhasil dihapus." : "Kerusakan gagal dihapus.");
        } catch (InputMismatchException e) {
            System.out.println("Validasi gagal: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void totalBiayaKerusakan() {
        System.out.println("\n--- Total Biaya Kerusakan ---");
        try {
            int idPengembalian = bacaInt("ID pengembalian: ");
            double total = kerusakanModel.totalBiayaKerusakan(idPengembalian);
            System.out.println("Total biaya kerusakan: " + Format.rupiah(total));
        } catch (InputMismatchException e) {
            System.out.println("Validasi gagal: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private static void tampilDaftarKerusakan(List<kerusakanInterface> daftar) {
        if (daftar.isEmpty()) {
            System.out.println("(belum ada data)");
            return;
        }
        System.out.printf("%-4s %-16s %-35s %-16s %-12s%n",
                "ID", "ID Pengembalian", "Deskripsi", "Biaya", "Dicatat Oleh");
        System.out.println("--------------------------------------------------------------------------------");
        for (kerusakanInterface k : daftar) {
            System.out.printf("%-4d %-16d %-35s %-16s %-12d%n",
                    k.id_kerusakan,
                    k.id_pengembalian,
                    k.deskripsi,
                    Format.rupiah(k.biaya),
                    k.dicatat_oleh);
        }
    }

    private static void tampilKerusakan(kerusakanInterface k) {
        System.out.println("ID kerusakan    : " + k.id_kerusakan);
        System.out.println("ID pengembalian : " + k.id_pengembalian);
        System.out.println("Deskripsi       : " + k.deskripsi);
        System.out.println("Biaya           : " + Format.rupiah(k.biaya));
        System.out.println("Dicatat oleh    : " + k.dicatat_oleh);
        System.out.println("Dibuat          : " + (k.created_at == null ? "-" : k.created_at));
    }

    private static String bacaText(String label) {
        System.out.print(label);
        return in.nextLine().trim();
    }

    private static String bacaTextAtau(String label, String nilaiDefault) {
        String teks = bacaText(label);
        return teks.isEmpty() ? nilaiDefault : teks;
    }

    private static int bacaInt(String label) {
        while (true) {
            String teks = bacaText(label);
            try {
                return Integer.parseInt(teks);
            } catch (NumberFormatException e) {
                System.out.println("  ! Masukkan angka yang valid.");
            }
        }
    }

    private static int bacaIntAtau(String label, int nilaiDefault) {
        String teks = bacaText(label);
        if (teks.isEmpty()) {
            return nilaiDefault;
        }
        try {
            return Integer.parseInt(teks);
        } catch (NumberFormatException e) {
            System.out.println("  ! Bukan angka, memakai nilai lama (" + nilaiDefault + ").");
            return nilaiDefault;
        }
    }

    private static double bacaDouble(String label) {
        while (true) {
            String teks = bacaText(label).replace(',', '.');
            try {
                return Double.parseDouble(teks);
            } catch (NumberFormatException e) {
                System.out.println("  ! Masukkan angka yang valid (contoh: 150000 atau 150000,50).");
            }
        }
    }

    private static double bacaDoubleAtau(String label, double nilaiDefault) {
        String teks = bacaText(label).replace(',', '.');
        if (teks.isEmpty()) {
            return nilaiDefault;
        }
        try {
            return Double.parseDouble(teks);
        } catch (NumberFormatException e) {
            System.out.println("  ! Bukan angka, memakai nilai lama (" + nilaiDefault + ").");
            return nilaiDefault;
        }
    }

    private static boolean bacaBoolean(String label) {
        while (true) {
            String teks = bacaText(label).trim().toLowerCase();
            if (teks.equals("1") || teks.equals("y") || teks.equals("ya")
                    || teks.equals("true") || teks.equals("aktif")) {
                return true;
            }
            if (teks.equals("0") || teks.equals("t") || teks.equals("tidak")
                    || teks.equals("false") || teks.equals("nonaktif")) {
                return false;
            }
            System.out.println("  ! Jawab 1/0 atau y/t.");
        }
    }

    private static boolean bacaBooleanAtau(String label, boolean nilaiDefault) {
        String teks = bacaText(label).trim().toLowerCase();
        if (teks.isEmpty()) {
            return nilaiDefault;
        }
        if (teks.equals("1") || teks.equals("y") || teks.equals("ya")
                || teks.equals("true") || teks.equals("aktif")) {
            return true;
        }
        if (teks.equals("0") || teks.equals("t") || teks.equals("tidak")
                || teks.equals("false") || teks.equals("nonaktif")) {
            return false;
        }
        System.out.println("  ! Bukan pilihan valid, memakai nilai lama.");
        return nilaiDefault;
    }

    private static String bacaStatusPelanggan() {
        while (true) {
            System.out.println("  Status: 1=aktif, 2=sedang_menyewa, 3=blacklist");
            int pilihan = bacaInt("  Pilih status: ");
            switch (pilihan) {
                case 1:
                    return "aktif";
                case 2:
                    return "sedang_menyewa";
                case 3:
                    return "blacklist";
                default:
                    System.out.println("  ! Pilihan status tidak valid.");
            }
        }
    }

    private static String bacaStatusPelangganAtau(String nilaiDefault) {
        System.out.println("  Status sekarang: " + nilaiDefault);
        System.out.println("  Status: 1=aktif, 2=sedang_menyewa, 3=blacklist, 0=tetap");
        int pilihan = bacaInt("  Pilih status: ");
        switch (pilihan) {
            case 1:
                return "aktif";
            case 2:
                return "sedang_menyewa";
            case 3:
                return "blacklist";
            default:
                return nilaiDefault;
        }
    }

    private static String teksAtauStrip(String teks) {
        return (teks == null || teks.isBlank()) ? "-" : teks;
    }
}
