package app.config;

public class Payment {
    private int paymentId;
    private Order order;
    private String paymentLink;
    private String paymentStatus;

    public String generatePaymentLink() {
        // Implementation here
        return paymentLink;
    }

    public boolean processPayment() {
        // Implementation here
        return false;
    }
}

