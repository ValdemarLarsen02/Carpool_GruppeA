package app.controllers;

import app.config.Customer;
import app.config.Inquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseController {

    private final Connection connection;

    public DatabaseController(Connection connection) {
        this.connection = connection;
    }

    public void createCustomer(Customer customer) throws SQLException {
        String query = "INSERT INTO kunde (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPassword());
            stmt.executeUpdate();
        }
    }

    public Customer loginCustomer(String email, String password) throws SQLException {
        String query = "SELECT * FROM kunde WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }
        }
        return null;
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        String query = "UPDATE kunde SET name = ?, password = ? WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPassword());
            stmt.setString(3, customer.getEmail());
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    public List<Inquiry> getOrderStatus(int customerId) throws SQLException {
        String query = "SELECT * FROM inquiries WHERE customer_id = ?";
        List<Inquiry> inquiries = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                inquiries.add(new Inquiry(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getString("status"),
                        rs.getDate("created_date"),
                        rs.getString("special_request")
                ));
            }
        }
        return inquiries;
    }
}
