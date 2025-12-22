// =======================
// AUTH CHECK
// =======================
const token = localStorage.getItem('token');
if (!token) {
    window.location.href = 'login.html';
}

// =======================
// DOM
// =======================
const crearBtn = document.getElementById('create-game-btn');
const logoutBtn = document.getElementById('logout-btn');
const userNameEl = document.getElementById('user-name');
const userAvatarEl = document.getElementById('user-avatar');

// =======================
// INIT
// =======================
init();

async function init() {
    await loadUser();
}

// =======================
// LOAD USER
// =======================
async function loadUser() {
    try {
        const me = await gameApi.getMyPlayer();

        userNameEl.textContent = me.nickname;
        userAvatarEl.textContent = me.nickname.charAt(0).toUpperCase();

    } catch (e) {
        console.error('No se pudo cargar el jugador', e);
        localStorage.clear();
        window.location.href = 'login.html';
    }
}

// =======================
// CREAR PARTIDA
// =======================
if (crearBtn) {
    crearBtn.addEventListener('click', async () => {
        crearBtn.disabled = true;

        try {
            const game = await gameApi.createGame('Partida Oh Hell!');
            window.location.href = `waiting-room.html?code=${game.code}`;
        } catch (error) {
            console.error(error);
            alert(error.message || 'No se pudo crear la partida');
            crearBtn.disabled = false;
        }
    });
}

// =======================
// LOGOUT
// =======================
if (logoutBtn) {
    logoutBtn.addEventListener('click', () => {
        localStorage.clear();
        window.location.href = 'login.html';
    });
}
