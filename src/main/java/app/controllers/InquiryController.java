package app.controllers;

import app.Services.InquiryService;
import app.config.Customer;
import app.config.Inquiry;
import app.persistence.InquiryMapper;
import io.javalin.http.Context;
import app.config.Salesman;
import app.Services.EmailService;

import java.util.Date;


public class InquiryController {

    private InquiryService inquiryService;
    private Salesman salesman;
    private EmailService emailService;
    private Customer customer;
    private Date date;


    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    public void editInquiry(Context ctx) {
        try {
            //Mapper data fra HTTP-anmodningen til et Inquiry objekt
            Inquiry inquiry = InquiryMapper.mapFromRequest(ctx);

            //Opdaterer forespørgslen i databasen
            inquiryService.updateInquiryInDatabase(inquiry);

            ctx.status(200).result("Forespørgslen er blevet redigeret.");
        } catch (Exception e) {
            ctx.status(400).result("Fejl under opdateringen af forespørgslen.");
            e.printStackTrace();
        }
    }

    public void submitInquiry(Context ctx) {
        try {

            //Mapper data fra HTTP-anmodningen til Customer og Inquiry objekter
            Customer customer = getCustomerFromRequest(ctx);
            Inquiry inquiry = getInquiryFromRequest(ctx);

            //Send email med forespørgslen
            emailService.sendCustomerInquiryEmail(customer, inquiry);

            ctx.status(200).result("Din forespørgsel er sendt til sælger.");
        } catch (Exception e) {
            ctx.status(400).result("Fejl under afsendelse af forespørgsel.");
            e.printStackTrace();
        }
    }

    private Customer getCustomerFromRequest(Context ctx) {
        String name = ctx.formParam("name");
        String email = ctx.formParam("email");
        int id = Integer.parseInt(ctx.formParam("id"));
        int phone = Integer.parseInt(ctx.formParam("phone"));
        return new Customer(name, email, id, phone);
    }

    private Inquiry getInquiryFromRequest(Context ctx) {
        String dimensions = ctx.formParam("dimensions");
        String materials = ctx.formParam("materials");
        boolean specialRequest = Boolean.parseBoolean(ctx.formParam("specialRequest"));
        String price = ctx.formParam("price");
        Date date = new Date(); // Automatisk tidspunkt for forespørgsel
        return new Inquiry(1, getCustomerFromRequest(ctx), null, true, "Afventer svar fra sælger", date, materials, price, specialRequest);
    }
}


