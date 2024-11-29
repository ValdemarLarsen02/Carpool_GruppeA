package app;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import app.config.ThymeleafConfig;
import app.controllers.CustomerController;
import app.controllers.DatabaseController;
import app.controllers.InquiryController;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {
        // Initialize database connection
        String dbUrl = "jdbc:postgresql://<your-database-host>/<your-database-name>";
        String dbUser = "<your-username>";
        String dbPassword = "<your-password>";
        Connection connection;

        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (Exception e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            return;
        }

        // Initialize controllers
        DatabaseController dbController = new DatabaseController(connection);
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
        app.post("/api/create-customer", customerController::createCustomer);
        app.post("/api/login", customerController::loginCustomer);
        app.put("/api/update-customer", customerController::updateCustomer);

        app.get("/api/order-status/:customerId", inquiryController::getOrderStatus);
    }
}
