// game.js - Lógica del juego Oh Hell!

// Funciones para la pantalla principal
function crearPartida() {
    console.log("Creando partida...");
    // Redirigir a la pantalla de búsqueda
    window.location.href = 'searching.html';
}

function unirsePartida() {
    console.log("Uniéndose a partida...");
    // Redirigir a la pantalla de búsqueda
    window.location.href = 'searching.html';
}

// Funciones para la pantalla de búsqueda
function cancelarBusqueda() {
    console.log("Cancelando búsqueda...");
    window.location.href = 'home.html';
}

// Simular encontrar partida después de 3 segundos
if (window.location.pathname.includes('searching.html')) {
    setTimeout(() => {
        console.log("Partida encontrada!");
        window.location.href = 'game.html';
    }, 3000);
}

// Funciones para el juego
function playCard(cardElement) {
    console.log("Jugando carta:", cardElement);
    // Añadir animación de jugar carta
    cardElement.style.transform = 'translateY(-50px)';
    cardElement.style.opacity = '0.5';
    
    setTimeout(() => {
        // Mover la carta al centro de la mesa
        const playedCards = document.querySelector('.played-cards');
        if (playedCards) {
            const newCard = cardElement.cloneNode(true);
            newCard.classList.add('played-card');
            newCard.style.transform = 'none';
            newCard.style.opacity = '1';
            playedCards.appendChild(newCard);
            cardElement.remove();
        }
    }, 500);
}

// Funciones para la pantalla de resultados
function volverAlInicio() {
    console.log("Volviendo al inicio...");
    window.location.href = 'home.html';
}

function verEstadisticas() {
    console.log("Ver estadísticas...");
    // Aquí se implementaría la lógica para mostrar estadísticas
    alert('Función de estadísticas en desarrollo');
}

// Funciones de autenticación
function handleLogin(event) {
    event.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    console.log("Iniciando sesión:", email);
    
    // Aquí se implementaría la lógica de autenticación
    // Por ahora, simulamos un login exitoso
    if (email && password) {
        localStorage.setItem('userEmail', email);
        alert('¡Inicio de sesión exitoso!');
        window.location.href = 'home.html';
    } else {
        alert('Por favor, completa todos los campos');
    }
}

function handleRegister(event) {
    event.preventDefault();
    const nombre = document.getElementById('nombre').value;
    const email = document.getElementById('email-register').value;
    const password = document.getElementById('password-register').value;
    
    console.log("Registrando usuario:", nombre, email);
    
    // Aquí se implementaría la lógica de registro
    // Por ahora, simulamos un registro exitoso
    if (nombre && email && password) {
        localStorage.setItem('userName', nombre);
        localStorage.setItem('userEmail', email);
        alert('¡Registro exitoso! Ahora puedes iniciar sesión.');
        window.location.href = 'login.html';
    } else {
        alert('Por favor, completa todos los campos');
    }
}

// Verificar autenticación al cargar páginas protegidas
document.addEventListener('DOMContentLoaded', () => {
    const protectedPages = ['home.html', 'game.html', 'searching.html', 'results.html'];
    const currentPage = window.location.pathname.split('/').pop();
    
    if (protectedPages.includes(currentPage)) {
        const userEmail = localStorage.getItem('userEmail');
        if (!userEmail) {
            // Si no está autenticado, redirigir al login
            console.log('Usuario no autenticado, redirigiendo al login...');
            // Descomenta la siguiente línea cuando quieras activar la protección
            // window.location.href = 'login.html';
        }
    }
    
    // Actualizar nombre de usuario en la interfaz si está disponible
    const userName = localStorage.getItem('userName');
    if (userName) {
        console.log('Usuario actual:', userName);
    }
});

// Lógica del juego
class OhHellGame {
    constructor() {
        this.players = [];
        this.currentRound = 1;
        this.maxRounds = 10;
        this.deck = [];
        this.trumpCard = null;
        this.currentTrick = [];
        this.scores = {};
    }
    
    initializeDeck() {
        const suits = ['♠', '♥', '♦', '♣'];
        const values = ['2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K', 'A'];
        
        this.deck = [];
        for (let suit of suits) {
            for (let value of values) {
                this.deck.push({
                    suit: suit,
                    value: value,
                    color: (suit === '♥' || suit === '♦') ? 'red' : 'black'
                });
            }
        }
    }
    
    shuffleDeck() {
        for (let i = this.deck.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [this.deck[i], this.deck[j]] = [this.deck[j], this.deck[i]];
        }
    }
    
    dealCards(numCards) {
        const hands = {};
        for (let player of this.players) {
            hands[player] = [];
            for (let i = 0; i < numCards; i++) {
                hands[player].push(this.deck.pop());
            }
        }
        return hands;
    }
    
    startNewRound() {
        this.initializeDeck();
        this.shuffleDeck();
        
        const cardsPerPlayer = this.currentRound;
        const hands = this.dealCards(cardsPerPlayer);
        
        // Carta de triunfo
        if (this.deck.length > 0) {
            this.trumpCard = this.deck.pop();
        }
        
        return hands;
    }
    
    calculateScore(player, tricksTaken, tricksBid) {
        if (tricksTaken === tricksBid) {
            return 10 + tricksBid;
        } else {
            return Math.abs(tricksTaken - tricksBid) * -1;
        }
    }
}

// Instancia global del juego
let gameInstance = new OhHellGame();

// Funciones de utilidad
function renderCard(card) {
    return `
        <div class="card card-front">
            <span class="card-value">${card.value}</span>
            <span class="card-suit ${card.color}">${card.suit}</span>
        </div>
    `;
}

function updatePlayerScore(playerId, score) {
    const scoreElement = document.querySelector(`#player-${playerId} .points-value`);
    if (scoreElement) {
        scoreElement.textContent = score;
    }
}

console.log("Game.js cargado correctamente");
