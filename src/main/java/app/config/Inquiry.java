package app.config;

import app.config.Customer;
import app.config.Salesman;
import app.controllers.DatabaseController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class Inquiry {
    private int id;
    private Double length;
    private Double width;
    private String status;
    private Date createdDate;
    private boolean emailSent;
    private Customer customer;
    private Salesman assignedSalesman;
    private Boolean specialRequest;


    public Inquiry() {
    }

    public Inquiry(int id, Customer customer, Salesman assignedSalesman, boolean emailSent, String status,
                   Date createdDate, double length, double width, Boolean specialRequest) {
        this.id = id;
        this.length = length;
        this.width = width;
        this.status = status;
        this.createdDate = createdDate;
        this.emailSent = emailSent;
        this.customer = customer;
        this.assignedSalesman = assignedSalesman;
        this.specialRequest = specialRequest;
    }

    public void saveToDatabase(DatabaseController dbController) {
        String insertQuery = """
                INSERT INTO inquiries (customer_id, salesman_id, email_sent, status, created_date, length, width, special_request)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) 
                DO UPDATE SET 
                    customer_id = EXCLUDED.customer_id, 
                    salesman_id = EXCLUDED.salesman_id, 
                    email_sent = EXCLUDED.email_sent, 
                    status = EXCLUDED.status, 
                    created_date = EXCLUDED.created_date, 
                    length = EXCLUDED.length,
                    width = EXCLUDED.width,
                    special_request = EXCLUDED.special_request
                """;

        try (Connection connection = dbController.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            statement.setInt(1, this.customer.getId());
            if (this.assignedSalesman != null) {
                statement.setInt(2, this.assignedSalesman.getId());
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }
            statement.setBoolean(3, this.emailSent);
            statement.setString(4, this.status);
            statement.setDate(5, new java.sql.Date(this.createdDate.getTime()));
            statement.setDouble(6, this.length);
            statement.setDouble(7, this.width);
            statement.setBoolean(8, this.specialRequest);


            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Inquiry gemt i databasen.");
            } else {
                System.out.println("Ingen rækker blev opdateret eller tilføjet.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Kunne ikke gemme Inquiry i databasen.");
        }
    }


    public Double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public int getId() {
        return id;
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

    public Boolean isSpecialRequest() {
        return specialRequest;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
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

    public void setSpecialRequest(boolean specialRequest) {
        this.specialRequest = specialRequest;
    }
}
