package app.config;


import com.fasterxml.jackson.annotation.JsonIgnoreType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Salesman {

    private String name;
    private Integer id;
    private String email;


    public List<Inquiry> viewInquiries() {
        List<Inquiry> inqueries = new ArrayList<>();

        String query = "SELECT id, customer_name, created_date, status, dimensions, materials, assigned_salesman, email_sent FROM inqueries ORDER BY id";

        try (Connection connection = db.controller.getConnection(); PreparedStatement statement = connection.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                Customer customerName = resultSet.getString("customer_name");
                Date createdDate = resultSet.getDate("created_date");
                String status = resultSet.getString("status");
                String dimensions = resultSet.getString("dimensions");
                String materials = resultSet.getString("materials");
                Salesman assignedSalesman = resultSet.getString("assigned_salesman");
                Boolean emailSent = resultSet.getBoolean("email_sent");

                inqueries.add(new Inquiry(id, dimensions, materials, status, createdDate, emailSent, customerName, assignedSalesman));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inqueries;
    }

    //Skal sørges for at metoden tjekker om der allerede er en salesman assigned
    public void assignInquiry(Inquiry inquiry, Salesman salesman) {
        String checkQuery = "SELECT assigned_salesman FROM inqueries WHERE id = ?";
        String query = "UPDATE inqueries SET assigned_salesman = ?, salesman_id = ? WHERE id = ?";

        try (Connection connection = db.controller.getConnection()) {

            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setInt(1, inquiry.getId());
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String existingSalesman = resultSet.getString("assigned_salesman");
                        if (existingSalesman != null) {
                            System.out.println("Forespørgsel " + inquiry.getId() + "har allerede en sælger tildelt: " + existingSalesman);
                            return;
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, salesman.getName());
                statement.setInt(2, salesman.getId());
                statement.setInt(3, inquiry.getId());

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Forespørgsel " + inquiry.getId() + " tildelt " + salesman.getName());
                } else {
                    System.out.println("Der findes ikke en forespørgsel med ID'et " + inquiry.getId());
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void editInquiry(Inquiry inquiry) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE inqueries SET ");
        List<Object> parameters = new ArrayList<>();

        if (inquiry.getDimensions() != null) {
            queryBuilder.append(" dimensions = ?, ");
            parameters.add(inquiry.getDimensions());
        }

        if (inquiry.getMaterials() != null) {
            queryBuilder.append(" materials = ?, ");
            parameters.add(inquiry.getMaterials());
        }

        if (inquiry.getAssignedSalesman() != null) {
            queryBuilder.append(" assigned_salesman = ?, ");
            parameters.add(inquiry.getAssignedSalesman());
        }

        if (inquiry.getStatus() != null) {
            queryBuilder.append(" status = ?, ");
            parameters.add(inquiry.getStatus());
        }

        if (parameters.isEmpty()) {
            return;
        }
        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(" WHERE id = ?");
        parameters.add(inquiry.getId());

        try (Connection connection = db.controller.GetConnection();
             PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public String generate3DModel(Inquiry inquiry) {
        // Implementation here
        return null;
    }

    public boolean sendOfferEmail(Customer customer, String offer) {
        // Implementation here
        return false;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

