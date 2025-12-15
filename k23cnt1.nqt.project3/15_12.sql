-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: k23cnt1_nqt_quanlykhachsan
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `nqtblog`
--

DROP TABLE IF EXISTS `nqtblog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nqtblog` (
  `nqtId` int NOT NULL AUTO_INCREMENT,
  `nqtTieuDe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtNoiDung` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `nqtHinhAnh` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtNgayTao` datetime DEFAULT CURRENT_TIMESTAMP,
  `nqtStatus` bit(1) DEFAULT b'1',
  `nqtMetaTitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtMetaKeyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtMetaDescription` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`nqtId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nqtblog`
--

LOCK TABLES `nqtblog` WRITE;
/*!40000 ALTER TABLE `nqtblog` DISABLE KEYS */;
INSERT INTO `nqtblog` VALUES (3,'Tiêu đề mặc định','1','','2025-12-13 09:23:12',_binary '','1','1','1');
/*!40000 ALTER TABLE `nqtblog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nqtcronlog`
--

DROP TABLE IF EXISTS `nqtcronlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nqtcronlog` (
  `nqtId` int NOT NULL AUTO_INCREMENT,
  `nqtTaskName` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Tên task (ví dụ: autoUpdateRoomStatus)',
  `nqtStatus` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SUCCESS, ERROR, WARNING',
  `nqtMessage` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'Thông báo chi tiết',
  `nqtRecordsProcessed` int DEFAULT NULL COMMENT 'Số bản ghi đã xử lý',
  `nqtExecutionTime` bigint DEFAULT NULL COMMENT 'Thời gian thực thi (milliseconds)',
  `nqtErrorDetails` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'Chi tiết lỗi nếu có',
  `nqtCreatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo log',
  PRIMARY KEY (`nqtId`),
  KEY `idx_task_name` (`nqtTaskName`),
  KEY `idx_status` (`nqtStatus`),
  KEY `idx_created_at` (`nqtCreatedAt`)
) ENGINE=InnoDB AUTO_INCREMENT=888 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng lưu log của các cron task';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nqtcronlog`
--

