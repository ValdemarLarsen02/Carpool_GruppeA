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


    public List<Inquiry> getInquiriesFromDatabase(DatabaseController dbController) {
        List<Inquiry> inquiries = new ArrayList<>();

        // Query der henter data fra inquiries, customers og salesmen
        String query = """
                SELECT
                                         inquiries.id AS inquiry_id,
                                         inquiries.email_sent,
                                         inquiries.status,
                                         inquiries.order_date,
                                         inquiries.carport_length,
                                         inquiries.carport_width,
                                         inquiries.shed_length,
                                         inquiries.shed_width,
                                         inquiries.comments,
                                         customers.id AS customer_id,
                                         customers.name AS customer_name,
                                         customers.email AS customer_email,
                                         customers.phone AS customer_phone,
                                         customers.address AS customer_address,
                                         customers.city AS customer_city,
                                         customers.zipcode AS customer_zipcode,
                                         salesmen.id AS salesman_id,
                                         salesmen.name AS salesman_name,
                                         salesmen.email AS salesman_email
                                     FROM inquiries
                                     JOIN customers ON inquiries.customer_id = customers.id
                                     LEFT JOIN salesmen ON inquiries.salesmen_id = salesmen.id
                                     ORDER BY inquiries.id;
                                     
                """;

        try (Connection connection = dbController.getConnection();
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


    /*public void assignInquiry(Inquiry inquiry, Salesman salesman) {

        String checkQuery = "SELECT salesmen_id FROM inquiries WHERE id = ?";

        String query = "UPDATE inquiries SET salesmen_id = ? WHERE id = ?";

        try (Connection connection = dbController.getConnection()) {

            // Tjekker om forespørgslen allerede har en sælger tilknyttet
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setInt(1, inquiry.getId());
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int existingSalesmanId = resultSet.getInt("salesmen_id");
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
    }*/

    /*public void updateInquiryInDatabase(Inquiry inquiry) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE inquiries SET ");
        List<Object> parameters = new ArrayList<>();

        if (inquiry.getSalesmanId() != null) {
            queryBuilder.append("salesmen_id = ?, ");
            parameters.add(inquiry.getSalesmanId());
        }

        if (inquiry.getStatus() != null) {
            queryBuilder.append("status = ?, ");
            parameters.add(inquiry.getStatus());
        }

        if (inquiry.getComments() != null) {
            queryBuilder.append("comments = ?, ");
            parameters.add(inquiry.getComments());
        }

        if (inquiry.getCarportLength() != null) {
            queryBuilder.append("carport_length = ?, ");
            parameters.add(inquiry.getCarportLength());
        }

        if (inquiry.getCarportWidth() != null) {
            queryBuilder.append("carport_width = ?, ");
            parameters.add(inquiry.getCarportWidth());
        }

        if (inquiry.getShedLength() != null) {
            queryBuilder.append("shed_length = ?, ");
            parameters.add(inquiry.getShedLength());
        }

        if (inquiry.getShedWidth() != null) {
            queryBuilder.append("shed_width = ?, ");
            parameters.add(inquiry.getShedWidth());
        }

        if (inquiry.isEmailSent() != null) {
            queryBuilder.append("email_sent = ?, ");
            parameters.add(inquiry.isEmailSent());
        }

        if (parameters.isEmpty()) {
            return;
        }

        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(" WHERE id = ?");
        parameters.add(inquiry.getId());

        try (Connection connection = dbController.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
}
