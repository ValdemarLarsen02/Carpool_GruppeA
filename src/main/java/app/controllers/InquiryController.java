package app.controllers;

import app.Services.InquiryService;
import app.config.Inquiry;
import app.persistence.InquiryMapper;
import io.javalin.http.Context;
import app.config.Salesman;

public class InquiryController {

    private InquiryService inquiryService;
    private Salesman salesman;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    public void editInquiry(Context ctx) {
        try {
            //Mapper data fra HTTP-anmodningen til et Inquiry objekt
            Inquiry inquiry = InquiryMapper.mapFromRequest(ctx);

            //Opdaterer forespørgslen i databasen
            inquiryService.updateInquiryInDatabase(inquiry);

            ctx.status(200).result("Forespørgslen er blevet redigeret.");
        } catch (Exception e) {
            ctx.status(400).result("Fejl under opdateringen af forespørgslen.");
            e.printStackTrace();
        }
    }

}
