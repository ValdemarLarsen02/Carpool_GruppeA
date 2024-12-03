package app.Services;

import app.config.Customer;
import app.config.Inquiry;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {

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
        message.setFrom(new InternetAddress("sender@example.com")); // Opdater til din e-mail
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("sælger@example.com")); // Opdater modtageren
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
        content.append("<h1>Forespørgsel om Carport</h1>");
        content.append("<p><strong>Navn:</strong> ").append(customer.getName()).append("</p>");
        content.append("<p><strong>Email:</strong> ").append(customer.getEmail()).append("</p>");
        content.append("<p><strong>Telefon:</strong> ").append(customer.getPhoneNumber()).append("</p>");

        // Inkluder forespørgselsdetaljer
        content.append("<h2>Carport Forespørgsel</h2>");
        content.append("<p><strong>Carport Længde:</strong> ").append(inquiry.getCarportLength()).append(" m</p>");
        content.append("<p><strong>Carport Bredde:</strong> ").append(inquiry.getCarportWidth()).append(" m</p>");
        content.append("<p><strong>Redskabsskur længde:</strong> ").append(inquiry.getShedLength()).append("m</p>");
        content.append("<p><strong> Redskabsskur bredde:</strong> ").append(inquiry.getShedWidth()).append(" m</p>");

        return content.toString();
    }
}
