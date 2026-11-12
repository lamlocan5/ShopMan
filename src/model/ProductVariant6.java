package model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductVariant6 implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String variantId;
    private String attributeDescription;
    private String salesUnit;
    private BigDecimal currentPrice;
    private String businessStatus;
    private Product6 product;

    public ProductVariant6() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVariantId() { return variantId; }
    public void setVariantId(String variantId) { this.variantId = variantId; }
    public String getAttributeDescription() { return attributeDescription; }
    public void setAttributeDescription(String attributeDescription) { this.attributeDescription = attributeDescription; }
    public String getSalesUnit() { return salesUnit; }
    public void setSalesUnit(String salesUnit) { this.salesUnit = salesUnit; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public String getBusinessStatus() { return businessStatus; }
    public void setBusinessStatus(String businessStatus) { this.businessStatus = businessStatus; }
    public Product6 getProduct() { return product; }
    public void setProduct(Product6 product) { this.product = product; }

}
