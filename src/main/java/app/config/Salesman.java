package app.config;


import com.fasterxml.jackson.annotation.JsonIgnoreType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Salesman {

    private String name;
    private String id;
    private String email;


    public List<Inquiry> viewInquiries() {
        List<Inquiry> inqueries = new ArrayList<>();

        String query = "SELECT id, customer_name, created_date, status, dimensions, materials, assigned_salesman, email_sent FROM inqueries ORDER BY id";

        try (Connection connection = db.controller.getConnection(); PreparedStatement statement = connection.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String customerName = resultSet.getString("customer_name");
                int createdDate = resultSet.getInt("created_date");
                String status = resultSet.getString("status");
                String dimensions = resultSet.getString("dimensions");
                String materials = resultSet.getString("materials");
                String assignedSalesman = resultSet.getString("assigned_salesman");
                String emailSent = resultSet.getString("email_sent");

                inqueries.add(new Inquiry(id, dimensions, materials, status, createdDate, emailSent, customerName, assignedSalesman));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inqueries;
    }

    public void assignInquiry(Inquiry inquiry, Salesman salesman) {
        String query = "UPDATE inqueries SET assigned_salesman = ? WHERE id = ?";

        try (Connection connection = db.controller.getConnection();
             PreparedStatement statement = connection.prepareStatement(query) {

            statement.setString(1, salesman.getName());
            statement.setInt(2, salesman.getInt());
            statement.setInt(3, inquiry.getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Inquiry " + inquiry.getId() + " assigned to " + salesmanName);
            } else {
                System.out.println("No inquiry found with ID " + inquiry.getId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void editInquiry(Inquiry inquiry) {

    }

    public String generate3DModel(Inquiry inquiry) {
        // Implementation here
        return null;
    }

    public boolean sendOfferEmail(Customer customer, String offer) {
        // Implementation here
        return false;
    }
}