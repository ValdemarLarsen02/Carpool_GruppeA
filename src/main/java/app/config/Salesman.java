package app.config;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Salesman {

    private String name;
    private String email;


    public List<Inquiry> viewInquiries() {
        List<Inquiry> inquiries = new ArrayList<>();

        String query = "SELECT id, customer_name, created_date, status, dimensions, materials FROM inqueries ORDER BY id";

        try (Connection connection = db.controller.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String customerName = resultSet.getString("customer_name");
                int createdDate = resultSet.getInt("created_date");
                String status = resultSet.getString("status");
                String dimensions = resultSet.getString("dimensions");
                String materials = resultSet.getString("materials");

                inquiries.add(new Inquiry(id, customerName, createdDate, status, dimensions, materials));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inquiries;
    }

    public void assignInquiry(Inquiry inquiry) {


    }


    public void editInquiry(Inquiry inquiry) {
        // Implementation here
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