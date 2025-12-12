package com.ohhell.ohhellapi.services;

import com.ohhell.ohhellapi.dao.TrickDAO;
import com.ohhell.ohhellapi.dao.RoundDAO;
import com.ohhell.ohhellapi.dao.PlayerDAO;
import com.ohhell.ohhellapi.dao.BidDAO;
import com.ohhell.ohhellapi.models.Trick;
import com.ohhell.ohhellapi.models.Round;
import com.ohhell.ohhellapi.models.Player;
import com.ohhell.ohhellapi.models.Card;
import com.ohhell.ohhellapi.models.Bid;
import java.util.stream.Collectors;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TrickService - Servicio para gestión de bazas
 *
 * Oh Hell! Card Game - UPV
 * Autor: Tomás Criado García
 *
 * Gestiona el juego de bazas (tricks):
 * - Crear bazas
 * - Jugar cartas
 * - Determinar ganadores
 * - Controlar turnos
 */
public class TrickService {

    private final TrickDAO trickDAO;
    private final RoundDAO roundDAO;
    private final PlayerDAO playerDAO;
    private final BidDAO bidDAO;
    private final CardService cardService;
    private final BidService bidService;

    // Mapa temporal para almacenar las cartas jugadas en cada baza
    // En producción, esto debería estar en BD en una tabla trick_cards
    private final Map<Long, Map<Long, Card>> trickCards;

    public TrickService() {
        this.trickDAO = new TrickDAO();
        this.roundDAO = new RoundDAO();
        this.playerDAO = new PlayerDAO();
        this.bidDAO = new BidDAO();
        this.cardService = new CardService();
        this.bidService = new BidService();
        this.trickCards = new HashMap<>();
    }

    /**
     * Crea una nueva baza en una ronda
     *
     * @param roundId ID de la ronda
     * @return Trick creado
     * @throws SQLException si hay error en BD
     */
    public Trick createTrick(Long roundId) throws SQLException {
        Round round = roundDAO.getRoundById(roundId);
        if (round == null) {
            throw new IllegalArgumentException("La ronda no existe");
        }

        if (!round.getStatus().equalsIgnoreCase("PLAYING")) {
            throw new IllegalStateException("La ronda no está en fase de juego");
        }

        // Obtener número de la siguiente baza
        int trickCount = trickDAO.countTotalTricksInRound(roundId);
        int nextTrickNumber = trickCount + 1;

        // Verificar que no exceda el número de cartas
        if (nextTrickNumber > round.getNumCards()) {
            throw new IllegalStateException("Ya se jugaron todas las bazas de esta ronda");
        }

        // Crear nueva baza
        Trick trick = new Trick();
        trick.setRoundId(roundId);
        trick.setTrickNumber(nextTrickNumber);
        trick.setCompleted(false);

        Trick createdTrick = trickDAO.createTrick(trick);

        // Inicializar mapa de cartas para esta baza
        trickCards.put(createdTrick.getId(), new HashMap<>());

        System.out.println("🎴 Baza " + nextTrickNumber + " creada");

        return createdTrick;
    }

