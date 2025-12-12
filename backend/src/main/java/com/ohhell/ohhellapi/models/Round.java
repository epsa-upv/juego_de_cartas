package com.ohhell.ohhellapi.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Round {

    private Long id;
    private Long gameId;
    private int roundNumber;
    private int cardsPerPlayer;
    private String trumpSuit;
    private Card trump;
    private Long dealerId;

    private List<Bid> bids;
    private List<Trick> tricks;

    private int currentTrick;
    private String status; // "bidding", "playing", "finished"

    private Map<Long, Integer> scores;

    // -----------------------------
    // CONSTRUCTORES
    // -----------------------------
    public Round() {
        this.bids = new ArrayList<>();
        this.tricks = new ArrayList<>();
        this.scores = new HashMap<>();
        this.currentTrick = 0;
        this.status = "bidding";
    }

    public Round(int roundNumber, int cardsPerPlayer, String trumpSuit, Long dealerId) {
        this();
        this.roundNumber = roundNumber;
        this.cardsPerPlayer = cardsPerPlayer;
        this.trumpSuit = trumpSuit;
        this.dealerId = dealerId;
    }

    // -----------------------------
    // GETTERS / SETTERS
    // -----------------------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public int getRoundNumber() { return roundNumber; }
    public void setRoundNumber(int roundNumber) { this.roundNumber = roundNumber; }

    public int getCardsPerPlayer() { return cardsPerPlayer; }
    public void setCardsPerPlayer(int cardsPerPlayer) { this.cardsPerPlayer = cardsPerPlayer; }

    public String getTrumpSuit() { return trumpSuit; }
    public void setTrumpSuit(String trumpSuit) { this.trumpSuit = trumpSuit; }

    public Card getTrump() { return trump; }
    public void setTrump(Card trump) {
        this.trump = trump;
        this.trumpSuit = (trump != null ? trump.getSuit() : null);
    }

    public Long getDealerId() { return dealerId; }
    public void setDealerId(Long dealerId) { this.dealerId = dealerId; }

    public List<Bid> getBids() { return bids; }
    public void setBids(List<Bid> bids) { this.bids = bids; }

    public List<Trick> getTricks() { return tricks; }
    public void setTricks(List<Trick> tricks) { this.tricks = tricks; }

    public int getCurrentTrick() { return currentTrick; }
    public void setCurrentTrick(int currentTrick) { this.currentTrick = currentTrick; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<Long, Integer> getScores() { return scores; }
    public void setScores(Map<Long, Integer> scores) { this.scores = scores; }

    // -----------------------------
    // MÉTODOS DE LÓGICA
    // -----------------------------

    /** Agregar apuesta */
    public void addBid(Bid bid) {
        this.bids.add(bid);
    }

    /**
     * Valida la regla del último jugador:
     * La suma total de apuestas NO PUEDE ser igual a cartasPerPlayer
     */
    public boolean isValidBid(int bidValue, int totalPlayers) {
        int sum = bids.stream().mapToInt(Bid::getBidAmount).sum();
        boolean isLast = (bids.size() == totalPlayers - 1);
        return !isLast || (sum + bidValue) != cardsPerPlayer;
    }

    /** Iniciar una nueva baza */
    public void startNewTrick() {
        currentTrick++;
        tricks.add(new Trick(currentTrick));
    }

    /** Obtener baza actual */
    public Trick getCurrentTrickObject() {
        if (currentTrick == 0 || currentTrick > tricks.size()) return null;
        return tricks.get(currentTrick - 1);
    }

    /** Calcular puntuaciones finales de la ronda */
    public void calculateScores() {
        for (Bid bid : bids) {
            if (bid.isBidMet()) {
                scores.put(bid.getPlayerId(), 10 + (bid.getBidAmount() * 5));
            } else {
                scores.put(bid.getPlayerId(), 0);
            }
        }
        this.status = "finished";
    }
}
