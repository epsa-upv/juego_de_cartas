const token = localStorage.getItem('token');
if (!token) {
    window.location.href = 'login.html';
}

// =======================
// Leer CODE de la URL
// =======================
const params = new URLSearchParams(window.location.search);
const code = params.get('code');

if (!code) {
    alert('Código de partida inválido');
    window.location.href = 'home.html';
}

document.getElementById('gameCode').textContent = code;

// =======================
// Elementos
// =======================
const playersList = document.getElementById('players-list');
const readyBtn = document.getElementById('ready-btn');
const startBtn = document.getElementById('start-btn');
const waitingMsg = document.getElementById('waiting-message');
const leaveBtn = document.getElementById('leave-btn');
const copyBtn = document.getElementById('copy-code-btn');

// =======================
// Estado
// =======================
let pollInterval = null;
let myPlayerId = null;

// =======================
// Inicializar
// =======================
init();

async function init() {
    try {
        const me = await gameApi.getMyPlayer();
        myPlayerId = me.id;

        await loadLobby();
        pollInterval = setInterval(loadLobby, 2000);

    } catch (e) {
        console.error(e);
        alert('Error inicializando la sala');
        window.location.href = 'home.html';
    }
}

// =======================
// Cargar lobby
// =======================
async function loadLobby() {
    try {
        const lobby = await gameApi.getLobby(code);

        renderPlayers(lobby.players);

        const me = lobby.players.find(p => p.playerId === myPlayerId);
        const isHost = me?.host;

        // Mostrar botón start solo al host
        startBtn.style.display = isHost ? 'block' : 'none';
        waitingMsg.style.display = isHost ? 'none' : 'block';

        // Si la partida empieza → ir al juego
        if (lobby.status !== 'WAITING') {
            clearInterval(pollInterval);
            window.location.href = `game.html?code=${code}`;
        }

    } catch (e) {
        console.error(e);
    }
}

// =======================
// Render jugadores
// =======================
function renderPlayers(players) {
    playersList.innerHTML = '';

    players.forEach(p => {
        const div = document.createElement('div');
        div.className = 'player-slot filled';

        div.innerHTML = `
            <div class="player-slot-text">
                <strong>${p.nickname}</strong>
                ${p.ready ? '✅' : '⏳'}
                ${p.host ? ' (Host)' : ''}
            </div>
        `;

        playersList.appendChild(div);
    });
}

// =======================
// READY
// =======================
readyBtn.addEventListener('click', async () => {
    try {
        await gameApi.ready(code);
        readyBtn.disabled = true;
    } catch (e) {
        alert(e.message);
    }
});

// =======================
// START (solo host)
// =======================
startBtn.addEventListener('click', async () => {
    try {
        await gameApi.startGame(code);
    } catch (e) {
        alert(e.message);
    }
});

// =======================
// Copiar código
// =======================
copyBtn.addEventListener('click', () => {
    navigator.clipboard.writeText(code);
    alert('Código copiado');
});

// =======================
// Salir
// =======================
leaveBtn.addEventListener('click', () => {
    clearInterval(pollInterval);
    window.location.href = 'home.html';
});
