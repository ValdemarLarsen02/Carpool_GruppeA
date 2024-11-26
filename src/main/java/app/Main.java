package app;

import app.Services.InquiryService;
import app.config.*;
import app.controllers.InquiryController;
import app.persistence.InquiryMapper;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import app.controllers.DatabaseController;

import java.sql.ResultSet;
import java.util.Date;

public class Main {
    public static void main(String[] args) {


        // Initializing Javalin and Jetty webserver

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        //database:
        DatabaseController dbController = new DatabaseController();

        dbController.initialize();
        Customer customer = new Customer();
        customer.setId(1);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");

        customer.saveToDatabase(dbController);

        Salesman salesman = null;

        Inquiry inquiry = new Inquiry(1, customer, salesman, false, "Pending", new Date(), "steel, Glass", "200x300x400");
        inquiry.saveToDatabase(dbController);






        // Routing

        app.get("/", ctx -> ctx.render("index.html"));


    }
}