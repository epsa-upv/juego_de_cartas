// API base configurable: si quieres apuntar a otro backend en desarrollo, asigna window.__API_BASE__ antes de cargar este script.
const API_BASE = window.__API_BASE__ || '/ohhell-api/api';

function getToken() {
    return localStorage.getItem('token');
}

async function apiRequest(path, options = {}) {
    const headers = options.headers || {};
    if (getToken()) {
        headers['Authorization'] = 'Bearer ' + getToken();
    }
    if (!headers['Content-Type'] && !(options.body instanceof FormData)) {
        headers['Content-Type'] = 'application/json';
    }

    const res = await fetch(API_BASE + path, {
        ...options,
        headers
    });

    const text = await res.text().catch(() => '');
    let payload;
    try { payload = text ? JSON.parse(text) : null; } catch (e) { payload = text; }

    if (!res.ok) {
        const message = (payload && payload.message) || payload || res.statusText || 'Error API';
        throw new Error(message);
    }

    return payload;
}

// API básico (compatibilidad con nombres usados en main.js)
const apiClient = {
    // Auth
    login: (email, password) => apiRequest('/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),
    register: (email, password) => apiRequest('/auth/register', { method: 'POST', body: JSON.stringify({ email, password }) }),

    // Players
    createPlayer: (username, email, password) => apiRequest('/players', { method: 'POST', body: JSON.stringify({ username, email, password }) }),
    getMyPlayer: () => apiRequest('/players/me'),
    searchPlayers: (query) => apiRequest('/players/search?' + new URLSearchParams(query).toString()),
    hashPassword: (password, email) => {
        // El backend debería ofrecer hashing; si no, esta ruta puede fallar.
        return apiRequest('/utils/hash-password', { method: 'POST', body: JSON.stringify({ password, email }) });
    },

    // Games
    createGame: (gamePayload) => apiRequest('/games', { method: 'POST', body: JSON.stringify(gamePayload) }),
    getAvailableGames: () => apiRequest('/games/available'),
    getGame: (gameId) => apiRequest('/games/' + encodeURIComponent(gameId)),
    joinGame: (gameId, playerId) => apiRequest(`/games/${encodeURIComponent(gameId)}/join`, { method: 'POST', body: JSON.stringify({ playerId }) }),
    startGame: (gameId) => apiRequest(`/games/${encodeURIComponent(gameId)}/start`, { method: 'POST' }),
    startNextRound: (gameId) => apiRequest(`/games/${encodeURIComponent(gameId)}/rounds/next`, { method: 'POST' }),

    // Rounds / bids / tricks
    getCurrentRound: (gameId) => apiRequest(`/games/${encodeURIComponent(gameId)}/rounds/current`),
    getBids: (gameId, roundNumber) => apiRequest(`/games/${encodeURIComponent(gameId)}/rounds/${roundNumber}/bids`),
    getCurrentTrick: (gameId, roundNumber) => apiRequest(`/games/${encodeURIComponent(gameId)}/rounds/${roundNumber}/trick`),
    placeBid: (gameId, roundNumber, playerId, bid) => apiRequest(`/games/${encodeURIComponent(gameId)}/rounds/${roundNumber}/bids`, { method: 'POST', body: JSON.stringify({ playerId, bid }) }),
    playCard: (gameId, roundNumber, playerId, card) => apiRequest(`/games/${encodeURIComponent(gameId)}/rounds/${roundNumber}/play`, { method: 'POST', body: JSON.stringify({ playerId, card }) }),

    // Health / testing
    testDatabase: () => apiRequest('/health'),
};

// Alias para compatibilidad con código antiguo
const api = apiClient;
