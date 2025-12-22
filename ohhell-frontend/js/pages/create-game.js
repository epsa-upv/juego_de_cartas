const token = localStorage.getItem('token');
if (!token) {
    window.location.href = 'login.html';
}

// =======================
// DOM Elements
// =======================
const form = document.getElementById('createGameForm');
const gameTitleInput = document.getElementById('game-title');
const numRoundsSlider = document.getElementById('num-rounds');
const startingCardsSlider = document.getElementById('starting-cards');
const numRoundsValue = document.getElementById('num-rounds-value');
const startingCardsValue = document.getElementById('starting-cards-value');
const summaryRounds = document.getElementById('summary-rounds');
const summaryCards = document.getElementById('summary-cards');
const createBtn = document.getElementById('create-btn');

// =======================
// Update Values on Slider Change
// =======================
numRoundsSlider.addEventListener('input', () => {
    const value = numRoundsSlider.value;
    numRoundsValue.textContent = value;
    summaryRounds.textContent = value;
});

startingCardsSlider.addEventListener('input', () => {
    const value = startingCardsSlider.value;
    startingCardsValue.textContent = value;
    summaryCards.textContent = value;
});

// =======================
// Form Submit
// =======================
form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const title = gameTitleInput.value.trim() || 'Oh Hell!';
    const numRounds = parseInt(numRoundsSlider.value);
    const startingCards = parseInt(startingCardsSlider.value);

    createBtn.disabled = true;
    createBtn.classList.add('loading');

    try {
        // Crear partida (backend crea con valores por defecto)
        const game = await gameApi.createGame(title);

        // TODO: Si necesitas pasar configuración personalizada,
        // deberías agregar un endpoint en el backend para actualizar estos valores
        // Por ahora, se crean con valores por defecto

        // Redirigir a sala de espera
        window.location.href = `waiting-room.html?code=${game.code}`;

    } catch (error) {
        console.error(error);
        alert(`❌ Error al crear la partida: ${error.message}`);
        createBtn.disabled = false;
        createBtn.classList.remove('loading');
    }
});