package app.utils;

import app.config.Customer;
import app.config.Inquiry;
import io.javalin.http.Context;

public class RequestParser {

    private Double parseNullableDouble(String param) {
        return param != null && !param.isEmpty() ? Double.parseDouble(param) : null;
    }

    public Integer parseNullableInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Integer.parseInt(value);
    }

    public Double parseDouble(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Double.parseDouble(value);
    }

    public Boolean parseNullableBoolean(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }

    public Customer parseCustomerFromRequest(Context ctx) {
        String name = ctx.formParam("name");
        String email = ctx.formParam("email");
        int phone = Integer.parseInt(ctx.formParam("phone"));
        String address = ctx.formParam("address");
        String city = ctx.formParam("city");
        int zipcode = Integer.parseInt(ctx.formParam("zipcode"));

        if (name == null || name.isBlank() || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Navn og email skal udfyldes");
        }

        return new Customer(name, email, phone, address, city, zipcode);
    }

    public Inquiry parseInquiryFromRequest(Context ctx, Customer customer) {
        Double carportLength = parseNullableDouble(ctx.formParam("carportLength"));
        Double carportWidth = parseNullableDouble(ctx.formParam("carportWidth"));
        Double shedLength = parseNullableDouble(ctx.formParam("shedLength"));
        Double shedWidth = parseNullableDouble(ctx.formParam("shedWidth"));
        String comments = ctx.formParam("comments");

        Inquiry inquiry = new Inquiry();
        inquiry.setCustomerId(customer.getId());
        inquiry.setCarportLength(carportLength);
        inquiry.setCarportWidth(carportWidth);
        inquiry.setShedLength(shedLength);
        inquiry.setShedWidth(shedWidth);
        inquiry.setComments(comments);
        inquiry.setStatus("Under behandling");
        inquiry.setOrderDate(new java.util.Date());

        return inquiry;
    }

}
