package app.utils;

import io.javalin.http.Context;

public class SessionUtils {

    // Metode til at tjekke om en bruger er logget ind som admin
    public static boolean isAdmin(Context ctx) {
        String role = ctx.sessionAttribute("role");
        return role != null && role.equals("seller");
    }

    // Metode til at hente seller ID fra session
    public static Integer getSellerId(Context ctx) {
        return ctx.sessionAttribute("sellerId"); // Returnerer sellerId eller null
    }

    // Metode til at validere og sikre en bruger er logget ind
    public static boolean isLoggedIn(Context ctx) {
        return ctx.sessionAttribute("role") != null;
    }

    // Metode til at sikre, at sessionen har en bestemt rolle
    public static void ensureAdminAccess(Context ctx) {
        System.out.println("Tjekker session data..");
        if (!isAdmin(ctx)) {
            ctx.redirect("/salesman-login");
        }
    }
}
