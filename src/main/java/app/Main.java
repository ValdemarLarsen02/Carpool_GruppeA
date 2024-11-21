package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import app.controllers.DatabaseController;
public class Main {
    public static void main(String[] args)
    {
        // Initializing Javalin and Jetty webserver

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler ->  handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        //database:
        DatabaseController dbController = new DatabaseController();

        dbController.initialize();


        // Routing

        app.get("/", ctx ->  ctx.render("index.html"));
    }
}