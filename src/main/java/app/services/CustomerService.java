package app.services;

import app.models.Customer;
import app.controllers.DatabaseController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerService {
    private final DatabaseController dbController; // Database controller til at håndtere databaseforbindelser
    private ErrorLoggerService errorLogger; // Service til at logge fejl

    // Konstruktør til initialisering af dbController og errorLogger
    public CustomerService(DatabaseController dbController, ErrorLoggerService errorLogger) {
        this.dbController = dbController;
        this.errorLogger = errorLogger;
    }

    // Gemmer en kunde i databasen og returnerer kundens ID
    public int saveCustomerToDatabase(Customer customer) {

        // SQL-query til at indsætte en ny kunde
        String insertQuery = """
                INSERT INTO customers (name, email, phone, address, city, zipcode)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        // Forsøger at oprette forbindelse til databasen og køre INSERT-queryen
        try (Connection connection = dbController.getConnection(); PreparedStatement statement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Sætter de nødvendige parametre i SQL-queryen
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getEmail());
            statement.setInt(3, customer.getPhoneNumber());
            statement.setString(4, customer.getAddress());
            statement.setString(5, customer.getCity());
            statement.setInt(6, customer.getZipcode());

            // Udfører opdateringen i databasen
            statement.executeUpdate();

            // Henter den genererede nøgle (ID) for den nye kunde
            var keys = statement.getGeneratedKeys();
            if (keys.next()) {
                customer.setId(keys.getInt(1)); // Sætter den genererede ID på kunden
            }

            System.out.println("Kunde gemt i databasen med ID: " + customer.getId());
        } catch (SQLException e) {
            // Logger fejl, hvis der opstår en SQLException under indsættelsen af kunden i databasen
            String errorMessage = "Der skete en fejl, da kunden skulle gemmes i databasen, i saveCustomerToDatabase metoden";
            errorLogger.logError(errorMessage, "HIGH", e);
            System.out.println(errorMessage);
        }

        // Returnerer kundens ID (kan være 0, hvis der opstod en fejl)
        return customer.getId();
    }

    // Henter en kunde fra databasen ved hjælp af kundens ID
    public Customer getCustomerById(int customerId) {
        // SQL-query til at hente kundeoplysninger baseret på ID
        String query = "SELECT * FROM customers WHERE id = ?";

        try (Connection connection = dbController.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Sætter ID'et i SQL-queryen
            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Hvis kunden findes, opretter en Customer-objekt og fylder det med data
            if (resultSet.next()) {
                Customer customer = new Customer(resultSet.getString("name"), resultSet.getString("email"), resultSet.getInt("phone"), resultSet.getString("address"), resultSet.getString("city"), resultSet.getInt("zipcode"));

                customer.setId(resultSet.getInt("id")); // Sætter kundens ID
                return customer;
            }
        } catch (SQLException e) {
            // Logger fejl, hvis der opstår en SQLException ved hentning af kunden
            String errorMessage = "Der skete en fejl, ved at hente en kundes data via kunde id'et, i getCustomerById metoden";
            errorLogger.logError(errorMessage, "HIGH", e);
            System.out.println(errorMessage);
        }

        // Returnerer null, hvis kunden ikke findes eller der opstår en fejl
        return null;
    }
}
