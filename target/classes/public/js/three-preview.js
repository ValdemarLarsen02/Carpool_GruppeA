// Initialize Three.js
const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera(75, 2, 0.1, 1000); // Field of view, aspect ratio, near/far clipping planes
const renderer = new THREE.WebGLRenderer({ antialias: true });
renderer.setSize(window.innerWidth / 2, 400); // Adjust the canvas size

// Attach the renderer to the HTML element
const previewContainer = document.getElementById('garage-preview');
const canvas = renderer.domElement;
previewContainer.appendChild(canvas);

// Add a light source
const light = new THREE.DirectionalLight(0xffffff, 1);
light.position.set(5, 5, 5).normalize();
scene.add(light);

// Create a basic cube to represent the garage
let garage;
const geometry = new THREE.BoxGeometry(1, 1, 1); // Initial dimensions (1x1x1 meters)
const material = new THREE.MeshLambertMaterial({ color: 0x0077ff });
garage = new THREE.Mesh(geometry, material);
scene.add(garage);

// Set the camera position
camera.position.z = 5; // Move the camera away from the origin

// Function to render the scene
function animate() {
    requestAnimationFrame(animate);
    renderer.render(scene, camera);
}
animate();

// Update the garage's dimensions based on form inputs
function updatePreview(width, length, height) {
    garage.scale.set(width, height, length); // Adjust the dimensions of the cube
    garage.geometry.translate(0, height / 2, 0); // Position the garage correctly
}
document.getElementById('custom-garage-form').addEventListener('input', function () {
    const width = parseFloat(document.getElementById('width').value) || 1;
    const length = parseFloat(document.getElementById('length').value) || 1;
    const height = parseFloat(document.getElementById('height').value) || 1;

    // Update the preview
    updatePreview(width, length, height);
});
function addExtrasToGarage(options) {
    // Add windows
    if (options.windows) {
        const windowGeometry = new THREE.BoxGeometry(0.5, 1, 0.1);
        const windowMaterial = new THREE.MeshLambertMaterial({ color: 0xcccccc });
        const window = new THREE.Mesh(windowGeometry, windowMaterial);
        window.position.set(0, 1, 2); // Adjust position relative to garage
        garage.add(window);
    }

    // Add extra doors
    if (options.doors) {
        const doorGeometry = new THREE.BoxGeometry(1, 2, 0.1);
        const doorMaterial = new THREE.MeshLambertMaterial({ color: 0x654321 });
        const door = new THREE.Mesh(doorGeometry, doorMaterial);
        door.position.set(0, 0, -2); // Adjust position relative to garage
        garage.add(door);
    }

    // Add gutters (dummy representation)
    if (options.gutters) {
        const gutterGeometry = new THREE.CylinderGeometry(0.1, 0.1, 5);
        const gutterMaterial = new THREE.MeshLambertMaterial({ color: 0x333333 });
        const gutter = new THREE.Mesh(gutterGeometry, gutterMaterial);
        gutter.position.set(-2, 2, 0); // Adjust position relative to garage
        garage.add(gutter);
    }
}
