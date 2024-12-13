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

    public void initialize() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(ConfigLoader.getProperty("db.url"));
            config.setUsername(ConfigLoader.getProperty("db.user"));
            config.setPassword(ConfigLoader.getProperty("db.password"));
            config.addDataSourceProperty("sslmode", ConfigLoader.getProperty("db.sslmode"));

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);

            this.dataSource = new HikariDataSource(config);
            System.out.println("Connection pool initialized.");
        } catch (Exception e) {
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Data source is not initialized.");
        }
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("Connection pool closed.");
        }
    }
}
