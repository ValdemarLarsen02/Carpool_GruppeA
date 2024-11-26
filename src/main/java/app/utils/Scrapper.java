package app.utils;

import com.google.gson.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class Scrapper {

    private static final String API_URL = "https://johannesfog-dk.54proxy.com/search";

    // Metode til at hente søgeresultater
    public List<Product> searchProducts(String searchTerm) {
        String jsonPayload = "{"
                + "\"query\": \"" + searchTerm + "\""
                + "}";

        try {
            // Opret en HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Opret en HttpRequest med POST-metoden
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .header("api-version", "V3")
                    .header("origin", "https://www.johannesfog.dk")
                    .header("user-id", "moJclro7UW")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            // Send forespørgslen
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) { // Tjekker at vi modtager et succesfuldt response fra vores HttpResponse.
                // Parse JSON-responsen
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonObject results = jsonResponse.getAsJsonObject("results");
                JsonArray items = results.getAsJsonArray("items");

                // Liste til at holde fundne produkter
                List<Product> products = new ArrayList<>();

                // Gennemgå hvert produkt
                for (JsonElement item : items) {
                    JsonObject product = item.getAsJsonObject();
                    String productName = getAttribute(product, "ItemName");
                    String productPrice = getAttribute(product, "PriceInclVat");
                    String meterPrice = getAttribute(product, "AlternativePriceInclVat");
                    String productUrl = getAttribute(product, "ItemUrl");
                    String productImage = getAttribute(product, "ImageUrl");

                    // Opret et Product-objekt og tilføj til listen
                    products.add(new Product(productName, productPrice, meterPrice, productUrl, productImage));
                }

                return products;
            } else {
                //Viser hvilken fejlkode vi modtager.
                System.out.println("Fejl: HTTP-statuskode " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(); // Returner tom liste i tilfælde af fejl
    }

    // Hjælpefunktion til at hente attributter
    private static String getAttribute(JsonObject product, String attributeName) {
        JsonArray attributes = product.getAsJsonArray("attributes");
        for (JsonElement attribute : attributes) {
            JsonObject attrObj = attribute.getAsJsonObject();
            if (attrObj.get("name").getAsString().equals(attributeName)) {
                return attrObj.getAsJsonArray("values").get(0).getAsString();
            }
        }
        return "Ikke fundet";
    }
}
