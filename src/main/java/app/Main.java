package app;

import app.Services.PriceCalculatorService;
import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.DatabaseController;
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

        // Database controller
        DatabaseController dbController = new DatabaseController();
        dbController.initialize();

        // Routing
        app.get("/", ctx -> ctx.render("index.html"));
        app.get("/test", ctx -> ctx.render("payment.html"));

        // Send inquiry-formular
        app.get("/send-inquiry", ctx -> ctx.render("send-inquiry.html", Map.of(
                "widthOptions", generateOptions(240, 600, 30),
                "lengthOptions", generateOptions(240, 780, 30),
                "shedOptions", generateOptions(210, 720, 30),
                "heightOptions", generateOptions(150, 690, 30)
        )));

        // Håndtering af forespørgsel
        app.post("/submit-request", ctx -> {
            String name = ctx.formParam("name");
            String address = ctx.formParam("address");
            String width = ctx.formParam("width");
            String length = ctx.formParam("length");

            ctx.render("inquiry-confirmation.html");
        });

        // Test af scrapper og priskalkulator
       /* Scrapper scrapper = new Scrapper();
        PriceCalculatorService pcs = new PriceCalculatorService(scrapper);
        System.out.println(pcs.generatePrices("carport"));*/
    }

    // Hjælpemetode til at generere muligheder for dropdown
    private static String[] generateOptions(int start, int end, int step) {
        return IntStream.rangeClosed(start, end)
                .filter(i -> i % step == 0)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);
    }
}
