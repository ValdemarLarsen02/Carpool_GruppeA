package app.service;

import app.models.Carport;
import app.utils.Scrapper;

import java.util.ArrayList;
import java.util.List;

public class CarportFinder {

    private final Scrapper scrapper = new Scrapper();

    public List<Carport> findCarports(String searchTerm) {
        // Mock-database eller scraper
        List<Carport> carports = new ArrayList<>();
        if (searchTerm == null || searchTerm.isBlank()) {
            carports.addAll(scrapper.scrapeCarports("carport"));
        } else {
            carports.addAll(scrapper.scrapeCarports(searchTerm));
        }
        return carports;
    }
}
