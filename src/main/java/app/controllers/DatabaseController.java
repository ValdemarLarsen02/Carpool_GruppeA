package app.controllers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import app.utils.ConfigLoader;

public class DatabaseController {
    private HikariDataSource dataSource;

    // Opretter vores hikari forbindelse
    public void initialize() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(ConfigLoader.getProperty("db.url")); // læser fra vores config.
            config.setUsername(ConfigLoader.getProperty("db.user"));
            config.setPassword(ConfigLoader.getProperty("db.password"));
            config.addDataSourceProperty("sslmode", ConfigLoader.getProperty("db.sslmode"));

            // Optimeringsindstillinger og forskelline settings værdier er i ms.
            config.setMaximumPoolSize(10); // Hvor mange forbindelser kan vi have i vores pool?
            config.setMinimumIdle(2);     // Minimum antal ledige forbindelser
            config.setIdleTimeout(30000); // Timeout for inaktive forbindelser
            config.setConnectionTimeout(30000); // Timeout for at få en forbindelse

            this.dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
        }
    }

    // Hent en forbindelse
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Data source is not initialized.");
        }
        return dataSource.getConnection();
    }

    // Luk connection poolen
    public void close() {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("Connection pool closed.");
        }
    }

    // SELECT query
    public void executeQuery(String query) {
        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("Result: " + rs.getString(1)); // Her skal vi evt gøre noget andet end bare at printe.
            }
        } catch (SQLException e) {
            System.err.println("Query execution failed: " + e.getMessage());
        }
    }

    // UPDATE/INSERT/DELETE query
    public void executeUpdate(String query) {
        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {

            int affectedRows = stmt.executeUpdate();
            System.out.println("Query executed successfully. Rows affected: " + affectedRows);
        } catch (SQLException e) {
            System.err.println("Query execution failed: " + e.getMessage());
        }
    }
}