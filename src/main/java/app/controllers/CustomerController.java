package app.controllers;

import app.config.Customer;
import io.javalin.http.Context;

public class CustomerController {
    private final DatabaseController dbController;

    public CustomerController(DatabaseController dbController) {
        this.dbController = dbController;
    }


    public void createCustomer(Context ctx) {
        try {
            Customer customer = ctx.bodyAsClass(Customer.class);
            dbController.createCustomer(customer);
            ctx.status(201).result("Customer created successfully.");
        } catch (Exception e) {
            ctx.status(500).result("Error creating customer: " + e.getMessage());
        }
    }


    public void loginCustomer(Context ctx) {
        try {
            Customer customer = ctx.bodyAsClass(Customer.class);
            Customer loggedInCustomer = dbController.loginCustomer(customer.getEmail(), customer.getPassword());
            if (loggedInCustomer != null) {
                ctx.status(200).json(loggedInCustomer);
            } else {
                ctx.status(401).result("Invalid email or password.");
            }
        } catch (Exception e) {
            ctx.status(500).result("Error logging in: " + e.getMessage());
        }
    }


    public void updateCustomer(Context ctx) {
        try {
            Customer customer = ctx.bodyAsClass(Customer.class);
            boolean updated = dbController.updateCustomer(customer);
            if (updated) {
                ctx.status(200).result("Customer updated successfully.");
            } else {
                ctx.status(404).result("Customer not found.");
            }
        } catch (Exception e) {
            ctx.status(500).result("Error updating customer: " + e.getMessage());
        }
    }
}
