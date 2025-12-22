// =======================
// Verificación de autenticación
// =======================
const token = localStorage.getItem('token');
if (!token) {
    window.location.href = 'login.html';
}

// =======================
// Obtener código de la partida
// =======================
const params = new URLSearchParams(window.location.search);
const code = params.get('code');
if (!code) {
    alert('Código de partida inválido');
    window.location.href = 'home.html';
}

// =======================
// Configuración API
// =======================
const API_BASE = 'http://localhost:8080/ohhell-api/api';

// =======================
// API Calls - Versión Mejorada
// =======================
const gameApi = {
    // Obtener mi player (UUID)
    getMyPlayer: async () => {
        const res = await fetch(`${API_BASE}/players/me`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`No se pudo obtener información del jugador: ${errorText}`);
        }
        return res.json();
    },

    // Obtener ronda actual
    getCurrentRound: async () => {
        const res = await fetch(`${API_BASE}/games/${code}/rounds/current`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) {
            const errorText = await res.text();
            if (res.status === 404) {
                throw new Error('La partida no existe o no tiene ronda activa');
            }
            throw new Error(`No se pudo obtener la ronda actual: ${errorText}`);
        }
        return res.json();
    },

    // Obtener estado de la baza
    getTrick: async () => {
        const res = await fetch(`${API_BASE}/games/${code}/rounds/current/trick`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`No se pudo obtener el estado de la baza: ${errorText}`);
        }
        return res.json();
    },

    // Obtener mi mano de cartas
    getHand: async () => {
        const res = await fetch(`${API_BASE}/games/${code}/hand`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) {
            const errorText = await res.text();
            if (res.status === 400) {
                throw new Error('400: Las cartas aún no están disponibles');
            }
            throw new Error(`No se pudo obtener la mano: ${errorText}`);
        }
        return res.json();
    },

    // Obtener apuestas de la ronda actual
    getBets: async () => {
        const res = await fetch(`${API_BASE}/games/${code}/rounds/current/bets`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (res.status === 400 || res.status === 404) {
            // No hay apuestas aún
            return { bets: [] };
        }

        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`No se pudo obtener las apuestas: ${errorText}`);
        }
        return res.json();
    },

    // Realizar apuesta
    placeBet: async (value) => {
        const res = await fetch(`${API_BASE}/games/${code}/rounds/current/bets`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ value })
        });

        if (!res.ok) {
            const error = await res.text();
            throw new Error(error);
        }
        return res.json();
    },

    // Jugar una carta
    playCard: async (card) => {
        const res = await fetch(`${API_BASE}/games/${code}/rounds/current/play`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ card })
        });

        if (!res.ok) {
            const error = await res.text();
            throw new Error(error);
        }
        return res.json();
    },

    // Obtener información del lobby (para jugadores)
    getLobby: async () => {
        const res = await fetch(`${API_BASE}/games/${code}/players`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`No se pudo obtener información del lobby: ${errorText}`);
        }
        return res.json();
    },

    // Salir de la partida
    leaveGame: async () => {
        const res = await fetch(`${API_BASE}/games/${code}/leave`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!res.ok) {
            throw new Error('Error al salir de la partida');
        }
    }
};

// =======================
// State Management
// =======================
let gameState = {
    roundId: null,
    roundNumber: null,
    phase: null,
    trump: null,
    currentTurnPlayerId: null,
    currentPlaySuit: null,
    players: [],
    myHand: [],
    myPlayerId: null,
    cardsPlayed: [],
    code: code,
    myBet: null  // Asegurar que empieza como null
};

let pollInterval = null;
const POLL_INTERVAL = 2000; // 2 segundos

// =======================
// DOM Elements
// =======================
const leaveBtn = document.getElementById('leave-btn');
const betModal = document.getElementById('bet-modal');
const betInput = document.getElementById('bet-input');
const confirmBetBtn = document.getElementById('confirm-bet-btn');
const cancelBetBtn = document.getElementById('cancel-bet-btn');
const quickBets = document.getElementById('quick-bets');
const playersPositions = document.getElementById('players-positions');
const handCards = document.getElementById('hand-cards');
const gameStatus = document.getElementById('game-status');
const cardsPlayedArea = document.getElementById('cards-played');
const scoresListArea = document.getElementById('scores-list');
const bettingStatusContainer = document.getElementById('betting-status-container');
const bettingStatusList = document.getElementById('betting-status-list');
const lastTrickContainer = document.getElementById('last-trick-container');
const lastTrickWinner = document.getElementById('last-trick-winner');
const userAvatar = document.getElementById('user-avatar');
const userName = document.getElementById('user-name');
const trumpSuitElement = document.getElementById('trump-suit');
const myBetElement = document.getElementById('my-bet');
const handCountElement = document.getElementById('hand-count');
const playerCountElement = document.getElementById('player-count');
const roundInfoElement = document.getElementById('round-info');
const phaseInfoElement = document.getElementById('phase-info');

