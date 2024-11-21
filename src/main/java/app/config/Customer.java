package app.config;

import java.util.List;
import java.util.Map;

public class Customer {
    private String name;
    private String email;


    public void submitInquiry(Inquiry inquiry) {
        // Implementation here
    }

    public List<Carport> viewCarports(Map<String, String> filters) {
        // Implementation here
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}