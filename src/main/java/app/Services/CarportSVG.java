package app.services;

public class CarportSVG {
    private int carportWidth;
    private int carportLength;
    private int shedWidth;
    private int shedLength;

    public CarportSVG(int carportWidth, int carportLength, int shedWidth, int shedLength) {
        this.carportWidth = carportWidth;
        this.carportLength = carportLength;
        this.shedWidth = shedWidth;
        this.shedLength = shedLength;
    }

    public String generateSVG() {
        StringBuilder svg = new StringBuilder();

        // Starter vores svg tag.
        svg.append("<svg width=\"").append(carportWidth + 50).append("\" height=\"").append(carportLength + 50).append("\" xmlns=\"http://www.w3.org/2000/svg\" style=\"background-color: #f9f9f9;\">\n");

        // Tegner vores "firkant/rektangel"
        svg.append("    <rect x=\"25\" y=\"25\" width=\"").append(carportWidth).append("\" height=\"").append(carportLength).append("\" fill=\"none\" stroke=\"black\" stroke-width=\"2\" />\n");

        // Tilføj bjælker
        addBeams(svg);

        // Tilføj spær med korrekt placering og længde | har brugt data fra: https://github.com/dat2Cph/svg/blob/main/src/main/java/dat/services/CarportSvg.java
        addRafters(svg, 55.714);

        // Tilføj redskabsrum, hvis angivet
        if (shedWidth > 0 && shedLength > 0) {
            addShed(svg);
        }

        // Tilføjer vores stolper i siderne.
        addPoles(svg);

        // Tilføjer vores målestok tekst.
        svg.append("    <text x=\"").append(carportWidth / 2 + 25).append("\" y=\"15\" font-size=\"14\">").append("Carport: ").append(carportWidth).append(" x ").append(carportLength).append(" cm").append("</text>\n");

        // SVG luk tag så vi kan return korrekt og læse det på frontend.
        svg.append("</svg>");
        return svg.toString();
    }

    private void addBeams(StringBuilder svg) {
        int beamWidth = 10; // tykkelse
        String beamStyle = "stroke-width:2px; stroke:#000000; fill:#A9A9A9"; // Sætter vores farver..

        svg.append("    <rect x=\"25\" y=\"25\" width=\"").append(beamWidth).append("\" height=\"").append(carportLength).append("\" style=\"").append(beamStyle).append("\" />\n");
        svg.append("    <rect x=\"").append(carportWidth + 15).append("\" y=\"25\" width=\"").append(beamWidth).append("\" height=\"").append(carportLength).append("\" style=\"").append(beamStyle).append("\" />\n");
    }

    private void addRafters(StringBuilder svg, double spacing) {
        for (double y = 25; y <= carportLength + 25; y += spacing) {
            svg.append("    <rect x=\"25\" y=\"").append(y).append("\" width=\"").append(carportWidth).append("\" height=\"4.5\" style=\"stroke:#000000; fill:#ffffff\" />\n");
        }
    }

    private void addShed(StringBuilder svg) {
        // Sætter vores "redskabsrum" i toppen af vores svg.
        int shedX = carportWidth - shedWidth + 25;
        int shedY = 25; // Placeres øverst

        svg.append("    <rect x=\"").append(shedX).append("\" y=\"").append(shedY).append("\" width=\"").append(shedWidth).append("\" height=\"").append(shedLength).append("\" fill=\"#ccc\" stroke=\"black\" stroke-width=\"2\" />\n");

        svg.append("    <text x=\"").append(shedX + 5).append("\" y=\"").append(shedY + 15).append("\" font-size=\"12\">Redskabsrum: ").append(shedWidth).append(" x ").append(shedLength).append(" cm</text>\n");
    }

    private void addPoles(StringBuilder svg) {
        int poleSize = 10;
        String poleStyle = "fill:#654321; stroke:#000000; stroke-width:1";

        // Venstre forreste hjørne
        svg.append("    <rect x=\"25\" y=\"25\" width=\"").append(poleSize).append("\" height=\"").append(poleSize).append("\" style=\"").append(poleStyle).append("\" />\n");

        // Højre forreste hjørne
        svg.append("    <rect x=\"").append(carportWidth + 15).append("\" y=\"25\" width=\"").append(poleSize).append("\" height=\"").append(poleSize).append("\" style=\"").append(poleStyle).append("\" />\n");

        // Venstre bagerste hjørne
        svg.append("    <rect x=\"25\" y=\"").append(carportLength + 15).append("\" width=\"").append(poleSize).append("\" height=\"").append(poleSize).append("\" style=\"").append(poleStyle).append("\" />\n");

        // Højre bagerste hjørne
        svg.append("    <rect x=\"").append(carportWidth + 15).append("\" y=\"").append(carportLength + 15).append("\" width=\"").append(poleSize).append("\" height=\"").append(poleSize).append("\" style=\"").append(poleStyle).append("\" />\n");

        // Tilføjer stolper til vores redskabsskur hvis der er noget "data" på det..
        if (shedWidth > 0 && shedLength > 0) {
            int shedX = carportWidth - shedWidth + 25; // placering af vores redskabsrum.
            int shedY = 25;

            // Venstre forreste hjørne af redskabsrummet
            svg.append("    <rect x=\"").append(shedX).append("\" y=\"").append(shedY).append("\" width=\"").append(poleSize).append("\" height=\"").append(poleSize).append("\" style=\"").append(poleStyle).append("\" />\n");

            // Højre forreste hjørne af redskabsrummet
            svg.append("    <rect x=\"").append(shedX + shedWidth - poleSize).append("\" y=\"").append(shedY).append("\" width=\"").append(poleSize).append("\" height=\"").append(poleSize).append("\" style=\"").append(poleStyle).append("\" />\n");

            // Venstre bagerste hjørne af redskabsrummet
            svg.append("    <rect x=\"").append(shedX).append("\" y=\"").append(shedY + shedLength - poleSize).append("\" width=\"").append(poleSize).append("\" height=\"").append(poleSize).append("\" style=\"").append(poleStyle).append("\" />\n");

            // Højre bagerste hjørne af redskabsrummet
            svg.append("    <rect x=\"").append(shedX + shedWidth - poleSize).append("\" y=\"").append(shedY + shedLength - poleSize).append("\" width=\"").append(poleSize).append("\" height=\"").append(poleSize).append("\" style=\"").append(poleStyle).append("\" />\n");
        }
    }

}
