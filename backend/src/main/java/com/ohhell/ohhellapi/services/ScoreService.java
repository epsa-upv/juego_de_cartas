package com.ohhell.ohhellapi.services;

import com.ohhell.ohhellapi.dao.PlayerDAO;
import com.ohhell.ohhellapi.dao.GameDAO;
import com.ohhell.ohhellapi.dao.BidDAO;
import com.ohhell.ohhellapi.dao.RoundDAO;
import com.ohhell.ohhellapi.models.Player;
import com.ohhell.ohhellapi.models.Game;
import com.ohhell.ohhellapi.models.Bid;
import com.ohhell.ohhellapi.models.Round;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import java.util.stream.Collectors;

/**
 * ScoreService - Servicio para gestión de puntuación y vidas
 *
 * Oh Hell! Card Game - UPV
 * Autor: Tomás Criado García
 *
 * Gestiona el sistema de vidas según las reglas:
 * - Calcula vidas perdidas = |apuesta - realidad|
 * - Actualiza vidas de jugadores
 * - Elimina jugadores sin vidas
 * - Determina ganadores
 */
public class ScoreService {

    private final PlayerDAO playerDAO;
    private final GameDAO gameDAO;
    private final BidDAO bidDAO;
    private final RoundDAO roundDAO;

    public ScoreService() {
        this.playerDAO = new PlayerDAO();
        this.gameDAO = new GameDAO();
        this.bidDAO = new BidDAO();
        this.roundDAO = new RoundDAO();
    }

    /**
     * Calcula las vidas perdidas según la fórmula del juego
     *
     * FÓRMULA: Vidas perdidas = |Apuesta - Bazas Ganadas|
     *
     * @param bidAmount Número de bazas apostadas
     * @param tricksWon Número de bazas ganadas
     * @return Vidas a perder (0 si acertó)
     */
    public int calculateLivesLost(int bidAmount, int tricksWon) {
        if (bidAmount == tricksWon) {
            return 0; // ¡Acertó! No pierde vidas
        }

        // Diferencia absoluta
        return Math.abs(bidAmount - tricksWon);
    }

    /**
     * Actualiza las vidas de un jugador
     *
     * @param playerId ID del jugador
     * @param livesLost Vidas a restar
     * @return Vidas restantes después de la actualización
     * @throws SQLException si hay error en BD
     */
    public int updatePlayerLives(Long playerId, int livesLost) throws SQLException {
        Player player = playerDAO.getPlayerById(playerId);
        if (player == null) {
            throw new IllegalArgumentException("El jugador no existe");
        }

        int currentLives = player.getLives();
        int newLives = Math.max(0, currentLives - livesLost);

        player.setLives(newLives);
        playerDAO.updatePlayer(player);

        // Si se quedó sin vidas, eliminar
        if (newLives == 0) {
            eliminatePlayer(playerId);
        }

        System.out.println("💔 " + player.getName() + ": " +
                currentLives + " - " + livesLost + " = " + newLives + " vidas");

        return newLives;
    }

    /**
     * Elimina un jugador (sin vidas)
     *
     * @param playerId ID del jugador
     * @throws SQLException si hay error en BD
     */
    public void eliminatePlayer(Long playerId) throws SQLException {
        Player player = playerDAO.getPlayerById(playerId);
        if (player == null) return;

        player.setLives(0);
        player.setStatus(Player.PlayerStatus.ELIMINATED);
        playerDAO.updatePlayer(player);

        System.out.println("💀 " + player.getName() + " ELIMINADO");
    }

