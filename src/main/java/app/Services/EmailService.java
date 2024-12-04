package app.Services;

import app.config.Customer;
import app.config.Inquiry;
import app.controllers.DatabaseController;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class EmailService {

    public EmailService(DatabaseController dbController) {
        dbController.initialize();
    }

    private static final String SMTP_HOST = "smtp.mailersend.net";
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "MS_Q87gIR@trial-vywj2lpm2y1l7oqz.mlsender.net";
    private static final String PASSWORD = "MKWpmxZ3tS2bO2Pd";

    public void sendCustomerInquiryEmail(Customer customer, Inquiry inquiry) throws MessagingException {

        // Opretter session til at sende mailen
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
        message.setFrom(new InternetAddress("MS_Q87gIR@trial-vywj2lpm2y1l7oqz.mlsender.net")); // Opdater til din e-mail
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("Micke.dengaard@icloud.com")); // Opdater modtageren
        message.setSubject("Ny forespørgsel om carport");

        // Byg emailens indhold
        String emailContent = buildInquiryEmailContent(customer, inquiry);

        // Sæt indholdet af emailen
        message.setContent(emailContent, "text/html");

        // Send emailen
        Transport.send(message);
    }

    // Byg emailens indhold med kundens forespørgsel
    private String buildInquiryEmailContent(Customer customer, Inquiry inquiry) {
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>");
        content.append("<html lang='en'>");
        content.append("<head>");
        content.append("<meta charset='UTF-8'>");
        content.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        content.append("<style>");
        content.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        content.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; border: 1px solid #ddd; border-radius: 5px; }");
        content.append(".header { background-color: #007bff; color: white; padding: 10px; text-align: center; border-radius: 5px 5px 0 0; }");
        content.append(".content { padding: 20px; }");
        content.append(".content h1 { color: #007bff; }");
        content.append(".content p { margin: 10px 0; }");
        content.append(".footer { text-align: center; font-size: 0.9em; color: #666; margin-top: 20px; }");
        content.append("</style>");
        content.append("</head>");
        content.append("<body>");
        content.append("<div class='container'>");

        // Header
        content.append("<div class='header'>");
        content.append("<h1>Forespørgsel om Carport</h1>");
        content.append("</div>");

        // Body
        content.append("<div class='content'>");
        content.append("<p><strong>Navn:</strong> ").append(customer.getName()).append("</p>");
        content.append("<p><strong>Email:</strong> ").append(customer.getEmail()).append("</p>");
        content.append("<p><strong>Telefon:</strong> ").append(customer.getPhoneNumber()).append("</p>");

        content.append("<h2>Carport Forespørgsel</h2>");
        content.append("<p><strong>Carport Længde:</strong> ").append(inquiry.getCarportLength()).append(" m</p>");
        content.append("<p><strong>Carport Bredde:</strong> ").append(inquiry.getCarportWidth()).append(" m</p>");
        content.append("<p><strong>Redskabsskur længde:</strong> ").append(inquiry.getShedLength()).append(" m</p>");
        content.append("<p><strong>Redskabsskur bredde:</strong> ").append(inquiry.getShedWidth()).append(" m</p>");
        content.append("</div>");

        // Footer
        content.append("<div class='footer'>");
        content.append("<p>Tak for din forespørgsel! Vi vender tilbage hurtigst muligt.</p>");
        content.append("</div>");

        content.append("</div>");
        content.append("</body>");
        content.append("</html>");

        return content.toString();

    }

    public void saveEmailsToDatabase(Inquiry inquiry, Customer customer, DatabaseController dbController) {

        // SQL til indsættelse af e-mailoplysninger
        String query = "INSERT INTO sent_emails (recipient_email, subject, content) VALUES (?, ?, ?)";

        try (Connection connection = dbController.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Byg e-mailens indhold baseret på inquiry og customer
            String emailContent = buildInquiryEmailContent(customer, inquiry);

            // Sæt værdier til placeholders
            preparedStatement.setString(1, customer.getEmail());
            preparedStatement.setString(2, "Ny forespørgsel om carport");
            preparedStatement.setString(3, emailContent);


            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Fejl ved gemning af e-mail: " + e.getMessage());
        }
    }
}
