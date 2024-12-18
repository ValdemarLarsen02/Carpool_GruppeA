import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class InquiryDatabaseDirectTest extends DatabaseIntegrationTest {

    @BeforeEach
    void insertTestData() throws SQLException {
        try (Connection connection = getDataSource().getConnection(); Statement stmt = connection.createStatement()) {

            // Indsætter alt vores data til at teste med
            stmt.execute("""
                        INSERT INTO customers (id, name, email, phone, address, city, zipcode)
                        VALUES (1, 'Test Customer', 'test@example.com', '12345678', 'Testvej 1', 'TestCity', '1234');
                    """);


            stmt.execute("""
                        INSERT INTO salesmen (id, name, email)
                        VALUES (1, 'Test Salesman', 'salesman@example.com');
                    """);


            stmt.execute("""
                        INSERT INTO inquiries (id, email_sent, status, order_date, carport_length, carport_width, shed_length, shed_width, comments, customer_id, salesmen_id)
                        VALUES (1, true, 'PENDING', '2024-12-18', 600, 400, 200, 100, 'Test Comment', 1, 1);
                    """);
        }
    }

    @Test
    void testGetInquiriesDirectly() throws SQLException {
        // Henter vores metode på samme møde som i prod.
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
                    customers.name AS customer_name,
                    salesmen.name AS salesman_name
                FROM inquiries
                JOIN customers ON inquiries.customer_id = customers.id
                LEFT JOIN salesmen ON inquiries.salesmen_id = salesmen.id
                WHERE inquiries.id = 1;
                """;

        try (Connection connection = getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {

            // Validér resultatet
            assertTrue(resultSet.next(), "Result set should contain at least one row");
            assertEquals(1, resultSet.getInt("inquiry_id"));
            assertTrue(resultSet.getBoolean("email_sent"));
            assertEquals("PENDING", resultSet.getString("status"));
            assertEquals(Date.valueOf("2024-12-18"), resultSet.getDate("order_date"));
            assertEquals(600, resultSet.getInt("carport_length"));
            assertEquals(400, resultSet.getInt("carport_width"));
            assertEquals(200, resultSet.getInt("shed_length"));
            assertEquals(100, resultSet.getInt("shed_width"));
            assertEquals("Test Comment", resultSet.getString("comments"));
            assertEquals("Test Customer", resultSet.getString("customer_name"));
            assertEquals("Test Salesman", resultSet.getString("salesman_name"));
        }
    }

    @AfterEach
    void cleanTestData() throws SQLException {
        // Sletter alt data efer vi har kørt vores test.
        try (Connection connection = getDataSource().getConnection(); Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM inquiries;");
            stmt.execute("DELETE FROM customers;");
            stmt.execute("DELETE FROM salesmen;");
        }
    }
}