    /**
     * Obtiene la puntuación de un jugador en una partida
     *
     * @param playerId ID del jugador
     * @param gameId ID de la partida
     * @return PlayerScore con toda la información
     * @throws SQLException si hay error en BD
     */
    public PlayerScore getPlayerScore(Long playerId, Long gameId) throws SQLException {
        Player player = playerDAO.getPlayerById(playerId);
        if (player == null) {
            throw new IllegalArgumentException("El jugador no existe");
        }

        // Obtener todas las rondas de la partida
        List<Round> rounds = roundDAO.getRoundsByGameId(gameId);

        int roundsPlayed = 0;
        int roundsWon = 0; // Rondas donde acertó su apuesta
        int totalBids = 0;
        int totalTricksWon = 0;

        for (Round round : rounds) {
            Bid bid = bidDAO.getBidByPlayerAndRound(playerId, round.getId());
            if (bid != null) {
                roundsPlayed++;
                totalBids += bid.getBidAmount();
                totalTricksWon += bid.getTricksWon();

                if (bid.getBidAmount() == bid.getTricksWon()) {
                    roundsWon++;
                }
            }
        }

        // Calcular score: (rondas acertadas × 10) + vidas restantes
        int score = (roundsWon * 10) + player.getLives();

        return new PlayerScore(
                playerId,
                player.getName(),
                player.getLives(),
                roundsPlayed,
                roundsWon,
                totalBids,
                totalTricksWon,
                score
        );
    }

    /**
     * Obtiene los scores de todos los jugadores en una ronda
     *
     * @param roundId ID de la ronda
     * @return Lista de ScoreResult
     * @throws SQLException si hay error en BD
     */
    public List<ScoreResult> getRoundScores(Long roundId) throws SQLException {
        Round round = roundDAO.getRoundById(roundId);
        if (round == null) {
            throw new IllegalArgumentException("La ronda no existe");
        }

        List<Bid> bids = bidDAO.getBidsByRoundId(roundId);
        List<ScoreResult> results = new ArrayList<>();

        for (Bid bid : bids) {
            Player player = playerDAO.getPlayerById(bid.getPlayerId());

            int livesLost = calculateLivesLost(bid.getBidAmount(), bid.getTricksWon());
            boolean success = livesLost == 0;

            ScoreResult result = new ScoreResult(
                    player.getId(),
                    player.getName(),
                    bid.getBidAmount(),
                    bid.getTricksWon(),
                    success,
                    livesLost,
                    player.getLives()
            );

            results.add(result);
        }

        return results;
    }

    /**
     * Obtiene el ranking de jugadores en una partida
     *
     * Ordenado por:
     * 1. Vidas (descendente)
     * 2. Rondas acertadas (descendente)
     *
     * @param gameId ID de la partida
     * @return Lista ordenada de PlayerScore
     * @throws SQLException si hay error en BD
     */
    public List<PlayerScore> getGameLeaderboard(Long gameId) throws SQLException {
        List<Player> players = playerDAO.getPlayersByGameId(gameId);
        List<PlayerScore> leaderboard = new ArrayList<>();

        for (Player player : players) {
            PlayerScore score = getPlayerScore(player.getId(), gameId);
            leaderboard.add(score);
        }

        // Ordenar por vidas (desc) y luego por rondas ganadas (desc)
        leaderboard.sort(Comparator
                .comparingInt(PlayerScore::getCurrentLives).reversed()
                .thenComparingInt(PlayerScore::getRoundsWon).reversed()
        );

        return leaderboard;
    }

    /**
     * Determina el ganador de una partida
     *
     * Criterios:
     * 1. Solo queda 1 jugador con vidas → ganador
     * 2. Varios jugadores con vidas → gana quien tenga más vidas
     * 3. Empate en vidas → gana quien acertó más rondas
     *
     * @param gameId ID de la partida
     * @return Player ganador, o null si no hay
     * @throws SQLException si hay error en BD
     */
    public Player determineWinner(Long gameId) throws SQLException {
        List<Player> allPlayers = playerDAO.getPlayersByGameId(gameId);
        List<Player> activePlayers = allPlayers.stream()
                .filter(p -> p.getLives() > 0 && p.getStatus() == Player.PlayerStatus.ACTIVE)
                .collect(Collectors.toList());

        // Si no hay jugadores activos
        if (activePlayers.isEmpty()) {
            return null;
        }

        // Si solo queda 1 jugador
        if (activePlayers.size() == 1) {
            return activePlayers.get(0);
        }

        // Si quedan varios, obtener leaderboard
        List<PlayerScore> leaderboard = getGameLeaderboard(gameId);

        if (leaderboard.isEmpty()) {
            return null;
        }

        // El primero del leaderboard es el ganador
        Long winnerId = leaderboard.get(0).getPlayerId();
        return playerDAO.getPlayerById(winnerId);
    }

