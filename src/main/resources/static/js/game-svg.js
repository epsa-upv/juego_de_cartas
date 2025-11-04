/**
 * Oh Hell! Game - SVG Cards Integration
 * Funciones para trabajar con las cartas SVG
 */

// Configuración
const SVG_CARDS_PATH = 'assets/cards/svg-cards.svg';
const CARD_DIMENSIONS = {
    width: 169.075,
    height: 244.64,
    center: { x: 98.0375, y: 122.320 }
};

// Mapeo de palos
const SUITS = {
    'hearts': 'heart',
    'corazones': 'heart',
    'diamonds': 'diamond',
    'diamantes': 'diamond',
    'clubs': 'club',
    'tréboles': 'club',
    'trebol': 'club',
    'spades': 'spade',
    'picas': 'spade'
};

// Mapeo de valores
const VALUES = {
    'A': '1', 'ace': '1', 'as': '1',
    '2': '2', '3': '3', '4': '4', '5': '5',
    '6': '6', '7': '7', '8': '8', '9': '9', '10': '10',
    'J': 'jack', 'jack': 'jack', 'jota': 'jack',
    'Q': 'queen', 'queen': 'queen', 'reina': 'queen',
    'K': 'king', 'king': 'king', 'rey': 'king'
};

/**
 * Clase para manejar cartas SVG
 */
class SVGCard {
    constructor(suit, value) {
        this.suit = SUITS[suit.toLowerCase()] || suit;
        this.value = VALUES[value] || value;
        this.name = `${this.suit}_${this.value}`;
    }

    /**
     * Crea el elemento SVG de la carta
     */
    createElement(options = {}) {
        const {
            width = 80,
            height = 116,
            faceDown = false,
            backColor = '#DC143C',
            className = 'card-svg',
            playable = false,
            onClick = null
        } = options;

        const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        svg.setAttribute('class', className);
        svg.setAttribute('viewBox', `0 0 ${CARD_DIMENSIONS.width} ${CARD_DIMENSIONS.height}`);
        svg.setAttribute('width', width);
        svg.setAttribute('height', height);

        if (playable) {
            svg.classList.add('card-playable');
            svg.setAttribute('data-card', this.name);
        }

        const use = document.createElementNS('http://www.w3.org/2000/svg', 'use');

        if (faceDown) {
            use.setAttributeNS('http://www.w3.org/1999/xlink', 'href', 
                `${SVG_CARDS_PATH}#alternate-back`);
            use.setAttribute('fill', backColor);
        } else {
            use.setAttributeNS('http://www.w3.org/1999/xlink', 'href', 
                `${SVG_CARDS_PATH}#${this.name}`);
        }

        svg.appendChild(use);

        if (onClick) {
            svg.style.cursor = 'pointer';
            svg.addEventListener('click', onClick);
        }

        return svg;
    }

    /**
     * Obtiene el color del palo
     */
    getColor() {
        return (this.suit === 'heart' || this.suit === 'diamond') ? 'red' : 'black';
    }

    /**
     * Compara con otra carta
     */
    equals(otherCard) {
        return this.suit === otherCard.suit && this.value === otherCard.value;
    }

    /**
     * Representación en texto
     */
    toString() {
        const suitSymbols = {
            'heart': '♥',
            'diamond': '♦',
            'club': '♣',
            'spade': '♠'
        };
        return `${this.value.toUpperCase()}${suitSymbols[this.suit]}`;
    }
}

/**
 * Clase para manejar el mazo de cartas
 */
class Deck {
    constructor() {
        this.cards = [];
        this.initialize();
    }

    initialize() {
        const suits = ['heart', 'diamond', 'club', 'spade'];
        const values = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', 
                       'jack', 'queen', 'king'];

        this.cards = [];
        for (const suit of suits) {
            for (const value of values) {
                this.cards.push(new SVGCard(suit, value));
            }
        }
    }

    shuffle() {
        for (let i = this.cards.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [this.cards[i], this.cards[j]] = [this.cards[j], this.cards[i]];
        }
    }

    draw(count = 1) {
        return this.cards.splice(0, count);
    }

    reset() {
        this.initialize();
    }
}

/**
 * Funciones de utilidad para renderizar cartas
 */
