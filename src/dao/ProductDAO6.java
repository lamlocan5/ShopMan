package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class ProductDAO6 extends DAO6 {
    public static final int PAGE_SIZE = 10;
    private long totalResultCount;

    public ProductDAO6(Connection con, Account6 sessionAccount) { super(con, sessionAccount); }

    public List<Product6> searchByName(String keyword, int page) throws SQLException {
        requireConnection();
        totalResultCount = 0;
        keyword = normalizeKeyword(keyword);
        if (page < 1) throw new IllegalArgumentException("Trang kết quả phải bắt đầu từ 1.");
        String pattern = "%" + keyword.replace("!", "!!").replace("%", "!%")
                .replace("_", "!_") + "%";
        String condition = " FROM tblProduct6 p JOIN tblCategory6 c ON c.id=p.categoryRefId"
                + " WHERE p.businessStatus='ACTIVE' AND p.name LIKE ? ESCAPE '!'";
        try (PreparedStatement statement = con.prepareStatement("SELECT COUNT(*)" + condition)) {
            statement.setString(1, pattern);
            try (ResultSet rs = statement.executeQuery()) { rs.next(); totalResultCount = rs.getLong(1); }
        }
        List<Product6> products = new ArrayList<>();
        if (totalResultCount == 0) return products;
        long totalPages = (totalResultCount - 1) / PAGE_SIZE + 1;
        long actualPage = Math.min((long) page, totalPages);
        String sql = "SELECT p.*, c.categoryId" + condition + " ORDER BY p.productId, p.id LIMIT ? OFFSET ?";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, pattern);
            statement.setInt(2, PAGE_SIZE);
            statement.setLong(3, (actualPage - 1) * PAGE_SIZE);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) products.add(mapProduct(rs));
            }
        }
        return products;
    }

    public Product6 findActiveById(String productId) throws SQLException {
        requireConnection();
        if (productId == null || productId.trim().isEmpty()) return null;
        String sql = "SELECT p.*, c.categoryId FROM tblProduct6 p"
                + " JOIN tblCategory6 c ON c.id=p.categoryRefId"
                + " WHERE p.productId=? AND p.businessStatus='ACTIVE'";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, productId);
            try (ResultSet rs = statement.executeQuery()) { return rs.next() ? mapProduct(rs) : null; }
        }
    }

    public long getTotalResultCount() { return totalResultCount; }

    public static String normalizeKeyword(String keyword) {
        if (keyword == null) throw new IllegalArgumentException("Vui lòng nhập tên mặt hàng.");
        int start = 0, end = keyword.length();
        while (start < end && isSpace(keyword.codePointAt(start))) start += Character.charCount(keyword.codePointAt(start));
        while (end > start && isSpace(keyword.codePointBefore(end))) end -= Character.charCount(keyword.codePointBefore(end));
        keyword = keyword.substring(start, end);
        if (keyword.isEmpty()) throw new IllegalArgumentException("Vui lòng nhập tên mặt hàng.");
        if (keyword.codePointCount(0, keyword.length()) > 100)
            throw new IllegalArgumentException("Tên mặt hàng tìm kiếm không được quá 100 ký tự.");
        return keyword;
    }

    private static boolean isSpace(int value) { return Character.isWhitespace(value) || Character.isSpaceChar(value); }

    private Product6 mapProduct(ResultSet rs) throws SQLException {
        Product6 product = new Product6();
        product.setId(rs.getLong("id")); product.setProductId(rs.getString("productId"));
        product.setName(rs.getString("name")); product.setDescription(rs.getString("description"));
        product.setImageUrl(rs.getString("imageUrl")); product.setBusinessStatus(rs.getString("businessStatus"));
        Category6 category = new Category6();
        category.setId(rs.getLong("categoryRefId")); category.setCategoryId(rs.getString("categoryId"));
        product.setCategory(category);
        return product;
    }
}
