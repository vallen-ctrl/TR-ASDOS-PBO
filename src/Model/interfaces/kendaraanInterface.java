/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.interfaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 *
 * @author vallen
 */
public class kendaraanInterface {
    public Integer id_kendaraan;
    public String plat_nomor;
    public String jenis;
    public String merek;
    public String model;
    public Integer Year;
    public String warna;
    public BigDecimal Harga_sewa;
    public BigDecimal Harga_denda_telat;
    public String status;
    public LocalDateTime createAt;

    public kendaraanInterface(int id_kendaraan, String plat_nomor, String jenis, String merek, String model, Integer Year, String warna, BigDecimal Harga_sewa, BigDecimal Harga_denda_telat, String status, LocalDateTime createAt) {
        this.id_kendaraan = id_kendaraan;
        this.plat_nomor = plat_nomor;
        this.jenis = jenis;
        this.merek = merek;
        this.model = model;
        this.Year = Year;
        this.warna = warna;
        this.Harga_sewa = Harga_sewa;
        this.Harga_denda_telat = Harga_denda_telat;
        this.status = status;
        this.createAt = createAt;
    }
    
    
}
