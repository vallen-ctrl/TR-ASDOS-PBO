/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import config.Database;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Test sederhana untuk mengecek koneksi database.
 *
 * @author vallen
 */
public class DatabaseTest {

    public static void main(String[] args) {
        Database db = new Database();

        System.out.println("===== TEST KONEKSI DATABASE =====");

        try {
            Statement stm = db.start();
            Connection conn = stm.getConnection();

            System.out.println("[OK] Koneksi berhasil dibuat.");
            System.out.println("Valid (timeout 3s) : " + conn.isValid(3));
            System.out.println("Database           : " + conn.getCatalog());
            System.out.println("Versi server       : " + conn.getMetaData().getDatabaseProductVersion());

            try (ResultSet rs = stm.executeQuery("SELECT 1")) {
                if (rs.next() && rs.getInt(1) == 1) {
                    System.out.println("[OK] Query 'SELECT 1' berhasil.");
                }
            }

        } catch (IOException e) {
            System.out.println("[GAGAL] Konfigurasi: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("[GAGAL] Driver MySQL tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[GAGAL] Koneksi/query: " + e.getMessage());
            System.out.println("SQLState           : " + e.getSQLState());
            System.out.println("Error code         : " + e.getErrorCode());
        } finally {
            try {
                db.close();
                System.out.println("Koneksi ditutup.");
            } catch (Exception e) {
                // close() bisa gagal bila start() tidak pernah berhasil
                System.out.println("Koneksi tidak perlu ditutup (belum terbuka).");
            }
        }
    }
}