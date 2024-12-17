package app.repositories;

import app.models.Carport;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CarportRepository {

    private List<Carport> carports;

    public CarportRepository(List<Carport> carports) {
        this.carports = carports;
    }

    public List<Carport> getAllCarports() {
        return carports;  // In a real scenario, you would fetch data from the database
    }

    public List<Carport> filterCarports(String roofType, String dimensions, String price) {
        return carports.stream()
                .filter(carport -> (roofType == null || roofType.isEmpty() || carport.getRoofType().equalsIgnoreCase(roofType)))
                .filter(carport -> (dimensions == null || dimensions.isEmpty() || carport.getDimensions().contains(dimensions)))
                .filter(carport -> (price == null || price.isEmpty() || carport.getPrice() <= Double.parseDouble(price)))
                .collect(Collectors.toList());
    }
}
