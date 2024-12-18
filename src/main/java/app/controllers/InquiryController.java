package app.controllers;

import app.models.Material;
import app.models.PartsList;
import app.services.*;
import app.models.Customer;
import app.models.Inquiry;
import app.persistence.InquiryMapper;
import app.utils.RequestParser;
import app.utils.SessionUtils;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.models.Salesman;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class InquiryController {

    private InquiryService inquiryService;
    private EmailService emailService;
    private DatabaseController dbController;
    private SalesmanService salesmanService;
    private InquiryMapper inquiryMapper;
    private RequestParser requestParser;
    private CustomerService customerService;

    // Konstruktør for at initialisere controlleren med nødvendige services og mapper
    public InquiryController(InquiryService inquiryService, SalesmanService salesmanService, RequestParser requestParser, EmailService emailService, CustomerService customerService, DatabaseController dbController) {
        this.salesmanService = salesmanService;
        this.inquiryService = inquiryService;
        this.requestParser = requestParser;
        this.emailService = emailService;
        this.dbController = dbController;
        this.customerService = customerService;
    }

    // Registrerer javalin ruter for forespørgsler
    public void registerRoutes(Javalin app) {
        // Route for at vise forespørgselssiden med dropdowns
        app.get("/send-inquiry", ctx -> ctx.render("send-inquiry.html", inquiryService.generateDropdownOptions()));
        app.get("/edit-inquiry", ctx -> ctx.render("edit-inquiry.html", inquiryService.generateDropdownOptions()));
        app.get("/about-us", ctx -> ctx.render("about-us.html"));

        // Route for at indsende en ny forespørgsel
        app.post("/submit-inquiry", this::submitInquiry);
        // Vis alle forespørgsler uden sælger
        app.get("/unassigned-inquiries", this::showUnassignedInquiries);
        // Load sælgerportalen
        app.get("/sales-portal", this::loadSellerPortal);
        // Route for at tildele en sælger til en forespørgsel
        app.post("/assign-salesman", this::assignSalesmanToInquiry);
        // Vis alle forespørgsler
        app.get("/inquiries", this::showAllInquiries);
        // Route for at vise redigeringsformularen for en forespørgsel
        app.get("/show-edit-inquiry-form", this::showEditInquiryForm);
        // Route for at redigere en forespørgsel
        app.post("/edit-inquiry", this::editInquiry);
        // Vis kundeinformation baseret på kunde-id
        app.get("/customer-info/{id}", this::showCustomerInfo);

        // Route for at slette en forespørgsel
        app.post("/delete-inquiry", this::deleteInquiry);
    }

    // Tjekker om der er korrekt session data og viser sælgerportalen
    public void loadSellerPortal(Context ctx) {
        SessionUtils.ensureAdminAccess(ctx); // Tjekker session for admin rettigheder
        ctx.render("sales-portal.html");
    }

    // Vist kundeinfo baseret på kunde-id
    public void showCustomerInfo(Context ctx) {
        String idParam = ctx.pathParam("id");
        try {
            int customerId = Integer.parseInt(idParam);
            Customer customer = customerService.getCustomerById(customerId);
            ctx.render("customer-info.html", Map.of("customer", customer));
        } catch (NumberFormatException e) {
            ctx.status(400).result("Ugyldigt kunde-ID");
        }
    }

    // Vist alle forespørgsler
    public void showAllInquiries(Context ctx) {
        SessionUtils.ensureAdminAccess(ctx); // Tjekker session for admin rettigheder
        List<Inquiry> inquiries = inquiryService.getInquiriesFromDatabase();

        System.out.println("Antal forespørgsler: " + inquiries.size());
        System.out.println(inquiries);
        ctx.render("inquiries.html", Map.of("inquiries", inquiries));
    }

    // Vist dropdown af alle sælgere
    public List<Salesman> showSalesmenDropdown() {
        List<Salesman> salesmen = salesmanService.getAllSalesmen();
        return salesmen;
    }

    // Tildel en sælger til en forespørgsel
    public void assignSalesmanToInquiry(Context ctx) {
        showSalesmenDropdown();

        int inquiryID = Integer.parseInt(ctx.formParam("inquiryId"));
        int salesmanID = Integer.parseInt(ctx.formParam("salesmanId"));

        inquiryService.assignSalesmanToInquiry(inquiryID, salesmanID);
        ctx.redirect("/unassigned-inquiries");
    }

    // Vist alle forespørgsler uden en sælger tilknyttet
    public void showUnassignedInquiries(Context ctx) {
        SessionUtils.ensureAdminAccess(ctx); // Tjekker session for admin rettigheder
        List<Inquiry> inquiries = inquiryService.getInquiriesFromDatabase();

        // Filtrér forespørgsler, der ikke har en sælger tilknyttet
        List<Inquiry> unassignedInquiries = inquiries.stream().filter(inquiry -> !inquiryService.hasSalesmanAssigned(inquiry.getId())).toList();
        List<Salesman> salesmen = salesmanService.getAllSalesmen();

        // Render kun de unassigned inquiries
        ctx.render("unassigned-inquiries.html", Map.of("inquiries", unassignedInquiries, "salesmen", salesmen));
    }

    // Behandl indsending af ny forespørgsel
    public void submitInquiry(Context ctx) {
        try {
            // Hent og valider data fra formularen
            Customer customer = requestParser.parseCustomerFromRequest(ctx);
            Inquiry inquiry = requestParser.parseInquiryFromRequest(ctx, customer);

            // Gem kunden og forespørgslen
            inquiryService.saveInquiryWithCustomer(inquiry, customer);
            // Render bekræftelse
            ctx.render("inquiry-confirmation.html", Map.of("customerName", customer.getName(), "carportLength", inquiry.getCarportLength(), "carportWidth", inquiry.getCarportWidth(), "shedLength", inquiry.getShedLength() != null ? inquiry.getShedLength() : "Ingen", "shedWidth", inquiry.getShedWidth() != null ? inquiry.getShedWidth() : "Ingen", "comments", inquiry.getComments() != null ? inquiry.getComments() : "Ingen", "status", inquiry.getStatus()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Ugyldig input: " + e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Forespørgslen blev ikke gemt korrekt.");
            e.printStackTrace();
        }
    }

    // Behandl redigering af forespørgsel
    public void editInquiry(Context ctx) {
        try {
            // Hent parametre fra formularen og opret et Inquiry-objekt
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

            // Tegning og salgspris
            String tegning = ctx.formParam("svgOutput");
            Double salesPrice = requestParser.parseDouble(ctx.formParam("calculatedSellingPrice"));
            inquiry.setSalesPrice(salesPrice);

            // Opdater forespørgslen i databasen
            inquiryService.updateInquiryInDatabase(inquiry);

            Customer customer = inquiryService.getCustomerByInquiryId(inquiry.getId());
            String recipient = customer.getEmail();
            emailService.sendCustomerInquiryEmail(customer, inquiry, recipient, tegning);
            emailService.saveEmailsToDatabase(inquiry, customer, dbController, tegning);

            // Vis alle forespørgsler igen
            showAllInquiries(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Vis formularen til at redigere en forespørgsel
    public void showEditInquiryForm(Context ctx) {
        int inquiryId = Integer.parseInt(ctx.queryParam("id"));

        Inquiry inquiry = inquiryService.getInquiryById(inquiryId);
        inquiryService.getCustomerByInquiryId(inquiryId);

        // Opret en tegning af carporten
        CarportSVG carportSVG = new CarportSVG(inquiry.getCarportWidth() != null ? inquiry.getCarportWidth().intValue() : 0, inquiry.getCarportLength() != null ? inquiry.getCarportLength().intValue() : 0, inquiry.getShedWidth() != null ? inquiry.getShedWidth().intValue() : 0, inquiry.getShedLength() != null ? inquiry.getShedLength().intValue() : 0);
        String svgOutPut = carportSVG.generateSVG();

        // Opret en stykliste og beregn totalpris
        PartsListGenerator partsListGenerator = new PartsListGenerator(inquiry.getCarportLength(), inquiry.getCarportWidth(), 500);
        PartsList partsList = partsListGenerator.getPartsList();
        BigDecimal totalCost = partsList.getTotalCost();

        List<Material> materials = partsList.getMaterials();

        // Render redigeringsformularen
        ctx.render("edit-inquiry.html", Map.of("inquiry", inquiry, "svgOutput", svgOutPut, "partsList", materials, "totalPrice", totalCost));
    }

    // Slet en forespørgsel
    public void deleteInquiry(Context ctx) {
        String idParam = ctx.formParam("id");

        if (idParam != null && !idParam.isEmpty()) {
            int id = Integer.parseInt(idParam);
            boolean isDeleted = inquiryService.deleteInquiryById(id);

            if (isDeleted) {
                ctx.redirect("/inquiries");
            } else {
                ctx.status(500).result("Fejl ved sletning af forespørgsel.");
            }
        } else {
            ctx.status(400).result("ID mangler i forespørgslen.");
        }
    }
}
