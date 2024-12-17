package app.controllers;

import app.services.CarportSVG;
import app.utils.CarportRequest;
import io.javalin.Javalin;

public class SVGController {

    public void registerRoutes(Javalin app) {



        app.post("/generate-svg", ctx -> {

            // Læser vores JSON DATA.
            CarportRequest request = ctx.bodyAsClass(CarportRequest.class);

            // Kalder vores metode der opretter vores tegning.
            CarportSVG carportSVG = new CarportSVG(
                    request.carportWidth,
                    request.carportLength,
                    request.shedWidth,
                    request.shedLength
            );
            String svgOutput = carportSVG.generateSVG();

            // sender tilbage til client/frontend.
            ctx.html(svgOutput);
        });



    }



}
