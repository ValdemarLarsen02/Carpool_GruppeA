package app.services;

import app.config.Customer;
import app.controllers.DatabaseController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerService {
    private final DatabaseController dbController;

    public CustomerService(DatabaseController dbController) {
        this.dbController = dbController;
    }

    public int saveCustomerToDatabase(Customer customer) {

        String insertQuery = """
                INSERT INTO customers (name, email, phone, address, city, zipcode)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = dbController.getConnection(); PreparedStatement statement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, customer.getName());
            statement.setString(2, customer.getEmail());
            statement.setInt(3, customer.getPhoneNumber());
            statement.setString(4, customer.getAddress());
            statement.setString(5, customer.getCity());
            statement.setInt(6, customer.getZipcode());

            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();

            if (keys.next()) {
                customer.setId(keys.getInt(1)); // Hent det genererede id
            }

            System.out.println("Kunde gemt i databasen med ID: " + customer.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Kunne ikke gemme kunden i databasen.");
        }

        return customer.getId();
    }
}