// =======================
// Inicialización
// =======================
async function initGame() {
    try {
        // Obtener información del usuario
        const me = await gameApi.getMyPlayer();
        gameState.myPlayerId = me.id;
        userName.textContent = me.nickname;
        userAvatar.textContent = me.nickname.charAt(0).toUpperCase();

        // Iniciar polling del estado del juego
        startPolling();
    } catch (error) {
        console.error('Error inicializando el juego:', error);
        alert('Error al cargar la partida: ' + error.message);
        window.location.href = 'home.html';
    }
}

// =======================
// Polling del estado del juego
// =======================
function startPolling() {
    if (pollInterval) clearInterval(pollInterval);

    pollInterval = setInterval(async () => {
        try {
            await loadGame();
        } catch (error) {
            console.error('Error en polling:', error);
        }
    }, POLL_INTERVAL);

    // Cargar inmediatamente
    loadGame();
}

function stopPolling() {
    if (pollInterval) {
        clearInterval(pollInterval);
        pollInterval = null;
    }
}

// =======================
// Cargar estado del juego - VERSIÓN MEJORADA
// =======================
async function loadGame() {
    try {
        console.log('=== NUEVA CARGA ===');
        console.log('Estado anterior - miBet:', gameState.myBet);
        console.log('🎯 miPlayerId:', gameState.myPlayerId);

        console.log('🔄 Cargando estado del juego...');

        // 1. Obtener ronda actual
        const round = await gameApi.getCurrentRound();
        gameState.roundId = round.id;
        gameState.roundNumber = round.roundNumber || round.number || 1;
        gameState.phase = round.phase;
        gameState.trump = round.trumpSuit || round.trumpCard || '♠';

        console.log('📊 Ronda:', {
            id: gameState.roundId,
            numero: gameState.roundNumber,
            fase: gameState.phase,
            triunfo: gameState.trump,
            miApuesta: gameState.myBet
        });

        // 2. Obtener jugadores del lobby
        const lobby = await gameApi.getLobby();
        gameState.players = lobby.players || [];
        console.log('👥 Jugadores:', gameState.players.length);

        // DEBUG: Ver estructura de jugadores
        gameState.players.forEach((p, i) => {
            console.log(`👤 Jugador ${i}:`, {
                id: p.playerId,
                nickname: p.nickname,
                soyYo: p.playerId === gameState.myPlayerId
            });
        });

        // 3. Obtener apuestas - CÓDIGO CORREGIDO
        try {
            const bets = await gameApi.getBets();
            console.log('💰 Respuesta de API de apuestas:', bets);

            // Resetear apuestas
            gameState.players.forEach(player => {
                player.bet = undefined;
            });
            gameState.myBet = null;

            if (bets && bets.bets && Array.isArray(bets.bets)) {
                console.log('💰 Apuestas obtenidas:', bets.bets.length, 'apuestas');

                // DEBUG: Ver estructura de apuestas
                bets.bets.forEach((bet, i) => {
                    console.log(`🎲 Apuesta ${i}:`, {
                        playerId: bet.playerId,
                        nickname: bet.nickname,
                        value: bet.bet,
                        order: bet.order,
                        soyYo: bet.playerId === gameState.myPlayerId,
                        tipos: {
                            betPlayerId: typeof bet.playerId,
                            myPlayerId: typeof gameState.myPlayerId
                        }
                    });
                });

                bets.bets.forEach(bet => {
                    // IMPORTANTE: Comparar como strings porque pueden ser UUIDs
                    const betPlayerIdStr = String(bet.playerId);
                    const myPlayerIdStr = String(gameState.myPlayerId);

                    // Buscar jugador por ID (comparar como strings)
                    const player = gameState.players.find(p => {
                        const pIdStr = String(p.playerId);
                        return pIdStr === betPlayerIdStr;
                    });

                    if (player) {
                        player.bet = bet.bet;
                        console.log(`🎯 Asignando apuesta ${bet.bet} a ${player.nickname}`);

                        // Verificar si es mi apuesta
                        if (betPlayerIdStr === myPlayerIdStr) {
                            gameState.myBet = bet.bet;
                            console.log('🎯 ¡ES MI APUESTA!:', gameState.myBet);
                        }
                    } else {
                        console.warn('⚠️ Jugador no encontrado para apuesta:', {
                            betPlayerId: bet.playerId,
                            betNickname: bet.nickname,
                            availablePlayers: gameState.players.map(p => ({
                                id: p.playerId,
                                nickname: p.nickname
                            }))
                        });
                    }
                });
            }
        } catch (betError) {
            console.log('ℹ️ Error obteniendo apuestas:', betError.message);
            // Asegurar que myBet sea null
            gameState.myBet = null;
        }

        // 4. Manejar según la fase del juego - VERSIÓN MEJORADA CON DEPURACIÓN
        if (gameState.phase === 'BETTING') {
            try {
                const hand = await gameApi.getHand();
                gameState.myHand = hand.cards || [];
                console.log('🃏 Mano obtenida:', gameState.myHand.length, 'cartas');
            } catch (handError) {
                if (handError.message.includes('400')) {
                    console.log('⏳ Cartas aún no repartidas');
                    gameState.myHand = [];
                } else {
                    throw handError;
                }
            }
        } else if (gameState.phase === 'PLAYING') {
            const trick = await gameApi.getTrick();
            gameState.currentTurnPlayerId = trick.currentPlayer;
            gameState.currentPlaySuit = trick.leadSuit;
            gameState.cardsPlayed = trick.cards || [];

            const hand = await gameApi.getHand();
            gameState.myHand = hand.cards || [];

            console.log('🎮 Estado de baza - ¡FASE DE JUEGO!');
            console.log('   Turno actual:', trick.currentPlayer);
            console.log('   ¿Es mi turno?:', trick.currentPlayer === gameState.myPlayerId ? '✅ SÍ' : '❌ NO');
            console.log('   Palo líder:', trick.leadSuit);
            console.log('   Cartas jugadas:', trick.cards?.length || 0);

            // DEPURACIÓN: Ver cartas en mano y cuáles son jugables
            if (trick.currentPlayer === gameState.myPlayerId) {
                console.log('🎯 ¡ES MI TURNO! Cartas en mano:');
                gameState.myHand.forEach(card => {
                    const playable = isCardPlayable(card);
                    console.log(`   ${card}: ${playable ? '✅ JUGABLE' : '❌ NO JUGABLE'}`);
                });
            }
        } else if (gameState.phase === 'FINISHED') {
            stopPolling();
            window.location.href = `results.html?code=${code}`;
            return;
        }

        // 5. Actualizar UI
        updateUI();
        renderPlayers();
        renderHand();
        renderScores();
        renderCardsPlayed();
        renderBettingStatus();

        console.log('✅ Carga completada - miBet:', gameState.myBet);

    } catch (error) {
        console.error('❌ Error cargando juego:', error);
        if (error.message.includes('404') || error.message.includes('partida no existe')) {
            stopPolling();
            alert('❌ La partida no existe o ha terminado');
            window.location.href = 'home.html';
        }
    }
}

