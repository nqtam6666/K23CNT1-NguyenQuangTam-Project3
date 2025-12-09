-- 1. Tạo database
CREATE DATABASE IF NOT EXISTS nqt_book_library
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE nqt_book_library;

-- 2. Bảng nqt_author
CREATE TABLE IF NOT EXISTS nqt_author (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  code VARCHAR(45) DEFAULT NULL,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  imgUrl VARCHAR(255),
  email VARCHAR(255),
  phone VARCHAR(45),
  address VARCHAR(255),
  isActive TINYINT(4) DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY nqt_uq_author_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Bảng nqt_book
CREATE TABLE IF NOT EXISTS nqt_book (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  code VARCHAR(45) DEFAULT NULL,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  imgUrl VARCHAR(255),
  quantity INT(11) DEFAULT 0,
  price DOUBLE DEFAULT 0,
  isActive TINYINT(4) DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY nqt_uq_book_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Bảng liên kết nqt_book_author
CREATE TABLE IF NOT EXISTS nqt_book_author (
  bookid BIGINT(20) NOT NULL,
  authorid BIGINT(20) NOT NULL,
  is_editor TINYINT(4) DEFAULT 0,
  PRIMARY KEY (bookid, authorid),
  INDEX nqt_idx_book (bookid),
  INDEX nqt_idx_author (authorid),
  CONSTRAINT nqt_fk_ba_book FOREIGN KEY (bookid) 
      REFERENCES nqt_book(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT nqt_fk_ba_author FOREIGN KEY (authorid) 
      REFERENCES nqt_author(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

