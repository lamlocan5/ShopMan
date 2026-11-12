package dao;

import java.sql.*;
import model.Account6;

/** Module 1 reads inventory only. */
public class InventoryDAO6 extends DAO6 {
    public InventoryDAO6(Connection con, Account6 sessionAccount) { super(con, sessionAccount); }
    public int getAvailableQty(String variantId) throws SQLException {
        requireConnection();
        String sql = "SELECT COALESCE(SUM(i.onHandQuantity),0) FROM tblInventory6 i"
                + " JOIN tblWarehouse6 w ON w.id=i.warehouseRefId"
                + " JOIN tblProductVariant6 v ON v.id=i.variantRefId"
                + " WHERE v.variantId=? AND w.status='ACTIVE'";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, variantId);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next(); long quantity = rs.getLong(1);
                if (quantity < 0 || quantity > Integer.MAX_VALUE)
                    throw new SQLDataException("Tồn khả dụng vượt miền giá trị int của thiết kế.");
                return (int) quantity;
            }
        }
    }
}
