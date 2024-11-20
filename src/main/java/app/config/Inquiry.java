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

    public void assignSeller(Salesman salesman) {
        this.assignedSalesman = salesman;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}