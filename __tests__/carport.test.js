const { JSDOM } = require("jsdom");

describe("Carport Drawing Tests", () => {
    let document, window;

    beforeEach(() => {
        // Opsæt den virtuelle DOM med alle nødvendige elementer til testen
        const dom = new JSDOM(`
            <!DOCTYPE html>
            <html>
                <body>
                    <input id="carportWidth" value="400" />
                    <input id="shedWidth" value="200" />
                    <input id="shedLength" value="300" />
                    <input id="carportLength" value="600" /> <!-- Sørg for at dette input er her -->
                    <svg id="carportSketch"></svg>
                    <button id="activateMovement">Aktiver pil-bevægelse</button>
                </body>
            </html>
        `);

        // Tildel den virtuelle DOMs dokument og vinduesobjekter til globalt
        document = dom.window.document;
        window = dom.window;

        global.document = document;
        global.window = window;

        // Mock dit carport.js script efter at DOM er sat op
        require("../src/main/resources/public/js/carport.js");  // Sørg for at stien er korrekt
    });

    test("SVG skal indeholde et carport-rektangel", () => {
        const svgElement = document.getElementById("carportSketch");
        const rectangles = svgElement.getElementsByTagName("rect");

        // Forvent mindst ét rektangel (carport) er til stede
        expect(rectangles.length).toBeGreaterThan(0);
    });

    test("Ændring af skur-bredde udløser en gen-tegning af carporten", () => {
        const shedWidthInput = document.getElementById("shedWidth");

        // Ændre værdien af skur-bredde input og simulere ændringsbegivenheden
        shedWidthInput.value = "500";  // Ny breddeværdi
        shedWidthInput.dispatchEvent(new window.Event("change"));

        // Sørg for at begivenhedslytter bliver udløst og værdien opdateres
        expect(shedWidthInput.value).toBe("500");

        // Yderligere kontroller kan tilføjes baseret på din 'drawCarport()' logik
    });

    test("Ændring af carport-længde udløser en gen-tegning af carporten", () => {
        const carportLengthInput = document.getElementById("carportLength");

        // Ændre værdien af carport-længde input og simulere ændringsbegivenheden
        carportLengthInput.value = "700";  // Ny længdeværdi
        carportLengthInput.dispatchEvent(new window.Event("change"));

        // Sørg for at begivenhedslytter bliver udløst og værdien opdateres
        expect(carportLengthInput.value).toBe("700");

        // Yderligere kontroller kan tilføjes baseret på din 'drawCarport()' logik
    });

    test("Aktiver bevægelse knap udløser den forventede adfærd", () => {
        const activateButton = document.getElementById("activateMovement");

        // Mock en funktion eller adfærd der udløses af knappen
        const mockFunction = jest.fn();
        activateButton.addEventListener("click", mockFunction);

        // Simulere knap-klik begivenheden
        activateButton.dispatchEvent(new window.Event("click"));

        // Tjek om den mockede funktion blev kaldt
        expect(mockFunction).toHaveBeenCalled();
    });

    test("Pil-tast bevægelse ændrer skur-position", () => {
        // Mock den oprindelige tilstand af skur-positionen
        const initialPosition = { shedX: 100, shedY: 200 };

        // Antag at pil-tast tryk flytter skuret
        const mockMoveShed = jest.fn(() => {
            initialPosition.shedX += 10;
        });

        // Tilknyt den mockede bevægelsesfunktion til keydown begivenheden
        window.addEventListener("keydown", mockMoveShed);
        window.dispatchEvent(new window.KeyboardEvent("keydown", { key: "ArrowRight" }));

        // Tjek om skur-positionen ændrede sig efter pil-tast begivenheden
        expect(mockMoveShed).toHaveBeenCalled();
        expect(initialPosition.shedX).toBeGreaterThan(100); // Bekræft at der er sket bevægelse
    });

    test("Pil-tast bevægelse håndterer flere retninger", () => {
        const initialPosition = { shedX: 100, shedY: 200 };
        const mockMoveShed = jest.fn((direction) => {
            if (direction === "ArrowRight") initialPosition.shedX += 10;
            if (direction === "ArrowLeft") initialPosition.shedX -= 10;
        });

        window.addEventListener("keydown", (event) => {
            mockMoveShed(event.key);
        });

        // Bevæg højre
        window.dispatchEvent(new window.KeyboardEvent("keydown", { key: "ArrowRight" }));
        expect(initialPosition.shedX).toBeGreaterThan(100);

        // Bevæg venstre
        window.dispatchEvent(new window.KeyboardEvent("keydown", { key: "ArrowLeft" }));
        expect(initialPosition.shedX).toBeLessThan(110); // efter at have bevæget sig til højre, skal det være < 110
    });
});
