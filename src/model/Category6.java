package model;

import java.io.Serializable;

public class Category6 implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String categoryId;
    private String name;
    private String description;
    private String variantAttributeGuide;
    private String status;

    public Category6() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVariantAttributeGuide() { return variantAttributeGuide; }
    public void setVariantAttributeGuide(String variantAttributeGuide) { this.variantAttributeGuide = variantAttributeGuide; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