    /**
     * Obtiene estadísticas completas de un jugador en una partida
     *
     * @param playerId ID del jugador
     * @param gameId ID de la partida
     * @return PlayerStats con toda la información
     * @throws SQLException si hay error en BD
     */
    public PlayerStats getPlayerStats(Long playerId, Long gameId) throws SQLException {
        Player player = playerDAO.getPlayerById(playerId);
        if (player == null) {
            throw new IllegalArgumentException("El jugador no existe");
        }

        Game game = gameDAO.getGameById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("La partida no existe");
        }

        List<Round> rounds = roundDAO.getRoundsByGameId(gameId);
        List<RoundStats> roundStatsList = new ArrayList<>();

        int totalLivesLost = 0;
        int roundsWon = 0;

        for (Round round : rounds) {
            Bid bid = bidDAO.getBidByPlayerAndRound(playerId, round.getId());

            if (bid != null) {
                int livesLost = calculateLivesLost(bid.getBidAmount(), bid.getTricksWon());
                boolean success = livesLost == 0;

                if (success) roundsWon++;
                totalLivesLost += livesLost;

                RoundStats rs = new RoundStats(
                        round.getRoundNumber(),
                        round.getNumCards(),
                        bid.getBidAmount(),
                        bid.getTricksWon(),
                        success,
                        livesLost
                );

                roundStatsList.add(rs);
            }
        }

