// card-mapper.js - Mapeo de cartas del backend a imágenes PNG

/**
 * Mapea los datos de una carta del backend al nombre de archivo PNG
 * @param {Object} card - Carta del backend {suit: 'HEARTS', rank: 'ACE'}
 * @returns {string} - Nombre del archivo PNG
 */
function getCardImagePath(card) {
    if (!card || !card.suit || !card.rank) {
        return '/assets/cards/back-red.png'; // Carta por defecto
    }

    // Mapeo de palos del backend a nombres en archivos
    const suitMap = {
        'HEARTS': 'hearts',
        'DIAMONDS': 'diamonds',
        'CLUBS': 'clubs',
        'SPADES': 'spades'
    };

    // Mapeo de rangos del backend a nombres en archivos
    const rankMap = {
        'TWO': '2',
        'THREE': '3',
        'FOUR': '4',
        'FIVE': '5',
        'SIX': '6',
        'SEVEN': '7',
        'EIGHT': '8',
        'NINE': '9',
        'TEN': '10',
        'JACK': 'jack',
        'QUEEN': 'queen',
        'KING': 'king',
        'ACE': 'ace'
    };

    const suit = suitMap[card.suit.toUpperCase()] || 'hearts';
    const rank = rankMap[card.rank.toUpperCase()] || '2';

    // Formato: rank_of_suit.png (ej: ace_of_hearts.png)
    return `/assets/cards/png/${rank}_of_${suit}.png`;
}

/**
 * Obtiene la ruta de la imagen para el reverso de la carta
 * @param {string} color - Color del reverso (por defecto 'red')
 * @returns {string} - Ruta del archivo
 */
function getCardBackImage(color = 'red') {
    return `/assets/cards/back-${color}.png`;
}

/**
 * Obtiene el símbolo Unicode para un palo
 * @param {string} suit - Palo (HEARTS, DIAMONDS, CLUBS, SPADES)
 * @returns {string} - Símbolo Unicode
 */
function getSuitSymbol(suit) {
    const symbols = {
        'HEARTS': '♥',
        'DIAMONDS': '♦',
        'CLUBS': '♣',
        'SPADES': '♠'
    };
    return symbols[suit.toUpperCase()] || '?';
}

/**
 * Obtiene el color del palo (para CSS)
 * @param {string} suit - Palo
 * @returns {string} - Clase CSS ('red' o 'black')
 */
function getSuitColor(suit) {
    const redSuits = ['HEARTS', 'DIAMONDS'];
    return redSuits.includes(suit.toUpperCase()) ? 'red' : 'black';
}

/**
 * Obtiene el valor display del rango
 * @param {string} rank - Rango de la carta
 * @returns {string} - Valor a mostrar (A, 2-10, J, Q, K)
 */
function getRankDisplay(rank) {
    const displayMap = {
        'TWO': '2',
        'THREE': '3',
        'FOUR': '4',
        'FIVE': '5',
        'SIX': '6',
        'SEVEN': '7',
        'EIGHT': '8',
        'NINE': '9',
        'TEN': '10',
        'JACK': 'J',
        'QUEEN': 'Q',
        'KING': 'K',
        'ACE': 'A'
    };
    return displayMap[rank.toUpperCase()] || '?';
}

/**
 * Pre-carga todas las imágenes de cartas para mejor rendimiento
 */
function preloadCardImages() {
    const suits = ['hearts', 'diamonds', 'clubs', 'spades'];
    const ranks = ['2', '3', '4', '5', '6', '7', '8', '9', '10', 'jack', 'queen', 'king', 'ace'];
    
    const images = [];
    
    // Pre-cargar reversos
    images.push(new Image());
    images[images.length - 1].src = getCardBackImage('red');
    
    // Pre-cargar todas las cartas
    suits.forEach(suit => {
        ranks.forEach(rank => {
            const img = new Image();
            img.src = `/assets/cards/png/${rank}_of_${suit}.png`;
            images.push(img);
        });
    });
    
    console.log(`Pre-cargadas ${images.length} imágenes de cartas`);
    return images;
}

// Pre-cargar imágenes al cargar la página
if (typeof window !== 'undefined') {
    window.addEventListener('load', () => {
        preloadCardImages();
    });
}