    /**
     * Un jugador juega una carta en una baza
     *
     * @param trickId ID de la baza
     * @param playerId ID del jugador
     * @param card Carta a jugar
     * @return true si se jugó correctamente
     * @throws SQLException si hay error en BD
     */
    public boolean playCard(Long trickId, Long playerId, Card card) throws SQLException {
        // Validaciones
        Trick trick = trickDAO.getTrickById(trickId);
        if (trick == null) {
            throw new IllegalArgumentException("La baza no existe");
        }

        if (trick.isCompleted()) {
            throw new IllegalStateException("Esta baza ya está completada");
        }

        Player player = playerDAO.getPlayerById(playerId);
        if (player == null) {
            throw new IllegalArgumentException("El jugador no existe");
        }

        // Verificar que es el turno del jugador
        Long nextPlayerId = getNextPlayer(trick.getRoundId(), trickId);
        if (!playerId.equals(nextPlayerId)) {
            throw new IllegalStateException("No es el turno de este jugador");
        }

        // Validar que la carta es legal según las reglas
        if (!canPlayCard(playerId, trickId, card)) {
            throw new IllegalStateException("No puedes jugar esta carta (debes seguir el palo)");
        }

        // Obtener o inicializar mapa de cartas jugadas
        Map<Long, Card> cardsInTrick = trickCards.computeIfAbsent(trickId, k -> new HashMap<>());

        // Si es la primera carta, establecer leadSuit
        if (cardsInTrick.isEmpty()) {
            trick.setLeadSuit(card.getSuit());
            trickDAO.updateLeadCard(trickId, card);
        }

        // Registrar la carta jugada
        cardsInTrick.put(playerId, card);

        // Eliminar carta de la mano del jugador (en un juego real)
        // player.getHand().remove(card);
        // playerDAO.updatePlayer(player);

        System.out.println("🃏 " + player.getName() + " juega: " +
                cardService.cardToString(card));

        // Verificar si la baza está completa
        if (isTrickComplete(trickId)) {
            completeTrick(trickId);
        }

        return true;
    }

    /**
     * Verifica si un jugador puede jugar una carta específica
     *
     * @param playerId ID del jugador
     * @param trickId ID de la baza
     * @param card Carta a jugar
     * @return true si puede jugarla
     * @throws SQLException si hay error en BD
     */
    public boolean canPlayCard(Long playerId, Long trickId, Card card) throws SQLException {
        Player player = playerDAO.getPlayerById(playerId);
        if (player == null) return false;

        Trick trick = trickDAO.getTrickById(trickId);
        if (trick == null) return false;

        // Obtener la mano del jugador
        List<Card> playerHand = player.getHand();

        // Obtener leadSuit de la baza (puede ser null si es el primero)
        String leadSuit = trick.getLeadSuit();

        // Usar CardService para validar
        return cardService.isValidPlay(playerHand, card, leadSuit);
    }

    /**
     * Verifica si todos los jugadores han jugado en la baza
     *
     * @param trickId ID de la baza
     * @return true si está completa
     * @throws SQLException si hay error en BD
     */
    public boolean isTrickComplete(Long trickId) throws SQLException {
        Trick trick = trickDAO.getTrickById(trickId);
        if (trick == null) return false;

        Round round = roundDAO.getRoundById(trick.getRoundId());
        if (round == null) return false;

        // Obtener jugadores activos
        List<Player> allPlayers = playerDAO.getPlayersByGameId(round.getGameId());
        List<Player> activePlayers = allPlayers.stream()
                .filter(p -> p.getLives() > 0 && p.getStatus() == Player.PlayerStatus.ACTIVE)
                .collect(Collectors.toList());

        // Obtener cartas jugadas
        Map<Long, Card> cardsInTrick = trickCards.get(trickId);
        if (cardsInTrick == null) return false;

        // Completa si todos los jugadores activos jugaron
        return cardsInTrick.size() == activePlayers.size();
    }

    /**
     * Determina el ganador de una baza
     *
     * @param trickId ID de la baza
     * @return ID del jugador ganador
     * @throws SQLException si hay error en BD
     */
    public Long determineTrickWinner(Long trickId) throws SQLException {
        Trick trick = trickDAO.getTrickById(trickId);
        if (trick == null) {
            throw new IllegalArgumentException("La baza no existe");
        }

        Round round = roundDAO.getRoundById(trick.getRoundId());
        if (round == null) {
            throw new IllegalArgumentException("La ronda no existe");
        }

        // Obtener cartas jugadas
        Map<Long, Card> cardsPlayed = trickCards.get(trickId);
        if (cardsPlayed == null || cardsPlayed.isEmpty()) {
            throw new IllegalStateException("No hay cartas jugadas en esta baza");
        }

        // Obtener trumpSuit y leadSuit
        String trumpSuit = round.getTrump() != null ? round.getTrump().getSuit() : null;
        String leadSuit = trick.getLeadSuit();

        // Usar CardService para determinar ganador
        Long winnerId = cardService.determineTrickWinner(cardsPlayed, trumpSuit, leadSuit);

        return winnerId;
    }

