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
        // Opret en DatabaseController, som håndterer forbindelsen til databasen
        DatabaseController dbController = new DatabaseController();
        dbController.initialize(); // Initialiserer connection poolen for databasen

        // Opret controllere til at håndtere forskellige funktionaliteter
        CustomerController customerController = new CustomerController(dbController);
        InquiryController inquiryController = new InquiryController(dbController);


        // Start Javalin serveren
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH); // Finder filer i "resources/public"
        }).start(8080);

        // Thymeleaf-ruter til at vise HTML-sider
        // Disse ruter viser HTML-sider til brugeren
        app.get("/", ctx -> ctx.html(renderThymeleaf("index", new Context())));
        app.get("/customer", ctx -> ctx.html(renderThymeleaf("customer", new Context())));
        app.get("/order-status", ctx -> ctx.html(renderThymeleaf("order-status", new Context())));
        app.get("/payment", ctx -> ctx.html(renderThymeleaf("payment", new Context())));

        // API-ruter til at håndtere backend-funktionalitet
        // Disse ruter håndterer klientens anmodninger og udfører databaseoperationer
        app.post("/api/create-customer", customerController::createProfile);
        app.post("/api/login", customerController::login);
        app.put("/api/update-customer", customerController::updateProfile);
        // Forespørgsler på ordrestatus
        app.get("/api/order-status/{customerId}", inquiryController::getOrderStatus);

        // Opret en shutdown-hook for at sikre en ordentlig lukning af ressourcer
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dbController.close(); // Luk databaseforbindelserne, når serveren stoppes
        }));
    }

    /**
     * En hjælpefunktion til at rendere Thymeleaf-skabeloner.
     * Denne funktion bruges til at generere HTML-sider baseret på Thymeleaf-skabeloner.
     *
     * @param template Navn på Thymeleaf-skabelonen (uden filtypen .html)
     * @param context  Context-objektet, der bruges til at levere data til skabelonen
     * @return Renderet HTML som en streng
     */
    private static String renderThymeleaf(String template, Context context) {
        TemplateEngine engine = ThymeleafConfig.templateEngine();
        return engine.process(template, context);
    }
}
