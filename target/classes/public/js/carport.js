// Initialize variables for movement control
let movementModeActive = false;
let moveSpeed = 10; // Speed of movement when arrow keys are pressed

// Select SVG elements and the button for activating movement
const svgElement = document.getElementById("carportSketch");
const activateMovementButton = document.getElementById("activateMovement");

// Initial carport and shed dimensions
let carportWidth = 400;
let carportLength = 600;
let shedWidth = 200;
let shedLength = 150;
let shedX = 600;  // Initial position of the shed
let shedY = 400;

const carport = document.createElementNS("http://www.w3.org/2000/svg", "rect");
const shed = document.createElementNS("http://www.w3.org/2000/svg", "rect");

activateMovementButton.addEventListener("click", () => {
    movementModeActive = !movementModeActive; // Toggle the movement mode
    activateMovementButton.textContent = movementModeActive
        ? "Deaktiver pil-bevægelse"
        : "Aktiver pil-bevægelse";

    if (movementModeActive) {
        window.addEventListener("keydown", handleArrowKeyMovement);
    } else {
        window.removeEventListener("keydown", handleArrowKeyMovement);
    }
});

// Function to handle arrow key movements
function handleArrowKeyMovement(e) {
    if (!movementModeActive) return;

    // Prevent default scrolling behavior of arrow keys
    e.preventDefault();

    // Move the carport or shed with the arrow keys
    switch (e.key) {
        case "ArrowUp":
            shedY -= moveSpeed;
            break;
        case "ArrowDown":
            shedY += moveSpeed;
            break;
        case "ArrowLeft":
            shedX -= moveSpeed;
            break;
        case "ArrowRight":
            shedX += moveSpeed;
            break;
        default:
            return;
    }

    drawCarport(); // Re-draw the carport and shed after moving
}
function drawVerticalLines(xStart, yStart, carportLength, carportWidth, scaleFactor) {
    const spacing = 55; // Space between lines in cm
    const boxWidth = 5; // Width of each wooden box in cm (visualized as a thin rectangle)
    const scaledSpacing = spacing * scaleFactor;
    const scaledBoxWidth = boxWidth * scaleFactor;

    // Calculate total number of vertical lines that fit within the carport width
    const numberOfLines = Math.floor(carportWidth / spacing);

    // Loop to create each vertical wooden line
    for (let i = 0; i <= numberOfLines; i++) {
        const xPos = xStart + i * scaledSpacing; // Calculate the X position of each line

        // Ensure that the last line does not exceed the carport width
        if (xPos > xStart + carportWidth * scaleFactor) break;

        // Draw the vertical line as a small rectangle
        const verticalLine = document.createElementNS("http://www.w3.org/2000/svg", "rect");
        verticalLine.setAttribute("x", xPos);
        verticalLine.setAttribute("y", yStart);
        verticalLine.setAttribute("width", scaledBoxWidth); // Width of the wooden box
        verticalLine.setAttribute("height", carportLength * scaleFactor); // Stretch height to carport length
        verticalLine.setAttribute("fill", "white"); // Inside of the box is white
        verticalLine.setAttribute("stroke", "black"); // Outline is black
        verticalLine.setAttribute("stroke-width", "2");

        // Add the line to the SVG element
        svgElement.appendChild(verticalLine);
    }
}
// Function to draw the carport and shed
function drawCarport() {
    svgElement.innerHTML = ""; // Clear the previous drawing
    const carportWidth = parseInt(document.getElementById("carportWidth").value, 10); // Width in cm
    const carportLength = parseInt(document.getElementById("carportLength").value, 10); // Length in cm
    const scaleFactor = 0.5; // Example: Scale cm to pixels for visualization
    const xStart = 50; // Starting X coordinate for the carport
    const yStart = 50; // Starting Y coordinate for the carport
    drawGrid();

    // Draw the carport
    const carportRect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
    carportRect.setAttribute("x", 50);
    carportRect.setAttribute("y", 50);
    carportRect.setAttribute("width", carportWidth);
    carportRect.setAttribute("height", carportLength);
    carportRect.setAttribute("fill", "none");
    carportRect.setAttribute("stroke", "black");
    carportRect.setAttribute("stroke-width", "2");
    svgElement.appendChild(carportRect);

    // Add measurement lines for the carport
    addMeasurementLine(50, 50, 50 + carportWidth, 50, `${carportWidth} cm`); // Top width
    addMeasurementLine(50, 50, 50, 50 + carportLength, `${carportLength} cm`); // Left height

    drawVerticalLines(xStart, yStart, carportLength, carportWidth, scaleFactor);

    // Add poles to the carport (4 corners)
    addPoles(50, 50); // Top-left corner of carport
    addPoles(50 + carportWidth, 50); // Top-right corner of carport
    addPoles(50, 50 + carportLength); // Bottom-left corner of carport
    addPoles(50 + carportWidth, 50 + carportLength); // Bottom-right corner of carport

    // Add poles between the corners (carport)
    addPolesBetween(50, 50, 50 + carportWidth, 50); // Top edge (left-right)
    addPolesBetween(50, 50, 50, 50 + carportLength); // Left edge (top-bottom)
    addPolesBetween(50 + carportWidth, 50, 50 + carportWidth, 50 + carportLength); // Right edge (top-bottom)
    addPolesBetween(50, 50 + carportLength, 50 + carportWidth, 50 + carportLength); // Bottom edge (left-right)

    // Draw the shed
    const shedRect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
    shedRect.setAttribute("x", shedX);
    shedRect.setAttribute("y", shedY);
    shedRect.setAttribute("width", shedWidth);
    shedRect.setAttribute("height", shedLength);
    shedRect.setAttribute("fill", "none");
    shedRect.setAttribute("stroke", "black");
    shedRect.setAttribute("stroke-width", "2");
    svgElement.appendChild(shedRect);

    // Add measurement lines for the shed
    addMeasurementLine(shedX, shedY, shedX + shedWidth, shedY, `${shedWidth} cm`); // Top width
    addMeasurementLine(shedX, shedY, shedX, shedY + shedLength, `${shedLength} cm`); // Left height

    // Add poles to the shed (4 corners)
    addPoles(shedX, shedY); // Top-left corner
    addPoles(shedX + shedWidth, shedY); // Top-right corner
    addPoles(shedX, shedY + shedLength); // Bottom-left corner
    addPoles(shedX + shedWidth, shedY + shedLength); // Bottom-right corner

    // Add poles between the corners (shed)
    addPolesBetween(shedX, shedY, shedX + shedWidth, shedY); // Top edge (left-right)
    addPolesBetween(shedX, shedY, shedX, shedY + shedLength); // Left edge (top-bottom)
    addPolesBetween(shedX + shedWidth, shedY, shedX + shedWidth, shedY + shedLength); // Right edge (top-bottom)
    addPolesBetween(shedX, shedY + shedLength, shedX + shedWidth, shedY + shedLength); // Bottom edge (left-right)
}

