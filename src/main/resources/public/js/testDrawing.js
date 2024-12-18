let width = 240; // standardbredde i cm
let length = 240; // standardlængde i cm
let shed = false;
let shedWidthValue = 0;
let shedLengthValue = 0;

const canvas = document.getElementById("carportCanvas");
const ctx = canvas.getContext("2d");

// Skalering
const canvasWidth = canvas.width; // Canvas bredde
const canvasHeight = canvas.height; // Canvas højde

// Input-elementer
let carportWidth = document.getElementById("carportWidth");
let carportLength = document.getElementById("carportLength");
let shedWidth = document.getElementById("shedWidth");
let shedLength = document.getElementById("shedLength");

// Event listeners for inputs
carportWidth.addEventListener("change", function () {
    width = parseInt(this.value);
    updateDrawing();
});

carportLength.addEventListener("change", function () {
    length = parseInt(this.value);
    updateDrawing();
});

shedWidth.addEventListener("change", function () {
    shedWidthValue = parseInt(this.value) || 0;
    updateDrawing();
});

shedLength.addEventListener("change", function () {
    shedLengthValue = parseInt(this.value) || 0;
    shed = shedLengthValue > 0;
    updateDrawing();
});

// Tegner Carport
function updateDrawing() {
    ctx.clearRect(0, 0, canvasWidth, canvasHeight); // Rydder canvas

    // Beregn skalering, så tegningen passer på canvas
    const scaleX = canvasWidth / (width + 20); // 20 ekstra plads på siderne
    const scaleY = canvasHeight / (length + 20);
    const scale = Math.min(scaleX, scaleY); // Brug den mindste skaleringsfaktor

    const scaledWidth = width * scale;
    const scaledLength = length * scale;

    // Tegn carport
    ctx.strokeStyle = "black";
    ctx.lineWidth = 2;
    ctx.strokeRect(10, 10, scaledWidth, scaledLength);

    // Hvis der er et skur, tegn det i det ene hjørne
    if (shed) {
        const scaledShedWidth = shedWidthValue * scale;
        const scaledShedLength = shedLengthValue * scale;
        ctx.fillStyle = "rgba(100, 100, 100, 0.5)";
        ctx.fillRect(10 + (scaledWidth - scaledShedWidth), 10, scaledShedWidth, scaledShedLength);
        ctx.strokeRect(10 + (scaledWidth - scaledShedWidth), 10, scaledShedWidth, scaledShedLength);
    }

    // Tilføj dimensionstekst
    ctx.font = "14px Arial";
    ctx.fillStyle = "black";
    ctx.fillText(`Bredde: ${width} cm`, 10, scaledLength + 30);
    ctx.fillText(`Længde: ${length} cm`, 10, scaledLength + 50);
    if (shed) {
        ctx.fillText(`Skur: ${shedWidthValue} x ${shedLengthValue} cm`, 10, scaledLength + 70);
    }
}

// Start med standard tegning
updateDrawing();
