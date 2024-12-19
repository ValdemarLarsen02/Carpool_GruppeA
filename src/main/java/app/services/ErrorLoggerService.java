package app.services;

import app.controllers.DatabaseController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorLoggerService {

    private DatabaseController dbController;

    // Konstruktør der initialiserer databasecontrolleren
    public ErrorLoggerService(DatabaseController dbController) {
        this.dbController = dbController;
    }

    private static final String LOG_FILE = "errors.log"; // Filen hvor logfiler kan gemmes (bruges ikke i koden men kunne bruges senere)

    // Denne metode logfører fejl, formaterer dem og gemmer dem i databasen
    public void logError(String errorMessage, String severity, Exception exception) {
        String formattedMessage = formatError(errorMessage, severity, exception); // Formaterer fejlbeskeden
        saveLogToDatabase(formattedMessage, severity, exception); // Gemmer den formaterede besked i databasen
    }

    // Formaterer fejlinformationen med tidsstempel
    private String formatError(String errorMessage, String severity, Exception exception) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Definerer formatet for tidsstemplet
        String timestamp = dateFormat.format(new Date()); // Henter det aktuelle tidsstempel
        String exceptionMessage = (exception != null) ? exception.getMessage() : ""; // Henter undtagelsesbesked hvis der er en undtagelse
        String stackTrace = (exception != null) ? getStackTrace(exception) : ""; // Henter stack trace hvis der er en undtagelse

        // Formaterer og returnerer den endelige fejlbesked med alle detaljer
        return String.format("[%s] [%s] %s: %s\n%s", timestamp, severity, errorMessage, exceptionMessage, stackTrace);
    }

    // Gemmer den formaterede fejlbesked i databasen
    private void saveLogToDatabase(String errorMessage, String severity, Exception exception) {
        try (Connection connection = dbController.getConnection()) { // Får forbindelse til databasen
            String sql = "INSERT INTO error_logs (severity, error_message, exception_message, stack_trace) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                // Sætter parametre til SQL-kommandoen
                preparedStatement.setString(1, severity);
                preparedStatement.setString(2, errorMessage);
                preparedStatement.setString(3, exception != null ? exception.getMessage() : ""); // Lagrer undtagelsesbesked
                preparedStatement.setString(4, exception != null ? getStackTrace(exception) : ""); // Lagrer stack trace
                preparedStatement.executeUpdate(); // Eksekverer SQL-kommandoen for at indsætte loggen i databasen
            }
        } catch (SQLException e) {
            // Hvis der opstår en fejl under lagring af loggen i databasen, udskrives fejlen
            System.out.println("Fejl under lagring af log i databasen: " + e.getMessage());
        }
    }

    // Konverterer stack trace fra en undtagelse til en streng
    private String getStackTrace(Exception exception) {
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n"); // Tilføjer hvert element fra stack trace til strengen
        }
        return stackTrace.toString(); // Returnerer hele stack trace som en streng
    }
}
