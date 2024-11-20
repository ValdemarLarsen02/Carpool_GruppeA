package app.config;

import java.util.Date;

public class Inquiry {
    private int id;
    private String dimensions;
    private String materials;
    private String status;
    private Date createdDate;
    private boolean emailSent;
    private Customer customer;
    private Seller assignedSeller;

    public void assignSeller(Seller seller) {
        this.assignedSeller = seller;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}