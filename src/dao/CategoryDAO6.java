package dao;

import java.sql.*;
import model.*;

public class CategoryDAO6 extends DAO6 {
    public CategoryDAO6(Connection con, Account6 sessionAccount) { super(con, sessionAccount); }
    public Category6 findById(String categoryId) throws SQLException {
        requireConnection();
        try (PreparedStatement statement = con.prepareStatement("SELECT * FROM tblCategory6 WHERE categoryId=?")) {
            statement.setString(1, categoryId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) return null;
                Category6 category = new Category6();
                category.setId(rs.getLong("id")); category.setCategoryId(rs.getString("categoryId"));
                category.setName(rs.getString("name")); category.setDescription(rs.getString("description"));
                category.setVariantAttributeGuide(rs.getString("variantAttributeGuide"));
                category.setStatus(rs.getString("status")); return category;
            }
        }
    }
}
