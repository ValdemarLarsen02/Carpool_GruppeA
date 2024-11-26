package app.models;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CarportCatalog {
    private List<Carport> carports;

    // Constructor
    public CarportCatalog(List<Carport> carports) {
        this.carports = carports;
    }

    // Method to filter carports based on a map of filter criteria
    public List<Carport> filterCarports(Map<String, String> filters) {
        return carports.stream()
                .filter(carport -> filters.entrySet().stream()
                        .allMatch(entry -> {
                            switch (entry.getKey()) {
                                case "dimensions":
                                    return carport.getDimensions().equalsIgnoreCase(entry.getValue());
                                case "roofType":
                                    return carport.getRoofType().equalsIgnoreCase(entry.getValue());
                                case "price":
                                    return carport.getPrice() <= Double.parseDouble(entry.getValue());
                                default:
                                    return true;
                            }
                        })
                )
                .collect(Collectors.toList());
    }

    // Getters and Setters
    public List<Carport> getCarports() {
        return carports;
    }

    public void setCarports(List<Carport> carports) {
        this.carports = carports;
    }
}
