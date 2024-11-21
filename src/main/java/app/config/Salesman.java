package app.config;


import app.controllers.DatabaseController;
import app.persistence.InquiryMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Salesman {

    DatabaseController db = new DatabaseController();
    private String name;
    private Integer id;
    private String email;


    public List<Inquiry> getInquiriesFromDatabase() {
        List<Inquiry> inquiries = new ArrayList<>();

        String query = """
                SELECT 
                    inquiries.id, inquiries.email_sent, inquiries.status, 
                    inquiries.created_date, inquiries.dimensions, inquiries.materials,
                    customers.name, customers.email,
                    salesmen.id, salesmen.name, salesmen.email
                FROM inquiries
                JOIN customers ON inquiries.customer_id = customers.id
                LEFT JOIN salesmen ON inquiries.salesman_id = salesmen.id
                ORDER BY inquiries.id
                """;

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                inquiries.add(InquiryMapper.mapInquiry(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inquiries;
    }

    public List<Inquiry> viewInquiries() {
        return getInquiriesFromDatabase();
    }


    public void assignInquiry(Inquiry inquiry, Salesman salesman) {
        String checkQuery = "SELECT salesman_id FROM inquiries WHERE id = ?";
        String query = "UPDATE inquiries SET salesman_id = ? WHERE id = ?";

        try (Connection connection = db.getConnection()) {

            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setInt(1, inquiry.getId());
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int existingSalesmanId = resultSet.getInt("salesman_id");
                        if (existingSalesmanId != 0) {

                            System.out.println("Forespørgsel " + inquiry.getId() + " har allerede en sælger tildelt.");
                            return;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }

            }

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, salesman.getId());
                statement.setInt(2, inquiry.getId());

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Forespørgsel " + inquiry.getId() + " tildelt " + salesman.getName());
                } else {
                    System.out.println("Der findes ikke en forespørgsel med ID'et " + inquiry.getId());
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void editInquiry(Inquiry inquiry) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE inquiries SET ");
        List<Object> parameters = new ArrayList<>();

        if (inquiry.getDimensions() != null) {
            queryBuilder.append("dimensions = ?, ");
            parameters.add(inquiry.getDimensions());
        }

        if (inquiry.getMaterials() != null) {
            queryBuilder.append("materials = ?, ");
            parameters.add(inquiry.getMaterials());
        }

        if (inquiry.getAssignedSalesman() != null) {
            queryBuilder.append("salesman_id = ?, ");
            parameters.add(inquiry.getAssignedSalesman().getId());
        }

        if (inquiry.getStatus() != null) {
            queryBuilder.append("status = ?, ");
            parameters.add(inquiry.getStatus());
        }

        if (parameters.isEmpty()) {
            return;
        }

        queryBuilder.setLength(queryBuilder.length() - 2);

        queryBuilder.append(" WHERE id = ?");
        parameters.add(inquiry.getId());

        try (Connection connection = db.getConnection();
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

