const suits = ['♠', '♣', '♥', '♦'];
const ranks = ['2','3','4','5','6','7','8','9','10','J','Q','K','A'];
const suitNames = {'♠':'spades','♣':'clubs','♥':'hearts','♦':'diamonds'};

let gameState = {
    players: [
        {id:1,name:'Tú',hand:[],bid:null,tricks:0,score:0},
        {id:2,name:'Bill',hand:[],bid:null,tricks:0,score:0},
        {id:3,name:'Mike',hand:[],bid:null,tricks:0,score:0},
        {id:4,name:'Lisa',hand:[],bid:null,tricks:0,score:0}
    ],
    deck:[],
    trumpSuit:null,
    currentPlayer:0
};

function createDeck() {
    const deck=[];
    for(const s of suits){
        for(const r of ranks){
            deck.push({suit:s,rank:r});
        }
    }
    return shuffle(deck);
}

function shuffle(a){
    for(let i=a.length-1;i>0;i--){
        const j=Math.floor(Math.random()*(i+1));
        [a[i],a[j]]=[a[j],a[i]];
    }
    return a;
}

function createCardElement(card){
    const el=document.createElement('img');
    const rankMap = { 'J': 'jack', 'Q': 'queen', 'K': 'king', 'A': 'ace' };
    const rankName = rankMap[card.rank] || card.rank;
    const file = `${rankName.toLowerCase()}_of_${suitNames[card.suit]}.png`;

    el.src = `../assets/cards/png/${file}`;
    el.alt = `${card.rank}${card.suit}`;
    el.className = 'card-img';
    el.onclick = () => playCard(0, gameState.players[0].hand.indexOf(card));
    return el;
}

function dealCards(){
    playSound('deal');
    document.getElementById('dealButton').style.display = 'none';
    document.getElementById('deckArea').style.display = 'flex';
    gameState.deck = createDeck();

    const perPlayer = 8;
    gameState.players.forEach(p => {
        p.hand = [];
        p.tricks = 0;
        p.bid = null;
        for(let i=0; i<perPlayer; i++) p.hand.push(gameState.deck.pop());
    });

    const trump = gameState.deck[0];
    gameState.trumpSuit = trump.suit;

    const suitMap = {
        '♠': 'spade',
        '♣': 'club',
        '♥': 'heart',
        '♦': 'diamond'
    };

    // Mostrar la carta de triunfo con el ícono suit-*.png
    document.getElementById('trumpCard').src = `../assets/cards/png/suit-${suitMap[trump.suit]}.png`;
    document.getElementById('trumpIndicator').textContent = `El triunfo es ${trump.suit}`;

    displayHands();
    startBidding();
}

function displayHands(){
    const hand = document.getElementById('playerHand');
    hand.innerHTML = '';
    gameState.players[0].hand.forEach(c => hand.appendChild(createCardElement(c)));

    // Jugadores IA: solo mostrar dorso
    for(let i=1; i<4; i++){
        const h = document.getElementById(`player${i+1}Hand`);
        h.innerHTML = '';
        gameState.players[i].hand.forEach(() => h.appendChild(backCard()));
    }
}

function backCard(){
    const img = document.createElement('img');
    img.src = '../assets/cards/png/back-red.png';
    img.className = 'card-img';
    return img;
}

function startBidding(){
    const modal = document.getElementById('bidModal');
    modal.classList.add('active');
    const btns = document.getElementById('bidButtons');
    btns.innerHTML = '';

    for(let i=0; i<=gameState.players[0].hand.length; i++){
        const b = document.createElement('button');
        b.className = 'bid-btn';
        b.textContent = i;
        b.onclick = () => makeBid(i);
        btns.appendChild(b);
    }

    for(let i=1; i<4; i++){
        const aiBid = Math.floor(Math.random()*4);
        gameState.players[i].bid = aiBid;
    }
}

function makeBid(b){
    gameState.players[0].bid = b;
    document.getElementById('bidModal').classList.remove('active');
    showMessage(`Apuestas listas. El triunfo es ${gameState.trumpSuit}`);
    setTimeout(hideMessage, 2000);
}

function playCard(playerIndex, cardIndex){
    const player = gameState.players[playerIndex];
    if(cardIndex < 0 || cardIndex >= player.hand.length) return;

    const card = player.hand.splice(cardIndex, 1)[0];
    const area = document.getElementById('playedCards');
    const el = createCardElement(card);
    el.onclick = null;
    area.appendChild(el);
    playSound('play');
}

function newGame(){
    document.getElementById('dealButton').style.display = 'block';
    document.getElementById('deckArea').style.display = 'none';
    document.getElementById('playedCards').innerHTML = '';
    document.getElementById('playerHand').innerHTML = '';
    ['player2Hand','player3Hand','player4Hand'].forEach(id => document.getElementById(id).innerHTML = '');
    showMessage('Nuevo juego iniciado');
    setTimeout(hideMessage, 2000);
}

function showMessage(t){
    const el = document.getElementById('gameMessage');
    el.textContent = t;
    el.style.display = 'block';
}

function hideMessage(){
    document.getElementById('gameMessage').style.display = 'none';
}

/* Sonidos */
const sounds = {
    deal: new Audio('../assets/sounds/carta-naipes-naipe-carta-repartir-6-.mp3'),
    play: new Audio('../assets/sounds/JugarCarta.wav')
};
function playSound(n){
    const s = sounds[n];
    if(s){ s.currentTime = 0; s.play(); }
}

/* Info */
function showRules(){ alert('Reglas básicas: apuesta tus bazas y gana según tus aciertos.'); }
function showStats(){ document.getElementById('scoreTable').style.display = 'block'; }
function toggleMultiplayer(){ alert('Modo multijugador aún no disponible.'); }
function showOptions(){ alert('Opciones próximamente.'); }

window.onload = () => {
    showMessage('Bienvenido a Oh Hell! Pulsa "Nuevo Juego"');
    setTimeout(hideMessage, 3000);
};
