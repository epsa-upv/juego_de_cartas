/**
 * Main.js - Controlador principal de la aplicación Oh Hell!
 * Orquesta la comunicación entre API, Estado y UI
 */

// ==================== INICIALIZACIÓN ====================

document.addEventListener('DOMContentLoaded', () => {
    initializeApp();
});

async function initializeApp() {
    console.log('🎮 Inicializando Oh Hell! Game...');

    // Verificar conexión con el backend (con timeout corto)
    let backendAvailable = false;
    try {
        // Usar Promise.race para limitar el tiempo de espera
        const testPromise = apiClient.testDatabase();
        const timeoutPromise = new Promise((_, reject) =>
            setTimeout(() => reject(new Error('Timeout')), 2000)
        );

        await Promise.race([testPromise, timeoutPromise]);
        console.log('✓ Conexión con backend establecida');
        backendAvailable = true;
    } catch (error) {
        console.warn('⚠️ Backend no disponible inicialmente:', error.message);
        console.log('ℹ️ Intentando reconectar en background...');

        // Intentar reconectar en background (no bloqueante)
        retryBackendConnection();
    }

    // Cargar estado del jugador si existe
    const savedPlayer = gameState.getCurrentPlayer();
    if (savedPlayer) {
        console.log('✓ Jugador encontrado:', savedPlayer.name);
    }

    // Inicializar página actual
    const currentPage = getCurrentPage();
    console.log('📄 Página actual:', currentPage);

    // Guardar estado de backend
    window.backendAvailable = backendAvailable;

    switch (currentPage) {
        case 'index':
            handleIndexPage();
            break;
        case 'login':
            handleLoginPage();
            break;
        case 'register':
            handleRegisterPage();
            break;
        case 'home':
            handleHomePage();
            break;
        case 'searching':
            handleSearchingPage();
            break;
        case 'waiting-room':
            handleWaitingRoomPage();
            break;
        case 'game':
            handleGamePage();
            break;
        case 'results':
            handleResultsPage();
            break;
        case 'test':
            handleTestPage();
            break;
        default:
            console.log('Página desconocida:', currentPage);
            setupGenericNavigation();
    }
}

// Reintentar conexión con backend en background
function retryBackendConnection() {
    let attempts = 0;
    const maxAttempts = 5;
    const interval = setInterval(async () => {
        attempts++;
        try {
            await apiClient.testDatabase();
            console.log('✅ Backend reconectado en background');
            window.backendAvailable = true;
            clearInterval(interval);
        } catch (error) {
            if (attempts >= maxAttempts) {
                console.warn('❌ Backend no disponible después de 5 intentos');
                clearInterval(interval);
            }
        }
    }, 2000); // Intentar cada 2 segundos
}

// ==================== NAVEGACIÓN GENÉRICA ====================
function setupGenericNavigation() {
    // Configurar todos los enlaces y botones para navegación
    document.querySelectorAll('a[href]').forEach(link => {
        if (!link.href.includes('http') && link.href.includes('.html')) {
            link.addEventListener('click', (e) => {
                console.log('Navegando a:', link.href);
            });
        }
    });
}

function getCurrentPage() {
    const path = window.location.pathname;
    const page = path.split('/').pop().replace('.html', '') || 'index';
    return page;
}

// ==================== INDEX PAGE ====================

function handleIndexPage() {
    // Redirigir automáticamente
    if (gameState.isAuthenticated()) {
        window.location.href = 'home.html';
    } else {
        window.location.href = 'login.html';
    }
}

// ==================== LOGIN PAGE ====================

function handleLoginPage() {
    console.log('📄 Inicializando página de login');

    const form = document.querySelector('.auth-form');
    if (!form) {
        console.warn('⚠️ Formulario de login no encontrado');
        return;
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        console.log('🔐 Intento de login...', { backendAvailable: window.backendAvailable });
        await handleLogin();
    });

    // Asegurar que el link de registro funciona
    const registerLinks = document.querySelectorAll('a[href="register.html"]');
    registerLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            console.log('Navegando a registro...');
        });
    });
}

