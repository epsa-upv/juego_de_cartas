package com.ohhell.ohhellapi.Services;

import com.ohhell.ohhellapi.dao.BidDAO;
import com.ohhell.ohhellapi.dao.RoundDAO;
import com.ohhell.ohhellapi.dao.PlayerDAO;
import com.ohhell.ohhellapi.models.Bid;
import com.ohhell.ohhellapi.models.Round;
import com.ohhell.ohhellapi.models.Player;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * BidService - Servicio para gestión de apuestas
 *
 * Oh Hell! Card Game - UPV
 * Autor: Tomás Criado García
 *
 * Gestiona las apuestas de los jugadores y valida la REGLA CRÍTICA:
 * "La suma de todas las apuestas DEBE ser diferente al número de cartas"
 */
public class BidService {

    private final BidDAO bidDAO;
    private final RoundDAO roundDAO;
    private final PlayerDAO playerDAO;

    public BidService() {
        this.bidDAO = new BidDAO();
        this.roundDAO = new RoundDAO();
        this.playerDAO = new PlayerDAO();
    }

    /**
     * Un jugador hace su apuesta
     *
     * @param playerId ID del jugador
     * @param roundId ID de la ronda
     * @param bidAmount Número de bazas que apuesta ganar
     * @return Bid creado
     * @throws SQLException si hay error en BD
     */
    public Bid placeBid(Long playerId, Long roundId, int bidAmount) throws SQLException {
        // Validaciones básicas
        Player player = playerDAO.getPlayerById(playerId);
        if (player == null) {
            throw new IllegalArgumentException("El jugador no existe");
        }

        Round round = roundDAO.getRoundById(roundId);
        if (round == null) {
            throw new IllegalArgumentException("La ronda no existe");
        }

        if (!round.getStatus().equalsIgnoreCase("BIDDING")) {
            throw new IllegalStateException("La ronda no está en fase de apuestas");
        }

        // Verificar que el jugador no ha apostado ya
        Bid existingBid = bidDAO.getBidByPlayerAndRound(playerId, roundId);
        if (existingBid != null) {
            throw new IllegalStateException("El jugador ya ha apostado en esta ronda");
        }

        // Validar que la apuesta sea válida (≥0 y ≤ numCards)
        if (bidAmount < 0 || bidAmount > round.getNumCards()) {
            throw new IllegalArgumentException(
                    "La apuesta debe estar entre 0 y " + round.getNumCards()
            );
        }

        // VALIDACIÓN CRÍTICA: Verificar regla de suma
        if (!isValidBid(roundId, bidAmount, playerId)) {
            throw new IllegalStateException(
                    "Apuesta inválida: La suma de apuestas no puede ser igual al número de cartas"
            );
        }

        // Crear apuesta
        Bid bid = new Bid();
        bid.setPlayerId(playerId);
        bid.setRoundId(roundId);
        bid.setBidAmount(bidAmount);
        bid.setTricksWon(0);
        bid.setTimestamp(LocalDateTime.now());

        Bid createdBid = bidDAO.createBid(bid);

        System.out.println("🎲 " + player.getName() + " apuesta: " + bidAmount + " bazas");

        // Verificar si todos los jugadores han apostado
        List<Player> activePlayers = playerDAO.getActivePlayersByGameId(round.getGameId());
        if (bidDAO.allPlayersHaveBid(roundId, activePlayers.size())) {
            // Cambiar estado de la ronda a PLAYING
            roundDAO.updateRoundStatus(roundId, "PLAYING");
            System.out.println("✅ Todos han apostado. Ronda en estado PLAYING");
        }

        return createdBid;
    }

    /**
     * Valida si una apuesta es legal según la REGLA CRÍTICA
     *
     * REGLA CRÍTICA:
     * La suma de todas las apuestas DEBE ser diferente al número de cartas.
     *
     * El ÚLTIMO jugador en apostar NO puede hacer que la suma sea igual
     * al número de cartas en la ronda.
     *
     * @param roundId ID de la ronda
     * @param bidAmount Apuesta propuesta
     * @param playerId ID del jugador que apuesta
     * @return true si la apuesta es válida
     * @throws SQLException si hay error en BD
     */
    public boolean isValidBid(Long roundId, int bidAmount, Long playerId) throws SQLException {
        Round round = roundDAO.getRoundById(roundId);
        if (round == null) return false;

        // Obtener apuestas existentes
        List<Bid> existingBids = bidDAO.getBidsByRoundId(roundId);

        // Calcular suma actual
        int currentSum = existingBids.stream()
                .mapToInt(Bid::getBidAmount)
                .sum();

        // Obtener número de jugadores activos
        List<Player> activePlayers = playerDAO.getActivePlayersByGameId(round.getGameId());
        int totalPlayers = activePlayers.size();

        // Número de cartas en esta ronda
        int numCards = round.getNumCards();

        // Si NO es el último jugador en apostar
        if (existingBids.size() < totalPlayers - 1) {
            // Puede apostar cualquier cantidad válida
            return bidAmount >= 0 && bidAmount <= numCards;
        }

        // Si ES el último jugador en apostar
        if (existingBids.size() == totalPlayers - 1) {
            // La suma (actual + nueva apuesta) NO puede ser igual a numCards
            int potentialSum = currentSum + bidAmount;

            if (potentialSum == numCards) {
                System.out.println("❌ Apuesta inválida: " + currentSum + " + " + bidAmount +
                        " = " + potentialSum + " (igual a " + numCards + " cartas)");
                return false; // ¡PROHIBIDO!
            }

            return true; // Válida si la suma es diferente
        }

        // No debería llegar aquí (todos ya apostaron)
        return false;
    }

