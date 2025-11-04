// ==================== ESTADO DE LA APLICACIÓN ====================
const AppState = {
    currentScreen: 'login',
    user: null,
    gameRoom: null,
    players: [],
    currentPlayer: null,
    trump: null,
    hand: [],
    playedCards: [],
    scores: {}
};

// ==================== NAVEGACIÓN ENTRE PANTALLAS ====================
function showScreen(screenId) {
    // Ocultar todas las pantallas
    document.querySelectorAll('.screen').forEach(screen => {
        screen.classList.remove('active');
    });

    // Mostrar la pantalla solicitada
    const screen = document.getElementById(screenId + '-screen');
    if (screen) {
        screen.classList.add('active');
        AppState.currentScreen = screenId;
    }
}

// ==================== GESTIÓN DE AUTENTICACIÓN ====================
function setupAuthListeners() {
    // Toggle entre login y registro
    const goToRegister = document.getElementById('go-to-register');
    const goToLogin = document.getElementById('go-to-login');
    const backToLogin = document.getElementById('back-to-login');
    const loginSide = document.querySelector('.login-side');
    const registerSide = document.querySelector('.register-side');

    goToRegister?.addEventListener('click', (e) => {
        e.preventDefault();
        registerSide.classList.add('show');
        loginSide.style.display = 'none';
    });

    goToLogin?.addEventListener('click', (e) => {
        e.preventDefault();
        registerSide.classList.remove('show');
        setTimeout(() => {
            loginSide.style.display = 'flex';
        }, 500);
    });

    backToLogin?.addEventListener('click', (e) => {
        e.preventDefault();
        registerSide.classList.remove('show');
        setTimeout(() => {
            loginSide.style.display = 'flex';
        }, 500);
    });

    // Formulario de login
    const loginForm = document.getElementById('login-form');
    loginForm?.addEventListener('submit', (e) => {
        e.preventDefault();
        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;

        // Simulación de login (aquí irá la lógica real con Firebase)
        if (email && password) {
            AppState.user = {
                email: email,
                name: email.split('@')[0],
                id: generateId()
            };
            showNotification('¡Bienvenido de vuelta!', 'success');
            showScreen('home');
        } else {
            showNotification('Por favor completa todos los campos', 'error');
        }
    });

    // Formulario de registro
    const registerForm = document.getElementById('register-form');
    registerForm?.addEventListener('submit', (e) => {
        e.preventDefault();
        const name = document.getElementById('register-name').value;
        const email = document.getElementById('register-email').value;
        const password = document.getElementById('register-password').value;

        // Simulación de registro (aquí irá la lógica real con Firebase)
        if (name && email && password) {
            AppState.user = {
                email: email,
                name: name,
                id: generateId()
            };
            showNotification('¡Cuenta creada exitosamente!', 'success');
            showScreen('home');
        } else {
            showNotification('Por favor completa todos los campos', 'error');
        }
    });
}

// ==================== GESTIÓN DE PARTIDAS ====================
function setupGameListeners() {
    // Crear partida
    const createGameBtn = document.getElementById('create-game-btn');
    createGameBtn?.addEventListener('click', () => {
        createGame();
    });

    // Unirse a partida
    const joinGameBtn = document.getElementById('join-game-btn');
    joinGameBtn?.addEventListener('click', () => {
        joinGame();
    });

    // Cancelar búsqueda
    const cancelSearchBtn = document.getElementById('cancel-search-btn');
    cancelSearchBtn?.addEventListener('click', () => {
        cancelSearch();
    });

    // Volver al inicio desde resultados
    const backToHomeBtn = document.getElementById('back-to-home');
    backToHomeBtn?.addEventListener('click', () => {
        resetGame();
        showScreen('home');
    });
}

function createGame() {
    showNotification('Creando partida...', 'info');
    showScreen('search');

    // Simulación: después de 3 segundos, iniciar el juego
    setTimeout(() => {
        AppState.gameRoom = {
            id: generateId(),
            host: AppState.user.id,
            players: [AppState.user]
        };
        showNotification('¡Partida encontrada!', 'success');
        startGame();
    }, 3000);
}

function joinGame() {
    showNotification('Buscando partida...', 'info');
    showScreen('search');

    // Simulación: después de 3 segundos, unirse a una partida
    setTimeout(() => {
        showNotification('¡Partida encontrada!', 'success');
        startGame();
    }, 3000);
}

function cancelSearch() {
    showNotification('Búsqueda cancelada', 'info');
    showScreen('home');
}

