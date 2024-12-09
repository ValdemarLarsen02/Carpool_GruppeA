package app.services;

import app.controllers.DatabaseController;
import app.models.Product;
import app.utils.Scrapper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PriceFinder {
    private final DatabaseController databaseController = new DatabaseController();
    private final Scrapper scrapper = new Scrapper();

    public PriceFinder() {
        databaseController.initialize();
    }

    /**
     * Finder produktets pris ved først at søge i databasen og derefter i scrapper.
     *
     * @param searchTerm Søgeterm for produktet
     * @return Liste af produkter
     */
    public List<Product> findPrices(String searchTerm) {
        List<Product> products = searchInDatabase(searchTerm);
        // Hvis der ikke blev fundet produkter i databasen, brug scrapper
        if (products.isEmpty()) {
            products = scrapper.searchProducts(searchTerm);
        }
        System.out.println(products.toString());

        return products;
    }

    /**
     * Søger efter produkter i databasen baseret på en søgeterm.
     *
     * @param searchTerm Søgeterm for produktet
     * @return Liste af produkter fundet i databasen
     */
    public List<Product> searchInDatabase(String searchTerm) {
        List<Product> products = new ArrayList<>();

        // Query til at finde ligheder
        String query = "SELECT * FROM Product WHERE name ILIKE ?";

        try (Connection connection = databaseController.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Tilføj wildcard (%) til søgetermen for at finde lignende resultater
            searchTerm = "%" + searchTerm.trim().replaceAll("\\s*-\\s*", "-") + "%";
            stmt.setString(1, searchTerm);

            // Log den endelige query for debugging

            ResultSet rs = stmt.executeQuery();
            boolean found = false; // Brug en indikator til at spore, om noget blev fundet
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getString("price"));
                product.setCategory(rs.getString("category"));
                product.setImageUrl(rs.getString("image_url"));

                products.add(product);
                found = true;
            }

        } catch (Exception e) {
            System.err.println("Fejl under søgning i databasen: " + e.getMessage());
        }

        return products;
    }


}
