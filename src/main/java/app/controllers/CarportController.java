package app.controllers;

import app.repositories.CarportRepository;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.models.Carport;
import java.util.List;
import java.util.Map;

public class CarportController {

    private CarportRepository carportRepository;

    public CarportController(CarportRepository carportRepository) {
        this.carportRepository = carportRepository;
    }

    public void setupRoutes(Javalin app) {
        app.get("/", ctx -> {
            // Display the initial page with all carports
            List<Carport> carports = carportRepository.getAllCarports();
            ctx.render("index.html", Map.of("carports", carports));
        });

        app.get("/searchCarports", this::searchCarports);
    }

    public void searchCarports(Context ctx) {
        // Retrieve filter parameters from the query string
        String roofType = ctx.queryParam("roofType");
        String dimensions = ctx.queryParam("dimensions");
        String price = ctx.queryParam("price");

        // Perform filtering based on the given parameters
        List<Carport> filteredCarports = carportRepository.filterCarports(roofType, dimensions, price);

        // Render the index page with filtered carports
        ctx.render("index.html", Map.of("carports", filteredCarports));
    }
}
