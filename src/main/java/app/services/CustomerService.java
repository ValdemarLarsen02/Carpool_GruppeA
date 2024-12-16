package app.services;

import app.config.Customer;
import app.controllers.DatabaseController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerService {
    private final DatabaseController dbController;
    private ErrorLoggerService errorLogger;

    public CustomerService(DatabaseController dbController, ErrorLoggerService errorLogger) {
        this.dbController = dbController;
        this.errorLogger = errorLogger;
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
            String errorMessage = "Der skete en fejl, da kunden skulle gemmes i databasen, i saveCustomerToDatabase metoden";
            errorLogger.logError(errorMessage, "HIGH", e);
            System.out.println(errorMessage);

        }

        return customer.getId();
    }

    public Customer getCustomerById(int customerId) {
        String query = "SELECT * FROM customers WHERE id = ?";
        try (Connection connection = dbController.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Customer customer = new Customer(
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getInt("phone"),
                        resultSet.getString("address"),
                        resultSet.getString("city"),
                        resultSet.getInt("zipcode")
                );

                customer.setId(resultSet.getInt("id"));
                return customer;
            }
        } catch (SQLException e) {
            String errorMessage = "Der skete en fejl, ved at hente en kundes data via kunde id'et, i getCustomerById metoden";
            errorLogger.logError(errorMessage, "HIGH", e);
            System.out.println(errorMessage);
        }
        return null;
    }

}
