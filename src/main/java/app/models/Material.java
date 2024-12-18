package app.models;

import java.math.BigDecimal;

public class Material {
    private String name;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;


    public Material(String name, int quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice; // Bruges direkte uden beregning bruges på når vi har et samlet felt.
    }

    public Material(String name, int quantity, BigDecimal unitPrice) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return "Material{" + "name='" + name + '\'' + ", quantity=" + quantity + ", unitPrice=" + unitPrice + ", totalPrice=" + totalPrice + '}';
    }
}
