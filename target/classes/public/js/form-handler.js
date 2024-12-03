const form = document.getElementById('custom-garage-form');

form.addEventListener('submit', (event) => {
    event.preventDefault();

    // Fetch selected values
    const garageWidth = document.getElementById('garageWidth').value;
    const garageLength = document.getElementById('garageLength').value;
    const shedWidth = document.getElementById('shedWidth').value;
    const shedLength = document.getElementById('shedLength').value;
    const roofType = document.getElementById('roofType').value;
    const material = document.getElementById('material').value;
    const specialRequests = document.getElementById('specialRequests').value;

    console.log(`Garage: ${garageWidth}cm x ${garageLength}cm`);
    console.log(`Shed: ${shedWidth || 'None'} x ${shedLength || 'None'}`);
    console.log(`Roof Type: ${roofType}, Material: ${material}`);
    console.log(`Special Requests: ${specialRequests}`);

    // Use these values for further processing
    updatePreview({
        garageWidth,
        garageLength,
        shedWidth,
        shedLength,
        roofType,
        material,
        specialRequests,
    });
});

function updatePreview(data) {
    // Process the 3D visualization and backend logic
    console.log('Updating preview with:', data);
}