        return new PlayerStats(
                playerId,
                player.getName(),
                player.getLives(),
                game.getInitialLives(),
                roundStatsList.size(),
                roundsWon,
                totalLivesLost,
                roundStatsList
        );
    }

    /**
     * Imprime un resumen de puntuaciones de una ronda
     *
     * @param roundId ID de la ronda
     * @throws SQLException si hay error en BD
     */
    public void printRoundScoreSummary(Long roundId) throws SQLException {
        Round round = roundDAO.getRoundById(roundId);
        List<ScoreResult> scores = getRoundScores(roundId);

        System.out.println("\n📊 Resumen de Puntuaciones - Ronda " + round.getRoundNumber());
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.printf("%-15s %-8s %-8s %-10s %-6s %-6s%n",
                "Jugador", "Apostó", "Ganó", "Resultado", "Pierde", "Vidas");
        System.out.println("───────────────────────────────────────────────────────");

        for (ScoreResult score : scores) {
            String result = score.isSuccess() ? "✅ ACERTÓ" : "❌ FALLÓ";
            System.out.printf("%-15s %-8d %-8d %-10s %-6d %-6d%n",
                    score.getPlayerName(),
                    score.getBidAmount(),
                    score.getTricksWon(),
                    result,
                    score.getLivesLost(),
                    score.getCurrentLives()
            );
        }

        System.out.println("═══════════════════════════════════════════════════════\n");
    }

    /**
     * Imprime el leaderboard de una partida
     *
     * @param gameId ID de la partida
     * @throws SQLException si hay error en BD
     */
    public void printGameLeaderboard(Long gameId) throws SQLException {
        List<PlayerScore> leaderboard = getGameLeaderboard(gameId);

        System.out.println("\n🏆 LEADERBOARD");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.printf("%-5s %-15s %-6s %-8s %-6s%n",
                "Pos", "Jugador", "Vidas", "Aciertos", "Score");
        System.out.println("───────────────────────────────────────────────────────");

        String[] medals = {"🥇", "🥈", "🥉"};

        for (int i = 0; i < leaderboard.size(); i++) {
            PlayerScore ps = leaderboard.get(i);
            String position = i < 3 ? medals[i] : String.valueOf(i + 1);
            String status = ps.getCurrentLives() == 0 ? " ❌" : "";

            System.out.printf("%-5s %-15s %-6d %-8d %-6d%s%n",
                    position,
                    ps.getPlayerName(),
                    ps.getCurrentLives(),
                    ps.getRoundsWon(),
                    ps.getScore(),
                    status
            );
        }

        System.out.println("═══════════════════════════════════════════════════════\n");
    }

    // ==================== CLASES INTERNAS ====================

    /**
     * Clase para representar la puntuación de un jugador
     */
    public static class PlayerScore {
        private final Long playerId;
        private final String playerName;
        private final int currentLives;
        private final int roundsPlayed;
        private final int roundsWon;
        private final int totalBids;
        private final int totalTricksWon;
        private final int score;

        public PlayerScore(Long playerId, String playerName, int currentLives,
                           int roundsPlayed, int roundsWon, int totalBids,
                           int totalTricksWon, int score) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.currentLives = currentLives;
            this.roundsPlayed = roundsPlayed;
            this.roundsWon = roundsWon;
            this.totalBids = totalBids;
            this.totalTricksWon = totalTricksWon;
            this.score = score;
        }

        public Long getPlayerId() { return playerId; }
        public String getPlayerName() { return playerName; }
        public int getCurrentLives() { return currentLives; }
        public int getRoundsPlayed() { return roundsPlayed; }
        public int getRoundsWon() { return roundsWon; }
        public int getTotalBids() { return totalBids; }
        public int getTotalTricksWon() { return totalTricksWon; }
        public int getScore() { return score; }
    }

    /**
     * Clase para representar el resultado de una ronda
     */
    public static class ScoreResult {
        private final Long playerId;
        private final String playerName;
        private final int bidAmount;
        private final int tricksWon;
        private final boolean success;
        private final int livesLost;
        private final int currentLives;

        public ScoreResult(Long playerId, String playerName, int bidAmount,
                           int tricksWon, boolean success, int livesLost,
                           int currentLives) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.bidAmount = bidAmount;
            this.tricksWon = tricksWon;
            this.success = success;
            this.livesLost = livesLost;
            this.currentLives = currentLives;
        }

        public Long getPlayerId() { return playerId; }
        public String getPlayerName() { return playerName; }
        public int getBidAmount() { return bidAmount; }
        public int getTricksWon() { return tricksWon; }
        public boolean isSuccess() { return success; }
        public int getLivesLost() { return livesLost; }
        public int getCurrentLives() { return currentLives; }
    }

    /**
     * Clase para estadísticas de una ronda
     */
    public static class RoundStats {
        private final int roundNumber;
        private final int numCards;
        private final int bidAmount;
        private final int tricksWon;
        private final boolean success;
        private final int livesLost;

        public RoundStats(int roundNumber, int numCards, int bidAmount,
                          int tricksWon, boolean success, int livesLost) {
            this.roundNumber = roundNumber;
            this.numCards = numCards;
            this.bidAmount = bidAmount;
            this.tricksWon = tricksWon;
            this.success = success;
            this.livesLost = livesLost;
        }

        public int getRoundNumber() { return roundNumber; }
        public int getNumCards() { return numCards; }
        public int getBidAmount() { return bidAmount; }
        public int getTricksWon() { return tricksWon; }
        public boolean isSuccess() { return success; }
        public int getLivesLost() { return livesLost; }
    }

    /**
     * Clase para estadísticas completas de un jugador
     */
    public static class PlayerStats {
        private final Long playerId;
        private final String playerName;
        private final int currentLives;
        private final int initialLives;
        private final int roundsPlayed;
        private final int roundsWon;
        private final int totalLivesLost;
        private final List<RoundStats> roundStats;

        public PlayerStats(Long playerId, String playerName, int currentLives,
                           int initialLives, int roundsPlayed, int roundsWon,
                           int totalLivesLost, List<RoundStats> roundStats) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.currentLives = currentLives;
            this.initialLives = initialLives;
            this.roundsPlayed = roundsPlayed;
            this.roundsWon = roundsWon;
            this.totalLivesLost = totalLivesLost;
            this.roundStats = roundStats;
        }

        public Long getPlayerId() { return playerId; }
        public String getPlayerName() { return playerName; }
        public int getCurrentLives() { return currentLives; }
        public int getInitialLives() { return initialLives; }
        public int getRoundsPlayed() { return roundsPlayed; }
        public int getRoundsWon() { return roundsWon; }
        public int getTotalLivesLost() { return totalLivesLost; }
        public List<RoundStats> getRoundStats() { return roundStats; }

        public double getAccuracy() {
            return roundsPlayed > 0 ? (double) roundsWon / roundsPlayed * 100 : 0;
        }
    }
}