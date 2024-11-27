package app.config;

import app.controllers.DatabaseController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    private boolean specialRequest;

    public Inquiry() {

    }

    public Inquiry(int id, Customer customer, Salesman assignedSalesman, boolean emailSent, String status, Date createdDate, String materials, String dimensions, Boolean specialRequest) {
        this.id = id;
        this.materials = materials;
        this.dimensions = dimensions;
        this.status = status;
        this.createdDate = createdDate;
        this.emailSent = emailSent;
        this.customer = customer;
        this.assignedSalesman = assignedSalesman;
    }

    //Test metode - Jonas laver den rigtige.
    public void saveToDatabase(DatabaseController dbController) {
        String insertQuery = """
                INSERT INTO inquiries (id, customer_id, salesman_id, email_sent, status, created_date, dimensions, materials)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) 
                DO UPDATE SET 
                    customer_id = EXCLUDED.customer_id, 
                    salesman_id = EXCLUDED.salesman_id, 
                    email_sent = EXCLUDED.email_sent, 
                    status = EXCLUDED.status, 
                    created_date = EXCLUDED.created_date, 
                    dimensions = EXCLUDED.dimensions, 
                    materials = EXCLUDED.materials
                """;

        try (Connection connection = dbController.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            // Sæt parametre for INSERT-forespørgslen
            statement.setInt(1, this.id);
            statement.setInt(2, this.customer.getId());
            if (this.assignedSalesman != null) {
                statement.setInt(3, this.assignedSalesman.getId());
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);
            }
            statement.setBoolean(4, this.emailSent);
            statement.setString(5, this.status);
            statement.setDate(6, new java.sql.Date(this.createdDate.getTime()));
            statement.setString(7, this.dimensions);
            statement.setString(8, this.materials);

            // Udfør forespørgslen
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Inquiry gemt i databasen.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Kunne ikke gemme Inquiry i databasen.");
        }
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

    public void setMaterials(String materials) {
        this.materials = materials;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public void setAssignedSalesman(Salesman assignedSalesman) {
        this.assignedSalesman = assignedSalesman;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public boolean isSpecialRequest() {
        return specialRequest;
    }
}