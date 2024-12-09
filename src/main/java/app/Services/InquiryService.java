package app.Services;

import app.config.Customer;
import app.config.Inquiry;
import app.config.Salesman;
import app.controllers.DatabaseController;
import app.persistence.InquiryMapper;
import app.utils.DropdownOptions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InquiryService {
    private DatabaseController dbController;
    private CustomerService customerService;


    public InquiryService(CustomerService customerService, DatabaseController dbController) {
        this.dbController = dbController;
        this.customerService = customerService;
    }


    public Map<String, String[]> generateDropdownOptions() {
        return Map.of("carportWidthOptions", DropdownOptions.generateOptions(240, 600, 30), "carportLengthOptions", DropdownOptions.generateOptions(240, 780, 30), "shedWidthOptions", DropdownOptions.generateOptions(210, 720, 30), "shedLengthOptions", DropdownOptions.generateOptions(210, 720, 30));
    }

    //Henter inquiries fra databasen, og retunere dem i en liste
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

        try (Connection connection = dbController.getConnection(); PreparedStatement statement = connection.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                inquiries.add(InquiryMapper.mapInquiry(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inquiries;
    }

    //Tjekker om en inquiry har en sælger tilknyttet, retunere en boolean værdi
    public boolean hasSalesmanAssigned(int inquiryID, DatabaseController dbController) {
        String query = "SELECT salesmen_id FROM inquiries WHERE id = ?";

        try (Connection connection = dbController.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, inquiryID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("salesmen_id") != 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    //Tilknytter en sælger til en inquiry
    public void assignSalesmanToInquiry(int inquiryID, int salesmanID, DatabaseController dbController) {
        String checkQuery = "SELECT salesmen_id FROM inquiries WHERE id = ?";
        String updateQuery = "UPDATE inquiries SET salesmen_id = ? WHERE id = ?";

        try (Connection connection = dbController.getConnection()) {

            // Tjek om forespørgslen allerede har en sælger
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setInt(1, inquiryID);

                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int existingSalesmanId = resultSet.getInt("salesmen_id");
                        if (existingSalesmanId != 0) { // En sælger er allerede tildelt
                            System.out.println("Forespørgsel " + inquiryID + " har allerede sælger " + existingSalesmanId + " tildelt.");
                            return;
                        }
                    } else {
                        System.out.println("Forespørgsel " + inquiryID + " blev ikke fundet.");
                        return;
                    }
                }
            }

            // Tildel sælgeren
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, salesmanID);
                updateStatement.setInt(2, inquiryID);

                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Forespørgsel " + inquiryID + " er nu tildelt sælger " + salesmanID + ".");
                } else {
                    System.out.println("Opdateringen mislykkedes. Ingen rækker blev ændret.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fejl ved tildeling af sælger: " + e.getMessage(), e);
        }
    }

    public void updateInquiryInDatabase(Inquiry inquiry, DatabaseController dbController) {
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

        if (inquiry.getEmailSent() != null) {
            queryBuilder.append("email_sent = ?, ");
            parameters.add(inquiry.getEmailSent());
        }

        if (parameters.isEmpty()) {
            return;
        }

        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(" WHERE id = ?");
        parameters.add(inquiry.getId());

        try (Connection connection = dbController.getConnection(); PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Henter en enkelt forespørgsel fra databasen baseret på dens ID
    public Inquiry getInquiryById(int inquiryId, DatabaseController dbController) {
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
                WHERE inquiries.id = ?;
                """;

        try (Connection connection = dbController.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, inquiryId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return InquiryMapper.mapInquiry(resultSet);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fejl ved hentning af Inquiry med ID " + inquiryId, e);
        }

        return null; // Returner null, hvis ingen Inquiry blev fundet
    }

    public void saveInquiryWithCustomer(Inquiry inquiry, Customer customer) throws Exception {
        int customerId = customerService.saveCustomerToDatabase(customer);
        customer.setId(customerId); // Sæt det genererede ID
        inquiry.setCustomerId(customerId);
        saveInquiryToDatabase(inquiry);
    }

    public Customer getCustomerByInquiryId(int inquiryId, DatabaseController dbController) {
        String query = "SELECT c.* FROM inquiries i " + "JOIN customers c ON i.customer_id = c.id " + "WHERE i.id = ?";

        try (Connection connection = dbController.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, inquiryId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Customer customer = new Customer();
                customer.setId(resultSet.getInt("id"));
                customer.setName(resultSet.getString("name"));
                customer.setEmail(resultSet.getString("email"));
                customer.setPhoneNumber(resultSet.getInt("phone"));
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new IllegalArgumentException("Ingen kunde fundet for forespørgsel ID: " + inquiryId);
    }

    public void saveInquiryToDatabase(Inquiry inquiry) {
        String insertQuery = """
                INSERT INTO inquiries (customer_id, salesmen_id, email_sent, status, order_date, 
                                       carport_length, carport_width, shed_length, shed_width, comments)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) 
                DO UPDATE SET 
                    customer_id = EXCLUDED.customer_id,
                    salesmen_id = EXCLUDED.salesmen_id,
                    email_sent = EXCLUDED.email_sent,
                    status = EXCLUDED.status,
                    order_date = EXCLUDED.order_date,
                    carport_length = EXCLUDED.carport_length,
                    carport_width = EXCLUDED.carport_width,
                    shed_length = EXCLUDED.shed_length,
                    shed_width = EXCLUDED.shed_width,
                    comments = EXCLUDED.comments
                """;

        try (Connection connection = dbController.getConnection(); PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            // Sæt customer_id
            statement.setInt(1, inquiry.getCustomerId());

            // Sæt salesmen_id (kan være NULL)
            if (inquiry.getSalesmanId() != null) {
                statement.setInt(2, inquiry.getSalesmanId());
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }

            // Sæt email_sent (kan være NULL, men default er FALSE)
            if (inquiry.getEmailSent() != null) {
                statement.setBoolean(3, inquiry.getEmailSent());
            } else {
                statement.setNull(3, java.sql.Types.BOOLEAN);
            }

            // Sæt status
            statement.setString(4, inquiry.getStatus());

            // Sæt order_date
            if (inquiry.getOrderDate() != null) {
                statement.setDate(5, new java.sql.Date(inquiry.getOrderDate().getTime()));
            } else {
                statement.setNull(5, java.sql.Types.DATE);
            }

            // Sæt carport_length og carport_width
            statement.setDouble(6, inquiry.getCarportLength());
            statement.setDouble(7, inquiry.getCarportWidth());

            // Sæt shed_length og shed_width (kan være NULL)
            if (inquiry.getShedLength() != null) {
                statement.setDouble(8, inquiry.getShedLength());
            } else {
                statement.setNull(8, java.sql.Types.DOUBLE);
            }

            if (inquiry.getShedWidth() != null) {
                statement.setDouble(9, inquiry.getShedWidth());
            } else {
                statement.setNull(9, java.sql.Types.DOUBLE);
            }

            // Sæt comments (kan være NULL)
            if (inquiry.getComments() != null) {
                statement.setString(10, inquiry.getComments());
            } else {
                statement.setNull(10, java.sql.Types.VARCHAR);
            }

            // Udfør opdatering eller indsættelse i databasen
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Kunne ikke gemme forespørgslen i databasen.");
        }
    }


}
