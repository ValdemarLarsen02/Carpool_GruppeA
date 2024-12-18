package app.services;

import app.controllers.DatabaseController;
import app.models.Carport;
import app.persistence.InquiryMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PredefinedCarportsService {


    private DatabaseController dbController;

    public PredefinedCarportsService(DatabaseController dbController) {
        this.dbController = dbController;
    }

    //henter alle caporte fra databasen
    public List<Carport> getCarports() {
        List<Carport> carports = new ArrayList<>();
        String query = "SELECT * FROM carports";

        try (Connection connection = dbController.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                int carportId = resultSet.getInt("carport_id");
                String carportName = resultSet.getString("carport_name");
                int length = resultSet.getInt("length");
                int width = resultSet.getInt("width");
                boolean withShed = resultSet.getBoolean("with_shed");
                int shedLength = resultSet.getInt("shed_length");
                int shedWidth = resultSet.getInt("shed_width");
                double totalPrice = resultSet.getDouble("total_price");
                String image = resultSet.getString("picture_link");


                Carport carport = new Carport(carportId, carportName, length, width, withShed, shedLength, shedWidth, totalPrice, image);


                carports.add(carport);
            }

        } catch (SQLException e) {
            String errorMessage = "Fejl ved hentningen af carports fra databasen i getCarports metoden";
            System.err.println(errorMessage + ": " + e.getMessage());
        }

        return carports;
    }

    //henter caport fra databasen ud fra id
    public Carport getCarportById(int carportId) {
        Carport carport = null;
        String query = "SELECT * FROM carports WHERE carport_id = ?";

        try (Connection connection = dbController.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Sætter vores id som værdi..
            statement.setInt(1, carportId);

            //
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Hent data fra resultSet
                    int id = resultSet.getInt("carport_id");
                    String carportName = resultSet.getString("carport_name");
                    int length = resultSet.getInt("length");
                    int width = resultSet.getInt("width");
                    boolean withShed = resultSet.getBoolean("with_shed");
                    int shedLength = resultSet.getInt("shed_length");
                    int shedWidth = resultSet.getInt("shed_width");
                    double totalPrice = resultSet.getDouble("total_price");
                    String image = resultSet.getString("picture_link");

                    // Opret og returnér carport-objekt
                    carport = new Carport(id, carportName, length, width, withShed, shedLength, shedWidth, totalPrice, image);
                }
            }
        } catch (SQLException e) {
            System.err.println("Fejl ved hentning af carport fra databasen: " + e.getMessage());
        }

        return carport;
    }



}
