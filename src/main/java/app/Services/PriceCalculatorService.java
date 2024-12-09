package app.Services;

import app.config.PriceResult;
import app.utils.Product;
import app.utils.Scrapper;

import java.util.List;


public class PriceCalculatorService {

    private Scrapper scrapper;


    public PriceCalculatorService(Scrapper scrapper) {
        this.scrapper = scrapper;

    }

    public String generatePrices(String searchTerm) {
        List<Product> products = scrapper.searchProducts(searchTerm);

        if (products.isEmpty()) {
            System.out.println("Ingen produkter blev fundet for: " + searchTerm);
            return null;
        }

        StringBuilder summary = new StringBuilder();
        summary.append("Salgsoversigt for søgning: ").append(searchTerm).append("\n\n");

        double totalRevenue = 0;
        double totalCost = 0;

        for (Product product : products) {
            double price = parsePrice(product.getPrice());
            double cost = calculateCost(price);
            totalRevenue += price;
            totalCost += cost;

            summary.append(product).append("\n");

        }

        double coverage = calculateCoverage(totalRevenue, totalCost);

        PriceResult priceResult = new PriceResult(totalRevenue, totalCost, coverage);

        summary.append("\nSamlet pris for produkter: ").append(String.format("%.2f", priceResult.getSuggestedPrice())).append(" kr\n");
        summary.append("Samlede omkostninger: ").append(String.format("%.2f", priceResult.getTotalCost())).append(" kr\n");
        summary.append("Dækningsgrad: ").append(String.format("%.2f", priceResult.getCoveragePercentage())).append(" %\n");

        return summary.toString();

    }


    private double calculateCoverage(double totalPrice, double totalCost) {
        if (totalPrice == 0) {
            return 0;
        }
        return ((totalPrice - totalCost) / totalPrice) * 100;
    }

    private double parsePrice(String price) {
        try {
            return Double.parseDouble(price.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }

    }

    private double calculateCost(double price) {
        return price * 0.7;
    }
}
