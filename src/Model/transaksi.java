/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
import java.util.ArrayList;
import java.util.List;
import Model.interfaces.transaksiInterface;
import java.sql.SQLException;
import java.sql.ResultSet;
/**
 *
 * @author vallen
 */
public class transaksi extends model{
    public List<transaksiInterface> getAllTransaction(int limit, int offset) throws SQLException{
        List<transaksiInterface> tr = new ArrayList<>();
        ResultSet res = this.select()
                .setLimit(limit)
                .setOffset(offset)
                .run();
        
        while(res.next()){
            tr.add(new transaksiInterface(
                    res.getInt("id"),
                    res.getString("kode_transaksi"),
                    res.getInt("id_pelanggan"),
                    res.getInt("id_kendaraan"),
                    res.getInt("id_kasir"),
                    res.getDate("tgl_sewa").toLocalDate(),
                    res.getDate("tgl_kembali_rencana").toLocalDate(),
                    res.getBigDecimal("harga_sewa_per_hari"),
                    res.getInt("jumlah_hari"),
                    res.getBigDecimal("total_sewa"),
                    res.getDate("tgl_ambil").toLocalDate(),
                    res.getString("kondisi_ambil"),
                    res.getString("status"),
                    res.getTimestamp("create_at").toLocalDateTime()
            ));
        }
        return tr;
    }
}
