package app;

import app.Services.*;
import app.config.Customer;
import app.config.Inquiry;
import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.DatabaseController;
import app.controllers.EmailController;
import app.controllers.InquiryController;
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
        SalesmanService salesmanService = new SalesmanService(errorLogger);




        // Routing
        app.get("/", ctx -> ctx.render("index.html"));
        app.get("/test", ctx -> ctx.render("payment.html"));

        InquiryController inquiryController = new InquiryController(inquiryService, salesmanService, requestParser, emailService, dbController);
        EmailController emailController = new EmailController(emailService, dbController);
        inquiryController.registerRoutes(app);
        emailController.registerRoutes(app);


    }
}
