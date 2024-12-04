package app.persistence;

import app.config.Customer;
import app.config.Inquiry;
import app.config.Salesman;
import io.javalin.http.Context;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InquiryMapper {

    // Mapper værdier fra en ResultSet til et Inquiry objekt
    public static Inquiry mapInquiry(ResultSet resultSet) throws SQLException {
        try {

            // Hent værdier fra ResultSet og map dem til Inquiry-objektet
            int inquiryId = resultSet.getInt("inquiry_id");
            int customerId = resultSet.getInt("customer_id");
            Integer salesmanId = resultSet.getObject("salesman_id") != null ? resultSet.getInt("salesman_id") : null;
            boolean emailSent = resultSet.getBoolean("email_sent");
            String status = resultSet.getString("status");
            java.sql.Date orderDate = resultSet.getDate("order_date");
            double carportLength = resultSet.getDouble("carport_length");
            double carportWidth = resultSet.getDouble("carport_width");
            Double shedLength = resultSet.getObject("shed_length") != null ? resultSet.getDouble("shed_length") : null;
            Double shedWidth = resultSet.getObject("shed_width") != null ? resultSet.getDouble("shed_width") : null;
            String comments = resultSet.getString("comments");


            // Opret et nyt Inquiry objekt og sæt værdierne
            Inquiry inquiry = new Inquiry(inquiryId, customerId, salesmanId, emailSent, status, orderDate, carportLength, carportWidth, shedLength, shedWidth, comments);

            return inquiry;
        } catch (SQLException e) {
            // Log eller håndter fejlen på en mere detaljeret måde
            System.err.println("Fejl ved mapping af Inquiry: " + e.getMessage());
            throw e;
        }
    }
}