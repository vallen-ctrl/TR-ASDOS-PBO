/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.InputMismatchException;

/**
 *
 * @author vallen
 */
public class transaksiInterface {
    private final String[] enum_status = {"dipesan", "disewa", "selesai", "batal"};
    
    public final int id;
    public final int id_transaksi;
    public final String kode_transaksi;
    public final int id_pelanggan;
    public final int id_kendaraan;
    public final int id_kasir;
    public final LocalDate tgl_sewa;
    public final LocalDate tgl_kembali_rencana;
    
    public final BigDecimal harga_sewa_perHari;
    public final BigDecimal harga_sewa_per_hari;
    public final int jumlah_hari;
    public final BigDecimal total_sewa;
    public final LocalDate tgl_ambil;
    public final String kondisi_ambil;
    public final String status;
    
    public final LocalDateTime createAt;
    public final LocalDateTime created_at;

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
    
    private void chekEnumTrasaksi(String status){
        for(String enumStatus : this.enum_status){
            if(enumStatus.equals(status)){
                return;
            }
        }
        throw new InputMismatchException("Input hanya menerima: "+String.join(", ", enum_status));
    }

    public transaksiInterface(int id, String kode_transaksi, int id_pelanggan, int id_kendaraan, int id_kasir, LocalDate tgl_sewa, LocalDate tgl_kembali_rencana, BigDecimal harga_sewa_perHari, int jumlah_hari, BigDecimal total_sewa, LocalDate tgl_ambil, String kondisi_ambil, String status, LocalDateTime createAt) {
        this(id, kode_transaksi, id_pelanggan, id_kendaraan, id_kasir, tgl_sewa,
                tgl_kembali_rencana, harga_sewa_perHari, jumlah_hari, total_sewa,
                tgl_ambil, kondisi_ambil, status, createAt, null, null, null, null,
                kondisi_ambil, total_sewa, BigDecimal.ZERO, BigDecimal.ZERO,
                new BigDecimal("10.00"), 0, null, null, null);
    }

    /** Constructor lengkap untuk data transaksi hasil JOIN. */
    public transaksiInterface(int id, String kode_transaksi, int id_pelanggan,
            int id_kendaraan, int id_kasir, LocalDate tgl_sewa,
            LocalDate tgl_kembali_rencana, BigDecimal harga_sewa_perHari,
            int jumlah_hari, BigDecimal total_sewa, LocalDate tgl_ambil,
            String kondisi_ambil, String status, LocalDateTime createAt,
            String nama_pelanggan, String kendaraan, String jenis_kendaraan,
            String plat_nomor, String kondisi_ambil_lengkap, BigDecimal total_bayar,
            BigDecimal biaya_telat, BigDecimal total_biaya_rusak,
            BigDecimal persen_denda_telat, int hari_telat,
            LocalDate tgl_kembali_aktual, String kondisi_kembali,
            LocalDateTime tgl_ambil_datetime) {
        this.id = id;
        this.id_transaksi = id;
        this.kode_transaksi = kode_transaksi;
        this.id_pelanggan = id_pelanggan;
        this.id_kendaraan = id_kendaraan;
        this.id_kasir = id_kasir;
        this.tgl_sewa = tgl_sewa;
        this.tgl_kembali_rencana = tgl_kembali_rencana;
        this.harga_sewa_perHari = harga_sewa_perHari;
        this.harga_sewa_per_hari = harga_sewa_perHari;
        this.jumlah_hari = jumlah_hari;
        this.total_sewa = total_sewa;
        this.tgl_ambil = tgl_ambil;
        this.kondisi_ambil = kondisi_ambil;
        this.chekEnumTrasaksi(status);
        this.status = status;
        this.createAt = createAt;
        this.created_at = createAt;
        this.nama_pelanggan = nama_pelanggan;
        this.kendaraan = kendaraan;
        this.jenis_kendaraan = jenis_kendaraan;
        this.plat_nomor = plat_nomor;
        this.kondisi_ambil_lengkap = kondisi_ambil_lengkap;
        this.total_bayar = total_bayar == null ? BigDecimal.ZERO : total_bayar;
        this.biaya_telat = biaya_telat == null ? BigDecimal.ZERO : biaya_telat;
        this.total_biaya_rusak = total_biaya_rusak == null ? BigDecimal.ZERO : total_biaya_rusak;
        this.persen_denda_telat = persen_denda_telat == null
                ? new BigDecimal("10.00") : persen_denda_telat;
        this.hari_telat = hari_telat;
        this.tgl_kembali_aktual = tgl_kembali_aktual;
        this.kondisi_kembali = kondisi_kembali;
        this.tgl_ambil_datetime = tgl_ambil_datetime;
    }


}
