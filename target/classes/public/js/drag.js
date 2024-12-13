let selectedElement = null;
let offset = { x: 0, y: 0 };

function startDrag(event) {
    if (event.target.tagName === "rect" && event.target.getAttribute("fill") === "brown") {
        selectedElement = event.target;
        const rect = selectedElement.getBoundingClientRect();
        offset = {
            x: event.clientX - rect.x,
            y: event.clientY - rect.y,
        };
    }
}

function drag(event) {
    if (selectedElement) {
        const x = event.clientX - offset.x;
        const y = event.clientY - offset.y;
        selectedElement.setAttribute("x", Math.round(x / 20) * 20); // Snap to grid
        selectedElement.setAttribute("y", Math.round(y / 20) * 20); // Snap to grid
    }
}

function endDrag() {
    selectedElement = null;
}

// Add event listeners
const sketch = document.getElementById("carportSketch");
sketch.addEventListener("mousemove", drag);
sketch.addEventListener("mouseup", endDrag);
sketch.addEventListener("mouseleave", endDrag);