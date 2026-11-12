package model;

import java.io.Serializable;

public class Inventory6 implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private int onHandQuantity;
    private long version;
    private Warehouse6 warehouse;
    private ProductVariant6 variant;

    public Inventory6() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getOnHandQuantity() { return onHandQuantity; }
    public void setOnHandQuantity(int onHandQuantity) { this.onHandQuantity = onHandQuantity; }
    public long getVersion() { return version; }
    public void setVersion(long version) { this.version = version; }
    public Warehouse6 getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse6 warehouse) { this.warehouse = warehouse; }
    public ProductVariant6 getVariant() { return variant; }
    public void setVariant(ProductVariant6 variant) { this.variant = variant; }

}
