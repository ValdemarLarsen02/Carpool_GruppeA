package app.controllers;

import app.config.Inquiry;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InquiryController {
    private final DatabaseController dbController;

    public InquiryController(DatabaseController dbController) {
        this.dbController = dbController;
    }

    public void getOrderStatus(Context ctx) {
        String query = "SELECT * FROM inquiries WHERE customer_id = ?";
        try (Connection connection = dbController.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            int customerId = Integer.parseInt(ctx.pathParam("customerId"));
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            List<Inquiry> inquiries = new ArrayList<>();
            while (rs.next()) {
                inquiries.add(new Inquiry(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getString("status"),
                        rs.getDate("created_date"),
                        rs.getString("special_request")
                ));
            }
            ctx.json(inquiries);
        } catch (Exception e) {
            ctx.status(500).result("Error fetching order status: " + e.getMessage());
        }
    }
}
