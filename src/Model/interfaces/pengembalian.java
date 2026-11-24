/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author vallen
 */
public class pengembalian {
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
}
