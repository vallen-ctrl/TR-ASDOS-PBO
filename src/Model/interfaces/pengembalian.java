/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import utils.Validation;
/**
 *
 * @author vallen
 */
public class pengembalian {
    private Validation valid = new Validation();
    public Integer id_pengembalian;
    public Integer id_transaksi;
    public Integer id_kasir;
    public LocalDate tgl_kembali_aktual;
    public String kondisi_kembali;
    public Integer hari_telat;
    public BigDecimal biaya_telat;
    public BigDecimal total_biaya_rusak;
    public BigDecimal total_dibayar;
    public LocalDateTime createAt;

    public pengembalian(Integer id_pengembalian, Integer id_transaksi, Integer id_kasir, LocalDate tgl_kembali_aktual, String kondisi_kembali, Integer hari_telat, BigDecimal biaya_telat, BigDecimal total_biaya_rusak, BigDecimal total_dibayar, LocalDateTime createAt) {
        this.id_pengembalian = id_pengembalian;
        this.id_transaksi = id_transaksi;
        this.id_kasir = id_kasir;
        this.tgl_kembali_aktual = tgl_kembali_aktual;
        this.kondisi_kembali = kondisi_kembali;
        this.hari_telat = hari_telat;
        this.biaya_telat = biaya_telat;
        this.total_biaya_rusak = total_biaya_rusak;
        this.total_dibayar = total_dibayar;
        this.createAt = createAt;
        
    }
    
    
}
