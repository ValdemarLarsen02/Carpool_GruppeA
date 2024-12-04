package app.controllers;

import app.Services.InquiryService;
import app.Services.SalesmanService;
import app.config.Customer;
import app.config.Inquiry;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.config.Salesman;
import app.Services.EmailService;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class InquiryController {

    private InquiryService inquiryService;
    private EmailService emailService;
    private DatabaseController dbController;
    private SalesmanService salesmanService;

    //Konstruktør
    public InquiryController(InquiryService inquiryService, SalesmanService salesmanService) {
        this.dbController = new DatabaseController();
        this.salesmanService = salesmanService;
        this.inquiryService = inquiryService;
        dbController.initialize();
    }

    public void registerRoutes(Javalin app) {
        app.get("/send-inquiry", ctx -> ctx.render("send-inquiry.html", Map.of(
                "carportWidthOptions", generateOptions(240, 600, 30),
                "carportLengthOptions", generateOptions(240, 780, 30),
                "shedWidthOptions", generateOptions(210, 720, 30),
                "shedLengthOptions", generateOptions(210, 720, 30)
        )));

        app.post("/submit-inquiry", this::submitInquiry);
        app.get("/unassigned-inquiries", this::showUnassignedInquiries);
        app.get("/sales-portal", ctx -> ctx.render("sales-portal.html"));
        app.post("/assign-salesman", this::assignSalesmanToInquiry);
        app.get("/inquiries", this::showAllInquiries);
    }

    public void showAllInquiries(Context ctx) {
        List<Inquiry> inquiries = inquiryService.getInquiriesFromDatabase(dbController);

        ctx.render("inquiries.html", Map.of("inquiries", inquiries));
    }

    public List<Salesman> showSalesmenDropdown() {
        List<Salesman> salesmen = salesmanService.getAllSalesmen(dbController);

        return salesmen;
    }

    public void assignSalesmanToInquiry(Context ctx) {

        showSalesmenDropdown();

        int inquiryID = Integer.parseInt(ctx.formParam("inquiryId"));
        int salesmanID = Integer.parseInt(ctx.formParam("salesmanId"));

        InquiryService inquiryService = new InquiryService();
        inquiryService.assignSalesmanToInquiry(inquiryID, salesmanID, dbController);

        ctx.redirect("/unassigned-inquiries");
    }


    public void showUnassignedInquiries(Context ctx) {
        List<Inquiry> inquiries = inquiryService.getInquiriesFromDatabase(dbController);

        // Filtrér inquiries, der ikke har en sælger
        List<Inquiry> unassignedInquiries = inquiries.stream()
                .filter(inquiry -> !inquiryService.hasSalesmanAssigned(inquiry.getId(), dbController))
                .toList();

        List<Salesman> salesmen = salesmanService.getAllSalesmen(dbController);

        // Render kun de unassigned inquiries
        ctx.render("unassigned-inquiries.html", Map.of("inquiries", unassignedInquiries, "salesmen", salesmen));
    }

    public void submitInquiry(Context ctx) {
        String name = ctx.formParam("name");
        String email = ctx.formParam("email");
        int phone = Integer.parseInt(ctx.formParam("phone"));
        String address = ctx.formParam("address");
        String city = ctx.formParam("city");
        int zipcode = Integer.parseInt(ctx.formParam("zipcode"));
        String comments = ctx.formParam("comments");

        if (name == null || name.isBlank() || email == null || email.isBlank()) {
            ctx.status(400).result("Navn og email skal udfyldes");
            return;
        }

        Double carportLength = parseFormParamAsDouble(ctx.formParam("carportLength"), "Carport længde");
        Double carportWidth = parseFormParamAsDouble(ctx.formParam("carportWidth"), "Carport bredde");
        Double shedLength = parseFormParamAsDouble(ctx.formParam("shedLength"), "Skurlængde");
        Double shedWidth = parseFormParamAsDouble(ctx.formParam("shedWidth"), "Skurbredde");

        // Opret en kunde
        Customer customer = new Customer(name, email, phone, address, city, zipcode);

        try {
            // Gem kunden i databasen og få det genererede id
            int customerId = customer.saveToDatabase(dbController);

            // Opret og gem forespørgslen
            Inquiry inquiry = new Inquiry();
            inquiry.setCustomerId(customerId); // Brug det genererede ID
            inquiry.setCarportLength(carportLength);
            inquiry.setCarportWidth(carportWidth);
            inquiry.setShedLength(shedLength);
            inquiry.setShedWidth(shedWidth);
            inquiry.setComments(comments);
            inquiry.setStatus("Under behandling");
            inquiry.setOrderDate(new java.util.Date());

            inquiry.saveToDatabase(dbController);

            // Render bekræftelsessiden
            ctx.render("inquiry-confirmation.html", Map.of(
                    "customerName", name,
                    "carportLength", carportLength,
                    "carportWidth", carportWidth,
                    "shedLength", shedLength != null ? shedLength : "Ingen",
                    "shedWidth", shedWidth != null ? shedWidth : "Ingen",
                    "comments", comments != null ? comments : "Ingen",
                    "status", "Under behandling"
            ));
        } catch (Exception e) {
            ctx.status(500).result("Forespørgslen blev ikke gemt i databasen");
            e.printStackTrace();
        }
    }


    /*public void editInquiry(Context ctx) {
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
    }*/

    private static Double parseFormParamAsDouble(String param, String fieldName) {
        try {
            return param != null && !param.isEmpty() ? Double.parseDouble(param) : null; // Returner null hvis param er tom
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " skal være et tal.");
        }
    }


    private static String[] generateOptions(int start, int end, int step) {
        return IntStream.rangeClosed(start, end)
                .filter(i -> i % step == 0)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);
    }
}
