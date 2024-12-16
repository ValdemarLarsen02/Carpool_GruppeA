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

    // Opret profil
    public void createProfile(Context ctx) {
        String query = "INSERT INTO kunde (name, email, password) VALUES (?, ?, ?)";
        try (Connection connection = dbController.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            Customer customer = ctx.bodyAsClass(Customer.class);
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPassword()); // In real-world, hash password before storing
            stmt.executeUpdate();
            ctx.status(201).result("Profile created successfully.");
        } catch (Exception e) {
            System.out.println("Error creating profile: " + e.getMessage());
            ctx.status(500).result("Failed to create profile.");
        }
    }

    // Log In
    public void login(Context ctx) {
        String query = "SELECT password FROM kunde WHERE email = ?";
        try (Connection connection = dbController.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            Customer customer = ctx.bodyAsClass(Customer.class);
            stmt.setString(1, customer.getEmail());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                // Simulating password check - ideally, use a library for password hashing
                if (storedPassword.equals(customer.getPassword())) {
                    ctx.status(200).result("Login successful.");
                } else {
                    ctx.status(401).result("Invalid password.");
                }
            } else {
                ctx.status(404).result("User not found.");
            }
        } catch (Exception e) {
            System.out.println("Error logging in: " + e.getMessage());
            ctx.status(500).result("Failed to log in.");
        }
    }

    // Updater Profil
    public void updateProfile(Context ctx) {
        String query = "UPDATE kunde SET name = ?, password = ? WHERE email = ?";
        try (Connection connection = dbController.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            Customer customer = ctx.bodyAsClass(Customer.class);
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPassword()); // In real-world, hash password
            stmt.setString(3, customer.getEmail());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ctx.status(200).result("Profile updated successfully.");
            } else {
                ctx.status(404).result("Profile not found.");
            }
        } catch (Exception e) {
            System.out.println("Error updating profile: " + e.getMessage());
            ctx.status(500).result("Failed to update profile.");
        }
    }

    // Se order status
    public void viewOrderStatus(Context ctx) {
        String query = "SELECT status FROM orders WHERE customer_id = ? AND active = true";
        try (Connection connection = dbController.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            int customerId = Integer.parseInt(ctx.formParam("customer_id")); // Using formParam for consistency
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                ctx.status(200).result("Order status: " + status);
            } else {
                ctx.status(404).result("No active orders found.");
            }
        } catch (Exception e) {
            System.out.println("Error retrieving order status: " + e.getMessage());
            ctx.status(500).result("Failed to retrieve order status.");
        }
    }
}
