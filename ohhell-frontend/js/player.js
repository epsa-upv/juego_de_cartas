async function ensurePlayer() {
    try {
        return await api("/players/me");
    } catch {
        const nickname = prompt("Elige un nickname");
        return api("/players", {
            method: "POST",
            body: JSON.stringify({ nickname })
        });
    }
}
