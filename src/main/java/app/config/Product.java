package app.config;

public class Product {
    private String name;
    private double priceInclVat;
    private double alternativePrice;
    private String productURL;
    private String imageURL;

    public Product(String name, double priceInclVat, double alternativePrice, String productURL, String imageURL) {
        this.name = name;
        this.priceInclVat = priceInclVat;
        this.alternativePrice = alternativePrice;
        this.productURL = productURL;
        this.imageURL = imageURL;
    }

    private double parsePrice(String price) {
        try {
            return Double.parseDouble(price.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public String getName() {
        return name;
    }

    public double getPriceInclVat() {
        return priceInclVat;
    }

    public double getAlternativePrice() {
        return alternativePrice;
    }

    public String getProductURL() {
        return productURL;
    }

    public String getImageURL() {
        return imageURL;
    }
}
