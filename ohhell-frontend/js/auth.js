async function login() {
    const email = document.getElementById('email')?.value;
    const password = document.getElementById('password')?.value;

    if (!email || !password) {
        alert('Completa todos los campos');
        return;
    }

    try {
        const res = await apiClient.login(email, password);
        localStorage.setItem('token', res.token);
        location.href = 'home.html';
    } catch (err) {
        console.error('Login error:', err);
        alert(err.message || 'Error al iniciar sesión');
    }
}

async function register() {
    const email = document.getElementById('email')?.value;
    const password = document.getElementById('password')?.value;

    if (!email || !password) {
        alert('Completa todos los campos');
        return;
    }

    try {
        await apiClient.register(email, password);
        alert('Usuario creado');
        location.href = 'index.html';
    } catch (err) {
        console.error('Register error:', err);
        alert(err.message || 'Error al registrar usuario');
    }
}
