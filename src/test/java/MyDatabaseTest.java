import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MyDatabaseTest extends DatabaseIntegrationTest {

    @BeforeEach
    void populateTestData() throws Exception {
        try (Connection conn = getDataSource().getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO kunde (id, name, email, password) VALUES (1, 'Test User', 'valde@gmail.com', '1234')");
        }
    }

    @Test
    void testUserRetrieval() throws Exception {
        try (Connection conn = getDataSource().getConnection(); Statement stmt = conn.createStatement()) {
            var rs = stmt.executeQuery("SELECT name FROM kunde WHERE id = 1");
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals("Test User", rs.getString("name"));
        }
    }

    @AfterAll
    void cleanUp() {
        try (Connection conn = getDataSource().getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM kunde");
            System.out.println("Table 'kunde' has been cleared.");
        } catch (SQLException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

}