function startGame() {
    // Inicializar jugadores (simulación con 4 jugadores)
    AppState.players = [
        { id: AppState.user.id, name: AppState.user.name, score: 0 },
        { id: generateId(), name: 'Jugador 2', score: 0 },
        { id: generateId(), name: 'Jugador 3', score: 0 },
        { id: generateId(), name: 'Jugador 4', score: 0 }
    ];

    AppState.currentPlayer = AppState.user.id;

    // Generar mano inicial
    dealCards();

    // Mostrar pantalla de juego
    showScreen('game');
    updateGameUI();
}

// ==================== LÓGICA DEL JUEGO ====================
function dealCards() {
    const suits = ['♠', '♥', '♦', '♣'];
    const values = ['2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K', 'A'];

    // Crear baraja
    const deck = [];
    suits.forEach(suit => {
        values.forEach(value => {
            deck.push({ value, suit, color: (suit === '♥' || suit === '♦') ? 'red' : 'black' });
        });
    });

    // Barajar
    shuffleArray(deck);

    // Repartir cartas (6 cartas por jugador para empezar)
    AppState.hand = deck.slice(0, 6);
    AppState.trump = deck[24]; // Carta de triunfo
}

function updateGameUI() {
    // Actualizar mano del jugador
    const playerHand = document.getElementById('player-hand');
    if (playerHand && AppState.hand) {
        playerHand.innerHTML = '';
        AppState.hand.forEach((card, index) => {
            const cardElement = createCardElement(card, true);
            cardElement.addEventListener('click', () => playCard(index));
            playerHand.appendChild(cardElement);
        });
    }

    // Actualizar puntuaciones
    updateScores();
}

function createCardElement(card, isFront = true) {
    const cardDiv = document.createElement('div');
    cardDiv.className = isFront ? 'card card-front playable' : 'card card-back';

    if (isFront && card) {
        const valueSpan = document.createElement('span');
        valueSpan.className = 'card-value';
        valueSpan.textContent = card.value;

        const suitSpan = document.createElement('span');
        suitSpan.className = `card-suit ${card.color}`;
        suitSpan.textContent = card.suit;

        cardDiv.appendChild(valueSpan);
        cardDiv.appendChild(suitSpan);
        cardDiv.dataset.card = `${card.value}${card.suit}`;
    }

    return cardDiv;
}

function playCard(index) {
    if (!isPlayerTurn()) {
        showNotification('No es tu turno', 'warning');
        return;
    }

    const card = AppState.hand[index];
    AppState.hand.splice(index, 1);
    AppState.playedCards.push(card);

    showNotification(`Jugaste ${card.value}${card.suit}`, 'info');
    updateGameUI();

    // Simular jugadas de otros jugadores
    setTimeout(() => {
        simulateOpponentPlays();
    }, 1000);

    // Verificar fin de ronda
    if (AppState.hand.length === 0) {
        setTimeout(() => {
            endRound();
        }, 3000);
    }
}

function isPlayerTurn() {
    return AppState.currentPlayer === AppState.user.id;
}

function simulateOpponentPlays() {
    // Simulación básica de jugadas de oponentes
    showNotification('Otros jugadores están jugando...', 'info');
}

function updateScores() {
    // Actualizar visualización de puntuaciones
    document.querySelectorAll('.player-score').forEach(scoreElement => {
        scoreElement.textContent = Math.floor(Math.random() * 50).toString();
    });
}

function endRound() {
    // Calcular ganador de la ronda
    const winner = AppState.players[Math.floor(Math.random() * AppState.players.length)];

    showNotification(`${winner.name} ganó la ronda!`, 'success');

    // Verificar si el juego terminó
    if (Math.random() > 0.7) { // 30% de probabilidad de terminar
        endGame(winner);
    } else {
        // Nueva ronda
        setTimeout(() => {
            dealCards();
            updateGameUI();
        }, 2000);
    }
}

function endGame(winner) {
    // Mostrar pantalla de resultados
    setTimeout(() => {
        const winnerNameElement = document.getElementById('winner-name');
        if (winnerNameElement) {
            winnerNameElement.textContent = `¡${winner.name} ganó el juego!!!`;
        }
        showScreen('results');
    }, 2000);
}

function resetGame() {
    AppState.gameRoom = null;
    AppState.players = [];
    AppState.hand = [];
    AppState.playedCards = [];
    AppState.scores = {};
}

