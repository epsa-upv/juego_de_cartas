// =======================
// GAME API
// =======================

import { apiRequest } from './http.js';

export const gameApi = {

    // =======================
    // GAME / LOBBY
    // =======================
    createGame(title) {
        return apiRequest('/games', {
            method: 'POST',
            body: JSON.stringify({ title })
        });
    },

    joinGame(code) {
        return apiRequest(`/games/${code}/join`, {
            method: 'POST'
        });
    },

    getLobby(code) {
        return apiRequest(`/games/${code}/players`);
    },

    ready(code) {
        return apiRequest(`/games/${code}/ready`, {
            method: 'POST'
        });
    },

    startGame(code) {
        return apiRequest(`/games/${code}/start`, {
            method: 'POST'
        });
    },

    // =======================
    // ROUND
    // =======================
    getCurrentRound(code) {
        return apiRequest(`/games/${code}/rounds/current`);
    },

    // =======================
    // BETTING
    // =======================
    getCurrentBets(code) {
        return apiRequest(`/games/${code}/rounds/current/bets`);
    },

    placeBet(code, value) {
        return apiRequest(`/games/${code}/rounds/current/bets`, {
            method: 'POST',
            body: JSON.stringify({ value })
        });
    },

    // =======================
    // PLAYING
    // =======================
    getHand(code) {
        return apiRequest(`/games/${code}/hand`);
    },

    playCard(code, card) {
        return apiRequest(`/games/${code}/rounds/current/play`, {
            method: 'POST',
            body: JSON.stringify({ card })
        });
    },

    getTrick(code) {
        return apiRequest(`/games/${code}/rounds/current/trick`);
    }
};
