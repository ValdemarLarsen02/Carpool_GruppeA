package app.controllers;

import app.config.Customer;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.Mockito.*;

public class CustomerControllerTest {

    private DatabaseController mockDbController;
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        mockDbController = mock(DatabaseController.class);
        customerController = new CustomerController(mockDbController);
    }

    @Test
    void testCreateProfile() throws Exception {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        when(mockDbController.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        Context mockCtx = mock(Context.class);
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPassword("password123");

        when(mockCtx.bodyAsClass(Customer.class)).thenReturn(customer);
        when(mockCtx.status(anyInt())).thenReturn(mockCtx);

        customerController.createProfile(mockCtx);

        verify(mockStatement).setString(1, "John Doe");
        verify(mockStatement).setString(2, "john@example.com");
        verify(mockStatement).setString(3, "password123");
        verify(mockStatement).executeUpdate();

        verify(mockCtx).status(201);
        verify(mockCtx).result("Profile created successfully.");
    }

    @Test
    void testLogin() throws Exception {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockDbController.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn("password123");

        Context mockCtx = mock(Context.class);
        Customer customer = new Customer();
        customer.setEmail("john@example.com");
        customer.setPassword("password123");

        when(mockCtx.bodyAsClass(Customer.class)).thenReturn(customer);
        when(mockCtx.status(anyInt())).thenReturn(mockCtx);

        customerController.login(mockCtx);

        verify(mockStatement).setString(1, "john@example.com");
        verify(mockStatement).executeQuery();

        verify(mockCtx).status(200);
        verify(mockCtx).result("Login successful.");
    }

    @Test
    void testUpdateProfile() throws Exception {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        when(mockDbController.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        // Mock `executeUpdate` to return 1 (indikerer, at én række blev opdateret)
        when(mockStatement.executeUpdate()).thenReturn(1);

        Context mockCtx = mock(Context.class);
        Customer customer = new Customer();
        customer.setName("Updated Name");
        customer.setEmail("updated@example.com");
        customer.setPassword("newpassword");

        when(mockCtx.bodyAsClass(Customer.class)).thenReturn(customer);
        when(mockCtx.status(anyInt())).thenReturn(mockCtx);

        customerController.updateProfile(mockCtx);

        // Verify SQL execution
        verify(mockStatement).setString(1, "Updated Name");
        verify(mockStatement).setString(2, "newpassword");
        verify(mockStatement).setString(3, "updated@example.com");
        verify(mockStatement).executeUpdate();

        // Verify response
        verify(mockCtx).status(200);
        verify(mockCtx).result("Profile updated successfully.");
    }


    @Test
    void testViewOrderStatus() throws Exception {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockDbController.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("status")).thenReturn("Order shipped");

        Context mockCtx = mock(Context.class);
        when(mockCtx.formParam("customer_id")).thenReturn("123");
        when(mockCtx.status(anyInt())).thenReturn(mockCtx);

        customerController.viewOrderStatus(mockCtx);

        verify(mockStatement).setInt(1, 123);
        verify(mockStatement).executeQuery();

        verify(mockCtx).status(200);
        verify(mockCtx).result("Order status: Order shipped");
    }
}
