package app.models;

import java.util.Date;

public class Inquiry {

    private int id;
    private int customerId;
    private boolean salesmanAssigned;
    private Integer salesmanId;
    private Boolean emailSent;
    private String status;
    private Date orderDate;
    private Double carportLength;
    private Double carportWidth;
    private Double shedLength;
    private Double shedWidth;
    private String comments;
    private Customer customer;
    private Double salesPrice;

    public Inquiry() {
    }

    // Konstruktør
    public Inquiry(int id, int customerId, Integer salesmanId, Boolean emailSent, String status, Date orderDate, Double carportLength, Double carportWidth, Double shedLength, Double shedWidth, String comments) {
        this.id = id;
        this.customerId = customerId;
        this.salesmanId = salesmanId;
        this.emailSent = emailSent;
        this.status = status;
        this.orderDate = orderDate;
        this.carportLength = carportLength;
        this.carportWidth = carportWidth;
        this.shedLength = shedLength;
        this.shedWidth = shedWidth;
        this.comments = comments;
    }


    // Getters og setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Double getCarportLength() {
        return carportLength;
    }

    public void setCarportLength(Double carportLength) {
        this.carportLength = carportLength;
    }

    public Double getCarportWidth() {
        return carportWidth;
    }

    public void setCarportWidth(Double carportWidth) {
        this.carportWidth = carportWidth;
    }

    public Double getShedLength() {
        return shedLength;
    }

    public void setShedLength(Double shedLength) {
        this.shedLength = shedLength;
    }

    public Double getShedWidth() {
        return shedWidth;
    }

    public void setShedWidth(Double shedWidth) {
        this.shedWidth = shedWidth;
    }


    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setSalesPrice(Double salesPrice) {
        this.salesPrice = salesPrice;
    }

    public boolean isSalesmanAssigned() {
        return salesmanAssigned;
    }

    public int getCustomerId() {
        return customerId;
    }

    public Integer getSalesmanId() {
        return salesmanId;
    }

    public Boolean getEmailSent() {
        return emailSent;
    }

    public String getStatus() {
        return status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public String getComments() {
        return comments;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setSalesmanAssigned(boolean salesmanAssigned) {
        this.salesmanAssigned = salesmanAssigned;
    }

    public void setSalesmanId(Integer salesmanId) {
        this.salesmanId = salesmanId;
    }

    public void setEmailSent(Boolean emailSent) {
        this.emailSent = emailSent;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Double getSalesPrice() {
        return salesPrice;
    }

    @Override
    public String toString() {
        return "Inquiry {" + "id=" + id + ", customerId=" + customerId + ", salesmanAssigned=" + salesmanAssigned + ", salesmanId=" + (salesmanId != null ? salesmanId : "N/A") + ", emailSent=" + (emailSent != null ? emailSent : "N/A") + ", status='" + (status != null ? status : "N/A") + '\'' + ", orderDate=" + (orderDate != null ? orderDate : "N/A") + ", carportLength=" + (carportLength != null ? carportLength : "N/A") + ", carportWidth=" + (carportWidth != null ? carportWidth : "N/A") + ", shedLength=" + (shedLength != null ? shedLength : "N/A") + ", shedWidth=" + (shedWidth != null ? shedWidth : "N/A") + ", comments='" + (comments != null ? comments : "N/A") + '\'' + ", customer=" + (customer != null ? customer.toString() : "N/A") + " }";
    }


}



