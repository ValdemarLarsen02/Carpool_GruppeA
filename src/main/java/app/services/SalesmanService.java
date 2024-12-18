package app.services;

import app.models.Salesman;
import app.controllers.DatabaseController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalesmanService {
    private ErrorLoggerService errorLogger;
    private DatabaseController dbController;

    public SalesmanService(ErrorLoggerService errorLogger, DatabaseController dbController) {
        this.errorLogger = errorLogger;
        this.dbController = dbController;
    }

    //Henter alle sælgere fra databasen
    public List<Salesman> getAllSalesmen() {
        List<Salesman> salesmen = new ArrayList<>();

        String query = "SELECT id, name, email FROM salesmen";
        try (Connection connection = dbController.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query); ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                salesmen.add(new Salesman(id, name, email));
            }

        } catch (Exception e) {
            String errorMessage = "Der skete en fejl under hentningen af alle sælgere, i getAllSalesmen metoden";
            errorLogger.logError(errorMessage, "HIGH", e);
            System.out.println(errorMessage);
        }

        return salesmen;
    }

    public void createSalesmanInDatabase(String name, String email) {
        String query = "INSERT INTO salesmen (name, email) VALUES (?, ?)";

        try (Connection connection = dbController.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            String errorMessage = "Der skete en fejl under oprettelse af en sælger, i createSalesmanInDatabase metoden";
            errorLogger.logError(errorMessage, "MEDIUM", e);
            System.out.println(errorMessage);
        }

    }
}

