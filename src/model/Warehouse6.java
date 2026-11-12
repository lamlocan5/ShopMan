package model;

import java.io.Serializable;

public class Warehouse6 implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String warehouseId;
    private String name;
    private String address;
    private String status;

    public Warehouse6() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
