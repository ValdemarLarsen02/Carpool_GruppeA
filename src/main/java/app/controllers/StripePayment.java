package app.controllers;

import app.config.StripeConfig;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

public class StripePayment {

    // Metode til at oprette en Stripe betalings-URL
    public static String createPaymentLink(double amountInDkk, String description) {
        try {
            // Initialiser Stripe (hvis ikke allerede gjort)
            StripeConfig.initialize();

            // Konverter DKK til øre/cent ved at gange med 100
            long amountInCents = Math.round(amountInDkk * 100);

            // Opret Checkout Session parametre
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:7000/success") // URL ved succes
                    .setCancelUrl("http://localhost:7000/cancel")   // URL ved annullering
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("dkk")
                                                    .setUnitAmount(amountInCents) // Beløb i cent
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(description) // Beskrivelse
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            // Opret session hos Stripe
            Session session = Session.create(params);

            // Returnér URL til betalingslinket
            return session.getUrl();
        } catch (Exception e) {
            System.err.println("Fejl ved oprettelse af betalingslink: " + e.getMessage());
            return null; // Returner null i tilfælde af fejl
        }
    }
}
