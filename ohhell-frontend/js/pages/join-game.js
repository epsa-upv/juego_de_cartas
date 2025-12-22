const token = localStorage.getItem('token');
if (!token) {
    window.location.href = 'login.html';
}

// =======================
// DOM Elements
// =======================
const gameCodeInput = document.getElementById('game-code-input');
const joinBtn = document.getElementById('join-btn');
const gamesList = document.getElementById('games-list');

// =======================
// Init
// =======================
init();

async function init() {
    await loadAvailableGames();
}

// =======================
// Load Available Games
// =======================
async function loadAvailableGames() {
    try {
        const response = await fetch('/ohhell-api/api/games?status=WAITING', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            gamesList.innerHTML = '<div class="no-games"><p>No hay partidas disponibles en este momento</p></div>';
            return;
        }

        const games = await response.json();

        if (!games || games.length === 0) {
            gamesList.innerHTML = '<div class="no-games"><p>No hay partidas disponibles</p></div>';
            return;
        }

        renderGames(games);

    } catch (error) {
        console.error('Error cargando partidas:', error);
        gamesList.innerHTML = '<div class="no-games"><p>Error al cargar partidas</p></div>';
    }
}

// =======================
// Render Games
// =======================
function renderGames(games) {
    gamesList.innerHTML = '';

    games.forEach(game => {
        const playerCount = game.playerCount || 0;
        const maxPlayers = 4;
        const availableSlots = maxPlayers - playerCount;

        const div = document.createElement('div');
        div.className = 'game-card';

        div.innerHTML = `
            <div class="game-card-header">
                <div class="game-card-id">${game.code}</div>
                <div class="game-players-badge">${playerCount}/${maxPlayers}</div>
            </div>

            <div class="game-card-body">
                <div class="game-info-item">
                    <div class="info-label">Partida</div>
                    <div class="info-value">${game.title || 'Oh Hell!'}</div>
                </div>
                <div class="game-info-item">
                    <div class="info-label">Lugares disponibles</div>
                    <div class="info-value">${availableSlots}</div>
                </div>
                <div class="game-info-item">
                    <div class="info-label">Creada hace</div>
                    <div class="info-value">${getTimeDiff(game.createdAt)}</div>
                </div>
            </div>

            <div class="game-card-footer">
                <button class="btn btn-primary btn-full" onclick="joinGameByCode('${game.code}')">
                    🔓 Unirse
                </button>
            </div>
        `;

        gamesList.appendChild(div);
    });
}

// =======================
// Get Time Difference
// =======================
function getTimeDiff(createdAt) {
    if (!createdAt) return 'Hace poco';

    const now = new Date();
    const created = new Date(createdAt);
    const diff = Math.floor((now - created) / 1000); // segundos

    if (diff < 60) return 'Hace poco';
    if (diff < 3600) return `Hace ${Math.floor(diff / 60)} min`;
    if (diff < 86400) return `Hace ${Math.floor(diff / 3600)} h`;

    return 'Hace varios días';
}

// =======================
// Join Game by Code
// =======================
async function joinGameByCode(code) {
    try {
        await gameApi.joinGame(code);
        window.location.href = `waiting-room.html?code=${code}`;
    } catch (error) {
        alert(`❌ ${error.message}`);
    }
}

// =======================
// Join Button Handler
// =======================
joinBtn.addEventListener('click', async () => {
    const code = gameCodeInput.value.trim().toUpperCase();

    if (!code) {
        alert('❌ Por favor ingresa un código de partida');
        return;
    }

    joinBtn.disabled = true;

    try {
        await gameApi.joinGame(code);
        window.location.href = `waiting-room.html?code=${code}`;
    } catch (error) {
        alert(`❌ ${error.message}`);
        joinBtn.disabled = false;
    }
});

// =======================
// Real-time Update Games
// =======================
setInterval(loadAvailableGames, 5000);