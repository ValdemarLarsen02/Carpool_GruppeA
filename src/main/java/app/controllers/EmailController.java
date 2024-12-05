package app.controllers;

import app.Services.EmailService;
import app.config.Email;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmailController {

    private DatabaseController dbController;
    private EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
        this.dbController = new DatabaseController();
        dbController.initialize();
    }

    public void registerRoutes(Javalin app) {
        app.get("/sent-emails", this::showSentEmails);
    }

    private void showSentEmails(Context ctx) {
        List<Email> sentEmails = emailService.showAllSentEmails(dbController);

        ctx.render("sent-emails.html", Map.of("emails", sentEmails));

    }


}
