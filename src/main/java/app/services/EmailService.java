package app.services;

import app.config.Customer;
import app.config.Email;
import app.config.Inquiry;
import app.controllers.DatabaseController;
import app.controllers.StripePayment;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import static app.utils.SvgConverter.convertSvgToPng;

public class EmailService {

    private DatabaseController dbController;
    private ErrorLoggerService errorLogger;

    public EmailService(DatabaseController dbController, ErrorLoggerService errorLogger) {
        this.dbController = dbController;
        this.errorLogger = errorLogger;
    }


    private static final String SMTP_HOST = "mail.smtp2go.com";
    private static final String SMTP_PORT = "2525";
    private static final String USERNAME = "cphbusiness.dk";
    private static final String PASSWORD = "m29sI48YWo1noeEc";

    public void sendCustomerInquiryEmail(Customer customer, Inquiry inquiry, String recipient, String tegning) {
        // Opretter session til at sende mailen

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });

            // Opret emailen
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("fog@vascripts.store")); // Opdater til din e-mail
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient)); // Opdater modtageren
            message.setSubject("Ny forespørgsel om carport");


            // Byg emailens indhold
            String emailContent = buildInquiryEmailContent(customer, inquiry, tegning);

            // Sæt indholdet af emailen
            message.setContent(emailContent, "text/html");

            // Send emailen
            Transport.send(message);
        } catch (MessagingException e) {
            String errorMessage = "Fejl ved afsendelse af email, i metoden sendCustomerInquiryEmail";
            errorLogger.logError(errorMessage, "HIGH", e);
            System.err.println(errorMessage + ": " + e.getMessage());
        }

    }


    // Byg emailens indhold med kundens forespørgsel
    private String buildInquiryEmailContent(Customer customer, Inquiry inquiry, String tegning) {
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>");
        content.append("<html lang='en'>");
        content.append("<head>");
        content.append("<meta charset='UTF-8'>");
        content.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        content.append("</head>");
        content.append("<body>");
        // Header
        content.append("<h1>Forespørgsel om Carport</h1>");

        // Body
        content.append("<p><strong>Navn:</strong> ").append(customer.getName()).append("</p>");
        content.append("<p><strong>Email:</strong> ").append(customer.getEmail()).append("</p>");
        content.append("<p><strong>Telefon:</strong> ").append(customer.getPhoneNumber()).append("</p>");

        content.append("<h2>Carport Forespørgsel</h2>");
        content.append("<p><strong>Carport Længde:</strong> ").append(inquiry.getCarportLength()).append(" m</p>");
        content.append("<p><strong>Carport Bredde:</strong> ").append(inquiry.getCarportWidth()).append(" m</p>");
        content.append("<p><strong>Redskabsskur længde:</strong> ").append(inquiry.getShedLength()).append(" m</p>");
        content.append("<p><strong>Redskabsskur bredde:</strong> ").append(inquiry.getShedWidth()).append(" m</p>");
        content.append("<h2>Opdatering fra sælger:</h2>");
        content.append("<p><strong>Kommentar fra sælger: </strong> ").append(inquiry.getComments()).append("</p>");

        if (inquiry.getSalesPrice() != null) {

            //opretter vores betalingslink
            String beskrivelse = "Betaling til din personlige carport";
            String paymentUrl = StripePayment.createPaymentLink(inquiry.getSalesPrice(), beskrivelse);

            content.append("<h2>Vi har beregnet en pris til dig!</h2>");
            content.append("<p><strong>Total pris: </strong> ").append(inquiry.getSalesPrice()).append(" DKK</p>");

            if (paymentUrl != null) {
                content.append("<p><strong>Link til betaling: </strong> ").append(paymentUrl).append("</p>");
                content.append("<p>Dette link er gyldigt i 24 timer</p>");
            }

        }


        // Base64-encode din SVG
        String pictureUrl = "";
        pictureUrl = convertSvgToPng(tegning);


        // Tilføj det i HTML som en data:image
        content.append("<h3>Din carport tegning:</h3>");
        content.append("<img src='").append(pictureUrl)
                .append("' alt='Carport Tegning' style='max-width: 600px; height: auto; display: block; margin: 0 auto;'/>");


        // Footer
        content.append("<p>Tak for din forespørgsel! Vi vender tilbage hurtigst muligt.</p>");

        content.append("</body>");
        content.append("</html>");

        return content.toString();
    }


    public void saveEmailsToDatabase(Inquiry inquiry, Customer customer, DatabaseController dbController, String tegning) {

        String query = "INSERT INTO sent_emails (recipient_email, subject, content) VALUES (?, ?, ?)";

        try (Connection connection = dbController.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Byg e-mailens indhold baseret på inquiry og customer
            String emailContent = buildInquiryEmailContent(customer, inquiry, tegning);

            // Sæt værdier til placeholders
            preparedStatement.setString(1, customer.getEmail());
            preparedStatement.setString(2, "Ny forespørgsel om carport");
            preparedStatement.setString(3, emailContent);


            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            String errorMessage = "Fejl ved gemning af e-mail i databasen, i metoden saveEmailsToDatabase";
            errorLogger.logError(errorMessage, "HIGH", e);
            System.err.println(errorMessage + ": " + e.getMessage());
        }
    }

    public List<Email> showAllEmails(DatabaseController dbController) {
        List<Email> sentEmails = new ArrayList<>();


        String query = "SELECT * FROM sent_emails";

        try (Connection connection = dbController.getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {


            while (resultSet.next()) {
                String recipient = resultSet.getString("recipient_email");
                String subject = resultSet.getString("subject");
                String content = resultSet.getString("content");


                Email email = new Email(recipient, subject, content);
                sentEmails.add(email);
            }
        } catch (SQLException e) {
            String errorMessage = "Der skete en fejl under visningen af alle emails, i showAllEmails metoden";
            errorLogger.logError(errorMessage, "HIGH", e);
            System.err.println(errorMessage + ": " + e.getMessage());
        }
        return sentEmails;


    }
}
