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

    public PriceResult generatePrices(String searchTerm) {
        List<Product> products = scrapper.searchProducts(searchTerm);

        if (products.isEmpty()) {
            System.out.println("No products found for search term: " + searchTerm);
            return null;
        }

        double totalPrice = products.stream().
                mapToDouble(Product::getPriceInclVat)
                .sum();

        double totalCost = totalPrice * 0.8; //Eksempel
        double coverage = calculateCoverage(totalPrice, totalCost);

        return new PriceResult(totalPrice, totalCost, coverage);


    }

    private double calculateCoverage(double totalPrice, double totalCost) {
        if (totalPrice == 0) {
            return 0;
        }
        return ((totalPrice - totalCost) / totalPrice) * 100;
    }
}