// =======================
// Actualizar UI
// =======================
function updateUI() {
    // Actualizar info de ronda y fase
    roundInfoElement.textContent = gameState.roundNumber || '1';
    phaseInfoElement.textContent = getPhaseText(gameState.phase);

    // Actualizar información del juego
    playerCountElement.textContent = gameState.players.length || '2';
    handCountElement.textContent = gameState.myHand.length || '0';
    myBetElement.textContent = gameState.myBet !== null ? gameState.myBet : '-';

    // Actualizar estado del juego
    updateGameStatus();

    // Actualizar triunfo
    updateTrumpDisplay();

    // Mostrar/ocultar paneles según fase
    if (gameState.phase === 'BETTING') {
        bettingStatusContainer.style.display = 'block';
        lastTrickContainer.style.display = 'none';

        console.log('🎯 Estado para modal:', {
            fase: 'BETTING',
            miApuesta: gameState.myBet,
            cartasEnMano: gameState.myHand.length,
            modalActivo: betModal.classList.contains('active')
        });

        // Solo gestionar el modal si no está ya visible
        if (!betModal.classList.contains('active')) {
            // Condiciones para mostrar modal:
            // 1. No he apostado (null o undefined)
            // 2. Tengo cartas en mano
            // 3. La partida está en fase de apuestas
            const shouldShowModal =
                (gameState.myBet === null || gameState.myBet === undefined) &&
                gameState.myHand.length > 0;

            console.log('🔍 Evaluando mostrar modal:', {
                shouldShowModal,
                myBet: gameState.myBet,
                handLength: gameState.myHand.length
            });

            if (shouldShowModal) {
                console.log('✅ Condiciones CUMPLIDAS - Mostrando modal');
                // Usar timeout para evitar conflictos con renderizado
                setTimeout(() => {
                    if (!betModal.classList.contains('active')) {
                        showBetModal();
                    }
                }, 500); // Aumentar un poco el delay
            } else {
                console.log('❌ Condiciones NO CUMPLIDAS');
                if (gameState.myBet !== null && gameState.myBet !== undefined) {
                    console.log('💰 Ya aposté:', gameState.myBet);
                }
                // No llamar a hideBetModal() aquí porque no está visible
            }
        }
    } else if (gameState.phase === 'PLAYING') {
        bettingStatusContainer.style.display = 'none';
        lastTrickContainer.style.display = 'block';
        // Solo ocultar si está visible
        if (betModal.classList.contains('active')) {
            hideBetModal();
        }
    } else {
        bettingStatusContainer.style.display = 'none';
        // Solo ocultar si está visible
        if (betModal.classList.contains('active')) {
            hideBetModal();
        }
    }
}