// ==================== SISTEMA DE NOTIFICACIONES ====================
function showNotification(message, type = 'info') {
    // Crear elemento de notificación
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;

    // Estilos inline para la notificación
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 25px;
        background: ${getNotificationColor(type)};
        color: white;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
        z-index: 10000;
        animation: slideIn 0.3s ease;
        font-weight: 500;
    `;

    document.body.appendChild(notification);

    // Eliminar después de 3 segundos
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 3000);
}

function getNotificationColor(type) {
    const colors = {
        success: '#4CAF50',
        error: '#f44336',
        warning: '#ff9800',
        info: '#2196F3'
    };
    return colors[type] || colors.info;
}

// Añadir animaciones CSS para notificaciones
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(400px);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }

    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(400px);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

// ==================== UTILIDADES ====================
function generateId() {
    return 'xxxx-xxxx-xxxx-xxxx'.replace(/x/g, () => {
        return (Math.random() * 16 | 0).toString(16);
    });
}

function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
}

// ==================== GESTIÓN DE PERFIL ====================
function setupProfileListeners() {
    const userProfile = document.getElementById('user-profile');
    userProfile?.addEventListener('click', () => {
        if (AppState.user) {
            showNotification(`Usuario: ${AppState.user.name}`, 'info');
        }
    });
}

// ==================== INICIALIZACIÓN ====================
document.addEventListener('DOMContentLoaded', () => {
    console.log('Oh Hell! Game - Inicializando...');

    // Configurar listeners
    setupAuthListeners();
    setupGameListeners();
    setupProfileListeners();

    // Mostrar pantalla inicial
    showScreen('login');

    // Auto-login para desarrollo (comentar en producción)
    // setTimeout(() => {
    //     AppState.user = {
    //         email: 'demo@test.com',
    //         name: 'Demo User',
    //         id: generateId()
    //     };
    //     showScreen('home');
    // }, 1000);

    console.log('Aplicación lista!');
});

// ==================== GESTIÓN DE WEBSOCKETS (Para implementar) ====================
class GameSocket {
    constructor() {
        this.socket = null;
        this.connected = false;
    }

    connect(userId) {
        // Aquí se implementará la conexión WebSocket real
        console.log('Conectando al servidor...', userId);
        // this.socket = new WebSocket('ws://tu-servidor.com');
        
        // this.socket.onopen = () => {
        //     this.connected = true;
        //     console.log('Conectado al servidor');
        // };

        // this.socket.onmessage = (event) => {
        //     this.handleMessage(JSON.parse(event.data));
        // };
    }

    send(type, data) {
        if (this.connected && this.socket) {
            this.socket.send(JSON.stringify({ type, data }));
        }
    }

    handleMessage(message) {
        // Manejar mensajes del servidor
        switch (message.type) {
            case 'game_start':
                startGame();
                break;
            case 'player_move':
                // Actualizar estado del juego
                break;
            case 'game_end':
                // Mostrar resultados
                break;
        }
    }

    disconnect() {
        if (this.socket) {
            this.socket.close();
            this.connected = false;
        }
    }
}

// Instancia global del socket
const gameSocket = new GameSocket();

// ==================== MANEJO DE ERRORES ====================
window.addEventListener('error', (event) => {
    console.error('Error en la aplicación:', event.error);
    showNotification('Ha ocurrido un error. Por favor, recarga la página.', 'error');
});

// ==================== PREVENIR RECARGA ACCIDENTAL ====================
window.addEventListener('beforeunload', (event) => {
    if (AppState.gameRoom && AppState.currentScreen === 'game') {
        event.preventDefault();
        event.returnValue = '¿Estás seguro de que quieres salir? Se perderá tu partida actual.';
        return event.returnValue;
    }
});

// ==================== GESTIÓN DE RESPONSIVE ====================
function handleResize() {
    const isMobile = window.innerWidth <= 768;
    document.body.classList.toggle('mobile', isMobile);
}

window.addEventListener('resize', handleResize);
handleResize();

// ==================== MODO DEBUG (Solo para desarrollo) ====================
if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
    window.debugGame = {
        showScreen,
        getState: () => AppState,
        simulateWin: () => endGame(AppState.players[0]),
        addCards: () => dealCards(),
        skipToGame: () => {
            AppState.user = { id: generateId(), name: 'Debug User', email: 'debug@test.com' };
            startGame();
        }
    };
    console.log('Modo debug activado. Usa window.debugGame para acceder a funciones de prueba.');
}