    /**
     * Obtiene todas las apuestas de una ronda
     *
     * @param roundId ID de la ronda
     * @return Lista de apuestas ordenadas por timestamp
     * @throws SQLException si hay error en BD
     */
    public List<Bid> getAllBidsForRound(Long roundId) throws SQLException {
        return bidDAO.getBidsByRoundId(roundId);
    }

    /**
     * Obtiene la apuesta de un jugador en una ronda
     *
     * @param playerId ID del jugador
     * @param roundId ID de la ronda
     * @return Bid del jugador, o null si no ha apostado
     * @throws SQLException si hay error en BD
     */
    public Bid getBidByPlayer(Long playerId, Long roundId) throws SQLException {
        return bidDAO.getBidByPlayerAndRound(playerId, roundId);
    }

    /**
     * Verifica si todos los jugadores han apostado
     *
     * @param roundId ID de la ronda
     * @return true si todos apostaron
     * @throws SQLException si hay error en BD
     */
    public boolean allPlayersHaveBid(Long roundId) throws SQLException {
        Round round = roundDAO.getRoundById(roundId);
        if (round == null) return false;

        List<Player> activePlayers = playerDAO.getActivePlayersByGameId(round.getGameId());
        return bidDAO.allPlayersHaveBid(roundId, activePlayers.size());
    }

    /**
     * Obtiene la suma total de apuestas en una ronda
     *
     * @param roundId ID de la ronda
     * @return Suma de todas las apuestas
     * @throws SQLException si hay error en BD
     */
    public int getTotalBids(Long roundId) throws SQLException {
        return bidDAO.getTotalBidsForRound(roundId);
    }

    /**
     * Actualiza las bazas ganadas de un jugador
     *
     * Se llama cuando un jugador GANA una baza
     *
     * @param playerId ID del jugador
     * @param roundId ID de la ronda
     * @return true si se actualizó correctamente
     * @throws SQLException si hay error en BD
     */
    public boolean updateTricksWon(Long playerId, Long roundId) throws SQLException {
        Bid bid = bidDAO.getBidByPlayerAndRound(playerId, roundId);
        if (bid == null) {
            throw new IllegalArgumentException("El jugador no tiene apuesta en esta ronda");
        }

        // Incrementar tricksWon
        boolean updated = bidDAO.incrementTricksWon(bid.getId());

        if (updated) {
            System.out.println("✅ " + playerId + " ganó una baza (" +
                    (bid.getTricksWon() + 1) + "/" + bid.getBidAmount() + ")");
        }

        return updated;
    }

    /**
     * Obtiene el resultado de una apuesta (acertó o no)
     *
     * @param bidId ID de la apuesta
     * @return BidResult con información del resultado
     * @throws SQLException si hay error en BD
     */
    public BidResult getBidResult(Long bidId) throws SQLException {
        Bid bid = bidDAO.getBidById(bidId);
        if (bid == null) {
            throw new IllegalArgumentException("La apuesta no existe");
        }

        boolean success = bid.getBidAmount() == bid.getTricksWon();
        int difference = Math.abs(bid.getBidAmount() - bid.getTricksWon());

        return new BidResult(bid, success, difference);
    }

    /**
     * Obtiene los resultados de todas las apuestas de una ronda
     *
     * @param roundId ID de la ronda
     * @return Lista de BidResult
     * @throws SQLException si hay error en BD
     */
    public List<BidResult> getAllBidResults(Long roundId) throws SQLException {
        List<Bid> bids = bidDAO.getBidsByRoundId(roundId);
        return bids.stream()
                .map(bid -> {
                    boolean success = bid.getBidAmount() == bid.getTricksWon();
                    int difference = Math.abs(bid.getBidAmount() - bid.getTricksWon());
                    return new BidResult(bid, success, difference);
                })
                .toList();
    }

    /**
     * Muestra un resumen de las apuestas de una ronda
     *
     * @param roundId ID de la ronda
     * @throws SQLException si hay error en BD
     */
    public void printBidSummary(Long roundId) throws SQLException {
        Round round = roundDAO.getRoundById(roundId);
        List<Bid> bids = bidDAO.getBidsByRoundId(roundId);

        System.out.println("\n📊 Resumen de Apuestas - Ronda " + round.getRoundNumber());
        System.out.println("   Cartas: " + round.getNumCards());

        int totalBids = 0;
        for (Bid bid : bids) {
            Player player = playerDAO.getPlayerById(bid.getPlayerId());
            System.out.println("   " + player.getName() + ": " +
                    bid.getBidAmount() + " bazas (ganó: " + bid.getTricksWon() + ")");
            totalBids += bid.getBidAmount();
        }

        System.out.println("   Suma total: " + totalBids +
                (totalBids == round.getNumCards() ? " ⚠️ ERROR" : " ✅"));
    }

    /**
     * Clase interna para representar el resultado de una apuesta
     */
    public static class BidResult {
        private final Bid bid;
        private final boolean success;
        private final int difference;

        public BidResult(Bid bid, boolean success, int difference) {
            this.bid = bid;
            this.success = success;
            this.difference = difference;
        }

        public Bid getBid() { return bid; }
        public boolean isSuccess() { return success; }
        public int getDifference() { return difference; }

        /**
         * Calcula cuántas vidas se pierden
         *
         * Según las reglas:
         * - Si aciertas → 0 vidas perdidas
         * - Si fallas → pierdes |apuesta - realidad| vidas
         *
         * @return Número de vidas a perder
         */
        public int getLivesLost() {
            return success ? 0 : difference;
        }

        @Override
        public String toString() {
            return "BidResult{" +
                    "playerId=" + bid.getPlayerId() +
                    ", bid=" + bid.getBidAmount() +
                    ", won=" + bid.getTricksWon() +
                    ", success=" + success +
                    ", livesLost=" + getLivesLost() +
                    '}';
        }
    }
}