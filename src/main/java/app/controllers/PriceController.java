package app.controllers;

import app.services.PriceCalculatorService;
import com.stripe.service.PriceService;

public class PriceController {

    private PriceCalculatorService priceCalculatorService;

    public PriceController(PriceCalculatorService priceCalculatorService) {
        this.priceCalculatorService = priceCalculatorService;
    }


}
