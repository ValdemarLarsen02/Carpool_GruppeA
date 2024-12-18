import app.models.Customer;
import app.utils.RequestParser;
import io.javalin.http.Context;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestParserTest {

    private final RequestParser requestParser = new RequestParser(); // Opretter en instans af RequestParser til at teste metoderne

    // Tester parseNullableInteger metoden, når input er null eller en tom streng
    @Test
    void parseNullableInteger_ShouldReturnNull_WhenValueIsNullOrEmpty() {
        assertNull(requestParser.parseNullableInteger(null)); // Forventer null hvis input er null
        assertNull(requestParser.parseNullableInteger("")); // Forventer null hvis input er en tom streng
    }

    // Tester parseNullableInteger metoden, når input er en gyldig integer
    @Test
    void parseNullableInteger_ShouldReturnInteger_WhenValueIsValid() {
        assertEquals(123, requestParser.parseNullableInteger("123")); // Forventer 123 hvis input er "123"
    }

    // Tester parseNullableBoolean metoden, når input er null eller en tom streng
    @Test
    void parseNullableBoolean_ShouldReturnNull_WhenValueIsNullOrEmpty() {
        assertNull(requestParser.parseNullableBoolean(null)); // Forventer null hvis input er null
        assertNull(requestParser.parseNullableBoolean("")); // Forventer null hvis input er en tom streng
    }

    // Tester parseNullableBoolean metoden, når input er en gyldig boolean værdi
    @Test
    void parseNullableBoolean_ShouldReturnBoolean_WhenValueIsValid() {
        assertTrue(requestParser.parseNullableBoolean("true")); // Forventer true hvis input er "true"
        assertFalse(requestParser.parseNullableBoolean("false")); // Forventer false hvis input er "false"
    }

    // Tester parseCustomerFromRequest metoden med mockede data fra en HTTP-anmodning
    @Test
    void parseCustomerFromRequest_ShouldReturnCustomer_WhenValidParams() {
        // Opretter en mock Context
        Context mockCtx = mock(Context.class);
        when(mockCtx.formParam("name")).thenReturn("John Doe"); // Simulerer form parameter for navn
        when(mockCtx.formParam("email")).thenReturn("john.doe@example.com"); // Simulerer form parameter for email
        when(mockCtx.formParam("phone")).thenReturn("12345678"); // Simulerer form parameter for telefonnummer
        when(mockCtx.formParam("address")).thenReturn("123 Main St"); // Simulerer form parameter for adresse
        when(mockCtx.formParam("city")).thenReturn("Copenhagen"); // Simulerer form parameter for by
        when(mockCtx.formParam("zipcode")).thenReturn("1000"); // Simulerer form parameter for postnummer

        // Kald på metoden der parser kunden fra anmodningen
        Customer customer = requestParser.parseCustomerFromRequest(mockCtx);

        // Asserts for at sikre, at de parsed værdier svarer til de forventede værdier
        assertEquals("John Doe", customer.getName()); // Forventer at navnet er "John Doe"
        assertEquals("john.doe@example.com", customer.getEmail()); // Forventer at emailen er "john.doe@example.com"
        assertEquals(12345678, customer.getPhoneNumber()); // Forventer at telefonnummeret er 12345678
        assertEquals("123 Main St", customer.getAddress()); // Forventer at adressen er "123 Main St"
        assertEquals("Copenhagen", customer.getCity()); // Forventer at byen er "Copenhagen"
        assertEquals(1000, customer.getZipcode()); // Forventer at postnummeret er 1000
    }

    // Tester parseNullableDouble metoden, når input er null eller en tom streng
    @Test
    void parseNullableDouble_ShouldReturnNull_WhenValueIsNullOrEmpty() {
        assertNull(requestParser.parseNullableDouble(null)); // Forventer null hvis input er null
        assertNull(requestParser.parseNullableDouble("")); // Forventer null hvis input er en tom streng
    }

    // Tester parseNullableDouble metoden, når input er en gyldig double værdi
    @Test
    void parseNullableDouble_ShouldReturnDouble_WhenValueIsValid() {
        assertEquals(123.45, requestParser.parseNullableDouble("123.45")); // Forventer 123.45 hvis input er "123.45"
    }
}
