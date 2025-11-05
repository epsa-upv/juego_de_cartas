// game.js - Oh Hell! Game - Cliente JavaScript con WebSocket
// Integración completa con backend Spring Boot

// ==================== CONFIGURACIÓN ====================
const CONFIG = {
    WS_URL: 'ws://localhost:8080/ws',
    API_BASE_URL: 'http://localhost:8080/api',
    MIN_PLAYERS: 3,
    MAX_PLAYERS: 7,
    TOTAL_ROUNDS: 19
};

// ==================== WEBSOCKET CLIENT ====================
class GameWebSocket {
    constructor() {
        this.socket = null;
        this.stompClient = null;
        this.gameId = null;
        this.playerId = null;
        this.connected = false;
    }

    connect(gameId, playerId) {
        this.gameId = gameId;
        this.playerId = playerId;

        return new Promise((resolve, reject) => {
            // Usar SockJS y STOMP para WebSocket
            const socket = new SockJS(CONFIG.WS_URL);
            this.stompClient = Stomp.over(socket);

            this.stompClient.connect({}, (frame) => {
                console.log('Connected to WebSocket:', frame);
                this.connected = true;

                // RF-23: Suscribirse a actualizaciones del juego
                this.stompClient.subscribe(`/topic/game/${gameId}`, (message) => {
                    const gameState = JSON.parse(message.body);
                    this.handleGameStateUpdate(gameState);
                });

                // Suscribirse a eventos de jugadores
                this.stompClient.subscribe(`/topic/game/${gameId}/players`, (message) => {
                    const playerEvent = JSON.parse(message.body);
                    this.handlePlayerEvent(playerEvent);
                });

                resolve();
            }, (error) => {
                console.error('WebSocket connection error:', error);
                this.connected = false;
                reject(error);
            });
        });
    }

    disconnect() {
        if (this.stompClient && this.connected) {
            this.stompClient.disconnect();
            this.connected = false;
        }
    }

    // RF-11, RF-12: Enviar acción de jugar carta
    sendPlayCard(card) {
        if (!this.connected) {
            console.error('WebSocket not connected');
            return;
        }

        this.stompClient.send(`/app/game/${this.gameId}/play`, {}, JSON.stringify({
            playerId: this.playerId,
            card: card
        }));
    }

    // RF-07: Enviar apuesta
    sendBet(bet) {
        if (!this.connected) {
            console.error('WebSocket not connected');
            return;
        }

        this.stompClient.send(`/app/game/${this.gameId}/bet`, {}, JSON.stringify({
            playerId: this.playerId,
            bet: bet
        }));
    }

    // RF-23: Manejar actualizaciones de estado del juego
    handleGameStateUpdate(gameState) {
        console.log('Game state updated:', gameState);
        gameManager.updateGameState(gameState);
    }

    handlePlayerEvent(playerEvent) {
        console.log('Player event:', playerEvent);
        // Notificar eventos de jugadores (conexión, desconexión, etc)
        if (playerEvent.type === 'PLAYER_JOINED') {
            showNotification(`${playerEvent.playerName} se ha unido al juego`);
        } else if (playerEvent.type === 'PLAYER_LEFT') {
            showNotification(`${playerEvent.playerName} ha salido del juego`);
        }
    }
}

// ==================== GAME MANAGER ====================
class GameManager {
    constructor() {
        this.gameState = null;
        this.currentPlayer = null;
        this.ws = new GameWebSocket();
    }

