package app.controllers;

import app.services.EmailService;
import app.models.Email;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class EmailController {

    private DatabaseController dbController; // Database controller til at interagere med databasen
    private EmailService emailService; // Email service til håndtering af e-mails

    // Konstruktør til initialisering af emailService og dbController
    public EmailController(EmailService emailService, DatabaseController dbController) {
        this.emailService = emailService;
        this.dbController = dbController;
    }

    // Registrerer ruter for email management
    public void registerRoutes(Javalin app) {
        // Rute til at vise den modtagne e-mailoversigt
        app.get("/email-management", this::showReceivedEmails);
    }

    // Håndterer visning af modtagne e-mails
    private void showReceivedEmails(Context ctx) {
        // Henter alle e-mails via emailService
        List<Email> sentEmails = emailService.showAllEmails(dbController);

        // Renderer email-management-siden og sender de hentede e-mails med
        ctx.render("email-management.html", Map.of("emails", sentEmails));
    }
}
