package com.ohhell.ohhellapi.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "round_id", nullable = false)
    private Long roundId;

    @Column(name = "bid_amount", nullable = false)
    private int bidAmount;

    @Column(name = "tricks_won", nullable = false)
    private int tricksWon = 0;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Constructor vacío (OBLIGATORIO para JPA)
    public Bid() {
        this.timestamp = LocalDateTime.now();
        this.tricksWon = 0;
    }

    // Constructor para crear apuesta
    public Bid(Long playerId, int bidAmount) {
        this();
        this.playerId = playerId;
        this.bidAmount = bidAmount;
    }

    // Constructor completo
    public Bid(Long playerId, Long roundId, int bidAmount) {
        this();
        this.playerId = playerId;
        this.roundId = roundId;
        this.bidAmount = bidAmount;
    }

    // --- GETTERS Y SETTERS (SIMPLE) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }

    public Long getRoundId() { return roundId; }
    public void setRoundId(Long roundId) { this.roundId = roundId; }

    public int getBidAmount() { return bidAmount; }
    public void setBidAmount(int bidAmount) { this.bidAmount = bidAmount; }

    public int getTricksWon() { return tricksWon; }
    public void setTricksWon(int tricksWon) { this.tricksWon = tricksWon; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    // --- MÉTODOS DE NEGOCIO ---
    public void incrementTricksWon() { this.tricksWon++; }

    public boolean isBidMet() { return this.bidAmount == this.tricksWon; }

    public int getDifference() { return Math.abs(this.bidAmount - this.tricksWon); }

    @Override
    public String toString() {
        return "Bid{id=" + id + ", playerId=" + playerId +
                ", bidAmount=" + bidAmount + ", tricksWon=" + tricksWon + "}";
    }
}