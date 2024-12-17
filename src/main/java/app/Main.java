package app;

import app.services.*;
import app.config.*;
import app.controllers.*;
import app.utils.RequestParser;
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
        DatabaseController dbController = new DatabaseController();
        dbController.initialize();


        RequestParser requestParser = new RequestParser();
        ErrorLoggerService errorLogger = new ErrorLoggerService(dbController);
        CustomerService customerService = new CustomerService(dbController, errorLogger);
        InquiryService inquiryService = new InquiryService(customerService, dbController, errorLogger);
        EmailService emailService = new EmailService(dbController, errorLogger);
        SalesmanService salesmanService = new SalesmanService(errorLogger, dbController);
        Admin admin = new Admin(errorLogger, dbController);
        AdminController adminController = new AdminController(admin, dbController);




        // Routing
        //app.get("/", ctx -> ctx.render("index.html"));

        app.get("/", ctx -> ctx.render("carport_oversigt.html"));


        app.get("/test", ctx -> ctx.render("payment.html"));

        InquiryController inquiryController = new InquiryController(inquiryService, salesmanService, requestParser, emailService, customerService, dbController);
        EmailController emailController = new EmailController(emailService, dbController);
        SalesmanController salesmanController = new SalesmanController(salesmanService, inquiryController, admin, dbController);
        salesmanController.registerRoutes(app);
        inquiryController.registerRoutes(app);
        emailController.registerRoutes(app);
        adminController.registerRoutes(app);


    }
}