    // RF-01: Crear nueva partida
    async createGame(playerName) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}/games`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ playerName })
            });

            const game = await response.json();
            this.gameState = game;
            this.currentPlayer = game.players[0];

            // Conectar WebSocket
            await this.ws.connect(game.id, this.currentPlayer.id);

            // RF-22: Redirigir a sala de espera
            window.location.href = `waiting-room.html?gameId=${game.id}`;
        } catch (error) {
            console.error('Error creating game:', error);
            showError('Error al crear la partida. Inténtalo de nuevo.');
        }
    }

    // RF-01: Unirse a partida existente
    async joinGame(gameId, playerName) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}/games/${gameId}/join`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ playerName })
            });

            if (!response.ok) {
                throw new Error('No se pudo unir a la partida');
            }

            const result = await response.json();
            this.currentPlayer = result.player;
            this.gameState = result.game;

            // Conectar WebSocket
            await this.ws.connect(gameId, this.currentPlayer.id);

            // RF-22: Redirigir a sala de espera
            window.location.href = `waiting-room.html?gameId=${gameId}`;
        } catch (error) {
            console.error('Error joining game:', error);
            showError('Error al unirse a la partida.');
        }
    }

    // Iniciar partida (solo host)
    async startGame(gameId) {
        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}/games/${gameId}/start`, {
                method: 'POST'
            });

            if (!response.ok) {
                throw new Error('No se pudo iniciar la partida');
            }

            // El estado se actualizará vía WebSocket
        } catch (error) {
            console.error('Error starting game:', error);
            showError('Error al iniciar la partida.');
        }
    }

    // RF-07: Realizar apuesta
    async placeBet(bet) {
        // RF-08: Validar apuesta del último jugador
        if (this.isLastBettor()) {
            const forbiddenBet = this.calculateForbiddenBet();
            if (bet === forbiddenBet) {
                showError(`No puedes apostar ${bet}. Ese valor equilibraría el total de bazas.`);
                return;
            }
        }

        // Enviar apuesta vía WebSocket
        this.ws.sendBet(bet);
    }

    // RF-08: Calcular apuesta prohibida para último jugador
    calculateForbiddenBet() {
        const totalBets = this.gameState.players
            .filter(p => p.bet !== null)
            .reduce((sum, p) => sum + p.bet, 0);
        
        const cardsPerPlayer = this.gameState.cardsPerPlayer;
        return cardsPerPlayer - totalBets;
    }

    isLastBettor() {
        const bettorsCount = this.gameState.players.filter(p => p.bet !== null).length;
        return bettorsCount === this.gameState.players.length - 1;
    }

    // RF-11: Jugar carta
    async playCard(card) {
        // RF-11: Validar que la carta sea jugable
        if (!this.isCardPlayable(card)) {
            showError('No puedes jugar esa carta. Debes seguir el palo inicial.');
            return;
        }

        // Enviar jugada vía WebSocket
        this.ws.sendPlayCard(card);
    }

    // RF-11: Validar si una carta es jugable
    isCardPlayable(card) {
        const leadSuit = this.gameState.currentTrick?.leadSuit;
        
        // Primera carta de la baza, cualquier carta es válida
        if (!leadSuit) {
            return true;
        }

        const playerHand = this.currentPlayer.hand;
        const hasLeadSuit = playerHand.some(c => c.suit === leadSuit);

        // Si tiene cartas del palo inicial, debe jugar ese palo
        if (hasLeadSuit) {
            return card.suit === leadSuit;
        }

        // Si no tiene el palo, puede jugar cualquier carta
        return true;
    }

    // RF-23: Actualizar estado del juego desde WebSocket
    updateGameState(gameState) {
        this.gameState = gameState;
        
        // Actualizar interfaz según el estado
        this.renderGameState();

        // RF-12: Si se completó una baza, mostrar ganador
        if (gameState.lastTrickWinner) {
            showTrickWinner(gameState.lastTrickWinner);
        }

        // RF-14: Si terminó una ronda, mostrar puntuaciones
        if (gameState.phase === 'ROUND_END') {
            showRoundScores(gameState.scores);
        }

        // RF-16: Si terminó el juego, mostrar ganador final
        if (gameState.phase === 'GAME_END') {
            showGameWinner(gameState.winner);
        }
    }

    // Renderizar estado del juego
    renderGameState() {
        if (!this.gameState) return;

        // RF-17: Mostrar cartas en mano
        this.renderPlayerHand();

        // RF-18: Mostrar mesa de juego
        this.renderGameTable();

        // RF-09: Mostrar apuestas
        this.renderBets();

        // RF-13: Mostrar bazas ganadas
        this.renderTricksWon();

        // RF-15: Mostrar tabla de puntuaciones
        this.renderScoreboard();
    }

    // RF-17: Renderizar cartas del jugador
    renderPlayerHand() {
        const handContainer = document.querySelector('.player-cards-bottom');
        if (!handContainer || !this.currentPlayer) return;

        handContainer.innerHTML = '';
        
        const hand = this.currentPlayer.hand || [];
        hand.forEach(card => {
            const cardElement = createCardElement(card);
            const isPlayable = this.isCardPlayable(card);
            
            if (isPlayable && this.isMyTurn()) {
                cardElement.classList.add('playable');
                cardElement.onclick = () => this.playCard(card);
            } else {
                cardElement.classList.add('not-playable');
            }

            handContainer.appendChild(cardElement);
        });
    }

    // RF-18: Renderizar mesa de juego
    renderGameTable() {
        // Renderizar cartas jugadas en la baza actual
        const playedCardsContainer = document.querySelector('.played-cards');
        if (!playedCardsContainer) return;

        playedCardsContainer.innerHTML = '';
        
        const currentTrick = this.gameState.currentTrick?.cards || [];
        currentTrick.forEach(playedCard => {
            const cardElement = createCardElement(playedCard.card);
            cardElement.classList.add('played-card');
            playedCardsContainer.appendChild(cardElement);
        });

        // RF-06: Mostrar carta de triunfo
        const trumpCard = document.querySelector('.trump-card');
        if (trumpCard && this.gameState.trumpSuit) {
            trumpCard.innerHTML = `
                <span class="card-value">${this.gameState.trumpCard?.rank || ''}</span>
                <span class="card-suit ${getSuitColor(this.gameState.trumpSuit)}">${getSuitSymbol(this.gameState.trumpSuit)}</span>
            `;
        }
    }

    // RF-09: Renderizar apuestas
    renderBets() {
        this.gameState.players.forEach((player, index) => {
            const betElement = document.querySelector(`#player-${index} .bet-value`);
            if (betElement) {
                betElement.textContent = player.bet !== null ? player.bet : '?';
            }
        });
    }

    // RF-13: Renderizar bazas ganadas
    renderTricksWon() {
        this.gameState.players.forEach((player, index) => {
            const tricksElement = document.querySelector(`#player-${index} .tricks-won`);
            if (tricksElement) {
                tricksElement.textContent = `${player.tricksWon}/${player.bet || '?'}`;
            }
        });
    }

    // RF-15: Renderizar tabla de puntuaciones
    renderScoreboard() {
        this.gameState.players.forEach((player, index) => {
            const scoreElement = document.querySelector(`#player-${index} .points-value`);
            if (scoreElement) {
                scoreElement.textContent = player.totalScore || 0;
            }
        });
    }

    isMyTurn() {
        return this.gameState.currentTurn === this.currentPlayer.id;
    }
}

