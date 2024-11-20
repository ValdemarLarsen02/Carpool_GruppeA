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
    private Salesman assignedSalesman;

    public Inquiry(int id, String dimensions, String materials, String status, Date createdDate, boolean emailSent, Customer customer, Salesman assignedSalesman) {
        this.id = id;
        this.dimensions = dimensions;
        this.materials = materials;
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
}