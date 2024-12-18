package app.models;

import java.util.List;

public class Carport {
    private int carportId;
    private String carportName;
    private int length;
    private int width;

    private boolean withShed;
    private int shedLength;
    private int shedWidth;

    //Priser

    private double totalPrice;

    private String image;

    // Constructor

    public Carport(int carportId, String carportName, int length, int width, boolean withShed, int shedLength, int shedWidth, double totalPrice, String image) {
        this.carportId = carportId;
        this.carportName = carportName;
        this.length = length;
        this.width = width;
        this.withShed = withShed;
        this.shedLength = shedLength;
        this.shedWidth = shedWidth;
        this.totalPrice = totalPrice;
        this.image = image;
    }

    //Getters & setters:
    public int getCarportId() {
        return carportId;
    }

    public String getCarportName() {
        return carportName;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public boolean isWithShed() {
        return withShed;
    }

    public int getShedLength() {
        return shedLength;
    }

    public int getShedWidth() {
        return shedWidth;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getImage() {
        return image;
    }


    @Override
    public String toString() {
        return "Carport [carportId=" + carportId + ", carportName=" + carportName + ", length=" + length + ", width=" + width + ", withShed=" + withShed + ", shedLength=" + shedLength + ", shedWidth=" + shedWidth + ", totalPrice=" + totalPrice + "Billede" + image + "]";
    }
}