// =======================
// Actualizar display del triunfo
// =======================
function updateTrumpDisplay() {
    if (!gameState.trump) {
        trumpSuitElement.textContent = '♠';
        return;
    }

    const trumpFileName = getCardFileName(gameState.trump);
    const trumpSymbol = getCardSymbol(gameState.trump);

    if (trumpFileName) {
        trumpSuitElement.innerHTML = getCardImageHtml(gameState.trump, trumpFileName, trumpSymbol, true);
    } else {
        trumpSuitElement.textContent = trumpSymbol;
        trumpSuitElement.className = 'trump-symbol';
        trumpSuitElement.style.fontSize = '2.5rem';
    }
}

// =======================
// Renderizar jugadores
// =======================
function renderPlayers() {
    playersPositions.innerHTML = '';

    // Posiciones fijas para 4 jugadores
    const positions = [
        { class: 'top', transform: 'translateX(-50%)' },
        { class: 'right', transform: 'translateY(-50%)' },
        { class: 'bottom', transform: 'translateX(-50%)' },
        { class: 'left', transform: 'translateY(-50%)' }
    ];

    gameState.players.forEach((player, index) => {
        if (index >= 4) return; // Máximo 4 jugadores

        const position = positions[index];
        const isCurrentTurn = gameState.currentTurnPlayerId === player.playerId;
        const isMe = player.playerId === gameState.myPlayerId;

        const playerElement = document.createElement('div');
        playerElement.className = `player-seat ${position.class}`;
        playerElement.style.transform = position.transform;

        playerElement.innerHTML = `
            <div class="player-card ${isCurrentTurn ? 'current-turn' : ''} ${isMe ? 'active' : ''}">
                <div class="player-name">${player.nickname}</div>
                <div class="player-status">${player.status || 'ACTIVE'}</div>
                <div class="player-bet">Apuesta: ${player.bet !== undefined ? player.bet : '-'}</div>
                ${isCurrentTurn ? '<div class="turn-indicator">🎯</div>' : ''}
            </div>
        `;

        playersPositions.appendChild(playerElement);
    });
}

// =======================
// Renderizar mano del jugador - VERSIÓN MEJORADA
// =======================
function renderHand() {
    handCards.innerHTML = '';

    if (gameState.myHand.length === 0) {
        const emptyMsg = document.createElement('div');
        emptyMsg.className = 'hand-card disabled';
        emptyMsg.innerHTML = '<p style="color: var(--text-muted);">📭 Sin cartas</p>';
        handCards.appendChild(emptyMsg);
        return;
    }

    console.log('🃏 Renderizando mano con', gameState.myHand.length, 'cartas');
    console.log('   Fase:', gameState.phase);
    console.log('   ¿Mi turno?:', gameState.currentTurnPlayerId === gameState.myPlayerId ? 'SÍ' : 'NO');
    console.log('   Palo líder:', gameState.currentPlaySuit);

    gameState.myHand.forEach(card => {
        const canPlay = isCardPlayable(card);
        const cardFileName = getCardFileName(card);
        const cardSymbol = getCardSymbol(card);

        const cardElement = document.createElement('div');
        cardElement.className = `hand-card ${canPlay ? 'playable' : 'disabled'}`;
        cardElement.title = `${card}${canPlay ? ' - CLICK PARA JUGAR' : ' - NO JUGABLE'}`;
        cardElement.dataset.card = card;

        if (cardFileName) {
            cardElement.innerHTML = getCardImageHtml(card, cardFileName, cardSymbol);
        } else {
            // Fallback a símbolo
            cardElement.innerHTML = getCardFallbackHtml(cardSymbol);
            cardElement.style.fontSize = '1.5rem';
            cardElement.style.fontWeight = 'bold';
        }

        if (canPlay) {
            cardElement.style.cursor = 'pointer';
            cardElement.style.boxShadow = '0 0 10px var(--secondary)';
            cardElement.addEventListener('click', () => playCard(card));
        } else {
            cardElement.style.cursor = 'not-allowed';
            cardElement.style.opacity = gameState.phase === 'PLAYING' ? '0.6' : '1';
        }

        handCards.appendChild(cardElement);
    });
}

