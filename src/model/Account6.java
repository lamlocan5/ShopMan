package model;

import java.io.Serializable;

public class Account6 implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    private String passwordHash;
    private String role;
    private String status;

    public Account6() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public boolean isActiveCustomer() {
        return id != null && ROLE_CUSTOMER.equals(role) && STATUS_ACTIVE.equals(status);
    }
}