    /**
     * Completa una baza: determina ganador y actualiza puntos
     *
     * @param trickId ID de la baza
     * @return ID del jugador ganador
     * @throws SQLException si hay error en BD
     */
    public Long completeTrick(Long trickId) throws SQLException {
        Trick trick = trickDAO.getTrickById(trickId);
        if (trick == null) {
            throw new IllegalArgumentException("La baza no existe");
        }

        if (trick.isCompleted()) {
            throw new IllegalStateException("Esta baza ya está completada");
        }

        // Verificar que todos jugaron
        if (!isTrickComplete(trickId)) {
            throw new IllegalStateException("No todos los jugadores han jugado");
        }

        // Determinar ganador
        Long winnerId = determineTrickWinner(trickId);

        // Actualizar baza en BD
        trickDAO.completeTrick(trickId, winnerId);

        // Actualizar tricksWon del ganador
        bidService.updateTricksWon(winnerId, trick.getRoundId());

        Player winner = playerDAO.getPlayerById(winnerId);
        System.out.println("🏆 " + winner.getName() + " gana la baza!");

        // Verificar si la ronda terminó
        Round round = roundDAO.getRoundById(trick.getRoundId());
        int completedTricks = trickDAO.countCompletedTricksInRound(trick.getRoundId());

        if (completedTricks == round.getNumCards()) {
            System.out.println("✅ Ronda completada: " + completedTricks + "/" + round.getNumCards() + " bazas");
            roundDAO.updateRoundStatus(trick.getRoundId(), "COMPLETED");
        }

        return winnerId;
    }

    /**
     * Obtiene la baza actual (no completada) de una ronda
     *
     * @param roundId ID de la ronda
     * @return Trick actual, o null si no hay
     * @throws SQLException si hay error en BD
     */
    public Trick getCurrentTrick(Long roundId) throws SQLException {
        return trickDAO.getCurrentTrick(roundId);
    }

    /**
     * Obtiene todas las bazas de una ronda
     *
     * @param roundId ID de la ronda
     * @return Lista de bazas
     * @throws SQLException si hay error en BD
     */
    public List<Trick> getAllTricksInRound(Long roundId) throws SQLException {
        return trickDAO.getTricksByRoundId(roundId);
    }

