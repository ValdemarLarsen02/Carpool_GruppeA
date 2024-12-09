package app.controllers;

import app.services.InquiryService;
import app.services.SalesmanService;
import app.config.Customer;
import app.config.Inquiry;
import app.persistence.InquiryMapper;
import app.utils.RequestParser;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.config.Salesman;
import app.services.EmailService;
import app.utils.DropdownOptions;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class InquiryController {

    private InquiryService inquiryService;
    private EmailService emailService;
    private DatabaseController dbController;
    private SalesmanService salesmanService;
    private InquiryMapper inquiryMapper;
    private RequestParser requestParser;

    //Konstruktør
    public InquiryController(InquiryService inquiryService, SalesmanService salesmanService, RequestParser requestParser, EmailService emailService, DatabaseController dbController) {
        this.salesmanService = salesmanService;
        this.inquiryService = inquiryService;
        this.requestParser = requestParser;
        this.emailService = emailService;
        this.dbController = dbController;

    }

    //Registrerer javalin ruter
    public void registerRoutes(Javalin app) {
        //Get rute der generer options til en forespørgsel, samt renderer forespørgselssiden
        app.get("/send-inquiry", ctx -> ctx.render("send-inquiry.html", inquiryService.generateDropdownOptions()));
        app.get("/edit-inquiry", ctx -> ctx.render("edit-inquiry.html", inquiryService.generateDropdownOptions()));

        app.post("/submit-inquiry", this::submitInquiry);
        app.get("/unassigned-inquiries", this::showUnassignedInquiries);
        app.get("/sales-portal", ctx -> ctx.render("sales-portal.html"));
        app.post("/assign-salesman", this::assignSalesmanToInquiry);
        app.get("/inquiries", this::showAllInquiries);
        app.get("/show-edit-inquiry-form", this::showEditInquiryForm);
        app.post("/edit-inquiry", this::editInquiry);
    }

    public void showAllInquiries(Context ctx) {
        List<Inquiry> inquiries = inquiryService.getInquiriesFromDatabase(dbController);

        ctx.render("inquiries.html", Map.of("inquiries", inquiries));
    }

    //Viser alle forespørgsler
    public List<Salesman> showSalesmenDropdown() {
        List<Salesman> salesmen = salesmanService.getAllSalesmen(dbController);

        return salesmen;
    }

    //Tildel sælger til en forespørgsel
    public void assignSalesmanToInquiry(Context ctx) {

        showSalesmenDropdown();

        int inquiryID = Integer.parseInt(ctx.formParam("inquiryId"));
        int salesmanID = Integer.parseInt(ctx.formParam("salesmanId"));

        inquiryService.assignSalesmanToInquiry(inquiryID, salesmanID, dbController);


        ctx.redirect("/unassigned-inquiries");
    }


    //Viser alle forespørgsler uden en sælger tilknyttet
    public void showUnassignedInquiries(Context ctx) {
        List<Inquiry> inquiries = inquiryService.getInquiriesFromDatabase(dbController);

        // Filtrér inquiries, der ikke har en sælger
        List<Inquiry> unassignedInquiries = inquiries.stream().filter(inquiry -> !inquiryService.hasSalesmanAssigned(inquiry.getId(), dbController)).toList();

        List<Salesman> salesmen = salesmanService.getAllSalesmen(dbController);

        // Render kun de unassigned inquiries
        ctx.render("unassigned-inquiries.html", Map.of("inquiries", unassignedInquiries, "salesmen", salesmen));
    }

    //Indsend en ny forespørgsel
    public void submitInquiry(Context ctx) {
        try {
            // Hent og valider data fra formularen
            Customer customer = requestParser.parseCustomerFromRequest(ctx);
            Inquiry inquiry = requestParser.parseInquiryFromRequest(ctx, customer);

            // Gem kunden og forespørgslen via services
            inquiryService.saveInquiryWithCustomer(inquiry, customer);

            // Send en bekræftelsesemail
            emailService.sendCustomerInquiryEmail(customer, inquiry);
            emailService.saveEmailsToDatabase(inquiry, customer, dbController);

            // Render bekræftelsessiden
            ctx.render("inquiry-confirmation.html", Map.of("customerName", customer.getName(), "carportLength", inquiry.getCarportLength(), "carportWidth", inquiry.getCarportWidth(), "shedLength", inquiry.getShedLength() != null ? inquiry.getShedLength() : "Ingen", "shedWidth", inquiry.getShedWidth() != null ? inquiry.getShedWidth() : "Ingen", "comments", inquiry.getComments() != null ? inquiry.getComments() : "Ingen", "status", inquiry.getStatus()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Ugyldig input: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Forespørgslen blev ikke gemt korrekt.");
            e.printStackTrace();
        }
    }

    public void editInquiry(Context ctx) {
        try {
            // Hent parametre fra forespørgslen og opret et Inquiry-objekt
            Inquiry inquiry = new Inquiry();
            inquiry.setId(Integer.parseInt(ctx.formParam("id")));
            inquiry.setSalesmanId(requestParser.parseNullableInteger(ctx.formParam("salesmanId")));
            inquiry.setStatus(ctx.formParam("status"));
            inquiry.setComments(ctx.formParam("comments"));
            inquiry.setCarportLength(requestParser.parseDouble(ctx.formParam("carportLength")));
            inquiry.setCarportWidth(requestParser.parseDouble(ctx.formParam("carportWidth")));
            inquiry.setShedLength(requestParser.parseDouble(ctx.formParam("shedLength")));
            inquiry.setShedWidth(requestParser.parseDouble(ctx.formParam("shedWidth")));
            inquiry.setEmailSent(requestParser.parseNullableBoolean(ctx.formParam("emailSent")));

            // Opdater forespørgslen i databasen
            inquiryService.updateInquiryInDatabase(inquiry, dbController);

            Customer customer = inquiryService.getCustomerByInquiryId(inquiry.getId(), dbController);
            emailService.sendCustomerInquiryEmail(customer, inquiry);
            emailService.saveEmailsToDatabase(inquiry, customer, dbController);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEditInquiryForm(Context ctx) {
        int inquiryId = Integer.parseInt(ctx.queryParam("id"));

        Inquiry inquiry = inquiryService.getInquiryById(inquiryId, dbController);

        ctx.render("edit-inquiry.html", Map.of("inquiry", inquiry));
    }

    //Parser formularparametre som Double
    private static Double parseFormParamAsDouble(String param, String fieldName) {
        try {
            return param != null && !param.isEmpty() ? Double.parseDouble(param) : null; // Returner null hvis param er tom
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " skal være et tal.");
        }
    }

}
