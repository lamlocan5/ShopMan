package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product6 implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String productId;
    private String name;
    private String description;
    private String imageUrl;
    private String businessStatus;
    private Category6 category;
    private List<ProductVariant6> variants = new ArrayList<>();

    public Product6() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getBusinessStatus() { return businessStatus; }
    public void setBusinessStatus(String businessStatus) { this.businessStatus = businessStatus; }
    public Category6 getCategory() { return category; }
    public void setCategory(Category6 category) { this.category = category; }
    public List<ProductVariant6> getVariants() { return variants; }
    public void setVariants(List<ProductVariant6> variants) { this.variants = variants; }

}
