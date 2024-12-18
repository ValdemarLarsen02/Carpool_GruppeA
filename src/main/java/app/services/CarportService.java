package app.services;

import app.controllers.DatabaseController;
import app.models.Carport;

import java.util.List;

public class CarportService {
    private final DatabaseController databaseController;

    public CarportService(DatabaseController databaseController) {
        this.databaseController = databaseController;
    }

    public List<Carport> getCarports() {
        return null;   //databaseController.fetchCarports();
    }
}