// =======================
// Renderizar cartas jugadas
// =======================
function renderCardsPlayed() {
    cardsPlayedArea.innerHTML = '';

    gameState.cardsPlayed.forEach((playedCard, index) => {
        const cardFileName = getCardFileName(playedCard.card);
        const cardSymbol = getCardSymbol(playedCard.card);

        const cardElement = document.createElement('div');
        cardElement.className = 'card-played';
        cardElement.title = playedCard.card;
        cardElement.dataset.card = playedCard.card;

        // Posicionar cartas en cascada
        cardElement.style.transform = `translateX(${index * 25}px) translateY(${index * 15}px) rotateZ(${(index - 1.5) * 10}deg)`;
        cardElement.style.zIndex = index;

        if (cardFileName) {
            cardElement.innerHTML = getCardImageHtml(playedCard.card, cardFileName, cardSymbol);
        } else {
            // Fallback a símbolo
            cardElement.innerHTML = getCardFallbackHtml(cardSymbol);
            cardElement.style.fontSize = '1.5rem';
            cardElement.style.fontWeight = 'bold';
        }

        cardsPlayedArea.appendChild(cardElement);
    });
}

// =======================
// Renderizar puntuaciones
// =======================
function renderScores() {
    scoresListArea.innerHTML = '';

    gameState.players.forEach(player => {
        const isMe = player.playerId === gameState.myPlayerId;
        const row = document.createElement('div');
        row.className = `score-row ${isMe ? 'current' : ''}`;

        row.innerHTML = `
            <div class="score-name">${player.nickname}${isMe ? ' (Tú)' : ''}</div>
            <div class="score-bet">${player.bet !== undefined ? player.bet : '-'}</div>
            <div class="score-points">${player.totalPoints || player.points || 0}</div>
        `;

        scoresListArea.appendChild(row);
    });
}

// =======================
// Renderizar estado de apuestas
// =======================
function renderBettingStatus() {
    bettingStatusList.innerHTML = '';

    if (!gameState.players.length) return;

    gameState.players.forEach(player => {
        const hasBet = player.bet !== undefined;
        const isMe = player.playerId === gameState.myPlayerId;

        const item = document.createElement('div');
        item.className = `betting-status-item ${hasBet ? 'done' : 'pending'}`;

        item.innerHTML = `
            <div class="betting-status-name">
                ${player.nickname} ${isMe ? '(Tú)' : ''}
            </div>
            <div class="betting-status-badge ${hasBet ? 'done' : 'pending'}">
                ${hasBet ? '✓ Apostado' : '⏳ Esperando'}
            </div>
            ${hasBet ? `<div class="betting-status-value">${player.bet}</div>` : ''}
        `;

        bettingStatusList.appendChild(item);
    });
}

// =======================
// HTML helpers para cartas
// =======================
function getCardImageHtml(card, fileName, symbol, isTrump = false) {
    const baseUrl = '/ohhell/assets/cards/svg/';
    const fullUrl = baseUrl + fileName;

    return `
        <div class="card-image-container">
            <img src="${fullUrl}" 
                 alt="${symbol}"
                 class="${isTrump ? 'trump-image' : 'card-image'}"
                 onload="console.log('✅ Imagen cargada: ${fileName}')"
                 onerror="
                     console.error('❌ Error cargando: ${fileName}');
                     this.style.display = 'none';
                     const fallback = this.parentElement.querySelector('.card-fallback');
                     if (fallback) {
                         fallback.style.display = 'flex';
                     }
                 ">
            <div class="card-fallback" style="display: none;">
                <div class="card-symbol">${symbol}</div>
            </div>
        </div>
    `;
}

function getCardFallbackHtml(symbol) {
    return `
        <div class="card-fallback">
            <div class="card-symbol">${symbol}</div>
        </div>
    `;
}

// =======================
// Modal de apuestas
// =======================
function showBetModal() {
    console.log('📊 Mostrando modal de apuestas');

    // Mostrar el modal
    betModal.classList.add('active');

    // Configurar botones rápidos basados en cartas en mano
    const maxCards = gameState.myHand.length || 7;
    quickBets.innerHTML = '';

    console.log(`🎯 Generando botones de 0 a ${maxCards}`);

    for (let i = 0; i <= maxCards; i++) {
        const btn = document.createElement('button');
        btn.className = 'quick-bet-btn';
        btn.textContent = i;
        btn.type = 'button';
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            betInput.value = i;
            console.log(`✅ Apuesta seleccionada: ${i}`);
        });
        quickBets.appendChild(btn);
    }

    // Establecer límites del input
    betInput.max = maxCards;
    betInput.min = 0;
    betInput.value = 0;

    // Enfocar el input
    setTimeout(() => betInput.focus(), 100);

    console.log('✅ Modal de apuestas mostrado');
}

