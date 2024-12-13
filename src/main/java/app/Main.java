package app;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import app.config.ThymeleafConfig;
import app.controllers.CustomerController;
import app.controllers.DatabaseController;
import app.controllers.InquiryController;
import app.controllers.CarportController;

public class Main {
    public static void main(String[] args) {
        // Initialize DatabaseController
        DatabaseController dbController = new DatabaseController();
        dbController.initialize(); // Initializes the connection pool

        // Initialize controllers
        CustomerController customerController = new CustomerController(dbController);
        InquiryController inquiryController = new InquiryController(dbController);
        CarportController carportController = new CarportController();

        // Start Javalin server
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH); // Serve static files
        }).start(8080);

        // Register routes
        carportController.registerRoutes(app);

        // Define Thymeleaf HTML routes
        app.get("/", ctx -> ctx.html(renderThymeleaf("index", new Context())));
        app.get("/customer", ctx -> ctx.html(renderThymeleaf("customer", new Context())));
        app.get("/order-status", ctx -> ctx.html(renderThymeleaf("order-status", new Context())));
        app.get("/payment", ctx -> ctx.html(renderThymeleaf("payment", new Context())));

        // Define API routes
        app.post("/api/create-customer", customerController::createProfile);
        app.post("/api/login", customerController::login);
        app.put("/api/update-customer", customerController::updateProfile);

        app.get("/api/order-status/{customerId}", inquiryController::getOrderStatus);

        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dbController.close(); // Close the database connection pool on shutdown
        }));
    }

    // Helper method for rendering Thymeleaf templates
    private static String renderThymeleaf(String template, Context context) {
        TemplateEngine engine = ThymeleafConfig.templateEngine();
        return engine.process(template, context);
    }
}
