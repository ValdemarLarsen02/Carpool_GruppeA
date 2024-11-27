package app.config;

import app.controllers.DatabaseController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Customer {
    private String name;
    private String email;
    private int id;
    private int phoneNumber;

    public Customer(String name, String email, int id, int phoneNumber) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.phoneNumber = phoneNumber;
    }

    public Customer() {
    }

    //Test metode
    public void saveToDatabase(DatabaseController dbController) {
        String insertQuery = """
                INSERT INTO customers (id, name, email)
                VALUES (?, ?, ?)
                ON CONFLICT (id) 
                DO UPDATE SET 
                    name = EXCLUDED.name,
                    email = EXCLUDED.email
                """;

        try (Connection connection = dbController.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            // Sæt parametre for INSERT-forespørgslen
            statement.setInt(1, this.id);
            statement.setString(2, this.name);
            statement.setString(3, this.email);

            // Udfør forespørgslen
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Kunde gemt i databasen.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Kunne ikke gemme kunden i databasen.");
        }
    }


    public void submitInquiry(Inquiry inquiry) {

    }

    public List<Carport> viewCarports(Map<String, String> filters) {

        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }
}

