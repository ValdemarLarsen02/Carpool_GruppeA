import app.models.Inquiry;
import app.persistence.InquiryMapper;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InquiryMapperTest {

    // Tester mapning af Inquiry-objekt med gyldigt ResultSet
    @Test
    void testMapInquiryWithValidResultSet() throws SQLException {
        // Mock ResultSet objektet
        ResultSet resultSet = mock(ResultSet.class);

        // Simuler værdier i ResultSet, der skal returneres af metoden
        when(resultSet.getInt("inquiry_id")).thenReturn(1);
        when(resultSet.getInt("customer_id")).thenReturn(2);
        when(resultSet.getInt("salesman_id")).thenReturn(3);
        when(resultSet.getBoolean("email_sent")).thenReturn(true);
        when(resultSet.getString("status")).thenReturn("Pending");
        when(resultSet.getDate("order_date")).thenReturn(Date.valueOf("2024-01-01"));
        when(resultSet.getDouble("carport_length")).thenReturn(5.5);
        when(resultSet.getDouble("carport_width")).thenReturn(3.0);
        when(resultSet.getString("comments")).thenReturn("Some comments");

        // Kald mapInquiry-metoden for at mappe ResultSet til Inquiry objektet
        Inquiry inquiry = InquiryMapper.mapInquiry(resultSet);

        // Verificer, at de returnerede værdier i Inquiry-objektet matcher det simulerede ResultSet
        assertEquals(1, inquiry.getId());  // Bekræft at inquiry_id er korrekt
        assertEquals(2, inquiry.getCustomerId());  // Bekræft at customer_id er korrekt
        assertTrue(inquiry.getEmailSent());  // Bekræft at email_sent er true
        assertEquals("Pending", inquiry.getStatus());  // Bekræft at status er "Pending"
        assertEquals(Date.valueOf("2024-01-01"), inquiry.getOrderDate());  // Bekræft at order_date er korrekt
        assertEquals(5.5, inquiry.getCarportLength());  // Bekræft at carport_length er korrekt
        assertEquals(3.0, inquiry.getCarportWidth());  // Bekræft at carport_width er korrekt
        assertEquals("Some comments", inquiry.getComments());  // Bekræft at comments er korrekt
    }

    // Tester mapning af Inquiry-objekt, hvor nogle værdier er null
    @Test
    void testMapInquiryWithNullSalesmanIdAndShedDimensions() throws SQLException {
        // Mock ResultSet objektet
        ResultSet resultSet = mock(ResultSet.class);

        // Simuler værdier i ResultSet, hvor nogle felter har null-værdier
        when(resultSet.getInt("inquiry_id")).thenReturn(1);
        when(resultSet.getInt("customer_id")).thenReturn(2);
        when(resultSet.getObject("salesman_id")).thenReturn(null); // Null værdi for salesman_id
        when(resultSet.getBoolean("email_sent")).thenReturn(false);
        when(resultSet.getString("status")).thenReturn("Completed");
        when(resultSet.getDate("order_date")).thenReturn(Date.valueOf("2024-01-01"));
        when(resultSet.getDouble("carport_length")).thenReturn(6.0);
        when(resultSet.getDouble("carport_width")).thenReturn(3.5);
        when(resultSet.getObject("shed_length")).thenReturn(null); // Null værdi for shed_length
        when(resultSet.getObject("shed_width")).thenReturn(null); // Null værdi for shed_width
        when(resultSet.getString("comments")).thenReturn("");

        // Kald mapInquiry-metoden for at mappe ResultSet til Inquiry objektet
        Inquiry inquiry = InquiryMapper.mapInquiry(resultSet);

        // Verificer, at de returnerede værdier i Inquiry-objektet matcher det simulerede ResultSet
        assertEquals(1, inquiry.getId());  // Bekræft at inquiry_id er korrekt
        assertEquals(2, inquiry.getCustomerId());  // Bekræft at customer_id er korrekt
        assertNull(inquiry.getSalesmanId());  // Bekræft at salesman_id er null
        assertFalse(inquiry.getEmailSent());  // Bekræft at email_sent er false
        assertEquals("Completed", inquiry.getStatus());  // Bekræft at status er "Completed"
        assertEquals(Date.valueOf("2024-01-01"), inquiry.getOrderDate());  // Bekræft at order_date er korrekt
        assertEquals(6.0, inquiry.getCarportLength());  // Bekræft at carport_length er korrekt
        assertEquals(3.5, inquiry.getCarportWidth());  // Bekræft at carport_width er korrekt
        assertNull(inquiry.getShedLength());  // Bekræft at shed_length er null
        assertNull(inquiry.getShedWidth());  // Bekræft at shed_width er null
        assertEquals("", inquiry.getComments());  // Bekræft at comments er tom
    }

    // Tester at SQLException kastes korrekt, hvis der opstår en fejl i ResultSet
    @Test
    void testMapInquiryThrowsSQLException() throws SQLException {
        // Mock ResultSet objektet
        ResultSet resultSet = mock(ResultSet.class);

        // Simuler en SQLException ved getInt-metoden
        when(resultSet.getInt("inquiry_id")).thenThrow(new SQLException("Database error"));

        // Verificer, at en SQLException kastes når mapInquiry-metoden køres
        SQLException exception = assertThrows(SQLException.class, () -> {
            InquiryMapper.mapInquiry(resultSet);  // Kald mapInquiry-metoden, som skal kaste SQLException
        });

        // Bekræft at fejlen der kastes har den forventede fejlmeddelelse
        assertEquals("Database error", exception.getMessage());
    }
}
