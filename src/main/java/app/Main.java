package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.AdminController;
import app.controllers.DatabaseController;
import app.controllers.StripePayment;
import app.models.Product;
import app.services.PriceFinder;
import app.utils.Scrapper;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.List;

public class Main {
    public static void main(String[] args)
    {
        // Initializing Javalin and Jetty webserver

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler ->  handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);


        //Db loader:
        DatabaseController dbController = new DatabaseController();
        dbController.initialize();

        // Opsætning af routes
        StripePayment.registerRoutes(app);
        AdminController.registerRoutes(app);

        app.get("/", ctx ->  ctx.render("index.html"));
        app.get("/test", ctx -> ctx.render("payment.html"));

        //Test af scrapper:
        PriceFinder priceFinder = new PriceFinder();

        // Søg efter priser for et produkt
        String searchTerm = "Spærtræ";
        List<Product> products = priceFinder.findPrices(searchTerm);

        // Udskriv resultaterne
        for (Product product : products) {
            System.out.println(product);
        }


    }
}