// ==================== UTILIDADES ====================

// Crear elemento de carta con imagen PNG real
function createCardElement(card, faceDown = false) {
    const cardDiv = document.createElement('div');
    cardDiv.className = faceDown ? 'card card-back-img' : 'card card-front-img';
    
    if (faceDown) {
        // Reverso de la carta (rojo)
        cardDiv.innerHTML = `<img src="/assets/cards/png/back-red.png" alt="Card back" class="card-image">`;
    } else {
        // Cara de la carta usando PNG
        const imagePath = getCardImagePath(card);
        cardDiv.innerHTML = `<img src="${imagePath}" alt="${getRankDisplay(card.rank)} of ${card.suit}" class="card-image">`;
    }
    
    return cardDiv;
}

// Función auxiliar para obtener la ruta de la imagen PNG
function getCardImagePath(card) {
    if (!card || !card.suit || !card.rank) {
        return '/assets/cards/back-red.png';
    }

    const suitMap = {
        'HEARTS': 'hearts',
        'DIAMONDS': 'diamonds',
        'CLUBS': 'clubs',
        'SPADES': 'spades'
    };

    const rankMap = {
        'TWO': '2', 'THREE': '3', 'FOUR': '4', 'FIVE': '5', 'SIX': '6',
        'SEVEN': '7', 'EIGHT': '8', 'NINE': '9', 'TEN': '10',
        'JACK': 'jack', 'QUEEN': 'queen', 'KING': 'king', 'ACE': 'ace'
    };

    const suit = suitMap[card.suit.toUpperCase()] || 'hearts';
    const rank = rankMap[card.rank.toUpperCase()] || '2';

    return `/assets/cards/png/${rank}_of_${suit}.png`;
}

