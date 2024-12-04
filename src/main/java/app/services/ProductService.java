package app.services;

import app.controllers.DatabaseController;
import app.models.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ProductService {
    private final DatabaseController dbController = new DatabaseController();


    public ProductService() {
        dbController.initialize();
    }

    /**
     * Henter alle produkter fra databasen.
     *
     * @return En liste af {@link Product}-objekter, der henviser til alle vores produkter i databasen
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Product";
        try (Connection conn = dbController.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // Iterer over resultaterne og opret produktobjekter
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        rs.getString("category"),
                        rs.getBigDecimal("external_price"),
                        rs.getString("url"),
                        rs.getString("image_url")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Fejl ved hentning af produkter: " + e.getMessage());
        }
        return products;
    }

    /**
     * Opretter et nyt produkt i databasen.
     *
     * @param product Et {@link Product}-objekt, der indeholder de data, der skal bruges til indsættelsen i databasen
     */
    public void createProduct(Product product) {
        String query = "INSERT INTO Product (name, description, price, category, external_price, url, image_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbController.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Sætter parametre baseret på produktets felter
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setString(4, product.getCategory());
            stmt.setBigDecimal(5, product.getExternalPrice());
            stmt.setString(6, product.getUrl());
            stmt.setString(7, product.getImageUrl());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Fejl ved oprettelse af produkt: " + e.getMessage());
        }
    }

    /**
     * Opdaterer et eksisterende produkt i databasen.
     *
     * @param product Et {@link Product}-objekt, der indeholder de opdaterede data. Som vi bruger til at opdateret produktet i databasen.
     */
    public void updateProduct(Product product) {
        String query = "UPDATE Product SET name = ?, description = ?, price = ?, category = ?, external_price = ?, url = ?, image_url = ? " +
                "WHERE id = ?";
        try (Connection conn = dbController.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Sætter parametre baseret på produktets felter
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setString(4, product.getCategory());
            stmt.setBigDecimal(5, product.getExternalPrice());
            stmt.setString(6, product.getUrl());
            stmt.setString(7, product.getImageUrl());
            stmt.setInt(8, product.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Fejl ved opdatering af produkt: " + e.getMessage());
        }
    }


    /**
     * Sletter et produkt fra databasen baseret på dets ID.
     *
     * @param productId ID for det produkt, der skal slettes
     */
    public void deleteProductById(int productId) {
        String query = "DELETE FROM Product WHERE id = ?";
        try (Connection conn = dbController.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Antal slettede rækker: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Fejl ved sletning af produkt: " + e.getMessage());
        }
    }

}
