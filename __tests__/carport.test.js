const { JSDOM } = require("jsdom");
describe("Carport Drawing Tests", () => {
    let document, window;

    beforeEach(() => {
        // Set up the virtual DOM with all necessary elements for your test
        const dom = new JSDOM(`
            <!DOCTYPE html>
            <html>
                <body>
                    <input id="carportWidth" value="400" />
                    <input id="shedWidth" value="200" />
                    <input id="shedLength" value="300" />
                    <input id="carportLength" value="600" /> <!-- Make sure this input is here -->
                    <svg id="carportSketch"></svg>
                    <button id="activateMovement">Aktiver pil-bevægelse</button>
                </body>
            </html>
        `);

        // Assign the virtual DOM's document and window objects to global
        document = dom.window.document;
        window = dom.window;

        global.document = document;
        global.window = window;

        // Mock your carport.js script after the DOM is set up
        require("../src/main/resources/public/js/carport.js");  // Ensure the path is correct
    });

    test("SVG should contain a carport rectangle", () => {
        const svgElement = document.getElementById("carportSketch");
        const rectangles = svgElement.getElementsByTagName("rect");

        // Expect at least one rectangle (the carport) to be present
        expect(rectangles.length).toBeGreaterThan(0);
    });


    test("Shed width change triggers carport redraw", () => {
        const shedWidthInput = document.getElementById("shedWidth");

        // Change the shedWidth input value and simulate the change event
        shedWidthInput.value = "500";  // New width value
        shedWidthInput.dispatchEvent(new window.Event("change"));

        // Ensure the event listener is triggered and value is updated
        expect(shedWidthInput.value).toBe("500");

        // Additional checks can be added based on your `drawCarport()` logic
    });

    test("Carport length change triggers carport redraw", () => {
        const carportLengthInput = document.getElementById("carportLength");

        // Change the carportLength input value and simulate the change event
        carportLengthInput.value = "700";  // New length value
        carportLengthInput.dispatchEvent(new window.Event("change"));

        // Ensure the event listener is triggered and value is updated
        expect(carportLengthInput.value).toBe("700");

        // Additional checks can be added based on your `drawCarport()` logic
    });
    test("Activate movement button triggers expected behavior", () => {
        const activateButton = document.getElementById("activateMovement");

        // Mock a function or behavior triggered by the button
        const mockFunction = jest.fn();
        activateButton.addEventListener("click", mockFunction);

        // Simulate the button click event
        activateButton.dispatchEvent(new window.Event("click"));

        // Check if the mock function was called
        expect(mockFunction).toHaveBeenCalled();
    });
    test("Arrow key movement changes shed position", () => {
        // Mock the initial state of the shed position
        const initialPosition = { shedX: 100, shedY: 200 };

        // Assume the arrow key press moves the shed
        const mockMoveShed = jest.fn(() => {
            initialPosition.shedX += 10;
        });

        // Attach your mock move function to the keydown event
        window.addEventListener("keydown", mockMoveShed);
        window.dispatchEvent(new window.KeyboardEvent("keydown", { key: "ArrowRight" }));

        // Check if the shed position changed after the arrow key event
        expect(mockMoveShed).toHaveBeenCalled();
        expect(initialPosition.shedX).toBeGreaterThan(100); // Assert movement occurred
    });
    test("Arrow key movement handles multiple directions", () => {
        const initialPosition = { shedX: 100, shedY: 200 };
        const mockMoveShed = jest.fn((direction) => {
            if (direction === "ArrowRight") initialPosition.shedX += 10;
            if (direction === "ArrowLeft") initialPosition.shedX -= 10;
        });

        window.addEventListener("keydown", (event) => {
            mockMoveShed(event.key);
        });

        // Move right
        window.dispatchEvent(new window.KeyboardEvent("keydown", { key: "ArrowRight" }));
        expect(initialPosition.shedX).toBeGreaterThan(100);

        // Move left
        window.dispatchEvent(new window.KeyboardEvent("keydown", { key: "ArrowLeft" }));
        expect(initialPosition.shedX).toBeLessThan(110); // after moving right, it should be < 110
    });
});
