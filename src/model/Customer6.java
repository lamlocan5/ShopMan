package model;

import java.io.Serializable;

public class Customer6 implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String customerId;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private Account6 account;

    public Customer6() { }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Account6 getAccount() { return account; }
    public void setAccount(Account6 account) { this.account = account; }

}
