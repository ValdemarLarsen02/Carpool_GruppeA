package app.persistence;

import app.config.Customer;
import app.config.Inquiry;
import app.config.Salesman;
import io.javalin.http.Context;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InquiryMapper {


    public static Inquiry mapFromRequest(Context ctx) {
        Inquiry inquiry = new Inquiry();

        //Mapper værdierne for formparametre til Inquiry objektet
        inquiry.setId(Integer.parseInt(ctx.formParam("id")));
        inquiry.setDimensions(ctx.formParam("dimensions"));
        inquiry.setMaterials(ctx.formParam("materials"));
        inquiry.setStatus(ctx.formParam("status"));

        //Hvis der er et salesman id i formparametrene, tilknyttes en sælger til Inquiry objektet
        Integer salesmanId = ctx.formParam("salesmanId") != null ? Integer.parseInt(ctx.formParam("salesmanId")) : null;
        if (salesmanId != null) {
            Salesman salesman = new Salesman();
            salesman.setId(salesmanId);
            inquiry.setAssignedSalesman(salesman);

        }

        return inquiry;
    }


    public static Inquiry mapInquiry(ResultSet resultSet) {
        // Mapper Customer
        Customer customer = new Customer();
        try {
            customer.setName(resultSet.getString("customers.name"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            customer.setEmail(resultSet.getString("customers.email"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Mapper Salesman (kan være null)
        Salesman assignedSalesman = null;
        try {
            if (resultSet.getObject("salesmen.id") != null) {
                assignedSalesman = new Salesman();
                assignedSalesman.setId(resultSet.getInt("salesmen.id"));
                assignedSalesman.setName(resultSet.getString("salesmen.name"));
                assignedSalesman.setEmail(resultSet.getString("salesmen.email"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Mapper Inquiry med data fra ResultSet
        try {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

