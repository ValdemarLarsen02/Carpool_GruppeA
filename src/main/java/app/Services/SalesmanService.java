package app.Services;

import app.config.Salesman;
import app.controllers.DatabaseController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SalesmanService {

    public List<Salesman> getAllSalesmen(DatabaseController dbController) {
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
            e.printStackTrace();
        }

        return salesmen;
    }
}