// Obtener valor display del rango
function getRankDisplay(rank) {
    const displayMap = {
        'TWO': '2', 'THREE': '3', 'FOUR': '4', 'FIVE': '5', 'SIX': '6',
        'SEVEN': '7', 'EIGHT': '8', 'NINE': '9', 'TEN': '10',
        'JACK': 'J', 'QUEEN': 'Q', 'KING': 'K', 'ACE': 'A'
    };
    return displayMap[rank?.toUpperCase()] || '?';
}
function getSuitSymbol(suit) {
    const symbols = {
        'HEARTS': '♥',
        'DIAMONDS': '♦',
        'CLUBS': '♣',
        'SPADES': '♠'
    };
    return symbols[suit] || suit;
}

// Obtener color del palo
function getSuitColor(suit) {
    return (suit === 'HEARTS' || suit === 'DIAMONDS') ? 'red' : 'black';
}

// Mostrar notificación
function showNotification(message) {
    // Implementar sistema de notificaciones
    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.textContent = message;
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Mostrar error
function showError(message) {
    alert(message); // Mejorar con modal personalizado
}

// RF-12: Mostrar ganador de baza
function showTrickWinner(winner) {
    showNotification(`${winner.name} ganó la baza!`);
}

// RF-14: Mostrar puntuaciones de ronda
function showRoundScores(scores) {
    // Implementar modal de puntuaciones
    console.log('Round scores:', scores);
}

// RF-16: Mostrar ganador del juego
function showGameWinner(winner) {
    localStorage.setItem('winner', JSON.stringify(winner));
    window.location.href = 'results.html';
}

// ==================== INICIALIZACIÓN ====================

const gameManager = new GameManager();

// Funciones globales para botones
function crearPartida() {
    const playerName = prompt('Ingresa tu nombre:');
    if (playerName) {
        gameManager.createGame(playerName);
    }
}

function unirsePartida() {
    const gameId = prompt('Ingresa el código de la partida:');
    const playerName = prompt('Ingresa tu nombre:');
    if (gameId && playerName) {
        gameManager.joinGame(gameId, playerName);
    }
}

function cancelarBusqueda() {
    gameManager.ws.disconnect();
    window.location.href = 'home.html';
}

function volverAlInicio() {
    gameManager.ws.disconnect();
    window.location.href = 'home.html';
}

function verEstadisticas() {
    // Implementar vista de estadísticas
    console.log('Ver estadísticas');
}

// Autenticación
function handleLogin(event) {
    event.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    if (email && password) {
        localStorage.setItem('userEmail', email);
        localStorage.setItem('isAuthenticated', 'true');
        window.location.href = 'home.html';
    }
}

function handleRegister(event) {
    event.preventDefault();
    const nombre = document.getElementById('nombre').value;
    const email = document.getElementById('email-register').value;
    const password = document.getElementById('password-register').value;
    
    if (nombre && email && password) {
        localStorage.setItem('userName', nombre);
        localStorage.setItem('userEmail', email);
        localStorage.setItem('isAuthenticated', 'true');
        window.location.href = 'login.html';
    }
}

// Verificar autenticación
document.addEventListener('DOMContentLoaded', () => {
    const protectedPages = ['home.html', 'game.html', 'waiting-room.html', 'results.html'];
    const currentPage = window.location.pathname.split('/').pop();
    
    if (protectedPages.includes(currentPage)) {
        const isAuthenticated = localStorage.getItem('isAuthenticated');
        if (!isAuthenticated) {
            window.location.href = 'login.html';
        }
    }

    // Inicializar juego si estamos en la página de juego
    if (currentPage === 'game.html') {
        const urlParams = new URLSearchParams(window.location.search);
        const gameId = urlParams.get('gameId');
        
        if (gameId) {
            // Cargar estado del juego
            fetch(`${CONFIG.API_BASE_URL}/games/${gameId}/state`)
                .then(response => response.json())
                .then(gameState => {
                    gameManager.updateGameState(gameState);
                });
        }
    }
});

// Limpiar al salir
window.addEventListener('beforeunload', () => {
    if (gameManager.ws.connected) {
        gameManager.ws.disconnect();
    }
});

console.log('Oh Hell! Game - Client loaded');
