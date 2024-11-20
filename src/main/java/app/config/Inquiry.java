package app.config;

import java.util.Date;

public class Inquiry {
    private int id;
    private String materials;
    private String dimensions;
    private String status;
    private Date createdDate;
    private boolean emailSent;
    private Customer customer;
    private Salesman assignedSalesman;

    public Inquiry(int id, String dimensions, String materials, String status, Date createdDate, Boolean emailSent, Customer customer, Salesman assignedSalesman) {
        this.id = id;
        this.materials = materials;
        this.dimensions = dimensions;
        this.status = status;
        this.createdDate = createdDate;
        this.emailSent = emailSent;
        this.customer = customer;
        this.assignedSalesman = assignedSalesman;
    }

    public void assignSeller(Salesman salesman) {
        this.assignedSalesman = salesman;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getDimensions() {
        return dimensions;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Salesman getAssignedSalesman() {
        return assignedSalesman;
    }

    public String getMaterials() {
        return materials;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setAssignedSalesman(Salesman assignedSalesman) {
        this.assignedSalesman = assignedSalesman;
    }
}