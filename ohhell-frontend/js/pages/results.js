const token = localStorage.getItem('token');
if (!token) {
    window.location.href = 'login.html';
}

// =======================
// Get Code from URL
// =======================
const params = new URLSearchParams(window.location.search);
const code = params.get('code');

if (!code) {
    alert('Código de partida inválido');
    window.location.href = 'home.html';
}

// =======================
// DOM Elements
// =======================
const winnerNameEl = document.getElementById('winner-name');
const winnerSubtitleEl = document.getElementById('winner-subtitle');
const resultsListEl = document.getElementById('results-list');
const playAgainBtn = document.getElementById('play-again-btn');

// =======================
// Init
// =======================
init();

async function init() {
    try {
        await loadResults();
    } catch (error) {
        console.error('Error cargando resultados:', error);
        alert('Error al cargar los resultados');
        window.location.href = 'home.html';
    }
}

// =======================
// Load Results
// =======================
async function loadResults() {
    try {
        // Obtener todas las rondas/datos de la partida
        const roundsData = await fetch(
            `/ohhell-api/api/games/${code}/results`,
            {
                headers: { 'Authorization': `Bearer ${token}` }
            }
        );

        if (!roundsData.ok) {
            throw new Error('No se pudieron cargar los resultados');
        }

        const data = await roundsData.json();
        const players = data.players || [];

        if (players.length === 0) {
            throw new Error('No hay datos de jugadores');
        }

        // Ordenar por puntos (descendente)
        const sorted = [...players].sort((a, b) => b.points - a.points);

        const winner = sorted[0];
        winnerNameEl.textContent = winner.nickname || 'Jugador Desconocido';
        winnerSubtitleEl.textContent = `Puntuación Final: ${winner.points} pts`;

        renderResults(sorted);

    } catch (error) {
        console.error(error);
        resultsListEl.innerHTML = `
            <div style="text-align: center; padding: 2rem; color: var(--text-muted);">
                <p>Error al cargar los resultados</p>
                <p style="font-size: 0.875rem;">Intenta nuevamente en unos momentos</p>
            </div>
        `;
    }
}

// =======================
// Render Results
// =======================
function renderResults(players) {
    resultsListEl.innerHTML = '';

    players.forEach((player, index) => {
        const position = index + 1;
        const medal = position === 1 ? '🥇' : position === 2 ? '🥈' : position === 3 ? '🥉' : position;

        const div = document.createElement('div');
        div.className = 'result-item';

        div.innerHTML = `
            <div class="result-position">${medal}</div>
            <div style="flex: 1; text-align: left;">
                <div style="font-weight: 600; color: var(--text-primary);">${player.nickname}</div>
                <div style="font-size: 0.875rem; color: var(--text-muted);">
                    ${player.bets || 0} apuestas | ${player.tricks || 0} bazas
                </div>
            </div>
            <div class="result-score">${player.points || 0} pts</div>
        `;

        resultsListEl.appendChild(div);
    });
}

// =======================
// Play Again
// =======================
playAgainBtn.addEventListener('click', () => {
    window.location.href = 'home.html';
});