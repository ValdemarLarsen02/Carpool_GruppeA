package app.controllers;

import io.javalin.Javalin;
import app.service.CarportFinder;
import app.models.Carport;

import java.util.List;
import java.util.Map;

public class CarportController {

    private final CarportFinder carportFinder = new CarportFinder();

    public void registerRoutes(Javalin app) {
        app.get("/carports", ctx -> {
            String searchTerm = ctx.queryParam("searchTerm"); // Kun ét parameter
            if (searchTerm == null) searchTerm = ""; // Tilføj standardværdi manuelt
            List<Carport> carports = carportFinder.findCarports(searchTerm);
            ctx.render("carport_list.html", Map.of("carports", carports));
        });

        app.get("/carports/details", ctx -> {
            int id = Integer.parseInt(ctx.queryParam("id"));
            Carport carport = carportFinder.findCarports("").stream()
                    .filter(c -> c.getId() == id)
                    .findFirst()
                    .orElse(null);
            ctx.render("carport_details.html", Map.of("carport", carport));
        });
    }
}
