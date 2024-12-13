package app.controllers;

import app.config.Admin;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AdminController {

    private Admin admin;
    private DatabaseController dbController;

    public AdminController(Admin admin, DatabaseController dbController) {
        this.dbController = dbController;
        this.admin = admin;
    }

    public void RegisterRoutes(Javalin app){

        app.get("/update-password", ctx -> ctx.render("/salesmen-password-management.html"));

        app.post("/update-password", this::updatePassword);

        app.get("/password-update-confirmation", ctx -> ctx.render("/password-update-confirmation.html"));
    }


    private void updatePassword(Context ctx) {
        String updatedPassword = ctx.formParam("password");
        admin.UpdatePassword(updatedPassword);

        ctx.render("/salesmen-password-management.html");
    }
}