async function handleLogin() {
    const email = document.getElementById('email')?.value;
    const password = document.getElementById('password')?.value;

    console.log('📋 Datos de login:', { email, passwordLength: password?.length });

    if (!email || !password) {
        uiManager.showError('Por favor completa todos los campos');
        return;
    }

    uiManager.showLoading('Iniciando sesión...');

    try {
        console.log('🔍 Buscando usuario por email...');
        // Buscar jugador por email
        const players = await apiClient.searchPlayers({ email: email });

        if (!players || players.length === 0) {
            throw new Error('Email no registrado. Por favor, regístrate primero.');
        }

        const player = players[0];

        console.log('✅ Usuario encontrado:', player.username);

        // ✅ IMPORTANTE: Verificar la contraseña
        const passwordHash = await apiClient.hashPassword(password, email);

        if (player.password !== passwordHash) {
            throw new Error('Contraseña incorrecta. Verifica tus datos.');
        }

        gameState.setCurrentPlayer(player);
        uiManager.hideLoading();
        uiManager.showSuccess(`¡Bienvenido de nuevo, ${player.username}!`);

        setTimeout(() => {
            window.location.href = 'home.html';
        }, 1000);

    } catch (error) {
        console.error('❌ Error al iniciar sesión:', error);
        uiManager.hideLoading();
        uiManager.showError(error.message || 'Error al iniciar sesión. Verifica tu email y contraseña.');
    }
}

// ==================== REGISTER PAGE ====================

function handleRegisterPage() {
    console.log('📄 Inicializando página de registro');

    const form = document.querySelector('.auth-form');
    if (!form) {
        console.warn('⚠️ Formulario de registro no encontrado');
        return;
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        console.log('📝 Intento de registro...', { backendAvailable: window.backendAvailable });
        await handleRegister();
    });

    // Asegurar que el link de login funciona
    const loginLinks = document.querySelectorAll('a[href="login.html"]');
    loginLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            console.log('Navegando a login...');
        });
    });
}

async function handleRegister() {
    const username = document.getElementById('username-register')?.value;
    const email = document.getElementById('email-register')?.value;
    const password = document.getElementById('password-register')?.value;

    console.log('📋 Datos del registro:', { username, email, passwordLength: password?.length });

    if (!username || !email || !password) {
        uiManager.showError('Por favor completa todos los campos');
        return;
    }

    if (password.length < 6) {
        uiManager.showError('La contraseña debe tener al menos 6 caracteres');
        return;
    }

    uiManager.showLoading('Creando cuenta...');

    try {
        console.log('✓ Creando jugador...');
        // Crear nuevo jugador (dejar que el backend verifique duplicados)
        const player = await apiClient.createPlayer(username, email, password);

        console.log('✅ Jugador creado:', player);
        gameState.setCurrentPlayer(player);
        uiManager.hideLoading();
        uiManager.showSuccess(`¡Cuenta creada! Bienvenido, ${player.username || username}!`);

        setTimeout(() => {
            window.location.href = 'home.html';
        }, 1000);

    } catch (error) {
        console.error('❌ Error al crear cuenta:', error);
        uiManager.hideLoading();

        // Detectar si es por email duplicado
        if (error.message.includes('ya existe') || error.message.includes('already exists')) {
            uiManager.showError('Este email ya está registrado. Usa otro o inicia sesión.');
        } else {
            uiManager.showError(error.message || 'Error al crear cuenta. Verifica la conexión.');
        }
    }
}

// ==================== HOME PAGE ====================

function handleHomePage() {
    console.log('🏠 Inicializando página Home');

    if (!gameState.isAuthenticated()) {
        console.warn('⚠️ No autenticado, redirigiendo a login');
        window.location.href = 'login.html';
        return;
    }

    const player = gameState.getCurrentPlayer();
    console.log('🏠 Home - Jugador:', player);

    // Actualizar nombre del jugador en la navbar
    const userNameEl = document.getElementById('user-name');
    if (userNameEl && player) {
        userNameEl.textContent = player.username || player.name || 'Usuario';
        console.log('✅ Nombre actualizado:', userNameEl.textContent);
    }

    // Actualizar avatar
    const userAvatarEl = document.getElementById('user-avatar');
    if (userAvatarEl && player) {
        const initial = (player.username || player.name || 'U')[0].toUpperCase();
        userAvatarEl.textContent = initial;
    }

    // Setup de botones
    setupHomeButtons();
}