function hideBetModal() {
    // Solo ocultar si está visible
    if (!betModal.classList.contains('active')) {
        console.log('ℹ️ Modal ya está oculto, no hacer nada');
        return;
    }

    console.log('❌ Ocultando modal de apuestas');
    betModal.classList.remove('active');
    betInput.value = '';

    // Limpiar los botones rápidos
    quickBets.innerHTML = '';
}

// =======================
// Funciones de juego
// =======================
async function placeBet(value) {
    console.log(`🎲 Intentando apostar: ${value}`);

    // Validar apuesta
    const maxBet = gameState.myHand.length;
    if (value < 0 || value > maxBet) {
        alert(`Apuesta inválida. Debe estar entre 0 y ${maxBet}`);
        return;
    }

    try {
        confirmBetBtn.disabled = true;
        confirmBetBtn.textContent = '⏳ Apostando...';

        const result = await gameApi.placeBet(value);

        console.log(`✅ Apuesta realizada: ${value}`, result);
        gameState.myBet = value;

        // Actualizar UI inmediatamente
        myBetElement.textContent = value;

        // Cerrar modal
        hideBetModal();

        // Recargar estado para sincronizar
        await loadGame();

    } catch (error) {
        console.error('❌ Error al apostar:', error);

        if (error.message.includes('Ya has apostado')) {
            // Si la API dice que ya apostamos, actualizar el estado local
            console.log('ℹ️ API dice que ya apostamos, actualizando estado local');
            gameState.myBet = value;
            myBetElement.textContent = value;

            // Forzar cierre del modal
            hideBetModal();

            // Recargar para sincronizar
            await loadGame();
        } else {
            alert('Error al realizar apuesta: ' + error.message);
        }
    } finally {
        confirmBetBtn.disabled = false;
        confirmBetBtn.textContent = '✅ Confirmar';
    }
}

async function playCard(card) {
    if (!isCardPlayable(card)) {
        alert('No puedes jugar esta carta. Debes seguir el palo líder.');
        return;
    }

    console.log(`🃏 Jugando carta: ${card}`);

    try {
        await gameApi.playCard(card);
        console.log('✅ Carta jugada exitosamente');

        // Eliminar de la mano localmente
        gameState.myHand = gameState.myHand.filter(c => c !== card);

        // Recargar estado inmediatamente
        await loadGame();

    } catch (error) {
        console.error('❌ Error al jugar carta:', error);
        alert('Error al jugar carta: ' + error.message);
    }
}

// =======================
// Verificar si carta es jugable - VERSIÓN MEJORADA CON DEPURACIÓN
// =======================
function isCardPlayable(card) {
    // Solo se puede jugar en fase PLAYING y si es mi turno
    if (gameState.phase !== 'PLAYING') {
        console.log(`❌ ${card}: No jugable - fase ${gameState.phase}`);
        return false;
    }

    if (gameState.currentTurnPlayerId !== gameState.myPlayerId) {
        console.log(`❌ ${card}: No jugable - no es mi turno`);
        return false;
    }

    // Si no hay palo líder (primera carta), cualquiera es válida
    if (!gameState.currentPlaySuit) {
        console.log(`✅ ${card}: Jugable - primera carta, no hay palo líder`);
        return true;
    }

    // Obtener el palo de la carta
    const cardSuit = extractSuit(card);
    console.log(`🔍 ${card}: Palo de carta = ${cardSuit}, Palo líder = ${gameState.currentPlaySuit}`);

    // Si es del palo líder, se puede jugar
    if (cardSuit === gameState.currentPlaySuit) {
        console.log(`✅ ${card}: Jugable - mismo palo líder`);
        return true;
    }

    // Si no es del palo líder, verificar si tengo cartas del palo líder
    const hasLeadSuit = gameState.myHand.some(c => {
        const suit = extractSuit(c);
        return suit === gameState.currentPlaySuit;
    });

    if (!hasLeadSuit) {
        console.log(`✅ ${card}: Jugable - no tengo del palo líder`);
        return true;
    } else {
        console.log(`❌ ${card}: NO jugable - tengo del palo líder (${gameState.currentPlaySuit})`);
        return false;
    }
}

// =======================
// Funciones de utilidad para cartas - CORREGIDAS
// =======================
function extractSuit(card) {
    if (!card) return null;

    // Limpiar .svg si existe
    const cleanCard = card.replace('.svg', '').toUpperCase();

    // Formato: "ACE_SPADES", "2_HEARTS", etc.
    const parts = cleanCard.split('_');
    if (parts.length >= 2) {
        const suitKey = parts[parts.length - 1];

        // Mapeo a símbolo
        const suitMap = {
            'SPADES': '♠', 'HEARTS': '♥', 'DIAMONDS': '♦', 'CLUBS': '♣',
            'SPADE': '♠', 'HEART': '♥', 'DIAMOND': '♦', 'CLUB': '♣'
        };

        return suitMap[suitKey] || suitKey.toLowerCase();
    }

    // Si ya es un símbolo: '♠', '♥', '♦', '♣'
    if (['♠', '♥', '♦', '♣'].includes(cleanCard)) {
        return cleanCard;
    }

    return null;
}

