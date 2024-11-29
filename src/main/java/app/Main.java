package app;

import app.Services.InquiryService;
import app.Services.PriceCalculatorService;
import app.config.Customer;
import app.config.Inquiry;
import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.DatabaseController;
import app.controllers.InquiryController;
import app.utils.Scrapper;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public"); // Til CSS og JS
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Routing
        app.get("/", ctx -> ctx.render("index.html"));
        app.get("/test", ctx -> ctx.render("payment.html"));

        InquiryController inquiryController = new InquiryController(app);
        inquiryController.registerRoutes(app);
    }




}
