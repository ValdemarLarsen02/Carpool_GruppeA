package app.utils;

public class Product {
    private String name;
    private String price;
    private String meterPrice;
    private String url;
    private String imageUrl;

    // Konstruktor
    public Product(String name, String price, String meterPrice, String url, String imageUrl) {
        this.name = name;
        this.price = price;
        this.meterPrice = meterPrice;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    // Gettere
    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
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

    // ToString-metode til udskrivning
    @Override
    public String toString() {
        return "Produktnavn: " + name + "\n" + "Pris: " + price + " kr.\n" + "Meterpris: " + meterPrice + " kr./meter\n" + "Link: https://www.johannesfog.dk" + url + "\n" + "Billede: " + imageUrl + "\n";
    }
}