function getCardFileName(card) {
    if (!card) {
        console.warn('⚠️ Carta nula recibida');
        return null;
    }

    console.log('🃏 Procesando carta:', card);

    // Si ya tiene .svg, devolverlo como está (en minúsculas)
    if (card.endsWith('.svg')) {
        return card.toLowerCase();
    }

    // Limpiar y normalizar
    const cleanCard = card.trim().toUpperCase();

    // Mapeo completo de valores de cartas
    const valueMap = {
        // Números directos (lo que probablemente viene de la API)
        '2': '2', '3': '3', '4': '4', '5': '5', '6': '6',
        '7': '7', '8': '8', '9': '9', '10': '10',
        // Valores especiales
        'ACE': 'ace', 'JACK': 'jack', 'QUEEN': 'queen', 'KING': 'king',
        'K': 'king', 'Q': 'queen', 'J': 'jack', 'A': 'ace'
    };

    // Mapeo de palos (D = Diamonds, S = Spades, H = Hearts, C = Clubs)
    const suitMap = {
        'D': 'diamonds', 'DIAMONDS': 'diamonds', 'DIAMOND': 'diamonds', '♦': 'diamonds',
        'S': 'spades', 'SPADES': 'spades', 'SPADE': 'spades', '♠': 'spades',
        'H': 'hearts', 'HEARTS': 'hearts', 'HEART': 'hearts', '♥': 'hearts',
        'C': 'clubs', 'CLUBS': 'clubs', 'CLUB': 'clubs', '♣': 'clubs'
    };

    // Formato: "10_D", "K_D", "Q_S", etc.
    const parts = cleanCard.split('_');
    if (parts.length === 2) {
        const valueKey = parts[0];  // "10", "K", "Q", etc.
        const suitKey = parts[1];   // "D", "S", "H", "C"

        // Obtener valor
        let value = valueMap[valueKey];
        if (!value) {
            // Si no está en el mapa, usar directamente (convertir a minúsculas)
            value = valueKey.toLowerCase();
        }

        // Obtener palo
        let suit = suitMap[suitKey];
        if (!suit) {
            // Si no está en el mapa, intentar con el primer carácter
            suit = suitMap[suitKey.charAt(0)] || suitKey.toLowerCase();
        }

        const fileName = `${value}_of_${suit}.svg`;
        console.log(`✅ Convertido: ${card} → ${fileName}`);
        return fileName;
    }

    // Formato: Solo símbolo del triunfo
    if (['♠', '♥', '♦', '♣'].includes(cleanCard)) {
        const suit = suitMap[cleanCard];
        if (suit) {
            const fileName = `ace_of_${suit}.svg`;
            console.log(`✅ Triunfo: ${card} → ${fileName}`);
            return fileName;
        }
    }

    console.warn(`❌ No se pudo procesar la carta: ${card}`);
    return null;
}

function getSuitSymbol(suit) {
    if (!suit) return '♠';

    const symbols = {
        'CLUBS': '♣', 'clubs': '♣', 'CLUB': '♣', 'club': '♣',
        'DIAMONDS': '♦', 'diamonds': '♦', 'DIAMOND': '♦', 'diamond': '♦',
        'HEARTS': '♥', 'hearts': '♥', 'HEART': '♥', 'heart': '♥',
        'SPADES': '♠', 'spades': '♠', 'SPADE': '♠', 'spade': '♠',
        '♣': '♣', '♦': '♦', '♥': '♥', '♠': '♠'
    };

    return symbols[suit] || '♠';
}