// Function to add poles between two points
function addPolesBetween(x1, y1, x2, y2) {
    const midX = (x1 + x2) / 2;
    const midY = (y1 + y2) / 2;
    addPoles(midX, midY); // Add a pole at the midpoint
}
// Function to draw a denser grid
function drawGrid() {
    const gridSpacing = 20; // Grid density
    const width = 800;
    const height = 600;

    // Draw vertical lines
    for (let x = 0; x <= width; x += gridSpacing) {
        const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
        line.setAttribute("x1", x);
        line.setAttribute("y1", 0);
        line.setAttribute("x2", x);
        line.setAttribute("y2", height);
        line.setAttribute("stroke", "#ddd");
        line.setAttribute("stroke-width", "1");
        svgElement.appendChild(line);
    }

    // Draw horizontal lines
    for (let y = 0; y <= height; y += gridSpacing) {
        const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
        line.setAttribute("x1", 0);
        line.setAttribute("y1", y);
        line.setAttribute("x2", width);
        line.setAttribute("y2", y);
        line.setAttribute("stroke", "#ddd");
        line.setAttribute("stroke-width", "1");
        svgElement.appendChild(line);
    }
}

// Function to add measurement lines
function addMeasurementLine(x1, y1, x2, y2, text) {
    const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
    line.setAttribute("x1", x1);
    line.setAttribute("y1", y1);
    line.setAttribute("x2", x2);
    line.setAttribute("y2", y2);
    line.setAttribute("stroke", "black");
    line.setAttribute("stroke-width", "1");
    svgElement.appendChild(line);

    const textElement = document.createElementNS("http://www.w3.org/2000/svg", "text");
    textElement.setAttribute("x", (x1 + x2) / 2);
    textElement.setAttribute("y", (y1 + y2) / 2);
    textElement.setAttribute("text-anchor", "middle");
    textElement.setAttribute("dy", "-10");
    textElement.textContent = text;
    svgElement.appendChild(textElement);
}

// Function to add poles (larger squares at corners and midpoints)
function addPoles(x, y) {
    const pole = document.createElementNS("http://www.w3.org/2000/svg", "rect");
    pole.setAttribute("x", x - 5);
    pole.setAttribute("y", y - 5);
    pole.setAttribute("width", 10);
    pole.setAttribute("height", 10);
    pole.setAttribute("fill", "black");
    svgElement.appendChild(pole);
}
// Event listener for form changes (update carport and shed dimensions)
document.getElementById("carportWidth").addEventListener("change", (event) => {
    carportWidth = parseInt(event.target.value, 10);
    drawCarport();
});

document.getElementById("carportLength").addEventListener("change", (event) => {
    carportLength = parseInt(event.target.value, 10);
    drawCarport();
});

document.getElementById("shedWidth").addEventListener("change", (event) => {
    shedWidth = parseInt(event.target.value, 10);
    drawCarport();
});

document.getElementById("shedLength").addEventListener("change", (event) => {
    shedLength = parseInt(event.target.value, 10);
    drawCarport();
});
// Initial carport drawing
drawCarport();
