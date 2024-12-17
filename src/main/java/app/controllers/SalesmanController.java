package app.controllers;

import app.services.SalesmanService;
import app.config.Admin;
import app.config.Salesman;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class SalesmanController {

    private SalesmanService salesmanService;
    private DatabaseController dbController;
    private Admin admin;
    private InquiryController inquiryController;

    public SalesmanController(SalesmanService salesmanService, InquiryController inquiryController, Admin admin, DatabaseController dbController) {
        this.salesmanService = salesmanService;
        this.dbController = dbController;
        this.inquiryController = inquiryController;
        this.admin = admin;

    }


    public void registerRoutes(Javalin app) {
        app.get("/salesman-login", ctx -> {
            List<Salesman> salesmen = inquiryController.showSalesmenDropdown();
            ctx.attribute("salesmen", salesmen);  // Send sælgerlisten til HTML-siden
            ctx.render("salesman-login.html");
        });

        app.post("/salesman-login", this::handleSalesmanLogin);
        app.get("/create-salesman", ctx -> ctx.render("/create-salesman.html"));
        app.post("/create-salesman", this::createSalesman);
        app.get("/salesman-creation-confirmation", ctx -> ctx.render("/salesman-creation-confirmation.html"));

        app.post("/salesman-logout", ctx -> {
            ctx.sessionAttribute("salesman", null);
            ctx.sessionAttribute("role", null);
            ctx.redirect("/salesman-login"); // Redirecter til login-siden
        });

    }

    public void createSalesman(Context ctx) {
        String name = ctx.formParam("name");
        String email = ctx.formParam("email");

        salesmanService.createSalesmanInDatabase(name, email);

        ctx.redirect("/salesman-creation-confirmation");

    }

    public void handleSalesmanLogin(Context ctx) {

        inquiryController.showSalesmenDropdown();
        String selectedSalesman = ctx.formParam("salesmanId");
        String password = ctx.formParam("password");

        String correctPassword = admin.getPassword();

        if (correctPassword != null && correctPassword.equals(password)) {

            System.out.println("Valgte sælger: " + selectedSalesman);
            ctx.sessionAttribute("salesman", selectedSalesman);
            ctx.sessionAttribute("role", "seller");
            ctx.redirect("/sales-portal");
        } else {
            ctx.status(400).result("Forkert kodeord");
        }
    }
}
