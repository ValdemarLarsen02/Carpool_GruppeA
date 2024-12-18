package app.models;

import app.services.ErrorLoggerService;
import app.controllers.DatabaseController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin {


    DatabaseController dbController;
    String correctPassword;
    ErrorLoggerService errorLogger;

    public Admin(ErrorLoggerService errorLogger, DatabaseController dbController) {
        this.dbController = dbController;
    }

    public void editAnyInquiry(Inquiry inquiry) {
        // Implementation here
    }

    public void manageProducts() {
        // Implementation here
    }

    public void UpdatePassword(String password) {
        String query = "UPDATE salesman_password SET password = ? WHERE id = 1"; // Id=1 kan være en fast post til kodeordet
        try (Connection connection = dbController.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, password);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            String errorMessage = "Der skete en fejl under opdatering af sælger kodeordet, i setPassword metoden";
            errorLogger.logError(errorMessage, "HIGH", e);
            System.out.println(errorMessage);
        }
    }

    public String getPassword() {
        String query = "SELECT password FROM salesman_password WHERE id = 1";
        try (Connection connection = dbController.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query); ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getString("password");
            }
        } catch (SQLException e) {
            String errorMessage = "Der skete en fejl under hentningen af sælger kodeordet, i metoden get passowrd";
            errorLogger.logError(errorMessage, "HIGH", e);
            System.out.println(errorMessage);
        }
        return null;
    }
}