package com.ohhell.ohhellapi.services;

import com.ohhell.ohhellapi.dao.BidDAO;
import com.ohhell.ohhellapi.dao.RoundDAO;
import com.ohhell.ohhellapi.dao.PlayerDAO;
import com.ohhell.ohhellapi.models.Bid;
import com.ohhell.ohhellapi.models.Round;
import com.ohhell.ohhellapi.models.Player;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class BidService {

    @Inject
    private BidDAO bidDAO;

    @Inject
    private RoundDAO roundDAO;

    @Inject
    private PlayerDAO playerDAO;

    /**
     * MÉTODO PRINCIPAL: Un jugador hace su apuesta
     * Incluye TODAS las validaciones del juego Oh Hell!
     */
    @Transactional
    public Bid placeBid(Long playerId, Long roundId, int bidAmount) {
        // 1. Validar que el jugador existe
        Player player = playerDAO.findById(playerId);
        if (player == null) {
            throw new IllegalArgumentException("Jugador no encontrado (ID: " + playerId + ")");
        }

        // 2. Validar que la ronda existe
        Round round = roundDAO.findById(roundId);
        if (round == null) {
            throw new IllegalArgumentException("Ronda no encontrada (ID: " + roundId + ")");
        }

        // 3. Validar fase de la ronda (debe estar en "bidding")
        if (!"bidding".equalsIgnoreCase(round.getStatus())) {
            throw new IllegalStateException("La ronda no está en fase de apuestas. Estado actual: " + round.getStatus());
        }

        // 4. Validar que el jugador no ha apostado ya
        if (bidDAO.playerHasBidInRound(playerId, roundId)) {
            throw new IllegalStateException("El jugador ya ha apostado en esta ronda");
        }

        // 5. Validar rango de apuesta (0 a cartas por jugador)
        if (bidAmount < 0 || bidAmount > round.getCardsPerPlayer()) {
            throw new IllegalArgumentException(
                    "La apuesta debe estar entre 0 y " + round.getCardsPerPlayer() +
                            ". Valor recibido: " + bidAmount
            );
        }

        // 6. Validar REGLA CRÍTICA del Oh Hell! (suma ≠ cartas)
        if (!isValidBidForOhHellRule(roundId, bidAmount)) {
            throw new IllegalStateException(
                    "Apuesta inválida según regla Oh Hell!: " +
                            "La suma total de apuestas NO puede ser igual al número de cartas en la ronda"
            );
        }

        // 7. Crear y guardar la apuesta
        Bid bid = new Bid(playerId, roundId, bidAmount);
        bidDAO.save(bid);

        // 8. Verificar si TODOS los jugadores han apostado
        int totalPlayers = round.getGame().getPlayerIds().size();
        int bidsCount = bidDAO.countBidsInRound(roundId);

        if (bidsCount == totalPlayers) {
            // Todos han apostado → Cambiar fase de la ronda a "playing"
            round.setStatus("playing");
            roundDAO.save(round);
            System.out.println("✅ Todos los jugadores han apostado. Ronda cambiada a fase 'playing'");
        }

        return bid;
    }

    /**
     * VALIDACIÓN CRÍTICA: Regla del Oh Hell!
     * La suma total de apuestas NO puede ser igual al número de cartas
     */
    private boolean isValidBidForOhHellRule(Long roundId, int newBidAmount) {
        // Obtener la ronda
        Round round = roundDAO.findById(roundId);
        if (round == null) return false;

        // Obtener apuestas existentes en esta ronda
        List<Bid> existingBids = bidDAO.findByRoundId(roundId);

        // Calcular suma actual de apuestas
        int currentSum = existingBids.stream()
                .mapToInt(Bid::getBidAmount)
                .sum();

        // Calcular suma potencial (actual + nueva)
        int potentialSum = currentSum + newBidAmount;
        int cardsPerPlayer = round.getCardsPerPlayer();
        int totalPlayers = round.getGame().getPlayerIds().size();

        // Si NO es el último jugador en apostar
        if (existingBids.size() < totalPlayers - 1) {
            // Cualquier apuesta válida es aceptable (ya validamos rango)
            return true;
        }

        // Si ES el último jugador en apostar
        if (existingBids.size() == totalPlayers - 1) {
            // La suma NO puede ser igual al número de cartas
            return potentialSum != cardsPerPlayer;
        }

        // No debería llegar aquí (todos ya apostaron)
        return false;
    }

    // --- MÉTODOS DE CONSULTA ---

    public List<Bid> getBidsByRound(Long roundId) {
        return bidDAO.findByRoundId(roundId);
    }

    public Bid getBidByPlayerAndRound(Long playerId, Long roundId) {
        return bidDAO.findByPlayerAndRound(playerId, roundId);
    }

    public List<Bid> getBidsByPlayer(Long playerId) {
        return bidDAO.findByPlayerId(playerId);
    }

    public int getTotalBidAmountForRound(Long roundId) {
        return bidDAO.getTotalBidAmountForRound(roundId);
    }

    public boolean allPlayersHaveBid(Long roundId, int totalPlayers) {
        return bidDAO.countBidsInRound(roundId) == totalPlayers;
    }

    // --- MÉTODOS DE ACTUALIZACIÓN ---

    @Transactional
    public boolean updateBid(Long bidId, int newBidAmount) {
        Bid bid = bidDAO.findById(bidId);
        if (bid != null) {
            bid.setBidAmount(newBidAmount);
            bidDAO.save(bid);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean incrementTricksWon(Long bidId) {
        return bidDAO.incrementTricksWon(bidId);
    }

    @Transactional
    public boolean deleteBid(Long bidId) {
        try {
            bidDAO.delete(bidId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // --- MÉTODOS DE CÁLCULO ---

    public int calculateScoreForBid(Bid bid) {
        if (bid == null) return 0;

        int difference = bid.getDifference();
        // En Oh Hell!: Si aciertas exactamente → +10 puntos, si fallas → -diferencia
        return difference == 0 ? 10 : -difference;
    }

    public String getBidStatus(Bid bid) {
        if (bid == null) return "NO_APOSTADO";

        if (bid.getTricksWon() < bid.getBidAmount()) {
            return "EN_PROGRESO";
        } else if (bid.getTricksWon() == bid.getBidAmount()) {
            return "EXACTO";
        } else {
            return "EXCEDIDO";
        }
    }
}