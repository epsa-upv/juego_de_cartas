package com.ohhell.ohhellapi.models;

import java.time.LocalDateTime;

public class Bid {
    private Long id;
    private Long bidId;
    private Long playerId;
    private Long roundId;
    private int bidValue;
    private int bidAmount;
    private int tricksWon;
    private LocalDateTime timestamp;
    
    public Bid() {
        this.tricksWon = 0;
        this.timestamp = LocalDateTime.now();
    }
    
    public Bid(Long playerId, int bidValue) {
        this();
        this.playerId = playerId;
        this.bidValue = bidValue;
        this.bidAmount = bidValue;
    }
    
    public Long getId() { return id != null ? id : bidId; }
    public void setId(Long id) { this.id = id; this.bidId = id; }
    public Long getBidId() { return bidId != null ? bidId : id; }
    public void setBidId(Long bidId) { this.bidId = bidId; this.id = bidId; }
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public Long getRoundId() { return roundId; }
    public void setRoundId(Long roundId) { this.roundId = roundId; }
    public int getBidValue() { return bidValue != 0 ? bidValue : bidAmount; }
    public void setBidValue(int bidValue) { this.bidValue = bidValue; this.bidAmount = bidValue; }
    public int getBidAmount() { return bidAmount != 0 ? bidAmount : bidValue; }
    public void setBidAmount(int bidAmount) { this.bidAmount = bidAmount; this.bidValue = bidAmount; }
    public int getTricksWon() { return tricksWon; }
    public void setTricksWon(int tricksWon) { this.tricksWon = tricksWon; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public void incrementTricksWon() { this.tricksWon++; }
    public boolean isBidMet() { return getBidValue() == this.tricksWon; }
}
