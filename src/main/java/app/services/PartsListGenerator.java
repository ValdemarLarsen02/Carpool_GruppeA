package app.services;

import app.models.Material;
import app.models.PartsList;
import app.models.Product;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PartsListGenerator {

    // Globale variabler
    int maxPostLength = 340; //
    int spaceBetweenRafts = 55; // Afstand mellem spær i cm
    int maxRafterLength = 600; // Maksimal længde af ét spær i cm
    double rafterThickness = 4.5; // Tykkelse af et spær i cm

    private final PriceFinder priceFinder = new PriceFinder();
    private final PartsList partsList = new PartsList();



    public PartsListGenerator(double length, double width, double height) {
        Material rafters = calculateRafters(width);
        Material Straps = calculateStraps(length, width);
        Material poles = calculatePoles(length, height);
        partsList.addMaterial(rafters);
        partsList.addMaterial(Straps);
        partsList.addMaterial(poles);
    }


    public Material calculateRafters(double width) {
        // Tilgængelige længder på spær (sorteret fra kortest til længst)
        int[] availableLengths = {300, 360, 420, 480, 540, 600, 660, 720};
        int rafterSpacing = 55; // Afstand mellem spær (standardafstand)
        int rafterThickness = 4; // Spærenes tykkelse

        // Beregn antal spær baseret på bredden
        int totalRafters = (int) Math.ceil((width - rafterThickness) / rafterSpacing) + 1;

        // Find den mindste længde, der kan dække bredden (width)
        int selectedLength = 0;
        for (int lengthOption : availableLengths) {
            if (lengthOption >= width) {
                selectedLength = lengthOption;
                break; // Stop når den første passende længde findes
            }
        }

        if (selectedLength == 0) {
            throw new IllegalStateException("Kunne ikke finde en passende længde for spær.");
        }

        // Generer søgeterm for prisen
        String searchTerm = String.format("47X200 MM SPÆRTRÆ C24 HØVLET TIL 45X195MM - 70%% PEFC - %dcm", selectedLength);
        System.out.println("Søgeterm: " + searchTerm);

        // Find prisen for spærene
        List<Product> products = priceFinder.findPrices(searchTerm);
        BigDecimal pricePerRafter;

        if (products.isEmpty()) {
            System.out.println("Ingen priser fundet for: " + searchTerm);
            pricePerRafter = BigDecimal.ZERO;
        } else {
            String priceAsString = products.get(0).getPrice() != null && !products.get(0).getPrice().isEmpty()
                    ? products.get(0).getPrice()
                    : products.get(0).getExternalPrice().toString();

            try {
                pricePerRafter = new BigDecimal(priceAsString);
            } catch (NumberFormatException e) {
                System.err.println("Ugyldigt format for pris: " + priceAsString);
                pricePerRafter = BigDecimal.ZERO;
            }
        }

        // Udregn totalpris for spærene
        BigDecimal totalPriceForRafters = pricePerRafter.multiply(BigDecimal.valueOf(totalRafters));

        // Tilføj hvert spær som et individuelt materiale til partsList
        partsList.addMaterial(new Material(
                String.format("Spærtræ - %d cm", selectedLength), // Navn med længden
                totalRafters,                                    // Antal
                pricePerRafter                                   // Enhedspris
        ));

        // Returnér samlet objekt for alle spær
        return new Material(
                "Spærtræ (samlet)",          // Navn for samlet oversigt
                totalRafters,                // Samlet antal spær
                BigDecimal.ZERO,             // Enhedspris er ikke relevant
                totalPriceForRafters         // Totalpris for alle spær
        );
    }




    public Material calculatePoles(double length, double height) {
        // Mulige længder der kan bruges (sorteret fra længst til kortest for at optimere først)
        int[] availableLengths = {420, 360, 300, 270, 240, 210, 180};
        int maxPostLength = 340; // Maksimal afstand mellem stolper

        // Antal stolper pr. side orale
        int polesPerSide = (int) Math.ceil(length / maxPostLength) + 1; // +1 for stolpen ved slutningen

        // Map til at holde antal af hver stolpelængde, der vælges
        Map<Integer, Integer> lengthUsage = new LinkedHashMap<>();
        for (int availableLength : availableLengths) {
            lengthUsage.put(availableLength, 0);
        }

        // Find ud af, hvilke længder der skal bruges til at opfylde højden
        int remainingHeight = (int) height;

        while (remainingHeight > 0) {
            int bestFit = -1;

            // Find den største længde, der kan bruges
            for (int availableLength : availableLengths) {
                if (availableLength <= remainingHeight) {
                    bestFit = availableLength;
                    break;
                }
            }

            if (bestFit == -1) {
                // Brug den mindste længde til at dække det resterende
                bestFit = availableLengths[availableLengths.length - 1]; // Mindste længde
            }

            // Opdater brugen af stolpelængden og reducer den resterende højde
            lengthUsage.put(bestFit, lengthUsage.get(bestFit) + 1);
            remainingHeight -= bestFit;
        }

        // Beregn antal stolper i højden
        int polesInHeight = lengthUsage.values().stream().mapToInt(Integer::intValue).sum();

        // Samlet antal stolper
        int totalPoles = polesPerSide * 2 * polesInHeight; // 2 sider

        // Find priser for stolperne og beregn totalprisen
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> entry : lengthUsage.entrySet()) {
            int poleLength = entry.getKey();
            int count = entry.getValue();

            if (count > 0) {
                String searchTerm = String.format("97X97 MM FULDKANTET FYR TRYKIMPRÆGNERET NTR/A - 70%% PEFC - %dcm", poleLength);

                // Find produktet via PriceFinder
                List<Product> products = priceFinder.findPrices(searchTerm);
                if (products.isEmpty()) {
                    System.out.println("Ingen priser fundet for: " + searchTerm);
                    continue;
                }

                String priceAsString = products.get(0).getPrice() != null && !products.get(0).getPrice().isEmpty()
                        ? products.get(0).getPrice()
                        : products.get(0).getExternalPrice().toString();

                BigDecimal pricePerPole;
                try {
                    pricePerPole = new BigDecimal(priceAsString);
                } catch (NumberFormatException e) {
                    System.err.println("Ugyldigt format for pris: " + priceAsString);
                    pricePerPole = BigDecimal.ZERO;
                }

                // Beregn totalpris for denne længde og tilføj til totalPrice
                BigDecimal subTotal = pricePerPole.multiply(BigDecimal.valueOf(count * polesPerSide * 2));
                totalPrice = totalPrice.add(subTotal);

                // Udskriv og tilføj til styklisten
                //System.out.printf("Stolper %dcm: %d stk. (pris pr. stk.: %s)%n", poleLength, count * polesPerSide * 2, pricePerPole);
                partsList.addMaterial(new Material("Stolper " + poleLength + "cm", count * polesPerSide * 2, pricePerPole));
            }
        }


        // Returnér et samlet objekt
        return new Material(
                "Stolper (samlet)",
                totalPoles, // Samlet antal stolper
                BigDecimal.ZERO, // Enhedspris ikke relevant for samlet objekt
                totalPrice // Totalpris
        );
    }







    public Material calculateStraps(double length, double width) {
        // Mulige længder på remme (i cm)
        int[] availableLengths = {720, 660, 600, 540, 480, 420, 360, 300};

        // Den samlede længde af remme, der skal bruges (omkredsen)
        double totalPerimeter = 2 * (length + width);

        // Map til at holde antal af hver remmelængde, der vælges
        Map<Integer, Integer> lengthUsage = new LinkedHashMap<>();
        for (int availableLength : availableLengths) {
            lengthUsage.put(availableLength, 0);
        }

        // Reducér perimeteren ved at vælge passende remmelængder
        while (totalPerimeter > 0) {
            int bestFit = -1;

            // Find den største længde, der kan bruges
            for (int availableLength : availableLengths) {
                if (availableLength <= totalPerimeter) {
                    bestFit = availableLength;
                    break;
                }
            }

            if (bestFit == -1) {
                // Brug den mindste længde til at dække det resterende
                bestFit = availableLengths[availableLengths.length - 1]; // Mindste længde
            }

            // Opdater brugen af remmelængden og reducer den resterende perimeter
            lengthUsage.put(bestFit, lengthUsage.get(bestFit) + 1);
            totalPerimeter -= bestFit;
        }

        // Find prisen på hver remmelængde og beregn totalprisen
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> entry : lengthUsage.entrySet()) {
            int remLength = entry.getKey();
            int count = entry.getValue();

            if (count > 0) {

                String searchTerm = String.format("47X200 MM SPÆRTRÆ C24 HØVLET TIL 45X195MM - 70%% PEFC - %d", remLength);
                //System.out.println("Søger efter remmelængde med søgeterm: " + searchTerm);

                // Find produktet via PriceFinder
                List<Product> products = priceFinder.findPrices(searchTerm);
                if (products.isEmpty()) {
                    System.out.println("Ingen priser fundet for: " + searchTerm);
                    continue;
                }

                String priceAsString = products.get(0).getPrice() != null && !products.get(0).getPrice().isEmpty()
                        ? products.get(0).getPrice()
                        : products.get(0).getExternalPrice().toString();

                BigDecimal pricePerUnit;
                try {
                    pricePerUnit = new BigDecimal(priceAsString);
                } catch (NumberFormatException e) {
                    System.err.println("Ugyldigt format for pris: " + priceAsString);
                    pricePerUnit = BigDecimal.ZERO;
                }

                // Finder prisen for denne længe og tilføjet til totalPrice
                BigDecimal subTotal = pricePerUnit.multiply(BigDecimal.valueOf(count));
                totalPrice = totalPrice.add(subTotal);

                // Tilføjer til styklisten
                partsList.addMaterial(new Material("Remme " + remLength + "cm", count, pricePerUnit));
            }
        }

        // Retuner et helt samlet objekt for alle vores remme
        return new Material(
                "Remme (samlet)",
                lengthUsage.values().stream().mapToInt(Integer::intValue).sum(), // Samlet antal remme
                BigDecimal.ZERO, // Vi bruger ikke unitPrice her
                totalPrice // Totalpris er korrekt beregnet
        );


    }








    public PartsList getPartsList() {
        return partsList;
    }
}
