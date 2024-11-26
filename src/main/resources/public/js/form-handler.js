// Fetch selected values from the form
const form = document.getElementById('custom-garage-form');

form.addEventListener('submit', (event) => {
    event.preventDefault(); // Prevent form submission for now

    // Get values from the dropdowns
    const width = document.getElementById('width').value;
    const length = document.getElementById('length').value;
    const height = document.getElementById('height').value;
    const roofType = document.getElementById('roofType').value;
    const material = document.getElementById('material').value;

    console.log(`Selected Dimensions: ${width}m x ${length}m x ${height}m`);
    console.log(`Roof Type: ${roofType}`);
    console.log(`Material: ${material}`);

    // Call updatePreview or other relevant functions
    updatePreview({ width, length, height, roofType, material });
});
