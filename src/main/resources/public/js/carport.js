// Initialiser variabler til bevægelseskontrol
let movementModeActive = false;
let moveSpeed = 10; // Hastigheden for bevægelse når pil-tasterne trykkes

// Vælg SVG-elementer og knappen til at aktivere bevægelse
const svgElement = document.getElementById("carportSketch");
const activateMovementButton = document.getElementById("activateMovement");

// Initiale mål for carporten og skuret
let carportWidth = 400;
let carportLength = 600;
let shedWidth = 200;
let shedLength = 150;
let shedX = 600;  // Skurets initiale position
let shedY = 400;

const carport = document.createElementNS("http://www.w3.org/2000/svg", "rect");
const shed = document.createElementNS("http://www.w3.org/2000/svg", "rect");

activateMovementButton.addEventListener("click", () => {
    movementModeActive = !movementModeActive; // Skift bevægelsestilstand
    activateMovementButton.textContent = movementModeActive
        ? "Deaktiver pil-bevægelse"
        : "Aktiver pil-bevægelse";

    if (movementModeActive) {
        window.addEventListener("keydown", handleArrowKeyMovement);
    } else {
        window.removeEventListener("keydown", handleArrowKeyMovement);
    }
});

// Funktion til at håndtere pil-tast bevægelser
function handleArrowKeyMovement(e) {
    if (!movementModeActive) return;

    // Forhindr standard scroll-adfærd for pil-tasterne
    e.preventDefault();

    // Bevæg carporten eller skuret med pil-tasterne
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

    drawCarport(); // Tegn carporten og skuret igen efter bevægelse
}

// Funktion til at tegne lodrette linjer
function drawVerticalLines(xStart, yStart, carportLength, carportWidth, scaleFactor) {
    const spacing = 55; // Afstand mellem linjer i cm
    const boxWidth = 5; // Bredde af hver træboks i cm (visualiseret som et tyndt rektangel)
    const scaledSpacing = spacing * scaleFactor;
    const scaledBoxWidth = boxWidth * scaleFactor;

    // Beregn det totale antal lodrette linjer, der passer indenfor carportens bredde
    const numberOfLines = Math.floor(carportWidth / spacing);

    // Loop for at skabe hver lodret trælinje
    for (let i = 0; i <= numberOfLines; i++) {
        const xPos = xStart + i * scaledSpacing; // Beregn X-positionen af hver linje

        // Sørg for, at den sidste linje ikke overskrider carportens bredde
        if (xPos > xStart + carportWidth * scaleFactor) break;

        // Tegn den lodrette linje som et lille rektangel
        const verticalLine = document.createElementNS("http://www.w3.org/2000/svg", "rect");
        verticalLine.setAttribute("x", xPos);
        verticalLine.setAttribute("y", yStart);
        verticalLine.setAttribute("width", scaledBoxWidth); // Bredde af træboksen
        verticalLine.setAttribute("height", carportLength * scaleFactor); // Stræk højden til carportens længde
        verticalLine.setAttribute("fill", "white"); // Indersiden af boksen er hvid
        verticalLine.setAttribute("stroke", "black"); // Kantlinje er sort
        verticalLine.setAttribute("stroke-width", "2");

        // Tilføj linjen til SVG-elementet
        svgElement.appendChild(verticalLine);
    }
}

