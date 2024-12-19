package app.config;

import com.stripe.Stripe;
import app.utils.ConfigLoader;


public class StripeConfig {
    public static void initialize() {
        //henter blot vores api key fra config.properties af
        Stripe.apiKey = ConfigLoader.getProperty("stripe.apiKey");
    }


}
