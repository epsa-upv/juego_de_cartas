package com.ohhell.ohhellapi.models;

import java.util.HashMap;
import java.util.Map;

public class Trick {
    private Long id;
    private Long roundId;
    private int trickNumber;
    private String leadingSuit;
    private String leadSuit;
    private Map<Long, Card> cardsPlayed;
    private Long winnerId;
    private boolean completed;
    
    public Trick() {
        this.cardsPlayed = new HashMap<>();
        this.completed = false;
    }
    
    public Trick(int trickNumber) {
        this();
        this.trickNumber = trickNumber;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRoundId() { return roundId; }
    public void setRoundId(Long roundId) { this.roundId = roundId; }
    public int getTrickNumber() { return trickNumber; }
    public void setTrickNumber(int trickNumber) { this.trickNumber = trickNumber; }
    public String getLeadingSuit() { return leadingSuit != null ? leadingSuit : leadSuit; }
    public void setLeadingSuit(String leadingSuit) { this.leadingSuit = leadingSuit; this.leadSuit = leadingSuit; }
    public String getLeadSuit() { return leadSuit != null ? leadSuit : leadingSuit; }
    public void setLeadSuit(String leadSuit) { this.leadSuit = leadSuit; this.leadingSuit = leadSuit; }
    public Map<Long, Card> getCardsPlayed() { return cardsPlayed; }
    public void setCardsPlayed(Map<Long, Card> cardsPlayed) { this.cardsPlayed = cardsPlayed; }
    public Long getWinnerId() { return winnerId; }
    public void setWinnerId(Long winnerId) { this.winnerId = winnerId; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    
    public void playCard(Long playerId, Card card) {
        if (cardsPlayed.isEmpty()) {
            this.leadingSuit = card.getSuit();
            this.leadSuit = card.getSuit();
        }
        cardsPlayed.put(playerId, card);
    }
    
    public Long determineWinner(String trumpSuit) {
        Long winner = null;
        Card winningCard = null;
        
        for (Map.Entry<Long, Card> entry : cardsPlayed.entrySet()) {
            Card card = entry.getValue();
            if (winningCard == null) {
                winner = entry.getKey();
                winningCard = card;
                continue;
            }
            
            if (card.getSuit().equals(trumpSuit) && !winningCard.getSuit().equals(trumpSuit)) {
                winner = entry.getKey();
                winningCard = card;
            } else if (card.getSuit().equals(trumpSuit) && winningCard.getSuit().equals(trumpSuit)) {
                if (card.getValue() > winningCard.getValue()) {
                    winner = entry.getKey();
                    winningCard = card;
                }
            } else if (!card.getSuit().equals(trumpSuit) && !winningCard.getSuit().equals(trumpSuit)) {
                if (card.getSuit().equals(getLeadingSuit()) && card.getValue() > winningCard.getValue()) {
                    winner = entry.getKey();
                    winningCard = card;
                }
            }
        }
        
        this.winnerId = winner;
        return winner;
    }
    
    public boolean isComplete(int totalPlayers) {
        return cardsPlayed.size() == totalPlayers;
    }
}
