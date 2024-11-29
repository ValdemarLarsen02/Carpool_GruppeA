package app.controllers;

import app.config.Inquiry;
import io.javalin.http.Context;

import java.util.List;

public class InquiryController {
    private final DatabaseController dbController;

    public InquiryController(DatabaseController dbController) {
        this.dbController = dbController;
    }


    public void getOrderStatus(Context ctx) {
        try {
            int customerId = Integer.parseInt(ctx.pathParam("customerId"));
            List<Inquiry> inquiries = dbController.getOrderStatus(customerId);
            ctx.json(inquiries);
        } catch (Exception e) {
            ctx.status(500).result("Error fetching order status: " + e.getMessage());
        }
    }
}
