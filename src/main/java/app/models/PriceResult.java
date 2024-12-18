package app.models;

public class PriceResult {
    private double suggestedPrice;
    private double totalCost;
    private double coveragePercentage;

    //Konstruktør
    public PriceResult(double suggestedPrice, double totalCost, double coveragePercentage) {
        this.suggestedPrice = suggestedPrice;
        this.totalCost = totalCost;
        this.coveragePercentage = coveragePercentage;
    }

    public double getSuggestedPrice() {
        return suggestedPrice;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getCoveragePercentage() {
        return coveragePercentage;
    }

    @Override
    public String toString() {
        return "PriceResult{" + "suggestedPrice=" + suggestedPrice + ", totalCost=" + totalCost + ", coveragePercentage=" + coveragePercentage + '}';
    }
}
