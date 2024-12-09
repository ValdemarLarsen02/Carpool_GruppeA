package app.controllers;

import app.config.StripeConfig;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.javalin.Javalin;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class StripePayment {
    public static void registerRoutes(Javalin app) {
        StripeConfig.initialize();

        app.post("/create-payment", ctx -> {
            try {
                // Parse JSON-data fra klienten
                String body = ctx.body();
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();

                long amount = json.get("amount").getAsLong(); // Pris i cent
                String description = json.get("description").getAsString(); // Beskrivelse

                // Opret CheckoutSession
                SessionCreateParams params = SessionCreateParams.builder().setMode(SessionCreateParams.Mode.PAYMENT).setSuccessUrl("http://localhost:7000/success") // URL ved succes
                        .setCancelUrl("http://localhost:7000/cancel") // URL ved annullering
                        .addLineItem(SessionCreateParams.LineItem.builder().setQuantity(1L).setPriceData(SessionCreateParams.LineItem.PriceData.builder().setCurrency("dkk").setUnitAmount(amount) // Beløb i cent
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder().setName(description) // Beskrivelse
                                        .build()).build()).build()).build();

                // Opret session og få URL
                Session session = Session.create(params);

                // Returner Checkout link
                ctx.result("{\"url\": \"" + session.getUrl() + "\"}");
            } catch (Exception e) {
                ctx.status(500).result("Betalingsfejl: " + e.getMessage());
            }
        });
    }
}

