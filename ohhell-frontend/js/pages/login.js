const loginForm = document.getElementById('loginForm');
const loginBtn = document.getElementById('login-btn');
const messageEl = document.getElementById('login-message');

function showMessage(text, type = 'info') {
    messageEl.textContent = text;
    messageEl.className = type === 'error' ? 'form-error' : 'form-success';
}

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;

    if (!email || !password) {
        showMessage('Completa todos los campos', 'error');
        return;
    }

    loginBtn.disabled = true;
    showMessage('Iniciando sesión...', 'info');

    try {
        // 1️⃣ Login
        const res = await gameApi.login(email, password);
        localStorage.setItem('token', res.token);

        // 2️⃣ Asegurar Player
        try {
            await gameApi.getMyPlayer();
        } catch (err) {
            if (
                err.message.includes('PLAYER_NOT_FOUND') ||
                err.message.includes('Player')
            ) {
                const nickname = prompt('Elige un nickname');
                if (!nickname || !nickname.trim()) {
                    throw new Error('Nickname requerido');
                }
                await gameApi.createPlayer(nickname.trim());
            } else {
                throw err;
            }
        }

        // 3️⃣ Ir a Home
        window.location.href = 'home.html';

    } catch (error) {
        console.error(error);
        showMessage(error.message || 'Error al iniciar sesión', 'error');
        loginBtn.disabled = false;
    }
});
