async function ensurePlayer() {
    try {
        // Intentar obtener mi player
        return await gameApi.getMyPlayer();

    } catch (error) {
        // Solo crear si NO existe
        if (!error.message.includes('Player no encontrado')) {
            throw error;
        }

        let nickname = null;

        while (!nickname) {
            nickname = prompt('Elige un nickname');
            if (nickname === null) {
                throw new Error('Se requiere un nickname para jugar');
            }
            nickname = nickname.trim();
        }

        return await gameApi.createPlayer(nickname);
    }
}
