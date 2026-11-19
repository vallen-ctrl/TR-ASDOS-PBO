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
    public final String kode_transaksi;
    public final int id_pelanggan;
    public final int id_kendaraan;
    public final int id_kasir;
    public final LocalDate tgl_sewa;
    public final LocalDate tgl_kembali_rencana;
    public final BigDecimal harga_sewa_perHari;
    public final int jumlah_hari;
    public final BigDecimal total_sewa;
    public final LocalDate tgl_ambil;
    public final String kondisi_ambil;
    public final String status;
    public final LocalDateTime createAt;
    
    private void chekEnumTrasaksi(String status){
        for(String enumStatus : this.enum_status){
            if(enumStatus.equals(status)){
                return;
            }
        }
        throw new InputMismatchException("Input hanya menerima: "+String.join(", ", enum_status));
    }

    public transaksiInterface(int id, String kode_transaksi, int id_pelanggan, int id_kendaraan, int id_kasir, LocalDate tgl_sewa, LocalDate tgl_kembali_rencana, BigDecimal harga_sewa_perHari, int jumlah_hari, BigDecimal total_sewa, LocalDate tgl_ambil, String kondisi_ambil, String status, LocalDateTime createAt) {
        this.id = id;
        this.kode_transaksi = kode_transaksi;
        this.id_pelanggan = id_pelanggan;
        this.id_kendaraan = id_kendaraan;
        this.id_kasir = id_kasir;
        this.tgl_sewa = tgl_sewa;
        this.tgl_kembali_rencana = tgl_kembali_rencana;
        this.harga_sewa_perHari = harga_sewa_perHari;
        this.jumlah_hari = jumlah_hari;
        this.total_sewa = total_sewa;
        this.tgl_ambil = tgl_ambil;
        this.kondisi_ambil = kondisi_ambil;
        this.chekEnumTrasaksi(status);
        this.status = status;
        this.createAt = createAt;
    }


}
