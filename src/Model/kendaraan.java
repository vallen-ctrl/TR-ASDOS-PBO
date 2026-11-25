package Model;

import Model.interfaces.kendaraanInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class kendaraan extends model {

    private static final String[] COLS = {
        "plat_nomor", "jenis", "merek", "model", "year",
        "warna", "harga_sewa", "harga_denda_telat", "status"
    };

    private static String str(Object o) {
        return o == null ? null : o.toString();
    }

    private String[] toValues(kendaraanInterface k) {
        return new String[]{
            k.plat_nomor, k.jenis, k.merek, k.model, str(k.Year),
            k.warna, str(k.Harga_sewa), str(k.Harga_denda_telat), k.status
        };
    }

    private kendaraanInterface mapRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
        return new kendaraanInterface(
            rs.getInt("id_kendaraan"),
            rs.getString("plat_nomor"),
            rs.getString("jenis"),
            rs.getString("merek"),
            rs.getString("model"),
            rs.getInt("year"),
            rs.getString("warna"),
            rs.getBigDecimal("harga_sewa"),
            rs.getBigDecimal("harga_denda_telat"),
            rs.getString("status"),
            ts != null ? ts.toLocalDateTime() : null
        );
    }

    public boolean create(kendaraanInterface k) throws SQLException {
        return this.insert(COLS, this.toValues(k)).execute() > 0;
    }

    public List<kendaraanInterface> findAll() throws SQLException {
        List<kendaraanInterface> list = new ArrayList<>();
        ResultSet rs = this.select().run();
        if (rs == null) return list;
        try (rs) {
            while (rs.next()) {
                list.add(this.mapRow(rs));
            }
        }
        return list;
    }

    public kendaraanInterface findById(int id) throws SQLException {
        ResultSet rs = this.select()
                .where("id_kendaraan = " + id)
                .run();
        if (rs == null) return null;
        try (rs) {
            return rs.next() ? this.mapRow(rs) : null;
        }
    }

    public kendaraanInterface findByPlat(String plat) throws SQLException {
        ResultSet rs = this.select()
                .where("plat_nomor = " + esc(plat))
                .run();
        if (rs == null) return null;
        try (rs) {
            return rs.next() ? this.mapRow(rs) : null;
        }
    }

    public boolean updateData(int id, kendaraanInterface k) throws SQLException {
        return this.update(COLS, this.toValues(k))
                .where("id_kendaraan = " + id)
                .execute() > 0;
    }

    public boolean updateStatus(int id, String status) throws SQLException {
        return this.updateOnce("status", status)
                .where("id_kendaraan = " + id)
                .execute() > 0;
    }

    public boolean deleteById(int id) throws SQLException {
        return this.delete()
                .where("id_kendaraan = " + id)
                .execute() > 0;
    }
}