-- Only the seven tables required by module 1 and its optional customer account support.
-- Does not drop databases/tables or touch the reference FootballLeagueManagement database.
CREATE DATABASE IF NOT EXISTS ShopMan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ShopMan;
SET NAMES utf8mb4;
CREATE TABLE IF NOT EXISTS tblAccount6 (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 username VARCHAR(255) NOT NULL UNIQUE,
 passwordHash VARCHAR(255) NOT NULL,
 role VARCHAR(30) NOT NULL,
 status VARCHAR(30) NOT NULL
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS tblCustomer6 (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 customerId VARCHAR(50) NOT NULL UNIQUE,
 fullName VARCHAR(255) NOT NULL,
 phone VARCHAR(20) NOT NULL,
 email VARCHAR(255) NOT NULL,
 address TEXT NOT NULL,
 accountRefId BIGINT NOT NULL UNIQUE,
 CONSTRAINT fk_customer6_account FOREIGN KEY (accountRefId) REFERENCES tblAccount6(id) ON DELETE RESTRICT
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS tblCategory6 (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 categoryId VARCHAR(50) NOT NULL UNIQUE,
 name VARCHAR(255) NOT NULL,
 description TEXT NOT NULL,
 variantAttributeGuide TEXT NOT NULL,
 status VARCHAR(30) NOT NULL
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS tblProduct6 (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 productId VARCHAR(50) NOT NULL UNIQUE,
 name VARCHAR(255) NOT NULL,
 description TEXT NOT NULL,
 imageUrl VARCHAR(500) NOT NULL,
 businessStatus VARCHAR(30) NOT NULL,
 categoryRefId BIGINT NOT NULL,
 CONSTRAINT fk_product6_category FOREIGN KEY (categoryRefId) REFERENCES tblCategory6(id) ON DELETE RESTRICT
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS tblProductVariant6 (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 variantId VARCHAR(50) NOT NULL UNIQUE,
 attributeDescription VARCHAR(255) NOT NULL,
 salesUnit VARCHAR(255) NOT NULL,
 currentPrice DECIMAL(18,2) NOT NULL,
 businessStatus VARCHAR(30) NOT NULL,
 productRefId BIGINT NOT NULL,
 CONSTRAINT uq_variant6_attributes UNIQUE (productRefId,attributeDescription),
 CONSTRAINT ck_variant6_price CHECK (currentPrice>=0),
 CONSTRAINT fk_variant6_product FOREIGN KEY (productRefId) REFERENCES tblProduct6(id) ON DELETE RESTRICT
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS tblWarehouse6 (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 warehouseId VARCHAR(50) NOT NULL UNIQUE,
 name VARCHAR(255) NOT NULL,
 address TEXT NOT NULL,
 status VARCHAR(30) NOT NULL
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS tblInventory6 (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 onHandQuantity INT NOT NULL,
 version BIGINT NOT NULL DEFAULT 0,
 warehouseRefId BIGINT NOT NULL,
 variantRefId BIGINT NOT NULL,
 CONSTRAINT uq_inventory6_pair UNIQUE (warehouseRefId,variantRefId),
 CONSTRAINT ck_inventory6_quantity CHECK (onHandQuantity>=0),
 CONSTRAINT ck_inventory6_version CHECK (version>=0),
 CONSTRAINT fk_inventory6_warehouse FOREIGN KEY (warehouseRefId) REFERENCES tblWarehouse6(id) ON DELETE RESTRICT,
 CONSTRAINT fk_inventory6_variant FOREIGN KEY (variantRefId) REFERENCES tblProductVariant6(id) ON DELETE RESTRICT
) ENGINE=InnoDB;