    /**
     * Determina quién debe jugar la siguiente carta
     *
     * REGLAS:
     * - Primera baza: empieza el jugador a la izquierda del dealer
     * - Siguientes bazas: empieza el ganador de la baza anterior
     * - Dentro de una baza: orden rotatorio horario
     *
     * @param roundId ID de la ronda
     * @param currentTrickId ID de la baza actual
     * @return ID del jugador que debe jugar
     * @throws SQLException si hay error en BD
     */
    public Long getNextPlayer(Long roundId, Long currentTrickId) throws SQLException {
        Round round = roundDAO.getRoundById(roundId);
        if (round == null) {
            throw new IllegalArgumentException("La ronda no existe");
        }

        Trick currentTrick = trickDAO.getTrickById(currentTrickId);
        if (currentTrick == null) {
            throw new IllegalArgumentException("La baza no existe");
        }

        // Obtener jugadores activos en orden
        List<Player> activePlayers = playerDAO.getActivePlayersByGameId(round.getGameId());

        // Obtener cartas jugadas en esta baza
        Map<Long, Card> cardsInTrick = trickCards.get(currentTrickId);
        if (cardsInTrick == null) {
            cardsInTrick = new HashMap<>();
        }

        // Si es la primera carta de la baza
        if (cardsInTrick.isEmpty()) {
            // Si es la primera baza de la ronda
            if (currentTrick.getTrickNumber() == 1) {
                // Empieza el jugador a la izquierda del dealer
                return getPlayerToLeftOfDealer(round.getDealerId(), activePlayers);
            } else {
                // Empieza el ganador de la baza anterior
                Trick previousTrick = trickDAO.getTrickByNumber(roundId, currentTrick.getTrickNumber() - 1);
                if (previousTrick != null && previousTrick.getWinnerId() != null) {
                    return previousTrick.getWinnerId();
                }
            }
        }

        // Si ya hay cartas jugadas, continuar en orden rotatorio
        // Encontrar el primer jugador que jugó en esta baza
        Long firstPlayerId = null;
        for (Long playerId : activePlayers.stream().map(Player::getId).toList()) {
            if (cardsInTrick.containsKey(playerId)) {
                if (firstPlayerId == null) {
                    firstPlayerId = playerId;
                }
            }
        }

        // Encontrar siguiente jugador que NO ha jugado
        if (firstPlayerId != null) {
            int startIndex = -1;
            for (int i = 0; i < activePlayers.size(); i++) {
                if (activePlayers.get(i).getId().equals(firstPlayerId)) {
                    startIndex = i;
                    break;
                }
            }

            // Buscar siguiente jugador que no ha jugado
            for (int i = 1; i <= activePlayers.size(); i++) {
                int index = (startIndex + i) % activePlayers.size();
                Long candidateId = activePlayers.get(index).getId();
                if (!cardsInTrick.containsKey(candidateId)) {
                    return candidateId;
                }
            }
        }

        // Por defecto, devolver el primer jugador activo
        return activePlayers.isEmpty() ? null : activePlayers.get(0).getId();
    }

    /**
     * Obtiene el jugador a la izquierda del dealer
     *
     * @param dealerId ID del dealer
     * @param players Lista de jugadores activos
     * @return ID del jugador a la izquierda
     */
    private Long getPlayerToLeftOfDealer(Long dealerId, List<Player> players) {
        if (players.isEmpty()) return null;

        // Encontrar índice del dealer
        int dealerIndex = -1;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getId().equals(dealerId)) {
                dealerIndex = i;
                break;
            }
        }

        if (dealerIndex == -1) {
            return players.get(0).getId(); // Si no se encuentra, primer jugador
        }

        // Siguiente jugador (rotación circular)
        int nextIndex = (dealerIndex + 1) % players.size();
        return players.get(nextIndex).getId();
    }

    /**
     * Obtiene las cartas jugadas en una baza
     *
     * @param trickId ID de la baza
     * @return Map de playerId -> Card
     */
    public Map<Long, Card> getCardsPlayedInTrick(Long trickId) {
        return trickCards.getOrDefault(trickId, new HashMap<>());
    }

    /**
     * Muestra un resumen visual de una baza
     *
     * @param trickId ID de la baza
     * @throws SQLException si hay error en BD
     */
    public void printTrickSummary(Long trickId) throws SQLException {
        Trick trick = trickDAO.getTrickById(trickId);
        if (trick == null) return;

        Round round = roundDAO.getRoundById(trick.getRoundId());
        Map<Long, Card> cards = trickCards.get(trickId);

        System.out.println("\n🎴 Baza " + trick.getTrickNumber());
        System.out.println("   Palo inicial: " + trick.getLeadSuit());
        System.out.println("   Triunfo: " + (round.getTrump() != null ?
                cardService.cardToString(round.getTrump()) : "ninguno"));

        if (cards != null && !cards.isEmpty()) {
            System.out.println("   Cartas jugadas:");
            for (Map.Entry<Long, Card> entry : cards.entrySet()) {
                Player player = playerDAO.getPlayerById(entry.getKey());
                System.out.println("     " + player.getName() + ": " +
                        cardService.cardToString(entry.getValue()));
            }
        }

        if (trick.isCompleted() && trick.getWinnerId() != null) {
            Player winner = playerDAO.getPlayerById(trick.getWinnerId());
            System.out.println("   🏆 Ganador: " + winner.getName());
        }
    }
}