package app.models;

import java.util.List;

public class Carport {
    private int carportId;
    private String dimensions;
    private String roofType;
    private double price;
    private List<String> addons;

    // Constructor
    public Carport(int carportId, String dimensions, String roofType, double price, List<String> addons) {
        this.carportId = carportId;
        this.dimensions = dimensions;
        this.roofType = roofType;
        this.price = price;
        this.addons = addons;
    }

    // Getters and Setters
    public int getCarportId() {
        return carportId;
    }

    public void setCarportId(int carportId) {
        this.carportId = carportId;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getRoofType() {
        return roofType;
    }

    public void setRoofType(String roofType) {
        this.roofType = roofType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getAddons() {
        return addons;
    }

    public void setAddons(List<String> addons) {
        this.addons = addons;
    }

    // Method to get carport details
    public String getDetails() {
        return "Carport ID: " + carportId + ", Dimensions: " + dimensions + ", Roof Type: " + roofType + ", Price: " + price;
    }
}
