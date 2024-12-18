import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import app.services.CustomerService;
import app.services.ErrorLoggerService;
import app.services.InquiryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import app.models.Customer;
import app.controllers.DatabaseController;

import java.sql.*;
import java.util.*;

public class InquiryServiceTest {

    // Mocks for afhængighederne
    @Mock
    private DatabaseController dbController;

    @Mock
    private CustomerService customerService;

    @Mock
    private ErrorLoggerService errorLogger;

    // Injicerer mocks i InquiryService
    @InjectMocks
    private InquiryService inquiryService;

    // Setup-metode for at initialisere mocks før hver test
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialiserer mock-annotationerne
    }

    // Test af generering af dropdown muligheder
    @Test
    public void testGenerateDropdownOptions() {
        // Kald metoden til at generere dropdown muligheder
        Map<String, String[]> options = inquiryService.generateDropdownOptions();

        // Assertions for at verificere, at de genererede muligheder er korrekte
        assertNotNull(options); // Sørger for, at options ikke er null
        assertTrue(options.containsKey("carportWidthOptions")); // Verificerer, at carportWidthOptions nøgle findes
        assertTrue(options.containsKey("carportLengthOptions")); // Verificerer, at carportLengthOptions nøgle findes
    }


    // Test for at hente kundeoplysninger baseret på inquiry ID
    @Test
    public void testGetCustomerByInquiryId() throws SQLException {
        int inquiryID = 1;

        // Mock result set for kundedata
        String query = "SELECT c.* FROM inquiries i JOIN customers c ON i.customer_id = c.id WHERE i.id = ?";
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true); // Simulerer, at der er et resultat
        when(resultSet.getInt("id")).thenReturn(1); // Simulerer kunde-ID
        when(resultSet.getString("name")).thenReturn("John Doe"); // Simulerer kundens navn
        when(resultSet.getString("email")).thenReturn("johndoe@example.com"); // Simulerer kundens e-mail
        when(resultSet.getInt("phone")).thenReturn(12345678); // Simulerer kundens telefonnummer

        // Mock databaseforbindelse og præpareret statement
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet); // Returnerer den mockede result set
        when(dbController.getConnection()).thenReturn(connection);

        // Kald getCustomerByInquiryId og verificer resultatet
        Customer customer = inquiryService.getCustomerByInquiryId(inquiryID);

        // Verificerer, at kunden er korrekt hentet
        assertNotNull(customer); // Sørger for, at kunden ikke er null
        assertEquals("John Doe", customer.getName()); // Verificerer kundens navn
    }
}
