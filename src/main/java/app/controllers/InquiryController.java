package app.controllers;

import app.Services.InquiryService;
import app.config.Customer;
import app.config.Inquiry;
import app.persistence.InquiryMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.config.Salesman;
import app.Services.EmailService;

import java.util.Date;
import java.util.Map;
import java.util.stream.IntStream;

public class InquiryController {

    private InquiryService inquiryService;
    private EmailService emailService;
    private DatabaseController dbController;


    public InquiryController(Javalin app) {
        this.dbController = new DatabaseController();
        dbController.initialize();
    }

    public void registerRoutes(Javalin app){
        app.get("/send-inquiry", ctx -> ctx.render("send-inquiry.html", Map.of(
                "widthOptions", generateOptions(240, 600, 30),
                "lengthOptions", generateOptions(240, 780, 30),
                "shedOptions", generateOptions(210, 720, 30),
                "heightOptions", generateOptions(150, 690, 30)
        )));


        app.post("/submit-inquiry", ctx -> {
            String name = ctx.formParam("name");
            String email = ctx.formParam("email");
            int phone = Integer.parseInt(ctx.formParam("phone"));
            String address = ctx.formParam("address");
            String city = ctx.formParam("city");
            int zipcode = Integer.parseInt(ctx.formParam("zipcode"));
            String message = ctx.formParam("message");
            String lengthparam = ctx.formParam("length");
            String widthparam = ctx.formParam("width");
            boolean specialRequest = Boolean.parseBoolean(ctx.formParam("specialRequest"));

            if(name == null || name.isBlank() || email == null || email.isBlank() || message == null || message.isBlank()) {
                ctx.status(400).result("Navn, email og besked skal udfyldes");
                return;
            }

            double length = 0;
            double width = 0;
            try {
                if (lengthparam != null) {
                    length = Double.parseDouble(lengthparam);
                }
                if (widthparam != null) {
                    width = Double.parseDouble(widthparam);
                }
            } catch (NumberFormatException e) {
                ctx.status(400).result("Du skal vælge længde og bredde");
                return;
            }

            Inquiry inquiry = new Inquiry();
            inquiry.setCustomer(new Customer(name, email, phone, address, city, zipcode));
            inquiry.setSpecialRequest(specialRequest);
            inquiry.setLength(length);
            inquiry.setWidth(width);
            inquiry.setStatus("Under behandling");
            inquiry.setCreatedDate(new java.util.Date());

            try {
                inquiry.saveToDatabase(dbController);
            } catch (Exception e) {
                ctx.status(500).result("Forespørgslen blev ikke gemt i databasen");
                e.printStackTrace();
                return;
            }

            ctx.render("inquiry-confirmation.html");
        });
    }

    public InquiryController(InquiryService inquiryService, EmailService emailService) {
        this.inquiryService = inquiryService;
        this.emailService = emailService;
    }

    public void editInquiry(Context ctx) {
        try {
            // Mapper data fra HTTP-anmodningen til et Inquiry objekt
            Inquiry inquiry = InquiryMapper.mapFromRequest(ctx);

            // Opdaterer forespørgslen i databasen
            inquiryService.updateInquiryInDatabase(inquiry);

            ctx.status(200).result("Forespørgslen er blevet redigeret.");
        } catch (Exception e) {
            ctx.status(400).result("Fejl under opdateringen af forespørgslen.");
            e.printStackTrace();
        }
    }

    public void submitInquiry(Context ctx) {
        try {
            // Mapper data fra HTTP-anmodningen til Customer og Inquiry objekter
            Customer customer = getCustomerFromRequest(ctx);
            Inquiry inquiry = getInquiryFromRequest(ctx, customer);

            // Gem forespørgslen i databasen
            inquiry.saveToDatabase(dbController);

            // Send email med forespørgslen
            emailService.sendCustomerInquiryEmail(customer, inquiry);

            ctx.status(200).result("Din forespørgsel er sendt til sælger.");
        } catch (Exception e) {
            ctx.status(400).result("Fejl under afsendelse af forespørgsel.");
            e.printStackTrace();
        }
    }

    private Customer getCustomerFromRequest(Context ctx) {
        String name = ctx.pathParam("name");
        String email = ctx.formParam("email");
        int phone = Integer.parseInt(ctx.formParam("phone"));
        String address = ctx.formParam("address");
        String city = ctx.formParam("city");
        int zipcode = Integer.parseInt(ctx.formParam("zipcode"));

        return new Customer(name, email, phone, address, city, zipcode);
    }

    private Inquiry getInquiryFromRequest(Context ctx, Customer customer) {
        double length = Double.parseDouble(ctx.formParam("length"));
        double width = Double.parseDouble(ctx.formParam("width"));
        boolean specialRequest = Boolean.parseBoolean(ctx.formParam("specialRequest"));
        Date date = new Date(); // Automatisk tidspunkt for forespørgsel

        return new Inquiry(
                0, // id sættes til 0 eller håndteres af databasen som auto increment
                customer,
                null, // Ingen sælger tildelt endnu
                false, // Email er endnu ikke sendt
                "Afventer svar fra sælger", // Standardstatus
                date,
                length,
                width,
                specialRequest
        );
    }

    private static String[] generateOptions(int start, int end, int step) {
        return IntStream.rangeClosed(start, end)
                .filter(i -> i % step == 0)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);
    }
}