function setupHomeButtons() {
    console.log('⚙️ Configurando botones de home...');

    // Botón de crear partida
    const createBtn = document.getElementById('create-game-btn') || document.querySelector('.btn-primary');
    if (createBtn) {
        console.log('✅ Botón Crear Partida encontrado');
        createBtn.addEventListener('click', async (e) => {
            e.preventDefault();
            console.log('🎮 Intento de crear partida...');
            await handleCreateGame();
        });
    } else {
        console.warn('⚠️ Botón Crear Partida no encontrado');
    }

    // Botón de unirse a partida
    const joinBtn = document.getElementById('join-game-btn') || document.querySelector('.btn-secondary');
    if (joinBtn) {
        console.log('✅ Botón Unirse encontrado');
        joinBtn.addEventListener('click', async (e) => {
            e.preventDefault();
            console.log('🔍 Intento de unirse a partida...');
            await handleJoinGame();
        });
    } else {
        console.warn('⚠️ Botón Unirse no encontrado');
    }

    // Botón de estadísticas
    const statsBtn = document.getElementById('stats-btn') || document.querySelector('.btn-outline');
    if (statsBtn) {
        console.log('✅ Botón Estadísticas encontrado');
        statsBtn.addEventListener('click', (e) => {
            e.preventDefault();
            console.log('📊 Estadísticas');
            uiManager.showSuccess('Las estadísticas estarán disponibles próximamente...');
        });
    } else {
        console.warn('⚠️ Botón Estadísticas no encontrado');
    }

    console.log('✅ Botones configurados');
}

function updateUserInfo() {
    const player = gameState.getCurrentPlayer();
    if (!player) return;

    // Actualizar nombre en navbar
    const userNameEl = document.getElementById('user-name');
    if (userNameEl) {
        userNameEl.textContent = player.name;
    }

    // Actualizar avatar
    const avatarEl = document.getElementById('user-avatar');
    if (avatarEl) {
        avatarEl.textContent = player.name.charAt(0).toUpperCase();
    }
}

async function handleCreateGame() {
    console.log('🎮 Iniciando creación de partida...');
    const player = gameState.getCurrentPlayer();

    if (!player) {
        uiManager.showError('Error: No hay jugador autenticado');
        return;
    }

    uiManager.showLoading('Creando partida...');

    try {
        console.log('📡 Intentando crear partida en el servidor...');

        // Crear game
        const game = await apiClient.createGame({
            maxPlayers: 4,
            initialLives: 5,
            createdBy: player.id || 1
        });

        if (!game || !game.id) {
            throw new Error('No se pudo obtener el ID de la partida creada');
        }

        console.log('✅ Partida creada exitosamente:', game);
        gameState.setCurrentGame(game);

        // Crear bots automáticamente
        console.log('🤖 Creando bots para la partida...');
        const botsCreated = botManager.createBotsForGame(game.id, 1, 4);
        console.log(`🤖 ${botsCreated.length} bots creados para la partida ${game.id}`);

        uiManager.hideLoading();
        uiManager.showSuccess(`¡Partida creada con ${botsCreated.length} bots!`);

        // Ir directamente a sala de espera
        setTimeout(() => {
            console.log(`➡️ Redirigiendo a waiting-room con gameId: ${game.id}`);
            window.location.href = `waiting-room.html?gameId=${game.id}`;
        }, 800);

    } catch (error) {
        console.error('❌ Error al crear partida:', error);
        uiManager.hideLoading();

        // Mostrar errores más específicos
        if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
            uiManager.showError('Error de conexión. Verifica tu conexión a internet e intenta nuevamente.');
        } else if (error.message.includes('no se pudo obtener')) {
            uiManager.showError('Error al procesar la respuesta del servidor. Intenta de nuevo.');
        } else {
            uiManager.showError(error.message || 'Error al crear partida. Intenta de nuevo.');
        }
    }
}

