package app.utils;

import app.models.Carport;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Scrapper {

    public List<Carport> scrapeCarports(String searchTerm) {
        List<Carport> carports = new ArrayList<>();
        try {
            String url = "https://www.johannesfog.dk/have-fritid/carporte";
            Document doc = Jsoup.connect(url).get();
            Elements carportElements = doc.select(".product-card");

            for (Element carportElement : carportElements) {
                String name = carportElement.select(".product-card__title").text();
                String dimensions = carportElement.select(".product-card__dimensions").text();
                String priceText = carportElement.select(".product-card__price").text();

                if (!name.toLowerCase().contains(searchTerm.toLowerCase())) {
                    continue;
                }

                BigDecimal price = parsePrice(priceText);

                Carport carport = new Carport();
                carport.setName(name);
                carport.setDimensions(dimensions);
                carport.setPrice(price);

                carports.add(carport);
            }
        } catch (Exception e) {
            System.err.println("Fejl under scraping: " + e.getMessage());
        }
        return carports;
    }

    private BigDecimal parsePrice(String priceText) {
        try {
            String numericPart = priceText.replaceAll("[^0-9,]", "").replace(",", ".");
            return new BigDecimal(numericPart);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
