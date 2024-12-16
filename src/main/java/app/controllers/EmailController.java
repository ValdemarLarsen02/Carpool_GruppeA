package app.controllers;

import app.services.EmailService;
import app.config.Email;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class EmailController {

    private DatabaseController dbController;
    private EmailService emailService;

    public EmailController(EmailService emailService, DatabaseController dbController) {
        this.emailService = emailService;
        this.dbController = dbController;
    }

    public void registerRoutes(Javalin app) {
        app.get("/email-management", this::showReceivedEmails);


    }

    private void showReceivedEmails(Context ctx) {
        List<Email> sentEmails = emailService.showAllEmails(dbController);

        ctx.render("email-management.html", Map.of("emails", sentEmails));

    }


}
