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
            System.out.println("Produkt ikke fundet i databasen. Søger med Scrapper...");
            products = scrapper.searchProducts(searchTerm);
        }

        return products;
    }

    /**
     * Søger efter produkter i databasen baseret på en søgeterm.
     *
     * @param searchTerm Søgeterm for produktet
     * @return Liste af produkter fundet i databasen
     */
    private List<Product> searchInDatabase(String searchTerm) {
        List<Product> products = new ArrayList<>();

        String query = "SELECT * FROM Product WHERE name ILIKE ? OR description ILIKE ?";
        try (Connection connection = databaseController.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Brug wildcard søgning med "%"
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setCategory(rs.getString("category"));
                product.setImageUrl(rs.getString("image_url"));

                products.add(product);
            }
        } catch (Exception e) {
            System.err.println("Fejl under søgning i databasen: " + e.getMessage());
        }

        return products;
    }
}
