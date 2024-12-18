package app.controllers;

import app.models.Material;
import app.models.PartsList;
import app.services.*;
import app.config.Customer;
import app.config.Email;
import app.config.Inquiry;
import app.persistence.InquiryMapper;
import app.utils.RequestParser;
import app.utils.SessionUtils;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.config.Salesman;
import app.utils.DropdownOptions;

import java.math.BigDecimal;
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
    private CustomerService customerService;

    //Konstruktør
    public InquiryController(InquiryService inquiryService, SalesmanService salesmanService, RequestParser requestParser, EmailService emailService, CustomerService customerService, DatabaseController dbController) {
        this.salesmanService = salesmanService;
        this.inquiryService = inquiryService;
        this.requestParser = requestParser;
        this.emailService = emailService;
        this.dbController = dbController;
        this.customerService = customerService;

    }

    //Registrerer javalin ruter
    public void registerRoutes(Javalin app) {
        //Get rute der generer options til en forespørgsel, samt renderer forespørgselssiden
        app.get("/send-inquiry", ctx -> ctx.render("send-inquiry.html", inquiryService.generateDropdownOptions()));
        app.get("/edit-inquiry", ctx -> ctx.render("edit-inquiry.html", inquiryService.generateDropdownOptions()));
        app.get("/about-us", ctx -> ctx.render("about-us.html"));

        app.post("/submit-inquiry", this::submitInquiry);
        app.get("/unassigned-inquiries", this::showUnassignedInquiries);
        app.get("/sales-portal", this::loadSellerPortal);
        app.post("/assign-salesman", this::assignSalesmanToInquiry);
        app.get("/inquiries", this::showAllInquiries);
        app.get("/show-edit-inquiry-form", this::showEditInquiryForm);
        app.post("/edit-inquiry", this::editInquiry);
        app.get("/customer-info/{id}", this::showCustomerInfo);

        //Slet af forespørgelser
        app.post("/delete-inquiry", this::deleteInquiry);


    }



    public void loadSellerPortal(Context ctx) {
        SessionUtils.ensureAdminAccess(ctx); //Tjekker der er sat korrekt session data.
        ctx.render("sales-portal.html");
    }

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




    public void showAllInquiries(Context ctx) {
        SessionUtils.ensureAdminAccess(ctx); //Tjekker der er sat korrekt session data.
        List<Inquiry> inquiries = inquiryService.getInquiriesFromDatabase();


        System.out.println("Antal forespørgsler: " + inquiries.size());
        System.out.println(inquiries);
        ctx.render("inquiries.html", Map.of("inquiries", inquiries));
    }

    //Viser alle forespørgsler
    public List<Salesman> showSalesmenDropdown() {
        List<Salesman> salesmen = salesmanService.getAllSalesmen();

        return salesmen;
    }

    //Tildel sælger til en forespørgsel
    public void assignSalesmanToInquiry(Context ctx) {

        showSalesmenDropdown();

        int inquiryID = Integer.parseInt(ctx.formParam("inquiryId"));
        int salesmanID = Integer.parseInt(ctx.formParam("salesmanId"));

        inquiryService.assignSalesmanToInquiry(inquiryID, salesmanID);


        ctx.redirect("/unassigned-inquiries");
    }


    //Viser alle forespørgsler uden en sælger tilknyttet
    public void showUnassignedInquiries(Context ctx) {
        SessionUtils.ensureAdminAccess(ctx); //Tjekker der er sat korrekt session data.
        List<Inquiry> inquiries = inquiryService.getInquiriesFromDatabase();

        // Filtrér inquiries, der ikke har en sælger
        List<Inquiry> unassignedInquiries = inquiries.stream().filter(inquiry -> !inquiryService.hasSalesmanAssigned(inquiry.getId())).toList();

        List<Salesman> salesmen = salesmanService.getAllSalesmen();

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
            System.out.println(inquiry);
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



            //henter vores tegning:
            String tegning = ctx.formParam("svgOutput");



            //henter evt salgspris hvis udregnet
            Double salesPrice = requestParser.parseDouble(ctx.formParam("calculatedSellingPrice"));

            inquiry.setSalesPrice(salesPrice); // Sætter pris til vores objekt.


            // Opdater forespørgslen i databasen
            inquiryService.updateInquiryInDatabase(inquiry);

            Customer customer = inquiryService.getCustomerByInquiryId(inquiry.getId());
            String recipient = customer.getEmail();
            emailService.sendCustomerInquiryEmail(customer, inquiry, recipient, tegning);
            emailService.saveEmailsToDatabase(inquiry, customer, dbController, tegning);

            showAllInquiries(ctx); //viser vores liste igen her.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEditInquiryForm(Context ctx) {
        int inquiryId = Integer.parseInt(ctx.queryParam("id"));

        Inquiry inquiry = inquiryService.getInquiryById(inquiryId);
        inquiryService.getCustomerByInquiryId(inquiryId);


        System.out.println(inquiry);


        //Opretter en tegning af valgte carport
        CarportSVG carportSVG = new CarportSVG(
                inquiry.getCarportWidth() != null ? inquiry.getCarportWidth().intValue() : 0,
                inquiry.getCarportLength() != null ? inquiry.getCarportLength().intValue() : 0,
                inquiry.getShedWidth() != null ? inquiry.getShedWidth().intValue() : 0,
                inquiry.getShedLength() != null ? inquiry.getShedLength().intValue() : 0
        );

        String svgOutPut = carportSVG.generateSVG();


        //Opretter en stykliste til valgte carport
        PartsListGenerator partsListGenerator = new PartsListGenerator(inquiry.getCarportLength(), inquiry.getCarportWidth(), 500);
        PartsList partsList = partsListGenerator.getPartsList(); // Hent stykliste
        BigDecimal totalCost = partsList.getTotalCost();         // Hent totalpris

        System.out.println(partsList);


        List<Material> materials = partsListGenerator.getPartsList().getMaterials(); // henter listen med vores matrialer

        ctx.render("edit-inquiry.html", Map.of(
                "inquiry", inquiry,           // Din eksisterende inquiry-objekt
                "svgOutput", svgOutPut,       // SVG som en string
                "partsList", materials, // Stykliste
                "totalPrice", totalCost // Samlet pris (forudsat en metode til dette)
        ));


    }


    public void deleteInquiry(Context ctx) {
        // Henter vores id fra vores form.
        String idParam = ctx.formParam("id");

        // Validering på om id findes?
        if (idParam != null && !idParam.isEmpty()) {
            System.out.println("Sletning af forespørgsel med ID: " + idParam);

            // Laves om til korrekt datatype.
            int id = Integer.parseInt(idParam);


            boolean isDeleted = inquiryService.deleteInquiryById(id);

            if (isDeleted) {
                System.out.println("Forespørgsel blev slettet, redirecter...");
                // Redirect til siden med alle forespørgsler
                ctx.redirect("/inquiries");
            } else {
                System.out.println("Sletning fejlede.");
                ctx.status(500).result("Fejl ved sletning af forespørgsel.");
            }


        } else {
            System.out.println("ID mangler i slette-anmodningen.");
            ctx.status(400).result("ID mangler i forespørgslen.");
        }
    }



}
