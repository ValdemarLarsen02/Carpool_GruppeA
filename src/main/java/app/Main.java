package app;

import app.Services.EmailService;
import app.Services.InquiryService;
import app.Services.PriceCalculatorService;
import app.Services.SalesmanService;
import app.config.Customer;
import app.config.Inquiry;
import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.DatabaseController;
import app.controllers.InquiryController;
import app.utils.Scrapper;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.Date;


public class Main {
    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public"); // Til CSS og JS
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(8080);

        InquiryService inquiryService = new InquiryService();
        SalesmanService salesmanService = new SalesmanService();
        DatabaseController dbController = new DatabaseController();
        Customer customer = new Customer("Micke", "Micke.dengaard@icloud.com", 11111111, "Murergade 7", "Helsingør", 3000);
        int id = 1;
        int customerId = 101;
        Integer salesmanId = 202; // Kan være null, hvis ingen sælger er tilknyttet
        Boolean emailSent = false; // Indikerer, at e-mail endnu ikke er sendt
        String status = "Pending"; // Eksempelstatus
        Date orderDate = new Date(); // Brug den aktuelle dato og tid
        Double carportLength = 6.0; // 6 meter
        Double carportWidth = 3.0;  // 3 meter
        Double shedLength = 2.0;    // 2 meter
        Double shedWidth = 1.5;     // 1,5 meter
        String comments = "Testforespørgsel for carport med skur";

        // Opret en Inquiry-instans
        Inquiry testInquiry = new Inquiry(id, customerId, salesmanId, emailSent, status, orderDate,
                carportLength, carportWidth, shedLength, shedWidth, comments);
        // Routing
        app.get("/", ctx -> ctx.render("index.html"));
        app.get("/test", ctx -> ctx.render("payment.html"));

        InquiryController inquiryController = new InquiryController(inquiryService, salesmanService);
        inquiryController.registerRoutes(app);

        EmailService emailService = new EmailService(dbController);
        try {
            emailService.sendCustomerInquiryEmail(customer, testInquiry);
        } catch (Exception e) {
            e.printStackTrace();
        }

        emailService.saveEmailsToDatabase(testInquiry, customer, dbController);
    }

}
