package app.controllers;

import app.models.Product;
import app.services.ProductService;
import app.utils.Scrapper;
import io.javalin.Javalin;
import io.javalin.http.Context;


import java.util.List;
import java.util.Map;

public class AdminController {

    private final ProductService productService = new ProductService();

    public static void registerRoutes(Javalin app) {
        AdminController controller = new AdminController();

        //Admin siden:
        app.get("/admin", ctx -> controller.showAdminPage(ctx));

        // CRUD-operationer
        app.post("/admin/product", ctx -> controller.createProduct(ctx));
        app.post("/admin/product/update", ctx -> controller.updateProduct(ctx));
        app.get("/admin/products", ctx -> controller.getAllProducts(ctx));
        app.get("/admin/scrap", ctx -> controller.scrapExternalProducts(ctx));
        app.post("/admin/product/delete", ctx -> controller.deleteProduct(ctx));

    }


    private void showAdminPage(Context ctx) {
        List<Product> products = productService.getAllProducts();
        ctx.render("admin.html", Map.of("products", products));
    }

    private void createProduct(Context ctx) {
        Product product = ctx.bodyAsClass(Product.class);

        System.out.println(product);
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
