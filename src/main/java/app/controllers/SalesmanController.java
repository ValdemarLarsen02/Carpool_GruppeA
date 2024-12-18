package app.controllers;

import app.services.SalesmanService;
import app.models.Admin;
import app.models.Salesman;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class SalesmanController {

    private SalesmanService salesmanService;
    private DatabaseController dbController;
    private Admin admin;
    private InquiryController inquiryController;

    // Konstruktør, som initialiserer de nødvendige services og controllers
    public SalesmanController(SalesmanService salesmanService, InquiryController inquiryController, Admin admin, DatabaseController dbController) {
        this.salesmanService = salesmanService;
        this.dbController = dbController;
        this.inquiryController = inquiryController;
        this.admin = admin;
    }

    // Registrerer ruter for håndtering af HTTP-anmodninger
    public void registerRoutes(Javalin app) {
        // Route for at vise login-siden for sælgeren
        app.get("/salesman-login", ctx -> {
            List<Salesman> salesmen = inquiryController.showSalesmenDropdown();  // Henter en liste af sælgere
            ctx.attribute("salesmen", salesmen);  // Sender sælgerlisten til HTML-siden
            ctx.render("salesman-login.html");  // Render login-siden
        });

        // Route for at håndtere loginformularen
        app.post("/salesman-login", this::handleSalesmanLogin);

        // Route for at vise oprettelse af sælger-siden
        app.get("/create-salesman", ctx -> ctx.render("/create-salesman.html"));

        // Route for at håndtere oprettelse af sælger
        app.post("/create-salesman", this::createSalesman);

        // Route for at vise en bekræftelsesside for oprettelse af sælger
        app.get("/salesman-creation-confirmation", ctx -> ctx.render("/salesman-creation-confirmation.html"));

        // Route for at logge sælgeren ud
        app.post("/salesman-logout", ctx -> {
            ctx.sessionAttribute("salesman", null);  // Fjerner sælgerens session
            ctx.sessionAttribute("role", null);  // Fjerner sælgerens rolle
            ctx.redirect("/salesman-login");  // Omdirigerer til login-siden
        });
    }

    // Metode til at oprette en sælger
    public void createSalesman(Context ctx) {

        // Henter værdier fra HTTP requesten
        String name = ctx.formParam("name");
        String email = ctx.formParam("email");

        // Kalder service-metoden til at oprette sælger i databasen
        salesmanService.createSalesmanInDatabase(name, email);

        // Omdirigerer til bekræftelsessiden efter oprettelse
        ctx.redirect("/salesman-creation-confirmation");
    }

    // Metode til at håndtere login af sælger
    public void handleSalesmanLogin(Context ctx) {

        // Kalder inquiryControllers metode til at vise en dropdown med sælgere
        inquiryController.showSalesmenDropdown();

        // Henter de indtastede login-værdier
        String selectedSalesman = ctx.formParam("salesmanId");
        String password = ctx.formParam("password");

        // Henter det korrekte password fra admin
        String correctPassword = admin.getPassword();

        // Tjekker om passwordet er korrekt
        if (correctPassword != null && correctPassword.equals(password)) {
            System.out.println("Valgte sælger: " + selectedSalesman);
            // Gemmer sælger og rolle i sessionen, og omdirigerer til salgsportalen
            ctx.sessionAttribute("salesman", selectedSalesman);
            ctx.sessionAttribute("role", "seller");
            ctx.redirect("/sales-portal");
        } else {
            // Returnerer en fejl, hvis passwordet er forkert
            ctx.status(400).result("Forkert kodeord");
        }
    }
}
