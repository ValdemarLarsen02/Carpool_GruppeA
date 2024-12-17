package app.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CarportSVGTest {

    @Test
    public void testGenerateSVG_WithoutShed() {
        // Arrange
        CarportSVG carport = new CarportSVG(600, 780, 0, 0);

        // Act
        String svgOutput = carport.generateSVG();

        // Assert
        assertNotNull(svgOutput, "SVG output should not be null");
        assertTrue(svgOutput.contains("<svg"), "SVG output should contain <svg>");
        assertTrue(svgOutput.contains("Carport: 600 x 780 cm"), "SVG should include correct carport dimensions");
        assertTrue(svgOutput.contains("fill:#654321"), "Poles should be included in SVG");
    }

    @Test
    public void testGenerateSVG_WithShed() {
        // Arrange
        CarportSVG carport = new CarportSVG(600, 780, 200, 200);

        // Act
        String svgOutput = carport.generateSVG();



        // Assert
        assertNotNull(svgOutput, "SVG output should not be null");
        assertTrue(svgOutput.contains("Redskabsrum: 200 x 200 cm"), "SVG should include shed dimensions");
        assertTrue(svgOutput.matches("(?s).*fill=\"#ccc\".*"), "Shed should have correct fill color");
        assertTrue(svgOutput.contains("fill:#654321"), "Poles should be included in SVG");
    }


    @Test
    public void testAddBeams() {
        // Arrange
        CarportSVG carport = new CarportSVG(500, 700, 0, 0);

        // Act
        String svgOutput = carport.generateSVG();

        // Assert
        assertTrue(svgOutput.contains("fill:#A9A9A9"), "Beams should have the correct gray color");
        assertTrue(svgOutput.contains("stroke-width:2px"), "Beams should have the correct stroke width");
    }

    @Test
    public void testAddRafters() {
        // Arrange
        CarportSVG carport = new CarportSVG(500, 700, 0, 0);

        // Act
        String svgOutput = carport.generateSVG();

        // Assert
        assertTrue(svgOutput.contains("width=\"500\""), "Rafters should span the width of the carport");
        assertTrue(svgOutput.contains("height=\"4.5\""), "Rafters should have correct height");
    }

    @Test
    public void testAddPoles() {
        // Arrange
        CarportSVG carport = new CarportSVG(400, 600, 0, 0);

        // Act
        String svgOutput = carport.generateSVG();

        // Assert
        assertTrue(svgOutput.contains("fill:#654321"), "Poles should be brown in color");
        assertTrue(svgOutput.contains("width=\"10\""), "Poles should have the correct width");
    }

    @Test
    public void testAddShedAndPoles() {
        // Arrange
        CarportSVG carport = new CarportSVG(500, 700, 150, 200);

        // Act
        String svgOutput = carport.generateSVG();



        System.out.println(svgOutput);

        // Assert
        assertTrue(svgOutput.contains("Redskabsrum: 150 x 200 cm"), "SVG should include the shed text");
        assertTrue(svgOutput.matches("(?s).*fill=\"#ccc\".*"), "Shed should have the correct gray fill");
        assertTrue(svgOutput.contains("fill:#654321"), "Poles for the shed should be included");
    }
}
