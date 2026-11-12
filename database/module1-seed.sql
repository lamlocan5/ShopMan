-- Run once after schema creation, on empty module 1 tables.
-- Public demonstration account: kh01 / Kh01@123. passwordHash contains a PBKDF2 hash.
USE ShopMan;
SET NAMES utf8mb4;
START TRANSACTION;
INSERT INTO tblAccount6(id,username,passwordHash,role,status) VALUES
 (1,'kh01','pbkdf2_sha256$210000$tphmW1XgqzfK32OUlRI3eg==$4ArrT7YkvE3cqiSNmwY4QkzdyPNa1z6VPsBP34qDM2M=','CUSTOMER','ACTIVE');
INSERT INTO tblCustomer6(id,customerId,fullName,phone,email,address,accountRefId) VALUES
 (1,'KH01','Nguyễn Văn A','0900000001','kh01@example.test','Hà Nội',1);
INSERT INTO tblCategory6(id,categoryId,name,description,variantAttributeGuide,status) VALUES
 (1,'C01','Quần áo','Các mặt hàng quần áo','Màu/Kích cỡ','ACTIVE'),
 (2,'C02','Gia vị','Các mặt hàng gia vị','Thể tích','ACTIVE');
INSERT INTO tblProduct6(id,productId,name,description,imageUrl,businessStatus,categoryRefId) VALUES
 (1,'P01','Áo thun Cơ bản','Áo thun Cơ bản. Mặt hàng đang kinh doanh.','assets/images/product-P01.svg','ACTIVE',1),
 (2,'P02','Áo thun Thể thao','Áo thun dành cho hoạt động thể thao.','assets/images/product-P02.svg','ACTIVE',1),
 (3,'P03','Áo thun Trẻ em','Áo thun dành cho trẻ em.','assets/images/product-P03.svg','ACTIVE',1),
 (4,'P04','Nước mắm Biển','Nước mắm Biển đóng chai.','assets/images/no-image.svg','ACTIVE',2),
 (99,'P99','Áo thun ngừng kinh doanh','Không xuất hiện trong kết quả tìm kiếm.','assets/images/no-image.svg','INACTIVE',1);
INSERT INTO tblProductVariant6(id,variantId,attributeDescription,salesUnit,currentPrice,businessStatus,productRefId) VALUES
 (1,'AT-BAS-R-M','Đỏ/M','Cái',120000,'ACTIVE',1),
 (2,'AT-BAS-W-L','Trắng/L','Cái',125000,'ACTIVE',1),
 (3,'AT-BAS-B-XL','Đen/XL','Cái',130000,'ACTIVE',1),
 (4,'AT-SP-B-M','Xanh/M','Cái',150000,'ACTIVE',2),
 (5,'AT-KID-Y-S','Vàng/S','Cái',180000,'ACTIVE',3),
 (6,'NM-BIEN-500','500 ml','Chai',45000,'ACTIVE',4),
 (98,'AT-BAS-OLD','Mẫu cũ','Cái',1000,'INACTIVE',1),
 (99,'AT-STOP','Mẫu ngừng bán','Cái',10000,'ACTIVE',99);
INSERT INTO tblWarehouse6(id,warehouseId,name,address,status) VALUES
 (1,'W01','Kho chính','Hà Nội','ACTIVE'),
 (2,'W02','Kho ngừng hoạt động','Hà Nội','INACTIVE');
INSERT INTO tblInventory6(id,onHandQuantity,version,warehouseRefId,variantRefId) VALUES
 (1,10,1,1,1),(2,8,1,1,2),(3,6,1,1,3),(4,15,1,1,4),(5,12,1,1,5),(6,20,1,1,6),
 (7,999,1,2,2),(8,99,1,1,98);
COMMIT;
