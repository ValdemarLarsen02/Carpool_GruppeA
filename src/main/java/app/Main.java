package app;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import app.config.ThymeleafConfig;
import app.controllers.CustomerController;
import app.controllers.DatabaseController;
import app.controllers.InquiryController;

public class Main {
    public static void main(String[] args) {
        // Initialize DatabaseController
        DatabaseController dbController = new DatabaseController();
        dbController.initialize(); // Initializes the connection pool

        // Initialize controllers
        CustomerController customerController = new CustomerController(dbController);
        InquiryController inquiryController = new InquiryController(dbController);

        // Initialize Thymeleaf
        TemplateEngine templateEngine = ThymeleafConfig.templateEngine();

        // Start Javalin server
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH); // Serve static files
        }).start(8080);

        // Define HTML routes
        app.get("/", ctx -> {
            Context thymeleafContext = new Context();
            ctx.html(templateEngine.process("index", thymeleafContext));
        });

        app.get("/customer", ctx -> {
            Context thymeleafContext = new Context();
            ctx.html(templateEngine.process("customer", thymeleafContext));
        });

        app.get("/order-status", ctx -> {
            Context thymeleafContext = new Context();
            ctx.html(templateEngine.process("order-status", thymeleafContext));
        });

        app.get("/payment", ctx -> {
            Context thymeleafContext = new Context();
            ctx.html(templateEngine.process("payment", thymeleafContext));
        });

        // Define API routes
        app.post("/api/create-customer", customerController::createProfile); // Updated method name
        app.post("/api/login", customerController::login);                  // Updated method name
        app.put("/api/update-customer", customerController::updateProfile); // Updated method name

        app.get("/api/order-status/{customerId}", inquiryController::getOrderStatus); // Corrected syntax

        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dbController.close(); // Close the database connection pool on shutdown
        }));
    }
}