async function handleJoinGame() {
    console.log('🔍 Buscando partidas disponibles...');

    uiManager.showLoading('Buscando partidas disponibles...');

    try {
        console.log('📡 Solicitando partidas al servidor...');
        const games = await apiClient.getAvailableGames();
        uiManager.hideLoading();

        console.log('Partidas disponibles:', games);

        if (!games || games.length === 0) {
            uiManager.showError('No hay partidas disponibles. ¡Crea una!');
            return;
        }

        // Unirse a la primera partida disponible
        const game = games[0];
        const player = gameState.getCurrentPlayer();

        if (!player) {
            uiManager.showError('Error: No hay jugador autenticado');
            return;
        }

        uiManager.showLoading('Uniéndose a partida...');
        console.log(`📡 Uniéndose a partida ${game.id}...`);

        const updatedGame = await apiClient.joinGame(game.id, player.id || 1);

        console.log('✅ Unido a partida:', updatedGame);
        gameState.setCurrentGame(updatedGame);
        uiManager.hideLoading();
        uiManager.showSuccess('¡Te has unido a la partida!');

        setTimeout(() => {
            window.location.href = `waiting-room.html?gameId=${game.id}`;
        }, 500);

    } catch (error) {
        console.error('❌ Error al buscar/unirse a partida:', error);
        uiManager.hideLoading();
        uiManager.showError(error.message || 'Error al buscar partidas. Intenta de nuevo.');
    }
}

// ==================== WAITING ROOM PAGE ====================

