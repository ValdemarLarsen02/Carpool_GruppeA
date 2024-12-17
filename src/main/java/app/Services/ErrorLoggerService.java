package app.services;

import app.controllers.DatabaseController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorLoggerService {

    private DatabaseController dbController;

    public ErrorLoggerService(DatabaseController dbController) {
        this.dbController = dbController;
    }

    private static final String LOG_FILE = "errors.log";

    // Denne metode logfører fejl og gemmer dem i databasen
    public void logError(String errorMessage, String severity, Exception exception) {
        String formattedMessage = formatError(errorMessage, severity, exception);
        saveLogToDatabase(formattedMessage, severity, exception);
    }

    // Formaterer fejlinformation med timestamp
    private String formatError(String errorMessage, String severity, Exception exception) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        String exceptionMessage = (exception != null) ? exception.getMessage() : "";
        String stackTrace = (exception != null) ? getStackTrace(exception) : "";

        return String.format("[%s] [%s] %s: %s\n%s", timestamp, severity, errorMessage, exceptionMessage, stackTrace);
    }

    // Gemmer fejl i databasen
    private void saveLogToDatabase(String errorMessage, String severity, Exception exception) {
        try (Connection connection = dbController.getConnection()) {
            String sql = "INSERT INTO error_logs (severity, error_message, exception_message, stack_trace) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, severity);
                preparedStatement.setString(2, errorMessage);
                preparedStatement.setString(3, exception != null ? exception.getMessage() : "");
                preparedStatement.setString(4, exception != null ? getStackTrace(exception) : "");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error while saving log to database: " + e.getMessage());
        }
    }

    // Henter stack trace som en streng
    private String getStackTrace(Exception exception) {
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        return stackTrace.toString();
    }
}
