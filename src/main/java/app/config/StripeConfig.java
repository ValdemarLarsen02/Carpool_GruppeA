package app.config;
import com.stripe.Stripe;
import app.utils.ConfigLoader;


public class StripeConfig {
    public static void initialize() {


        Stripe.apiKey = ConfigLoader.getProperty("stripe.apiKey"); // Henter vores api nøgle.
    }


}
