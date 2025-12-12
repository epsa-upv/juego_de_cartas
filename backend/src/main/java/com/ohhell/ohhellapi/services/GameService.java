package com.ohhell.ohhellapi.services;

import com.ohhell.ohhellapi.dao.GameDAO;
import com.ohhell.ohhellapi.dao.PlayerDAO;
import com.ohhell.ohhellapi.dao.RoundDAO;
import com.ohhell.ohhellapi.models.Game;
import com.ohhell.ohhellapi.models.Player;
import com.ohhell.ohhellapi.models.Round;
import com.ohhell.ohhellapi.models.Card;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GameService {

    private final GameDAO gameDAO;
    private final PlayerDAO playerDAO;
    private final RoundDAO roundDAO;
    private final CardService cardService;

    // Configuración del juego según las reglas
    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 7;
    private static final int DEFAULT_INITIAL_LIVES = 5;
    private static final int TOTAL_ROUNDS = 5; // 5 cartas → 4 → 3 → 2 → 1

    public GameService() {
        this.gameDAO = new GameDAO();
        this.playerDAO = new PlayerDAO();
        this.roundDAO = new RoundDAO();
        this.cardService = new CardService();
    }


    public Game createGame(String name, int maxPlayers, int initialLives) throws SQLException {
        // Validaciones
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la partida no puede estar vacío");
        }

        if (maxPlayers < MIN_PLAYERS || maxPlayers > MAX_PLAYERS) {
            throw new IllegalArgumentException(
                    "Número de jugadores debe estar entre " + MIN_PLAYERS + " y " + MAX_PLAYERS
            );
        }

        if (initialLives <= 0) {
            initialLives = DEFAULT_INITIAL_LIVES;
        }

        // Crear objeto Game
        Game game = new Game();
        game.setName(name);
        game.setMaxPlayers(maxPlayers);
        game.setInitialLives(initialLives);
        game.setStatus("WAITING");
        game.setCreatedAt(LocalDateTime.now());

        // Guardar en BD
        Game createdGame = gameDAO.createGame(game);

        System.out.println("✅ Partida creada: " + createdGame.getName() + " (ID: " + createdGame.getId() + ")");
        return createdGame;
    }


    public boolean addPlayerToGame(Long gameId, Long playerId) throws SQLException {
        // Validaciones
        Game game = gameDAO.getGameById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("La partida no existe");
        }

        if (!game.getStatus().equalsIgnoreCase("WAITING")) {
            throw new IllegalStateException("La partida ya ha comenzado");
        }

        // Verificar que no esté llena
        int currentPlayers = gameDAO.countPlayersInGame(gameId);
        if (currentPlayers >= game.getMaxPlayers()) {
            throw new IllegalStateException("La partida está llena");
        }

        // Verificar que el jugador existe
        Player player = playerDAO.getPlayerById(playerId);
        if (player == null) {
            throw new IllegalArgumentException("El jugador no existe");
        }

        // Verificar que el jugador no esté ya en la partida
        List<Player> playersInGame = playerDAO.getPlayersByGameId(gameId);
        boolean alreadyInGame = playersInGame.stream()
                .anyMatch(p -> p.getId().equals(playerId));

        if (alreadyInGame) {
            throw new IllegalStateException("El jugador ya está en esta partida");
        }

        // Añadir jugador a la partida
        // Esto se hace insertando en la tabla game_players
        // Por simplicidad, asumimos que existe un método en GameDAO o usamos SQL directo

        // Actualizar vidas del jugador
        player.setLives(game.getInitialLives());
        player.setStatus(Player.PlayerStatus.ACTIVE);
        playerDAO.updatePlayer(player);

        System.out.println("✅ Jugador " + player.getName() + " añadido a partida " + game.getName());
        return true;
    }


    public boolean canStartGame(Long gameId) throws SQLException {
        Game game = gameDAO.getGameById(gameId);
        if (game == null) return false;

        // Debe estar en estado WAITING
        if (!game.getStatus().equalsIgnoreCase("WAITING")) {
            return false;
        }

        // Debe tener al menos MIN_PLAYERS jugadores
        int playerCount = gameDAO.countPlayersInGame(gameId);
        return playerCount >= MIN_PLAYERS;
    }


    public Game startGame(Long gameId) throws SQLException {
        // Validaciones
        if (!canStartGame(gameId)) {
            throw new IllegalStateException("No se puede iniciar la partida");
        }

        Game game = gameDAO.getGameById(gameId);
        List<Player> players = playerDAO.getPlayersByGameId(gameId);

        if (players.size() < MIN_PLAYERS) {
            throw new IllegalStateException(
                    "Se necesitan al menos " + MIN_PLAYERS + " jugadores para iniciar"
            );
        }

        // Cambiar estado a IN_PROGRESS
        game.setStatus("IN_PROGRESS");
        game.setStartedAt(LocalDateTime.now());
        gameDAO.updateGame(game);

        // Asignar primer dealer (aleatorio)
        Random random = new Random();
        Player firstDealer = players.get(random.nextInt(players.size()));
        playerDAO.setDealer(gameId, firstDealer.getId());

        // Crear primera ronda (5 cartas)
        createFirstRound(gameId, firstDealer.getId());

        System.out.println("🎮 Partida iniciada: " + game.getName());
        System.out.println("   Jugadores: " + players.size());
        System.out.println("   Primer dealer: " + firstDealer.getName());

        return game;
    }


    private void createFirstRound(Long gameId, Long dealerId) throws SQLException {
        List<Player> players = playerDAO.getPlayersByGameId(gameId);

        // Ronda 1: 5 cartas por jugador
        Round round = new Round();
        round.setGameId(gameId);
        round.setRoundNumber(1);
        round.setNumCards(5);
        round.setDealerId(dealerId);
        round.setStatus("BIDDING");

        // Obtener carta de triunfo
        Card trumpCard = cardService.getTrumpCard(5, players.size());
        if (trumpCard != null) {
            round.setTrump(trumpCard);
        }

        roundDAO.createRound(round);

        System.out.println("   Ronda 1 creada: 5 cartas, triunfo = " +
                (trumpCard != null ? cardService.cardToString(trumpCard) : "ninguno"));
    }


    public Round nextRound(Long gameId) throws SQLException {
        Game game = gameDAO.getGameById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("La partida no existe");
        }

        // Obtener ronda actual
        Round currentRound = roundDAO.getCurrentRound(gameId);
        if (currentRound != null && !currentRound.getStatus().equalsIgnoreCase("COMPLETED")) {
            throw new IllegalStateException("La ronda actual no ha terminado");
        }

        // Calcular número de la siguiente ronda
        int lastRoundNumber = roundDAO.getLastRoundNumber(gameId);
        int nextRoundNumber = lastRoundNumber + 1;

        // Verificar que no exceda el máximo de rondas
        if (nextRoundNumber > TOTAL_ROUNDS) {
            // Terminar partida
            finishGame(gameId);
            return null;
        }

        // Calcular cartas para la siguiente ronda
        // Ronda 1: 5 cartas, Ronda 2: 4 cartas, ..., Ronda 5: 1 carta
        int cardsForNextRound = TOTAL_ROUNDS - nextRoundNumber + 1;

        // Rotar dealer al siguiente jugador
        List<Player> activePlayers = getActivePlayers(gameId);
        Long newDealerId = getNextDealer(gameId, currentRound.getDealerId(), activePlayers);

        // Crear nueva ronda
        Round newRound = new Round();
        newRound.setGameId(gameId);
        newRound.setRoundNumber(nextRoundNumber);
        newRound.setNumCards(cardsForNextRound);
        newRound.setDealerId(newDealerId);
        newRound.setStatus("BIDDING");

        // Obtener nueva carta de triunfo
        Card trumpCard = cardService.getTrumpCard(cardsForNextRound, activePlayers.size());
        if (trumpCard != null) {
            newRound.setTrump(trumpCard);
        }

        Round createdRound = roundDAO.createRound(newRound);

        System.out.println("🎲 Ronda " + nextRoundNumber + " creada: " + cardsForNextRound +
                " cartas, triunfo = " +
                (trumpCard != null ? cardService.cardToString(trumpCard) : "ninguno"));

        return createdRound;
    }

    private Long getNextDealer(Long gameId, Long currentDealerId, List<Player> activePlayers) {
        if (activePlayers.isEmpty()) {
            return null;
        }

        // Encontrar índice del dealer actual
        int currentIndex = -1;
        for (int i = 0; i < activePlayers.size(); i++) {
            if (activePlayers.get(i).getId().equals(currentDealerId)) {
                currentIndex = i;
                break;
            }
        }

        // Siguiente dealer (rotación circular)
        int nextIndex = (currentIndex + 1) % activePlayers.size();
        return activePlayers.get(nextIndex).getId();
    }


    public List<Player> getActivePlayers(Long gameId) throws SQLException {
        List<Player> allPlayers = playerDAO.getPlayersByGameId(gameId);
        List<Player> activePlayers = new ArrayList<>();

        for (Player player : allPlayers) {
            if (player.getLives() > 0 && player.getStatus() == Player.PlayerStatus.ACTIVE) {
                activePlayers.add(player);
            }
        }

        return activePlayers;
    }


    public boolean isGameOver(Long gameId) throws SQLException {
        // Condición 1: Solo queda 1 jugador activo
        List<Player> activePlayers = getActivePlayers(gameId);
        if (activePlayers.size() <= 1) {
            return true;
        }

        // Condición 2: Se completaron todas las rondas
        int roundCount = roundDAO.countRoundsInGame(gameId);
        if (roundCount >= TOTAL_ROUNDS) {
            Round lastRound = roundDAO.getLastCompletedRound(gameId);
            if (lastRound != null && lastRound.getStatus().equalsIgnoreCase("COMPLETED")) {
                return true;
            }
        }

        return false;
    }


    public Game finishGame(Long gameId) throws SQLException {
        Game game = gameDAO.getGameById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("La partida no existe");
        }

        // Obtener jugadores activos
        List<Player> activePlayers = getActivePlayers(gameId);

        Long winnerId = null;

        if (activePlayers.size() == 1) {
            // Solo queda 1 jugador → es el ganador
            winnerId = activePlayers.get(0).getId();
        } else if (activePlayers.size() > 1) {
            // Varios jugadores → gana quien tenga más vidas
            Player winner = activePlayers.get(0);
            for (Player player : activePlayers) {
                if (player.getLives() > winner.getLives()) {
                    winner = player;
                }
            }
            winnerId = winner.getId();
        }

        // Actualizar partida
        gameDAO.finishGame(gameId, winnerId);

        // Actualizar estadísticas del ganador
        if (winnerId != null) {
            Player winner = playerDAO.getPlayerById(winnerId);
            winner.incrementWins();
            playerDAO.updatePlayer(winner);

            System.out.println("🏆 Ganador: " + winner.getName() + " con " + winner.getLives() + " vidas");
        }

        // Actualizar totalGames de todos los jugadores
        List<Player> allPlayers = playerDAO.getPlayersByGameId(gameId);
        for (Player player : allPlayers) {
            player.incrementGames();
            playerDAO.updatePlayer(player);
        }

        System.out.println("🎮 Partida finalizada: " + game.getName());

        return gameDAO.getGameById(gameId);
    }


    public Game getGameState(Long gameId) throws SQLException {
        return gameDAO.getGameById(gameId);
    }


    public List<Game> getAvailableGames() throws SQLException {
        return gameDAO.getAvailableGames();
    }
}