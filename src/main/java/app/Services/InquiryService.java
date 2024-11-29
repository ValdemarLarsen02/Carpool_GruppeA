package app.Services;

import app.config.Inquiry;
import app.config.Salesman;
import app.controllers.DatabaseController;
import app.persistence.InquiryMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InquiryService {


    DatabaseController db = new DatabaseController();

    public List<Inquiry> getInquiriesFromDatabase() {
        List<Inquiry> inquiries = new ArrayList<>();

        // Query der henter data fra inquiries, customers og salesmen
        String query = """
                SELECT 
                    inquiries.id, inquiries.email_sent, inquiries.status, 
                    inquiries.created_date, inquiries.length, inquiries.width, inquiries.materials, inquiries.special_request,
                    customers.name, customers.email,
                    salesmen.id AS salesman_id, salesmen.name AS salesman_name, salesmen.email AS salesman_email
                FROM inquiries
                JOIN customers ON inquiries.customer_id = customers.id
                LEFT JOIN salesmen ON inquiries.salesman_id = salesmen.id
                ORDER BY inquiries.id
                """;

        // Udfør query og map resultaterne
        try (Connection connection = db.getConnection(); PreparedStatement statement = connection.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {

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

        String checkQuery = "SELECT salesman_id FROM inquiries WHERE id = ?"; // CheckQuery tjekker om en sælger allerede er tildelt

        String query = "UPDATE inquiries SET salesman_id = ? WHERE id = ?"; // Query til opdatering af tildelt sælger

        try (Connection connection = db.getConnection()) {

            // Tjekker om forespørgslen allerede har en sælger tilknyttet
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

            // Opdaterer forespørgslen med den nye sælger
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

    public void updateInquiryInDatabase(Inquiry inquiry) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE inquiries SET "); // Initialiser SQL-query
        List<Object> parameters = new ArrayList<>(); // Liste til dynamiske parametre


        // Tilføjer salesman_id til query, hvis en sælger er tildelt
        if (inquiry.getAssignedSalesman() != null) {
            queryBuilder.append("salesman_id = ?, ");
            parameters.add(inquiry.getAssignedSalesman().getId());
        }

        // Tilføjer status til query, hvis en status findes
        if (inquiry.getStatus() != null) {
            queryBuilder.append("status = ?, ");
            parameters.add(inquiry.getStatus());
        }

        // Tilføjer special_request til query, hvis det findes
        if (inquiry.isSpecialRequest() != null) {
            queryBuilder.append("special_request = ?, ");
            parameters.add(inquiry.isSpecialRequest());
        }


        // Tilføjer width til query, hvis det findes
        if (inquiry.getWidth() != null) {
            queryBuilder.append("width = ?, ");
            parameters.add(inquiry.getWidth());
        }

        // Tilføjer length til query, hvis det findes
        if (inquiry.getLength() != null) {
            queryBuilder.append("length = ?, ");
            parameters.add(inquiry.getLength());
        }

        // Hvis der ikke er parametre, skal metoden afsluttes
        if (parameters.isEmpty()) {
            return;
        }

        // Fjerner det sidste komma og mellemrum fra query
        queryBuilder.setLength(queryBuilder.length() - 2);

        // Tilføjer WHERE-betingelse for at opdatere det korrekte inquiry
        queryBuilder.append(" WHERE id = ?");
        parameters.add(inquiry.getId());

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {

            // Binder parametre til den dynamiske query
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            // Udfør opdateringen
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

