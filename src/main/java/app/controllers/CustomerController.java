package app.controllers;

import app.config.Customer;
import io.javalin.http.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerController {
    private final DatabaseController dbController;

    public CustomerController(DatabaseController dbController) {
        this.dbController = dbController;
    }

    public void createProfile(Context ctx) {
        String query = "INSERT INTO kunde (name, email, password) VALUES (?, ?, ?)";
        try (Connection connection = dbController.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            Customer customer = ctx.bodyAsClass(Customer.class);
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPassword());
            stmt.executeUpdate();
            ctx.status(201).result("Profile created successfully.");
        } catch (Exception e) {
            ctx.status(500).result("Error creating profile: " + e.getMessage());
        }
    }

    public void login(Context ctx) {
        String query = "SELECT * FROM kunde WHERE email = ? AND password = ?";
        try (Connection connection = dbController.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            Customer customer = ctx.bodyAsClass(Customer.class);
            stmt.setString(1, customer.getEmail());
            stmt.setString(2, customer.getPassword());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Customer loggedInCustomer = new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                ctx.status(200).json(loggedInCustomer);
            } else {
                ctx.status(401).result("Invalid email or password.");
            }
        } catch (Exception e) {
            ctx.status(500).result("Error logging in: " + e.getMessage());
        }
    }

    public void updateProfile(Context ctx) {
        String query = "UPDATE kunde SET name = ?, password = ? WHERE email = ?";
        try (Connection connection = dbController.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            Customer customer = ctx.bodyAsClass(Customer.class);
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPassword());
            stmt.setString(3, customer.getEmail());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                ctx.status(200).result("Profile updated successfully.");
            } else {
                ctx.status(404).result("Profile not found.");
            }
        } catch (Exception e) {
            ctx.status(500).result("Error updating profile: " + e.getMessage());
        }
    }
}
