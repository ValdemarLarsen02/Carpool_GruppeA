package app.config;

import app.controllers.DatabaseController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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


    public Inquiry() {
    }

    // Konstruktør
    public Inquiry(int id, int customerId, Integer salesmanId, Boolean emailSent, String status, Date orderDate,
                   Double carportLength, Double carportWidth, Double shedLength, Double shedWidth, String comments) {
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

    // Metode til at gemme forespørgsel i databasen
    public void saveToDatabase(DatabaseController dbController) {
        String insertQuery = """
                INSERT INTO inquiries (customer_id, salesmen_id, email_sent, status, order_date, 
                                       carport_length, carport_width, shed_length, shed_width, comments)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) 
                DO UPDATE SET 
                    customer_id = EXCLUDED.customer_id,
                    salesmen_id = EXCLUDED.salesmen_id,
                    email_sent = EXCLUDED.email_sent,
                    status = EXCLUDED.status,
                    order_date = EXCLUDED.order_date,
                    carport_length = EXCLUDED.carport_length,
                    carport_width = EXCLUDED.carport_width,
                    shed_length = EXCLUDED.shed_length,
                    shed_width = EXCLUDED.shed_width,
                    comments = EXCLUDED.comments
                """;

        try (Connection connection = dbController.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            // Sætter customer_id
            statement.setInt(1, this.customerId);

            // Sætter salesmen_id (kan være NULL)
            if (this.salesmanId != null) {
                statement.setInt(2, this.salesmanId);
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }

            // Sætter email_sent (kan være NULL, men default er FALSE)
            if (this.emailSent != null) {
                statement.setBoolean(3, this.emailSent);
            } else {
                statement.setNull(3, java.sql.Types.BOOLEAN);
            }

            // Sætter status
            statement.setString(4, this.status);

            // Sætter order_date
            statement.setDate(5, new java.sql.Date(this.orderDate.getTime()));

            // Sætter carport_length og carport_width
            statement.setDouble(6, this.carportLength);
            statement.setDouble(7, this.carportWidth);

            // Sætter shed_length og shed_width (kan være NULL)
            if (this.shedLength != null) {
                statement.setDouble(8, this.shedLength);
            } else {
                statement.setNull(8, java.sql.Types.DOUBLE);
            }

            if (this.shedWidth != null) {
                statement.setDouble(9, this.shedWidth);
            } else {
                statement.setNull(9, java.sql.Types.DOUBLE);
            }

            // Sætter comments (kan være NULL)
            if (this.comments != null) {
                statement.setString(10, this.comments);
            } else {
                statement.setNull(10, java.sql.Types.VARCHAR);
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Kunne ikke gemme forespørgslen i databasen.");
        }
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
}


