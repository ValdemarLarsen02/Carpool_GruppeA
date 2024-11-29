package app.persistence;

import app.config.Customer;
import app.config.Inquiry;
import app.config.Salesman;
import io.javalin.http.Context;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InquiryMapper {

    // Mapper værdierne for formparametre til et Inquiry objekt
    public static Inquiry mapFromRequest(Context ctx) {
        Inquiry inquiry = new Inquiry();

        // Sætter Inquiry-attributter fra formparametrene
        inquiry.setId(Integer.parseInt(ctx.formParam("id")));
        inquiry.setLength(Double.parseDouble(ctx.formParam("length")));
        inquiry.setWidth(Double.parseDouble(ctx.formParam("width")));
        inquiry.setStatus(ctx.formParam("status"));
        inquiry.setSpecialRequest(Boolean.parseBoolean(ctx.formParam("specialRequest")));

        // Tilknyt sælger, hvis salesmanId er givet
        String salesmanIdParam = ctx.formParam("salesmanId");
        if (salesmanIdParam != null) {
            int salesmanId = Integer.parseInt(salesmanIdParam);
            Salesman salesman = new Salesman();
            salesman.setId(salesmanId);
            inquiry.setAssignedSalesman(salesman);
        }

        return inquiry;
    }

    // Mapper værdier fra en ResultSet til et Inquiry objekt
    public static Inquiry mapInquiry(ResultSet resultSet) {
        try {
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

            // Returnerer et Inquiry-objekt med data fra ResultSet
            return new Inquiry(
                    resultSet.getInt("inquiries.id"),
                    customer,
                    assignedSalesman,
                    resultSet.getBoolean("inquiries.email_sent"),
                    resultSet.getString("inquiries.status"),
                    resultSet.getDate("inquiries.created_date"),
                    resultSet.getDouble("inquiries.length"),
                    resultSet.getDouble("inquiries.width"),
                    resultSet.getBoolean("inquiries.special_request")
            );

        } catch (SQLException e) {
            throw new RuntimeException("Fejl under mapping af Inquiry fra ResultSet", e);
        }
    }
}
