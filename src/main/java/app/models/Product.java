package app.models;

import java.math.BigDecimal;

public class Product {
    private int id; // Til database
    private String name;
    private String description; // Til database
    private String price; // Intern pris
    private String category; // Til database
    private BigDecimal externalPrice; // Pris fra Scrapper
    private String meterPrice; // Meterpris fra Scrapper
    private String url;
    private String imageUrl;


    // Standardkonstruktør (krævet af Jackson) ellers fejer den...
    public Product() {

    }

    // Konstruktør til interne produkter (fra databasen)
    public Product(int id, String name, String description, String price, String category, BigDecimal externalPrice, String url, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.externalPrice = externalPrice;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    // Konstruktør til eksterne produkter (fra Scrapper)
    public Product(String name, String externalPrice, String meterPrice, String url, String imageUrl) {
        this.name = name;
        this.externalPrice = new BigDecimal(externalPrice);
        this.meterPrice = meterPrice;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    // Gettere
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }


    public String getCategory() {
        return category;
    }

    public BigDecimal getExternalPrice() {
        return externalPrice;
    }

    public String getMeterPrice() {
        return meterPrice;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Settere
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setExternalPrice(BigDecimal externalPrice) {
        this.externalPrice = externalPrice;
    }

    public void setMeterPrice(String meterPrice) {
        this.meterPrice = meterPrice;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // ToString-metode til debugging
    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description + '\'' + ", price=" + price + ", category='" + category + '\'' + ", externalPrice=" + externalPrice + ", meterPrice='" + meterPrice + '\'' + ", url='" + url + '\'' + ", imageUrl='" + imageUrl + '\'' + '}';
    }
}