const CardRenderer = {
    
    /**
     * Renderiza una mano de cartas en un contenedor
     */
    renderHand(cards, container, options = {}) {
        const {
            faceDown = false,
            playable = false,
            width = 80,
            height = 116,
            onCardClick = null
        } = options;

        container.innerHTML = '';

        cards.forEach((card, index) => {
            const cardElement = card.createElement({
                width,
                height,
                faceDown,
                playable,
                onClick: onCardClick ? () => onCardClick(card, index) : null
            });

            // Animación de entrada escalonada
            cardElement.style.animation = `dealCard 0.5s ease-out ${index * 0.1}s both`;

            container.appendChild(cardElement);
        });
    },

    /**
     * Renderiza cartas boca abajo
     */
    renderBackCards(count, container, options = {}) {
        const {
            width = 70,
            height = 102,
            backColor = '#DC143C'
        } = options;

        container.innerHTML = '';

        for (let i = 0; i < count; i++) {
            const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
            svg.setAttribute('class', 'card-svg');
            svg.setAttribute('viewBox', `0 0 ${CARD_DIMENSIONS.width} ${CARD_DIMENSIONS.height}`);
            svg.setAttribute('width', width);
            svg.setAttribute('height', height);

            const use = document.createElementNS('http://www.w3.org/2000/svg', 'use');
            use.setAttributeNS('http://www.w3.org/1999/xlink', 'href', 
                `${SVG_CARDS_PATH}#alternate-back`);
            use.setAttribute('fill', backColor);

            svg.appendChild(use);
            container.appendChild(svg);
        }
    },

    /**
     * Anima una carta jugada al centro
     */
    playCard(cardElement, targetContainer, callback) {
        cardElement.classList.add('card-playing');

        setTimeout(() => {
            const cardName = cardElement.getAttribute('data-card');
            
            // Crear nueva carta en el centro
            const newCard = cardElement.cloneNode(true);
            newCard.classList.remove('card-playable', 'card-playing');
            newCard.classList.add('played-card');
            newCard.removeAttribute('onclick');
            newCard.style.cursor = 'default';
            newCard.setAttribute('width', '85');
            newCard.setAttribute('height', '123');

            targetContainer.appendChild(newCard);
            cardElement.remove();

            if (callback) callback(cardName);
        }, 500);
    },

    /**
     * Limpia las cartas jugadas con animación
     */
    clearPlayedCards(container, callback) {
        const cards = container.querySelectorAll('.played-card');
        
        cards.forEach((card, index) => {
            setTimeout(() => {
                card.classList.add('trick-winner');
                setTimeout(() => card.remove(), 1000);
            }, index * 100);
        });

        if (callback) {
            setTimeout(callback, cards.length * 100 + 1000);
        }
    },

    /**
     * Muestra la carta de triunfo
     */
    setTrumpCard(card, container) {
        const cardElement = card.createElement({
            width: 70,
            height: 102,
            className: 'card-svg trump-card'
        });

        container.innerHTML = '';
        container.appendChild(cardElement);
    }
};

/**
 * Gestor del juego
 */
class GameManager {
    constructor() {
        this.deck = new Deck();
        this.players = [];
        this.currentTrick = [];
        this.trumpCard = null;
    }

    startNewRound(numPlayers = 4, cardsPerPlayer = 5) {
        this.deck.reset();
        this.deck.shuffle();

        // Repartir cartas a jugadores
        this.players = [];
        for (let i = 0; i < numPlayers; i++) {
            this.players.push({
                id: i,
                cards: this.deck.draw(cardsPerPlayer),
                score: 0
            });
        }

        // Establecer carta de triunfo
        const trumpCards = this.deck.draw(1);
        if (trumpCards.length > 0) {
            this.trumpCard = trumpCards[0];
        }

        return {
            players: this.players,
            trumpCard: this.trumpCard
        };
    }

    playCardToTrick(playerCard) {
        this.currentTrick.push(playerCard);
    }

    clearTrick() {
        this.currentTrick = [];
    }
}

/**
 * Funciones globales para compatibilidad
 */
function playCardSVG(cardElement) {
    const playedCardsContainer = document.getElementById('played-cards');
    CardRenderer.playCard(cardElement, playedCardsContainer, (cardName) => {
        console.log('Carta jugada:', cardName);
        
        // Aquí puedes añadir lógica adicional, como:
        // - Enviar al servidor via WebSocket
        // - Actualizar el estado del juego
        // - Validar la jugada
    });
}

/**
 * Inicialización del juego
 */
document.addEventListener('DOMContentLoaded', () => {
    console.log('SVG Cards Game inicializado');

    // Ejemplo: Iniciar un nuevo juego
    const game = new GameManager();
    
    // Descomenta para probar:
    /*
    const gameState = game.startNewRound(4, 5);
    
    // Renderizar mano del jugador
    const playerContainer = document.getElementById('player-bottom-cards');
    if (playerContainer) {
        CardRenderer.renderHand(gameState.players[0].cards, playerContainer, {
            playable: true,
            onCardClick: (card) => {
                console.log('Carta seleccionada:', card.toString());
            }
        });
    }
    
    // Renderizar carta de triunfo
    const trumpContainer = document.querySelector('.trump-container');
    if (trumpContainer && gameState.trumpCard) {
        CardRenderer.setTrumpCard(gameState.trumpCard, trumpContainer);
    }
    
    // Renderizar cartas de oponentes (boca abajo)
    for (let i = 1; i < 4; i++) {
        const opponentContainer = document.getElementById(`player-${['top', 'left', 'right'][i-1]}-cards`);
        if (opponentContainer) {
            CardRenderer.renderBackCards(5, opponentContainer);
        }
    }
    */
});

// Exportar para uso en módulos
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        SVGCard,
        Deck,
        CardRenderer,
        GameManager
    };
}