function handleWaitingRoomPage() {
    if (!gameState.isAuthenticated()) {
        window.location.href = 'login.html';
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    const gameId = urlParams.get('gameId');

    if (!gameId) {
        uiManager.showError('ID de partida no válido');
        window.location.href = 'home.html';
        return;
    }

    loadWaitingRoom(gameId);

    // Actualizar cada 2 segundos
    const intervalId = setInterval(() => {
        loadWaitingRoom(gameId);
    }, 2000);

    // Limpiar intervalo cuando se salga de la página
    window.addEventListener('beforeunload', () => {
        clearInterval(intervalId);
    });

    // Botón de iniciar partida (solo para el host)
    const startBtn = document.getElementById('start-game-btn');
    if (startBtn) {
        startBtn.onclick = () => handleStartGame(gameId);
    }
}

async function loadWaitingRoom(gameId) {
    try {
        const game = await apiClient.getGame(gameId);
        gameState.setCurrentGame(game);

        // Actualizar lista de jugadores
        // Si players no existe, crear array vacío o usar mock
        const players = game.players || game.playerIds || [];
        console.log('👥 Jugadores:', players);
        uiManager.renderPlayerList(Array.isArray(players) ? players : []);

        // Mostrar botón de inicio si eres el host y hay suficientes jugadores
        const startBtn = document.getElementById('start-game-btn');
        if (startBtn) {
            if (gameState.canStartGame()) {
                startBtn.style.display = 'block';
            } else {
                startBtn.style.display = 'none';
            }
        }

        // Si la partida ya empezó, redirigir al juego
        if (game.status === 'IN_PROGRESS' || game.gameStatus === 'IN_PROGRESS') {
            window.location.href = `game.html?gameId=${gameId}`;
        }

    } catch (error) {
        console.error('Error loading waiting room:', error);
    }
}

async function handleStartGame(gameId) {
    uiManager.showLoading('Iniciando partida...');

    try {
        await apiClient.startGame(gameId);
        await apiClient.startNextRound(gameId);

        uiManager.hideLoading();
        uiManager.showSuccess('¡Partida iniciada!');

        setTimeout(() => {
            window.location.href = `game.html?gameId=${gameId}`;
        }, 500);

    } catch (error) {
        uiManager.hideLoading();
        uiManager.showError(error.message || 'Error al iniciar partida');
    }
}

// ==================== GAME PAGE ====================

function handleGamePage() {
    if (!gameState.isAuthenticated()) {
        window.location.href = 'login.html';
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    const gameId = urlParams.get('gameId');

    if (!gameId) {
        uiManager.showError('ID de partida no válido');
        window.location.href = 'home.html';
        return;
    }

    console.log('🎮 Inicializando página de juego...');

    // Configurar callbacks de UI
    uiManager.onCardClick = (card) => handlePlayCard(gameId, card);
    uiManager.onBidSelect = (bid) => handlePlaceBid(gameId, bid);

    // Cargar estado del juego
    loadGameState(gameId);

    // Actualizar cada 2 segundos
    const intervalId = setInterval(() => {
        loadGameState(gameId);
    }, 2000);

    window.addEventListener('beforeunload', () => {
        clearInterval(intervalId);
        // Limpiar bots cuando sales de la página
        botManager.removeGameBots(gameId);
    });
}

async function initializeGameUI(gameId) {
    try {
        const game = await apiClient.getGame(gameId);
        const currentPlayer = gameState.getCurrentPlayer();

        // Si hay menos de 4 jugadores, crear bots
        const playerCount = (game.playerIds || []).length || 1;
        if (playerCount < 4) {
            console.log(`📊 ${playerCount} jugadores. Creando bots...`);
            const botsCreated = botManager.createBotsForGame(gameId, playerCount, 4);

            // Mostrar mensaje
            uiManager.showSuccess(`${botsCreated.length} bots agregados para completar la partida`);
        }

        // Inicializar paneles de jugadores
        const positions = gameCardManager.getPlayerPositions(4);
        const allPlayers = [
            currentPlayer,
            ...(botManager.getGameBots(gameId) || [])
        ];

        // Crear paneles
        allPlayers.forEach((player, index) => {
            const panel = gameCardManager.createPlayerPanel(player, positions[index], index);
            document.body.appendChild(panel);
        });

        console.log('✅ UI del juego inicializada con ' + allPlayers.length + ' jugadores');

    } catch (error) {
        console.error('Error initializing game UI:', error);
    }
}

async function loadGameState(gameId) {
    try {
        const game = await apiClient.getGame(gameId);
        gameState.setCurrentGame(game);

        // Cargar ronda actual
        const round = await apiClient.getCurrentRound(gameId);
        gameState.setCurrentRound(round);

        // Cargar apuestas
        const bids = await apiClient.getBids(gameId, round.round_number);
        gameState.setBids(bids);

        // Cargar baza actual
        const trick = await apiClient.getCurrentTrick(gameId, round.round_number);
        gameState.setCurrentTrick(trick);

        // Actualizar UI
        uiManager.updateGameTable(gameState);

        // Mostrar interfaz de apuestas si no has apostado
        if (!gameState.hasPlacedBid() && round.status === 'BETTING') {
            uiManager.showBiddingInterface(round.num_cards);
        } else {
            uiManager.hideBiddingInterface();
        }

        // Verificar si el juego terminó
        if (game.status === 'FINISHED') {
            window.location.href = `results.html?gameId=${gameId}`;
        }

    } catch (error) {
        console.error('Error loading game state:', error);
    }
}

async function handlePlaceBid(gameId, bidAmount) {
    const player = gameState.getCurrentPlayer();
    const round = gameState.getCurrentRound();

    uiManager.showLoading('Realizando apuesta...');

    try {
        await apiClient.placeBid(gameId, round.round_number, player.id, bidAmount);

        uiManager.hideLoading();
        uiManager.showSuccess(`Has apostado ${bidAmount} baza(s)`);
        uiManager.hideBiddingInterface();

        // Recargar estado
        await loadGameState(gameId);

    } catch (error) {
        uiManager.hideLoading();
        uiManager.showError(error.message || 'Error al realizar apuesta');
    }
}

async function handlePlayCard(gameId, card) {
    if (!gameState.isMyTurn) {
        uiManager.showError('No es tu turno');
        return;
    }

    if (!gameState.canPlayCard(card)) {
        uiManager.showError('No puedes jugar esta carta');
        return;
    }

    const player = gameState.getCurrentPlayer();
    const round = gameState.getCurrentRound();

    uiManager.showLoading('Jugando carta...');

    try {
        await apiClient.playCard(gameId, round.round_number, player.id, card);

        gameState.removeCardFromHand(card);
        uiManager.hideLoading();

        // Recargar estado
        await loadGameState(gameId);

    } catch (error) {
        uiManager.hideLoading();
        uiManager.showError(error.message || 'Error al jugar carta');
    }
}

// ==================== RESULTS PAGE ====================

function handleResultsPage() {
    const urlParams = new URLSearchParams(window.location.search);
    const gameId = urlParams.get('gameId');

    if (!gameId) {
        window.location.href = 'home.html';
        return;
    }

    loadResults(gameId);

    // Botón volver al inicio
    const backBtn = document.getElementById('back-to-home');
    if (backBtn) {
        backBtn.onclick = () => {
            gameState.reset();
            window.location.href = 'home.html';
        };
    }
}

async function loadResults(gameId) {
    try {
        const game = await apiClient.getGame(gameId);

        // Mostrar resultados finales
        const resultsContainer = document.getElementById('results-container');
        if (resultsContainer) {
            resultsContainer.innerHTML = '<h2>Resultados Finales</h2>';

            // Ordenar jugadores por puntuación
            const sortedPlayers = [...game.players].sort((a, b) => b.score - a.score);

            sortedPlayers.forEach((player, index) => {
                const playerEl = document.createElement('div');
                playerEl.className = 'result-item';
                playerEl.innerHTML = `
                    <div class="position">${index + 1}°</div>
                    <div class="player-name">${player.name}</div>
                    <div class="score">${player.score} puntos</div>
                `;
                resultsContainer.appendChild(playerEl);
            });
        }

    } catch (error) {
        console.error('Error loading results:', error);
        uiManager.showError('Error al cargar resultados');
    }
}

// ==================== TEST PAGE ====================

function handleTestPage() {
    console.log('📄 Página de prueba cargada');

    // Configurar botones de navegación
    const loginBtn = document.querySelector('a[href="login.html"]');
    const registerBtn = document.querySelector('a[href="register.html"]');
    const homeBtn = document.querySelector('a[href="home.html"]');

    if (loginBtn) {
        console.log('✓ Botón login en test encontrado');
    }
    if (registerBtn) {
        console.log('✓ Botón register en test encontrado');
    }
    if (homeBtn) {
        console.log('✓ Botón home en test encontrado');
    }

    // Mostrar estado del backend
    if (window.backendAvailable) {
        console.log('✅ Backend disponible');
        setTimeout(() => {
            if (typeof uiManager !== 'undefined') {
                uiManager.showSuccess('¡Sistema funcionando correctamente!');
            }
        }, 1000);
    } else {
        console.log('⚠️ Backend no disponible');
    }
}

// ==================== LOGOUT ====================

function handleLogout() {
    if (confirm('¿Estás seguro que quieres cerrar sesión?')) {
        console.log('🚪 Cerrando sesión...');
        gameState.logout();
        uiManager.showSuccess('Sesión cerrada correctamente');

        setTimeout(() => {
            window.location.href = 'login.html';
        }, 1000);
    }
}

// ==================== GLOBAL FUNCTIONS ====================

// Exponer funciones globales para los HTML existentes
window.crearPartida = handleCreateGame;
window.unirsePartida = handleJoinGame;
window.handleLogin = handleLogin;
window.handleRegister = handleRegister;

// ==================== BOT ACTIONS ====================

/**
 * Hacer que los bots hagan apuestas automáticas
 */
async function processBotBids(gameId, roundNumber, numCards) {
    const bots = botManager.getGameBots(gameId);

    for (const bot of bots) {
        // Esperar un poco entre cada apuesta
        await new Promise(resolve => setTimeout(resolve, 1000));

        // Calcular apuesta automática
        let forbiddenBid = null;

        // Si es el último jugador, no puede apostar el mismo número que la suma de los anteriores
        const lastBid = await apiClient.getBids(gameId, roundNumber);
        if (lastBid && lastBid.length === bots.length - 1) {
            const totalBids = lastBid.reduce((sum, b) => sum + b.bid_amount, 0);
            forbiddenBid = numCards - totalBids;
        }

        const bid = botManager.makeAutomaticBid(bot.id, numCards, forbiddenBid);

        // Registrar apuesta en el servidor (si es necesario)
        console.log(`🤖 ${bot.name} apuesta ${bid}`);

        // Mostrar animación
        gameCardManager.playBidAnimation(bot.id, bid);
        gameCardManager.updatePlayerBid(bot.id, bid);
    }
}

/**
 * Hacer que los bots lancen cartas automáticamente
 */
async function processBotMoves(gameId, roundNumber) {
    const bots = botManager.getGameBots(gameId);

    for (const bot of bots) {
        // Esperar un poco entre cada movimiento
        await new Promise(resolve => setTimeout(resolve, 1500));

        // Bot lanza una carta aleatoria
        const card = botManager.playRandomCard(bot.id);

        if (card) {
            console.log(`🤖 ${bot.name} lanza: ${card.rank} de ${card.suit}`);

            // Mostrar animación
            gameCardManager.playCardAnimation(bot.id, card);

            // Actualizar mano visible
            gameCardManager.updatePlayerHand(bot.id, bot.hand, false);
        }
    }
}

console.log('✓ Main.js cargado');