// Funktion til at tegne carporten og skuret
function drawCarport() {
    svgElement.innerHTML = ""; // Fjern den tidligere tegning
    const carportWidth = parseInt(document.getElementById("carportWidth").value, 10); // Bredde i cm
    const carportLength = parseInt(document.getElementById("carportLength").value, 10); // Længde i cm
    const scaleFactor = 0.5; // Eksempel: Skaler cm til pixels for visualisering
    const xStart = 50; // Start X-koordinat for carporten
    const yStart = 50; // Start Y-koordinat for carporten
    drawGrid();

    // Tegn carporten
    const carportRect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
    carportRect.setAttribute("x", 50);
    carportRect.setAttribute("y", 50);
    carportRect.setAttribute("width", carportWidth);
    carportRect.setAttribute("height", carportLength);
    carportRect.setAttribute("fill", "none");
    carportRect.setAttribute("stroke", "black");
    carportRect.setAttribute("stroke-width", "2");
    svgElement.appendChild(carportRect);

    // Tilføj målelinjer for carporten
    addMeasurementLine(50, 50, 50 + carportWidth, 50, `${carportWidth} cm`); // Øverste bredde
    addMeasurementLine(50, 50, 50, 50 + carportLength, `${carportLength} cm`); // Venstre højde

    drawVerticalLines(xStart, yStart, carportLength, carportWidth, scaleFactor);

    // Tilføj stolper til carporten (4 hjørner)
    addPoles(50, 50); // Øverste venstre hjørne af carporten
    addPoles(50 + carportWidth, 50); // Øverste højre hjørne af carporten
    addPoles(50, 50 + carportLength); // Nederste venstre hjørne af carporten
    addPoles(50 + carportWidth, 50 + carportLength); // Nederste højre hjørne af carporten

    // Tilføj stolper mellem hjørnerne (carporten)
    addPolesBetween(50, 50, 50 + carportWidth, 50); // Øverste kant (venstre-højre)
    addPolesBetween(50, 50, 50, 50 + carportLength); // Venstre kant (top-bund)
    addPolesBetween(50 + carportWidth, 50, 50 + carportWidth, 50 + carportLength); // Højre kant (top-bund)
    addPolesBetween(50, 50 + carportLength, 50 + carportWidth, 50 + carportLength); // Nederste kant (venstre-højre)

    // Tegn skuret
    const shedRect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
    shedRect.setAttribute("x", shedX);
    shedRect.setAttribute("y", shedY);
    shedRect.setAttribute("width", shedWidth);
    shedRect.setAttribute("height", shedLength);
    shedRect.setAttribute("fill", "none");
    shedRect.setAttribute("stroke", "black");
    shedRect.setAttribute("stroke-width", "2");
    svgElement.appendChild(shedRect);

    // Tilføj målelinjer for skuret
    addMeasurementLine(shedX, shedY, shedX + shedWidth, shedY, `${shedWidth} cm`); // Øverste bredde
    addMeasurementLine(shedX, shedY, shedX, shedY + shedLength, `${shedLength} cm`); // Venstre højde

    // Tilføj stolper til skuret (4 hjørner)
    addPoles(shedX, shedY); // Øverste venstre hjørne
    addPoles(shedX + shedWidth, shedY); // Øverste højre hjørne
    addPoles(shedX, shedY + shedLength); // Nederste venstre hjørne
    addPoles(shedX + shedWidth, shedY + shedLength); // Nederste højre hjørne

    // Tilføj stolper mellem hjørnerne (skuret)
    addPolesBetween(shedX, shedY, shedX + shedWidth, shedY); // Øverste kant (venstre-højre)
    addPolesBetween(shedX, shedY, shedX, shedY + shedLength); // Venstre kant (top-bund)
    addPolesBetween(shedX + shedWidth, shedY, shedX + shedWidth, shedY + shedLength); // Højre kant (top-bund)
    addPolesBetween(shedX, shedY + shedLength, shedX + shedWidth, shedY + shedLength); // Nederste kant (venstre-højre)
}

// Funktion til at tilføje stolper mellem to punkter
function addPolesBetween(x1, y1, x2, y2) {
    const midX = (x1 + x2) / 2;
    const midY = (y1 + y2) / 2;
    addPoles(midX, midY); // Tilføj en stolpe ved midtpunktet
}

// Funktion til at tegne et tættere gitter
function drawGrid() {
    const gridSpacing = 20; // Gitterets tæthed
    const width = 800;
    const height = 600;

    // Tegn lodrette linjer
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

    // Tegn vandrette linjer
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

// Funktion til at tilføje målelinjer
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

// Funktion til at tilføje stolper (større firkanter ved hjørner og midtpunkter)
function addPoles(x, y) {
    const pole = document.createElementNS("http://www.w3.org/2000/svg", "rect");
    pole.setAttribute("x", x - 5);
    pole.setAttribute("y", y - 5);
    pole.setAttribute("width", 10);
    pole.setAttribute("height", 10);
    pole.setAttribute("fill", "black");
    svgElement.appendChild(pole);
}

// Event listener for formularændringer (opdater carportens og skurets mål)
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

// Initial carport tegning
drawCarport();
module.exports = { drawCarport, drawVerticalLines };
