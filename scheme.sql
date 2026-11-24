CREATE TABLE IF NOT EXISTS `admin` (
	`id_user` INTEGER AUTO_INCREMENT,
	`nama_lengkap` VARCHAR(100) NOT NULL,
	`username` VARCHAR(50) NOT NULL UNIQUE,
	`password_hash` VARCHAR(255) NOT NULL,
	`status_aktif` BOOLEAN NOT NULL,
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(`id_user`)
);


CREATE TABLE IF NOT EXISTS `pelanggan` (
	`id_pelanggan` INTEGER AUTO_INCREMENT,
	`nama` VARCHAR(100) NOT NULL,
	`no_ktp` VARCHAR(20) NOT NULL UNIQUE,
	`no_sim` VARCHAR(20),
	`no_hp` VARCHAR(20) NOT NULL,
	`alamat` TEXT,
	`status` ENUM('aktif', 'sedang_menyewa', 'blacklist') NOT NULL DEFAULT 'aktif',
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(`id_pelanggan`)
);


CREATE TABLE IF NOT EXISTS `kendaraan` (
	`id_kendaraan` INTEGER AUTO_INCREMENT,
	`plat_nomor` VARCHAR(15) NOT NULL UNIQUE,
	`jenis` ENUM('motor', 'mobil') NOT NULL,
	`merek` VARCHAR(50) NOT NULL,
	`model` VARCHAR(50) NOT NULL,
	`tahun` YEAR,
	`warna` VARCHAR(30),
	`harga_sewa` DECIMAL(12,2) NOT NULL,
	`persen_denda_telat` DECIMAL(5,2) NOT NULL DEFAULT 10.00,
	`status` ENUM('tersedia', 'disewa', 'perawatan') NOT NULL DEFAULT 'tersedia',
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(`id_kendaraan`)
);


CREATE TABLE IF NOT EXISTS `transaksi` (
	`id_transaksi` INTEGER AUTO_INCREMENT,
	`kode_transaksi` VARCHAR(30) NOT NULL UNIQUE,
	`id_pelanggan` INTEGER NOT NULL,
	`id_kendaraan` INTEGER NOT NULL,
	`id_kasir` INTEGER NOT NULL,
	`tgl_sewa` DATE NOT NULL,
	`tgl_kembali_rencana` DATE NOT NULL,
	`harga_sewa_per_hari` DECIMAL(12,2) NOT NULL,
	`jumlah_hari` INTEGER NOT NULL,
	`total_sewa` DECIMAL(12,2) NOT NULL,
	`tgl_ambil` DATETIME NOT NULL,
	`kondisi_ambil` TEXT NOT NULL,
	`status` ENUM('dipesan', 'disewa', 'selesai', 'batal') NOT NULL DEFAULT 'dipesan',
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(`id_transaksi`)
);


CREATE TABLE IF NOT EXISTS `pengembalian` (
	`id_pengembalian` INTEGER AUTO_INCREMENT,
	`id_transaksi` INTEGER NOT NULL UNIQUE,
	`id_kasir` INTEGER NOT NULL,
	`tgl_kembali_aktual` DATETIME NOT NULL,
	`kondisi_kembali` TEXT NOT NULL,
	`hari_telat` INTEGER NOT NULL DEFAULT 0,
	`biaya_telat` DECIMAL(12,2) NOT NULL DEFAULT 0,
	`total_biaya_rusak` DECIMAL(12,2) NOT NULL DEFAULT 0,
	`total_bayar` DECIMAL(12,2) NOT NULL,
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(`id_pengembalian`)
);


CREATE TABLE IF NOT EXISTS `kerusakan` (
	`id_kerusakan` INTEGER AUTO_INCREMENT,
	`id_pengembalian` INTEGER NOT NULL,
	`deskripsi` VARCHAR(255) NOT NULL,
	`biaya` DECIMAL(12,2) NOT NULL DEFAULT 0,
	`dicatat_oleh` INTEGER NOT NULL,
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(`id_kerusakan`)
);


ALTER TABLE `transaksi`
ADD FOREIGN KEY(`id_pelanggan`) REFERENCES `pelanggan`(`id_pelanggan`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `transaksi`
ADD FOREIGN KEY(`id_kendaraan`) REFERENCES `kendaraan`(`id_kendaraan`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `transaksi`
ADD FOREIGN KEY(`id_kasir`) REFERENCES `admin`(`id_user`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `pengembalian`
ADD FOREIGN KEY(`id_transaksi`) REFERENCES `transaksi`(`id_transaksi`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `pengembalian`
ADD FOREIGN KEY(`id_kasir`) REFERENCES `admin`(`id_user`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `kerusakan`
ADD FOREIGN KEY(`id_pengembalian`) REFERENCES `pengembalian`(`id_pengembalian`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `kerusakan`
ADD FOREIGN KEY(`dicatat_oleh`) REFERENCES `admin`(`id_user`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
