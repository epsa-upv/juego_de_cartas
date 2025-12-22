// API base configurable: si quieres apuntar a otro backend en desarrollo, asigna window.__API_BASE__ antes de cargar este script.
const API_BASE = 'http://localhost:8080/ohhell-api/api';

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

    placeBet: (code, value) =>
        apiRequest(`/games/${code}/rounds/current/bets`, {
            method: 'POST',
            body: JSON.stringify({ bet })
        }),


    // AUTH
    login: (email, password) =>
        apiRequest('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        }),

    register: (email, password) =>
        apiRequest('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        }),

    // PLAYER
    getMyPlayer: () => apiRequest('/players/me'),

    createPlayer: (nickname) =>
        apiRequest('/players', {
            method: 'POST',
            body: JSON.stringify({ nickname })
        }),

    // GAME / LOBBY
    createGame: (title) =>
        apiRequest('/games', {
            method: 'POST',
            body: JSON.stringify({ title })
        }),

    joinGame: (code) =>
        apiRequest(`/games/${code}/join`, { method: 'POST' }),

    getLobby: (code) =>
        apiRequest(`/games/${code}/players`),

    ready: (code) =>
        apiRequest(`/games/${code}/ready`, { method: 'POST' }),

    startGame: (code) =>
        apiRequest(`/games/${code}/start`, { method: 'POST' }),

    // GAMEPLAY
    getCurrentRound: (code) =>
        apiRequest(`/games/${code}/rounds/current`),

    getCurrentBets: (code) =>
        apiRequest(`/games/${code}/rounds/current/bets`),

    playCard: (code, card) =>
        apiRequest(`/games/${code}/rounds/current/play`, {
            method: 'POST',
            body: JSON.stringify({ card })
        }),

    getTrick: (code) =>
        apiRequest(`/games/${code}/rounds/current/trick`),

    getHand: (code) =>
        apiRequest(`/games/${code}/hand`)
};
// Alias para compatibilidad con código antiguo
const gameApi = apiClient;
