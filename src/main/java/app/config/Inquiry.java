package app.config;

import java.util.Date;

public class Inquiry {
    private int id;
    private int customerId;
    private String status;
    private Date createdDate;
    private String specialRequest;

    // Constructors
    public Inquiry(int id, int customerId, String status, Date createdDate, String specialRequest) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.createdDate = createdDate;
        this.specialRequest = specialRequest;
    }

    public Inquiry() {} // Default constructor

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public String getSpecialRequest() { return specialRequest; }
    public void setSpecialRequest(String specialRequest) { this.specialRequest = specialRequest; }
}
