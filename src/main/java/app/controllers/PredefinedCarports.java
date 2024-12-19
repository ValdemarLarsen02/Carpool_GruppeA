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
        app.get("/carporte", this::loadCarports);
        app.get("/carport/details/{id}", this::showCarportDetails);

        app.get("/create-carport", this::showCreateCarportForm);
        app.post("/create-carport", this::createCarport);
        app.get("/carport-succes", this::showCarportSucces);
        app.post("/create-request", this::createRequest);
    }

    public void showCreateCarportForm(Context ctx) {
        ctx.render("create-carport.html");
    }


    //henter alle vores carporte fra databasen:
    public void loadCarports(Context ctx) {
        List<Carport> carports = predefinedCarportsService.getCarports();
        ctx.render("carport_oversigt.html", Map.of("carports", carports));
    }


    //henter en carport ud fra id.
    public void showCarportDetails(Context ctx) {
        int carportId = Integer.parseInt(ctx.pathParam("id"));
        Carport carport = predefinedCarportsService.getCarportById(carportId);


        //laver vores tegning på denne carport
        CarportSVG carportSVG = new CarportSVG(carport.getWidth(), carport.getLength(), carport.getShedWidth(), carport.getShedLength());

        String svgOutput = carportSVG.generateSVG();

        // Send carport-objekt og SVG-output til skabelonen
        ctx.render("carport_details.html", Map.of("carport", carport, "svg", svgOutput));
    }

    public void createCarport(Context ctx) {

        //Henter værdier fra HTTP requesten
        String carportName = ctx.formParam("carportName");
        int length = Integer.parseInt(ctx.formParam("length"));
        int width = Integer.parseInt(ctx.formParam("width"));
        boolean withShed = Boolean.parseBoolean(ctx.formParam("withShed"));
        int shedLength = withShed ? Integer.parseInt(ctx.formParam("shedLength")) : 0;
        int shedWidth = withShed ? Integer.parseInt(ctx.formParam("shedWidth")) : 0;
        double totalPrice = Double.parseDouble(ctx.formParam("totalPrice"));
        String image = ctx.formParam("image");

        //Opretter et nyt carport objekt med de indhentede værdier
        Carport newCarport = new Carport(predefinedCarportsService.getNextCarportId(), carportName, length, width, withShed, shedLength, shedWidth, totalPrice, image);

        //Gemmer carporten i databasen
        predefinedCarportsService.addPreDefinedCarportToDatabase(newCarport);

        ctx.redirect("/carport-succes");


    }

    public void createRequest(Context ctx) {

    }

    public void showCarportSucces(Context ctx) {
        ctx.render("carport-succes.html");
    }


}