LOCK TABLES `nqtcronlog` WRITE;
/*!40000 ALTER TABLE `nqtcronlog` DISABLE KEYS */;
INSERT INTO `nqtcronlog` VALUES (872,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,12,NULL,'2025-12-13 09:32:00'),(873,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,20,NULL,'2025-12-13 09:33:00'),(874,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,15,NULL,'2025-12-13 09:34:00'),(875,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,16,NULL,'2025-12-13 09:35:00'),(876,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,15,NULL,'2025-12-13 09:36:00'),(877,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,21,NULL,'2025-12-13 09:37:00'),(878,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,15,NULL,'2025-12-13 09:38:00'),(879,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,19,NULL,'2025-12-13 09:39:00'),(880,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,10,NULL,'2025-12-13 09:40:00'),(881,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,16,NULL,'2025-12-13 09:41:00'),(882,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,22,NULL,'2025-12-13 09:46:00'),(883,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,25,NULL,'2025-12-13 09:47:00'),(884,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,19,NULL,'2025-12-13 09:48:00'),(885,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,27,NULL,'2025-12-13 09:49:00'),(886,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,0,NULL,'2025-12-15 07:57:00'),(887,'autoUpdateRoomStatus','SUCCESS','Đã cập nhật 0 phòng',0,0,NULL,'2025-12-15 07:58:00');
/*!40000 ALTER TABLE `nqtcronlog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nqtdanhgia`
--

DROP TABLE IF EXISTS `nqtdanhgia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nqtdanhgia` (
  `nqtId` int NOT NULL AUTO_INCREMENT,
  `nqtDatPhongId` int DEFAULT NULL,
  `nqtNoiDungDanhGia` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtStatus` bit(1) DEFAULT b'1',
  `nqtNgayDanhGia` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`nqtId`),
  KEY `fk_danhgia_datphong` (`nqtDatPhongId`),
  CONSTRAINT `fk_danhgia_datphong` FOREIGN KEY (`nqtDatPhongId`) REFERENCES `nqtdatphong` (`nqtId`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nqtdanhgia`
--

LOCK TABLES `nqtdanhgia` WRITE;
/*!40000 ALTER TABLE `nqtdanhgia` DISABLE KEYS */;
INSERT INTO `nqtdanhgia` VALUES (1,1,'1',_binary '','2025-12-06 08:51:19'),(2,4,'Trải nghiệm rất tuyệt vời!',_binary '','2025-12-09 10:27:05');
/*!40000 ALTER TABLE `nqtdanhgia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nqtdatphong`
--

DROP TABLE IF EXISTS `nqtdatphong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nqtdatphong` (
  `nqtId` int NOT NULL AUTO_INCREMENT,
  `nqtNguoiDungId` int DEFAULT NULL,
  `nqtPhongId` int DEFAULT NULL,
  `nqtNgayDen` date DEFAULT NULL,
  `nqtNgayDi` date DEFAULT NULL,
  `nqtTongTien` float DEFAULT NULL,
  `nqtGiamGia` float DEFAULT NULL COMMENT 'Số tiền đã giảm',
  `nqtGiamGiaId` int DEFAULT NULL COMMENT 'FK: Mã giảm giá đã sử dụng',
  `nqtGhiChu` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtStatus` tinyint DEFAULT '0',
  `nqtNoiDungChuyenKhoan` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtNgayTao` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`nqtId`),
  KEY `fk_datphong_nguoidung` (`nqtNguoiDungId`),
  KEY `fk_datphong_phong` (`nqtPhongId`),
  KEY `idx_giam_gia` (`nqtGiamGiaId`),
  CONSTRAINT `fk_datphong_nguoidung` FOREIGN KEY (`nqtNguoiDungId`) REFERENCES `nqtnguoidung` (`nqtId`),
  CONSTRAINT `fk_datphong_phong` FOREIGN KEY (`nqtPhongId`) REFERENCES `nqtphong` (`nqtId`),
  CONSTRAINT `nqtdatphong_ibfk_1` FOREIGN KEY (`nqtGiamGiaId`) REFERENCES `nqtgiamgia` (`nqtId`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nqtdatphong`
--

LOCK TABLES `nqtdatphong` WRITE;
/*!40000 ALTER TABLE `nqtdatphong` DISABLE KEYS */;
INSERT INTO `nqtdatphong` VALUES (1,1,1,'2025-12-04','2025-12-19',100000,NULL,NULL,'note',1,NULL,NULL),(2,2,3,'2025-12-06','2025-12-06',11,NULL,NULL,'1',2,'',NULL),(3,1,2,'2025-12-01','2025-12-02',1310000,NULL,NULL,'cc',1,NULL,NULL),(4,1,2,'2025-12-09','2025-12-10',99000,11000,2,'',1,'CUX4VLM19C',NULL),(5,1,3,'2025-12-10','2025-12-11',9000.9,1000.1,2,'ghi chú nè',1,'PFUW53M37G',NULL),(6,1,3,'2025-12-09','2025-12-10',9001.8,1000.2,2,'ghi chú',2,'AAR8QQR2UX','2025-12-09 11:16:16.194397'),(7,1,2,'2025-12-11','2025-12-13',189001,21000.1,2,'11',2,'TZEITBR6UZ','2025-12-11 08:38:38.060162'),(8,1,2,'2025-12-11','2025-12-19',729001,81000.1,2,'111',2,'ZNJPDP7U02','2025-12-11 08:45:41.728726');
/*!40000 ALTER TABLE `nqtdatphong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nqtdichvu`
--

DROP TABLE IF EXISTS `nqtdichvu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nqtdichvu` (
  `nqtId` int NOT NULL AUTO_INCREMENT,
  `nqtTen` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtDonGia` float DEFAULT NULL,
  `nqtStatus` bit(1) DEFAULT b'1',
  `nqtMetaTitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtMetaKeyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtMetaDescription` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtHinhAnh` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`nqtId`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nqtdichvu`
--

LOCK TABLES `nqtdichvu` WRITE;
/*!40000 ALTER TABLE `nqtdichvu` DISABLE KEYS */;
INSERT INTO `nqtdichvu` VALUES (1,'Nước Lavie',10000,_binary '','1','1','1','/uploads/36511772-ebb7-4daa-a2c7-c584840fa54a_tải xuống.jfif'),(2,'1',1,_binary '','1','1','1',NULL);
/*!40000 ALTER TABLE `nqtdichvu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nqtdongiadichvu`
--

DROP TABLE IF EXISTS `nqtdongiadichvu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nqtdongiadichvu` (
  `nqtId` int NOT NULL AUTO_INCREMENT,
  `nqtSoLuong` int DEFAULT '1',
  `nqtThanhTien` float DEFAULT NULL,
  `nqtDatPhongId` int DEFAULT NULL,
  `nqtDichVuId` int DEFAULT NULL,
  PRIMARY KEY (`nqtId`),
  KEY `fk_chitiet_datphong` (`nqtDatPhongId`),
  KEY `fk_chitiet_dichvu` (`nqtDichVuId`),
  CONSTRAINT `fk_chitiet_datphong` FOREIGN KEY (`nqtDatPhongId`) REFERENCES `nqtdatphong` (`nqtId`),
  CONSTRAINT `fk_chitiet_dichvu` FOREIGN KEY (`nqtDichVuId`) REFERENCES `nqtdichvu` (`nqtId`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nqtdongiadichvu`
--

LOCK TABLES `nqtdongiadichvu` WRITE;
/*!40000 ALTER TABLE `nqtdongiadichvu` DISABLE KEYS */;
INSERT INTO `nqtdongiadichvu` VALUES (1,1,10000,1,1),(2,111,1110000,1,1),(5,22,22,2,2),(6,1,10000,3,1),(7,1,1,3,2),(8,1,10000,4,1),(9,1,10000,5,1),(10,1,10000,6,1),(11,1,1,6,2),(12,1,10000,7,1),(13,1,1,7,2),(14,1,10000,8,1),(15,1,1,8,2);
/*!40000 ALTER TABLE `nqtdongiadichvu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nqtemailtoken`
--

DROP TABLE IF EXISTS `nqtemailtoken`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nqtemailtoken` (
  `nqtId` int NOT NULL AUTO_INCREMENT,
  `nqtUserId` int NOT NULL COMMENT 'User ID from nqtNguoiDung',
  `nqtToken` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Unique token string',
  `nqtType` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Token type: EMAIL_VERIFICATION or PASSWORD_RESET',
  `nqtExpiresAt` datetime NOT NULL COMMENT 'Token expiration date',
  `nqtUsed` tinyint(1) DEFAULT '0' COMMENT 'Whether token has been used',
  `nqtCreatedAt` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Token creation date',
  PRIMARY KEY (`nqtId`),
  UNIQUE KEY `nqtToken` (`nqtToken`),
  KEY `idx_user_type` (`nqtUserId`,`nqtType`),
  KEY `idx_token` (`nqtToken`),
  KEY `idx_expires` (`nqtExpiresAt`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Email verification and password reset tokens';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nqtemailtoken`
--

LOCK TABLES `nqtemailtoken` WRITE;
/*!40000 ALTER TABLE `nqtemailtoken` DISABLE KEYS */;
INSERT INTO `nqtemailtoken` VALUES (1,1,'83af05ae-6e1c-4b2c-88ca-52385c14577a','PASSWORD_RESET','2025-12-08 21:04:33',1,'2025-12-08 20:04:33'),(2,6,'8aa63fc3-424c-4e0a-8fdb-109b7eebaebc','EMAIL_VERIFICATION','2025-12-10 08:40:53',1,'2025-12-09 08:40:53'),(4,7,'9e10bca0-c9a1-4f9e-8f82-6f07f1aa9548','EMAIL_VERIFICATION','2025-12-10 08:46:34',1,'2025-12-09 08:46:34'),(7,1,'ddaa637d-bdbb-496a-ae62-84feb6854426','PASSWORD_RESET','2025-12-10 09:34:55',1,'2025-12-10 08:34:55'),(8,1,'006a3e6c-7530-4066-867e-b9211db97481','PASSWORD_RESET','2025-12-13 09:08:35',1,'2025-12-13 08:08:35');
/*!40000 ALTER TABLE `nqtemailtoken` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nqtgiamgia`
--

DROP TABLE IF EXISTS `nqtgiamgia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nqtgiamgia` (
  `nqtId` int NOT NULL AUTO_INCREMENT,
  `nqtMaGiamGia` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Mã giảm giá (ví dụ: VIP2024, SUMMER10)',
  `nqtMoTa` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtLoaiGiam` tinyint NOT NULL DEFAULT '0' COMMENT '0-Phần trăm, 1-Số tiền cố định',
  `nqtGiaTriGiam` float NOT NULL COMMENT 'Giá trị giảm (phần trăm hoặc số tiền)',
  `nqtGiaTriToiThieu` float DEFAULT NULL COMMENT 'Giá trị đơn hàng tối thiểu để áp dụng',
  `nqtGiaTriGiamToiDa` float DEFAULT NULL COMMENT 'Giá trị giảm tối đa (chỉ áp dụng khi loại là phần trăm)',
  `nqtNgayBatDau` date DEFAULT NULL COMMENT 'Ngày bắt đầu áp dụng',
  `nqtNgayKetThuc` date DEFAULT NULL COMMENT 'Ngày kết thúc',
  `nqtSoLuongToiDa` int DEFAULT NULL COMMENT 'Số lần sử dụng tối đa (NULL = không giới hạn)',
  `nqtSoLuongDaDung` int NOT NULL DEFAULT '0' COMMENT 'Số lần đã sử dụng',
  `nqtStatus` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1-Hoạt động, 0-Không hoạt động',
  `nqtNgayTao` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Ngày tạo mã',
  `nqtNguoiDungId` int DEFAULT NULL COMMENT 'FK: NULL = mã chung cho tất cả VIP, có giá trị = mã riêng cho khách hàng',
  `nqtChiChoVip` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`nqtId`),
  UNIQUE KEY `nqtMaGiamGia` (`nqtMaGiamGia`),
  KEY `idx_ma_giam_gia` (`nqtMaGiamGia`),
  KEY `idx_status` (`nqtStatus`),
  KEY `idx_nguoi_dung` (`nqtNguoiDungId`),
  KEY `idx_ngay_ap_dung` (`nqtNgayBatDau`,`nqtNgayKetThuc`),
  CONSTRAINT `nqtgiamgia_ibfk_1` FOREIGN KEY (`nqtNguoiDungId`) REFERENCES `nqtnguoidung` (`nqtId`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng quản lý mã giảm giá';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nqtgiamgia`
--

LOCK TABLES `nqtgiamgia` WRITE;
/*!40000 ALTER TABLE `nqtgiamgia` DISABLE KEYS */;
INSERT INTO `nqtgiamgia` VALUES (2,'VIP10','mô tả ',0,10,NULL,NULL,NULL,NULL,NULL,5,1,'2025-12-09 09:06:40',NULL,0);
/*!40000 ALTER TABLE `nqtgiamgia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nqtloaiphong`
--

DROP TABLE IF EXISTS `nqtloaiphong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nqtloaiphong` (
  `nqtId` int NOT NULL AUTO_INCREMENT,
  `nqtTenLoaiPhong` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtGia` float DEFAULT NULL,
  `nqtSoNguoi` int DEFAULT NULL,
  `nqtHinhAnh` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtStatus` bit(1) DEFAULT b'1',
  `nqtMetaTitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtMetaKeyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtMetaDescription` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`nqtId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nqtloaiphong`
--

LOCK TABLES `nqtloaiphong` WRITE;
/*!40000 ALTER TABLE `nqtloaiphong` DISABLE KEYS */;
INSERT INTO `nqtloaiphong` VALUES (1,'Phòng thường',100000,2,'/uploads/d78a2f6b-bafa-44cb-981e-d16b9114294e_images.jfif',_binary '','1','1','11'),(2,'Phòng VIP',200000,4,'/uploads/41c242df-22fe-45c9-88e8-df440e57bed8_avatarJM6.png',_binary '','2','2','2'),(3,'1',1,1,'/uploads/07f92839-4d21-4ff2-8988-ea94996a2a1e_mmt_1.png',_binary '\0','1','1','1'),(4,'2',2,2,'',_binary '','2','2','2');
/*!40000 ALTER TABLE `nqtloaiphong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nqtloginattempt`
--

DROP TABLE IF EXISTS `nqtloginattempt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nqtloginattempt` (
  `nqtId` int NOT NULL AUTO_INCREMENT,
  `nqtIdentifier` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Username, email, or user ID',
  `nqtIpAddress` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IPv4 or IPv6 address',
  `nqtAttemptTime` datetime NOT NULL COMMENT 'Time of login attempt',
  `nqtSuccess` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'true if login successful, false if failed',
  `nqtFailureReason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Reason for failure (wrong password, account locked, etc.)',
  `nqtUserAgent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Browser/device info',
  `nqtActionType` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Action type: login, register, forgot_password, reset_password, email_verification, 2fa_verification',
  PRIMARY KEY (`nqtId`),
  KEY `idx_identifier` (`nqtIdentifier`),
  KEY `idx_ip_address` (`nqtIpAddress`),
  KEY `idx_attempt_time` (`nqtAttemptTime`),
  KEY `idx_action_type` (`nqtActionType`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Login attempts tracking for rate limiting and brute force protection';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nqtloginattempt`
--

LOCK TABLES `nqtloginattempt` WRITE;
/*!40000 ALTER TABLE `nqtloginattempt` DISABLE KEYS */;
INSERT INTO `nqtloginattempt` VALUES (45,'quangtam','0:0:0:0:0:0:0:1','2025-12-11 09:08:18',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','login'),(46,'quangtam','0:0:0:0:0:0:0:1','2025-12-13 07:38:46',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','login'),(47,'quangtam','0:0:0:0:0:0:0:1','2025-12-13 07:40:03',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','login'),(48,'quangtam','0:0:0:0:0:0:0:1','2025-12-13 07:42:54',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','login'),(49,'quangtam','0:0:0:0:0:0:0:1','2025-12-13 07:44:20',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','login'),(50,'quangtam','0:0:0:0:0:0:0:1','2025-12-13 07:44:41',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','login'),(51,'quangtam','0:0:0:0:0:0:0:1','2025-12-13 07:46:26',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','login'),(52,'quangtam','0:0:0:0:0:0:0:1','2025-12-13 07:50:30',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','login'),(53,'quangtam','127.0.0.1','2025-12-13 08:08:24',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','login'),(54,'nguyenquangtam179@gmail.com','0:0:0:0:0:0:0:1','2025-12-13 08:08:39',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','forgot_password'),(55,'nguyenquangtam179@gmail.com','0:0:0:0:0:0:0:1','2025-12-13 08:09:51',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','reset_password'),(56,'quangtam','0:0:0:0:0:0:0:1','2025-12-13 08:09:53',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','login'),(57,'quangtam','0:0:0:0:0:0:0:1','2025-12-13 08:33:38',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','login'),(58,'quangtam','0:0:0:0:0:0:0:1','2025-12-13 08:37:12',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','login'),(59,'quangtam','0:0:0:0:0:0:0:1','2025-12-15 07:57:04',1,NULL,'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36','login');
/*!40000 ALTER TABLE `nqtloginattempt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nqtnganhang`
--

DROP TABLE IF EXISTS `nqtnganhang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nqtnganhang` (
  `nqtId` int NOT NULL AUTO_INCREMENT,
  `nqtTenNganHang` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Tên ngân hàng (ví dụ: Vietcombank, Techcombank)',
  `nqtMaNganHang` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Mã ngân hàng theo VietQR (ví dụ: VCB, TCB)',
  `nqtSoTaiKhoan` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Số tài khoản',
  `nqtTenChuTaiKhoan` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Tên chủ tài khoản',
  `nqtChiNhanh` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Chi nhánh (tùy chọn)',
  `nqtGhiChu` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'Ghi chú',
  `nqtStatus` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1-Hoạt động, 0-Không hoạt động',
  `nqtThuTu` int NOT NULL DEFAULT '0' COMMENT 'Thứ tự hiển thị',
  `nqtNgayTao` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
  PRIMARY KEY (`nqtId`),
  UNIQUE KEY `nqtMaNganHang` (`nqtMaNganHang`),
  KEY `idx_ma_ngan_hang` (`nqtMaNganHang`),
  KEY `idx_status` (`nqtStatus`),
  KEY `idx_thu_tu` (`nqtThuTu`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng quản lý thông tin ngân hàng';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nqtnganhang`
--

LOCK TABLES `nqtnganhang` WRITE;
/*!40000 ALTER TABLE `nqtnganhang` DISABLE KEYS */;
INSERT INTO `nqtnganhang` VALUES (3,'Ngân hàng Á Châu','ACB','33963127','NGUYEN QUANG TAM','','Đây là ghi chú',1,0,'2025-12-08 18:00:55');
/*!40000 ALTER TABLE `nqtnganhang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nqtnguoidung`
--

DROP TABLE IF EXISTS `nqtnguoidung`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nqtnguoidung` (
  `nqtId` int NOT NULL AUTO_INCREMENT,
  `nqtHoVaTen` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtTaiKhoan` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nqtMatKhau` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nqtSoDienThoai` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtEmail` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtDiaChi` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtVaiTro` tinyint DEFAULT '0',
  `nqtStatus` bit(1) DEFAULT b'1',
  `nqtCapBac` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'KhachThuong',
  `nqt2faEnabled` tinyint(1) DEFAULT '0' COMMENT '2FA enabled flag',
  `nqt2faSecret` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'TOTP secret key for 2FA',
  `nqtEmailVerified` tinyint(1) DEFAULT '0' COMMENT 'Email verification status',
  `nqtPasswordResetToken` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Password reset token',
  `nqtPasswordResetTokenExpiresAt` datetime DEFAULT NULL COMMENT 'Password reset token expiration time',
  PRIMARY KEY (`nqtId`),
  UNIQUE KEY `nqtTaiKhoan` (`nqtTaiKhoan`),
  KEY `idx_password_reset_token` (`nqtPasswordResetToken`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nqtnguoidung`
--

LOCK TABLES `nqtnguoidung` WRITE;
/*!40000 ALTER TABLE `nqtnguoidung` DISABLE KEYS */;
INSERT INTO `nqtnguoidung` VALUES (1,'Nguyễn Quang Tâm','quangtam','$2a$10$dhwcVg6Goe12v5.zuyifp.cie.tf5rNb.dJ8Xf68cgKWPyaMp7UEW','0961138440','nguyenquangtam179@gmail.com','Hà Nội',99,_binary '','KhachVip',0,NULL,1,NULL,NULL),(2,'nqtamtest','nqtamtest','$2a$10$hQS28NTneI0L.U4RchzcreOLaFAAyXKqFd1sp6.Mc.5iKk.mwmx4.','0961138440','nqtamtest@gmail.com','HN',0,_binary '','KhachThuong',0,NULL,1,NULL,NULL),(3,'1','1','$2a$10$yPTvCf.Ui3C/uAtJKZ7JyOe32t4hybce4aakIWT0aGkc9nt.FDmz2','1','quangtam1@gmail.com','1',0,_binary '','KhachThuong',0,NULL,1,NULL,NULL),(4,'2','2','$2a$10$6Hun9A/qRLrm2JSeugZmzuqH0ZOM6iQNJcn8TRTmC5Vfh7Kg5XYpa','2','2@gmail.com','2',1,_binary '','KhachVip',0,NULL,1,NULL,NULL),(5,'Tâm Nguyễn Quang1','nguyenquangtam6666@gmail.com','','1','nguyenquangtam6666@gmail.com','1',0,_binary '','KhachThuong',NULL,NULL,1,NULL,NULL),(7,'NQTTEST1','NQTTEST1','$2a$10$o3qegVRv0Fm2fuigN6vOyujUkrSLJzhIz21eg0U1Mfw7mfN5RwaGm','','nguyenquangtam.info.vn@gmail.com','',0,_binary '','KhachThuong',NULL,NULL,1,NULL,NULL);
/*!40000 ALTER TABLE `nqtnguoidung` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nqtphong`
--

DROP TABLE IF EXISTS `nqtphong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nqtphong` (
  `nqtId` int NOT NULL AUTO_INCREMENT,
  `nqtSoPhong` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nqtTenPhong` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nqtLoaiPhongId` int DEFAULT NULL,
  `nqtStatus` bit(1) DEFAULT b'1',
  PRIMARY KEY (`nqtId`),
  KEY `fk_phong_loaiphong` (`nqtLoaiPhongId`),
  CONSTRAINT `fk_phong_loaiphong` FOREIGN KEY (`nqtLoaiPhongId`) REFERENCES `nqtloaiphong` (`nqtId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nqtphong`
--

LOCK TABLES `nqtphong` WRITE;
/*!40000 ALTER TABLE `nqtphong` DISABLE KEYS */;
INSERT INTO `nqtphong` VALUES (1,'10','Phòng 01',1,_binary '\0'),(2,'2','Phòng 02',1,_binary ''),(3,'1','1',3,_binary ''),(4,'test','test',1,_binary '');
/*!40000 ALTER TABLE `nqtphong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nqtsetting`
--

DROP TABLE IF EXISTS `nqtsetting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nqtsetting` (
  `nqtId` int NOT NULL AUTO_INCREMENT,
  `nqtName` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nqtValue` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`nqtId`),
  UNIQUE KEY `nqtName` (`nqtName`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nqtsetting`
--

LOCK TABLES `nqtsetting` WRITE;
/*!40000 ALTER TABLE `nqtsetting` DISABLE KEYS */;
INSERT INTO `nqtsetting` VALUES (1,'nqtTieuDe','Quản lý khách sạn'),(2,'nqtWebsiteColor','#a52c2c'),(3,'nqtWebsiteName','Quản lý khách sạn'),(4,'nqtWebsitePhone','0961138440'),(5,'nqtWebsiteEmail','nguyenquangtam6666@gmail.com'),(6,'nqtWebsiteLogo','/uploads/60257fd0-6dfd-44b6-9d60-699b4bcfcd38_icon_1756088250.ico'),(8,'nqtWebsiteFont','Roboto'),(9,'nqtWebsiteSupportLinks','Chính sách hủy|/nqtSupport/nqt-chinh-sach-huy|<h2>Chính sách hủy</h2><p>Khách hàng có thể hủy đặt phòng trước 24 giờ...</p>\r\nĐiều khoản sử dụng|/nqtSupport/nqt-dieu-khoan|<h2>Điều khoản</h2><p>Nội dung điều khoản...</p>\r\nThông tin sinh viên|/nqtSupport/nqtGioiThieu|<h2>Thông tin sinh viên</h2><ul><li><strong>Mã sinh viên:</strong> 2310900093</li><li><strong>Họ và tên:</strong> Nguyễn Quang Tâm</li><li><strong>Ngày sinh:</strong> 26/06/2005</li><li><strong>Giới tính:</strong> Nam</li><li><strong>Mã lớp:</strong> K23CNT1</li><li><strong>Email:</strong> nguyenquangtam6666@gmail.com</li><li><strong>SĐT:</strong> 0961138440</li></ul>'),(10,'nqtWebsiteAddress','Xã Đại Thanh, Hà Nội, Việt Nam'),(11,'nqtWebsiteFAQ',''),(12,'nqtWebsiteFacebook','https://fb.com/nqtam6666'),(13,'nqtWebsiteZalo','https://zalo.me/0961138440'),(14,'nqtWebsiteLink','https://nqtam.id.vn/profile'),(15,'TieuDe','Tiêu đề mặc định'),(16,'nqtVipDiscountPercent','20'),(17,'nqtSmtpHost','smtp.gmail.com'),(18,'nqtSmtpPort','587'),(19,'nqtSmtpUsername','nguyenquangtam6666@gmail.com'),(20,'nqtSmtpFromEmail','nguyenquangtam6666@gmail.com'),(21,'nqtSmtpFromName','Nguyễn Quang Tâm - K23CNT1 - Project 3'),(22,'nqtSmtpPassword','hwnp ojoz jhmo dgtq'),(23,'rate_limit_max_failed_attempts','3'),(24,'rate_limit_lockout_duration_minutes','5'),(25,'rate_limit_max_attempts','10'),(26,'rate_limit_window_minutes','15'),(27,'rate_limit_ip_max_attempts','10'),(28,'rate_limit_ip_window_minutes','1'),(29,'rate_limit_cleanup_days','30'),(30,'admin_path','nqtAdmin'),(31,'nqtBannerImages','[\"/uploads/e6eca961-9f8f-4028-a952-a7520eee8b0b_khach-san-la-gi-cach-phan-loai-khach-san.webp\",\"/uploads/fa9ab0a4-b52a-41de-b9e7-34f3c22d41a4_vietgoing_sdo2202244107.webp\",\"/uploads/660dc251-8453-4852-a0ae-f4d5595d6b9b_khach-san-5-sao-da-nang-1.jpg.webp\"]');
/*!40000 ALTER TABLE `nqtsetting` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-15  7:58:41
