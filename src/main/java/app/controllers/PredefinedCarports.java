package app.controllers;

import app.models.Carport;
import app.services.CarportSVG;
import app.services.PredefinedCarportsService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class PredefinedCarports {

    private DatabaseController dbController;
    private PredefinedCarportsService predefinedCarportsService;
    public PredefinedCarports(DatabaseController dbController, PredefinedCarportsService predefinedCarportsService) {
        this.dbController = dbController;
        this.predefinedCarportsService = predefinedCarportsService;
    }

    public void registerRoutes(Javalin app) {
        app.get("/carporte", this::loadCaports);
        app.get("/carport/details/{id}", this::showCarportDetails); // Route for detaljeret visning af en carport


        app.post("/create-request", this::createRequest);
    }


    public void loadCaports(Context ctx) {
        List<Carport> carports = predefinedCarportsService.getCarports();
        ctx.render("carport_oversigt.html", Map.of("carports", carports));
    }

    public void showCarportDetails(Context ctx) {
        int carportId = Integer.parseInt(ctx.pathParam("id"));
        Carport carport = predefinedCarportsService.getCarportById(carportId);


        //laver vores tegning på denne carport
        CarportSVG carportSVG = new CarportSVG(carport.getWidth(), carport.getLength(), carport.getShedWidth(), carport.getShedLength());

        String svgOutput = carportSVG.generateSVG();

        // Send carport-objekt og SVG-output til skabelonen
        ctx.render("carport_details.html", Map.of(
                "carport", carport,
                "svg", svgOutput
        ));
    }

    public void createRequest(Context ctx) {

    }




}
