document.getElementById("code").innerText =
    localStorage.getItem("gameCode");

// MVP: jugadores simulados
const players = ["Tommy", "Jugador 2"];

players.forEach(p => {
    const li = document.createElement("li");
    li.innerText = p;
    document.getElementById("players").appendChild(li);
});

function startGame() {
    location.href = "game.html";
}
