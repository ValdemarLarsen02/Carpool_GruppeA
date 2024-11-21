package app.persistence;

import app.config.Customer;
import app.config.Inquiry;
import app.config.Salesman;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InquiryMapper {

    public static Inquiry mapInquiry(ResultSet resultSet) throws SQLException {
        // Mapper Customer
        Customer customer = new Customer();
        customer.setName(resultSet.getString("customers.name"));
        customer.setEmail(resultSet.getString("customers.email"));

        // Mapper Salesman (kan være null)
        Salesman assignedSalesman = null;
        if (resultSet.getObject("salesmen.id") != null) {
            assignedSalesman = new Salesman();
            assignedSalesman.setId(resultSet.getInt("salesmen.id"));
            assignedSalesman.setName(resultSet.getString("salesmen.name"));
            assignedSalesman.setEmail(resultSet.getString("salesmen.email"));
        }

        // Mapper Inquiry
        return new Inquiry(
                resultSet.getInt("inquiries.id"),
                customer,
                assignedSalesman,
                resultSet.getBoolean("inquiries.email_sent"),
                resultSet.getString("inquiries.status"),
                resultSet.getDate("inquiries.created_date"),
                resultSet.getString("inquiries.dimensions"),
                resultSet.getString("inquiries.materials")
        );
    }
}

