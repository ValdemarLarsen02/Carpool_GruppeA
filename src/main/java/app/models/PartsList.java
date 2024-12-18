package app.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class PartsList {
    private List<Material> materials;

    public PartsList() {
        this.materials = new ArrayList<>();
    }

    public void addMaterial(Material material) {
        this.materials.add(material);
    }

    public BigDecimal getTotalCost() {
        return materials.stream().map(Material::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Material> getMaterials() {
        return materials;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Stykliste:\n");
        for (Material material : materials) {
            sb.append(material.toString()).append("\n");
        }
        sb.append("Samlet pris: ").append(getTotalCost()).append(" DKK");
        return sb.toString();
    }
}
