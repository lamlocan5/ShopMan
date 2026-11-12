package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class ProductVariantDAO6 extends DAO6 {
    private static final String SELECT = "SELECT v.*, p.productId FROM tblProductVariant6 v"
            + " JOIN tblProduct6 p ON p.id=v.productRefId"
            + " WHERE v.businessStatus='ACTIVE' AND p.businessStatus='ACTIVE'";
    public ProductVariantDAO6(Connection con, Account6 sessionAccount) { super(con, sessionAccount); }
    public List<ProductVariant6> findActiveByProduct(String productId) throws SQLException {
        requireConnection();
        List<ProductVariant6> variants = new ArrayList<>();
        try (PreparedStatement statement = con.prepareStatement(SELECT + " AND p.productId=? ORDER BY v.variantId, v.id")) {
            statement.setString(1, productId);
            try (ResultSet rs = statement.executeQuery()) { while (rs.next()) variants.add(mapVariant(rs)); }
        }
        return variants;
    }
    public ProductVariant6 findActiveById(String variantId) throws SQLException {
        requireConnection();
        try (PreparedStatement statement = con.prepareStatement(SELECT + " AND v.variantId=?")) {
            statement.setString(1, variantId);
            try (ResultSet rs = statement.executeQuery()) { return rs.next() ? mapVariant(rs) : null; }
        }
    }
    private ProductVariant6 mapVariant(ResultSet rs) throws SQLException {
        ProductVariant6 variant = new ProductVariant6();
        variant.setId(rs.getLong("id")); variant.setVariantId(rs.getString("variantId"));
        variant.setAttributeDescription(rs.getString("attributeDescription"));
        variant.setSalesUnit(rs.getString("salesUnit")); variant.setCurrentPrice(rs.getBigDecimal("currentPrice"));
        variant.setBusinessStatus(rs.getString("businessStatus"));
        Product6 product = new Product6(); product.setId(rs.getLong("productRefId"));
        product.setProductId(rs.getString("productId")); variant.setProduct(product); return variant;
    }
}