function getCardSymbol(card) {
    if (!card) return '?';

    // Limpiar .svg si existe
    const cleanCard = card.replace('.svg', '').toUpperCase();

    // Mapeo de valores a símbolos
    const valueMap = {
        'ACE': 'A', 'JACK': 'J', 'QUEEN': 'Q', 'KING': 'K',
        '2': '2', '3': '3', '4': '4', '5': '5', '6': '6',
        '7': '7', '8': '8', '9': '9', '10': '10',
        'TWO': '2', 'THREE': '3', 'FOUR': '4', 'FIVE': '5',
        'SIX': '6', 'SEVEN': '7', 'EIGHT': '8', 'NINE': '9', 'TEN': '10'
    };

    // Obtener valor y palo
    const parts = cleanCard.split('_');
    let value = '?';
    let suitSymbol = '♠';

    if (parts.length >= 2) {
        const valueKey = parts[0];
        const suitKey = parts[parts.length - 1];

        value = valueMap[valueKey] || valueKey;
        suitSymbol = getSuitSymbol(suitKey);
    } else if (['♠', '♥', '♦', '♣'].includes(cleanCard)) {
        // Solo símbolo de triunfo
        value = 'A';
        suitSymbol = cleanCard;
    } else if (cleanCard.includes('_OF_')) {
        // Formato "ACE_OF_SPADES"
        const subParts = cleanCard.split('_OF_');
        if (subParts.length === 2) {
            value = valueMap[subParts[0]] || subParts[0];
            suitSymbol = getSuitSymbol(subParts[1]);
        }
    }

    return `${value}${suitSymbol}`;
}

function getPhaseText(phase) {
    const phases = {
        'WAITING': 'Esperando',
        'BETTING': 'Apuestas',
        'PLAYING': 'Jugando',
        'FINISHED': 'Finalizado'
    };
    return phases[phase] || phase;
}

function updateGameStatus() {
    if (!gameState.phase) return;

    const statusConfig = {
        'WAITING': {
            text: '⏳ Esperando jugadores...',
            color: 'var(--warning)',
            bg: 'rgba(245, 158, 11, 0.1)'
        },
        'BETTING': {
            text: gameState.myBet === null ? '🎯 Haz tu apuesta' : '⏳ Esperando apuestas...',
            color: 'var(--warning)',
            bg: 'rgba(245, 158, 11, 0.1)'
        },
        'PLAYING': {
            text: gameState.currentTurnPlayerId === gameState.myPlayerId
                ? '🎮 ¡Es tu turno! Juega una carta'
                : '⏳ Esperando turno...',
            color: gameState.currentTurnPlayerId === gameState.myPlayerId
                ? 'var(--secondary)'
                : 'var(--primary)',
            bg: gameState.currentTurnPlayerId === gameState.myPlayerId
                ? 'rgba(16, 185, 129, 0.1)'
                : 'rgba(99, 102, 241, 0.1)'
        },
        'FINISHED': {
            text: '🏁 Ronda finalizada',
            color: 'var(--text-muted)',
            bg: 'rgba(100, 116, 139, 0.1)'
        }
    };

    const config = statusConfig[gameState.phase] || {
        text: gameState.phase,
        color: 'var(--text-primary)',
        bg: 'rgba(99, 102, 241, 0.1)'
    };

    gameStatus.textContent = config.text;
    gameStatus.style.color = config.color;
    gameStatus.style.background = config.bg;
    gameStatus.style.borderColor = config.color;
}

// =======================
// Event Listeners
// =======================
leaveBtn.addEventListener('click', async () => {
    if (confirm('¿Seguro que quieres salir de la partida?')) {
        try {
            stopPolling();
            await gameApi.leaveGame();
            window.location.href = 'home.html';
        } catch (error) {
            console.error('Error al salir:', error);
            window.location.href = 'home.html';
        }
    }
});

confirmBetBtn.addEventListener('click', () => {
    const value = parseInt(betInput.value);
    if (isNaN(value) || value < 0) {
        alert('Por favor ingresa un número válido');
        return;
    }
    placeBet(value);
});

cancelBetBtn.addEventListener('click', hideBetModal);

betInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        confirmBetBtn.click();
    }
});

// Permitir cerrar modal con ESC
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && betModal.classList.contains('active')) {
        hideBetModal();
    }
});

// Detener polling al salir
window.addEventListener('beforeunload', () => {
    stopPolling();
});

// =======================
// Inicializar juego cuando cargue la página
// =======================
document.addEventListener('DOMContentLoaded', initGame);

// Agregar estilo CSS dinámico para el fallback de cartas
const style = document.createElement('style');
style.textContent = `
    .card-image-container {
        width: 100%;
        height: 100%;
        position: relative;
    }
    
    .card-image {
        width: 100%;
        height: 100%;
        object-fit: contain;
        border-radius: 0.5rem;
    }
    
    .trump-image {
        width: 100%;
        height: 100%;
        object-fit: contain;
        border-radius: 0.5rem;
        border: 2px solid var(--warning);
    }
    
    .card-fallback {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: white;
        border-radius: 0.5rem;
        border: 2px solid #333;
    }
    
    .card-symbol {
        font-size: 1.8rem;
        font-weight: bold;
        color: #333;
    }
    
    .trump-symbol {
        font-size: 2.5rem;
        font-weight: bold;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .player-bet {
        font-size: 0.8rem;
        color: var(--warning);
        margin-top: 0.25rem;
    }
`;
document.head.appendChild(style);