package app;


import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.AdminController;
import app.controllers.DatabaseController;
import app.controllers.EmailController;
import app.controllers.InquiryController;
import app.utils.RequestParser;
import app.utils.Scrapper;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.Date;

import app.services.*;

public class Main {
    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public"); // Til CSS og JS
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(8080);
        app.get("/", ctx -> ctx.render("index.html"));
        DatabaseController dbController = new DatabaseController();
        dbController.initialize();

        CustomerService customerService = new CustomerService(dbController);
        InquiryService inquiryService = new InquiryService(customerService, dbController);
        SalesmanService salesmanService = new SalesmanService();
        RequestParser requestParser = new RequestParser();
        EmailService emailService = new EmailService(dbController);
        AdminController.registerRoutes(app);

        // Routing

        app.get("/test", ctx -> ctx.render("payment.html"));

        InquiryController inquiryController = new InquiryController(inquiryService, salesmanService, requestParser, emailService, dbController);
        EmailController emailController = new EmailController(emailService, dbController);
        inquiryController.registerRoutes(app);
        emailController.registerRoutes(app);


        //Test af priceFinder


        PartsListGenerator generator = new PartsListGenerator(540, 600);

        // Udskriv den samlede stykliste
        System.out.println(generator.getPartsList());

    }
}