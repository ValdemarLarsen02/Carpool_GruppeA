// filter-carports.js

document.getElementById('filter-form').addEventListener('submit', async function (e) {
    e.preventDefault();

    // Collect filter values
    const minWidth = document.getElementById('minWidth').value;
    const maxWidth = document.getElementById('maxWidth').value;
    const roofType = document.getElementById('roofTypeFilter').value;

    // Construct query parameters
    const params = new URLSearchParams();
    if (minWidth) params.append('minWidth', minWidth);
    if (maxWidth) params.append('maxWidth', maxWidth);
    if (roofType) params.append('roofType', roofType);

    // Fetch filtered carports
    const carports = await fetchData(`/api/carports?${params.toString()}`);

    // Update carport display
    const carportList = document.getElementById('carport-list');
    carportList.innerHTML = '';
    carports.forEach((carport) => {
        const carportItem = document.createElement('div');
        carportItem.className = 'carport-item';
        carportItem.innerHTML = `
      <h3>${carport.dimensions} - ${carport.roofType}</h3>
      <p>Price: $${carport.price}</p>
    `;
        carportList.appendChild(carportItem);
    });
});
