package app.controllers;

import app.models.Admin;
import app.models.Product;
import app.services.ProductService;
import app.utils.Scrapper;
import io.javalin.Javalin;
import io.javalin.http.Context;


import java.util.List;
import java.util.Map;

public class AdminController {

    private Admin admin;
    private DatabaseController dbController;

    public AdminController(Admin admin, DatabaseController dbController) {
        this.dbController = dbController;
        this.admin = admin;
    }

    private final ProductService productService = new ProductService();

    public void registerRoutes(Javalin app) {

        //Admin siden:
        app.get("/admin", this::showAdminPage);

        // CRUD-operationer
        app.post("/admin/product", this::createProduct);
        app.post("/admin/product/update", this::updateProduct);
        app.get("/admin/products", this::getAllProducts);
        app.get("/admin/scrap", this::scrapExternalProducts);
        app.post("/admin/product/delete", this::deleteProduct);

        app.get("/update-password", ctx -> ctx.render("/salesmen-password-management.html"));

        app.post("/update-password", this::updatePassword);

        app.get("/password-update-confirmation", ctx -> ctx.render("/password-update-confirmation.html"));

    }


    private void updatePassword(Context ctx) {
        String updatedPassword = ctx.formParam("password");
        admin.UpdatePassword(updatedPassword);

        ctx.render("/salesmen-password-management.html");
    }

    private void showAdminPage(Context ctx) {
        List<Product> products = productService.getAllProducts();
        ctx.render("admin.html", Map.of("products", products));
    }

    private void createProduct(Context ctx) {
        Product product = ctx.bodyAsClass(Product.class);

        productService.createProduct(product);
        ctx.redirect("/admin");
    }

    private void updateProduct(Context ctx) {
        Product product = ctx.bodyAsClass(Product.class);
        productService.updateProduct(product);
        ctx.redirect("/admin");
    }

    private void getAllProducts(Context ctx) {
        List<Product> products = productService.getAllProducts();
        ctx.json(products);
    }

    private void deleteProduct(Context ctx) {
        // Hent id fra formularen
        String idParam = ctx.formParam("id");
        productService.deleteProductById(Integer.parseInt(idParam));
        ctx.redirect("/admin");
    }

    private void scrapExternalProducts(Context ctx) {
        Scrapper scrapper = new Scrapper();
        String searchTerm = ctx.queryParam("search");
        List<Product> externalProducts = scrapper.searchProducts(searchTerm);
        ctx.json(externalProducts);
    }
}
