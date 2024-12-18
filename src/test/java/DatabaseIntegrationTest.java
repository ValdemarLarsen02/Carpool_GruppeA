import app.utils.ConfigLoader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import javax.sql.DataSource;

public class DatabaseIntegrationTest {

    private static HikariDataSource dataSource;

    @BeforeAll
    static void setUp() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://ep-cool-firefly-a2k89guh.eu-central-1.aws.neon.tech/test_db");
        config.setUsername("neondb_owner");
        config.setPassword("mGW2Rzt6abci");


        dataSource = new HikariDataSource(config);
    }

    @AfterAll
    static void tearDown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

}
