package com.ohhell.ohhellapi.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Player {
    public enum PlayerStatus {
        ACTIVE, INACTIVE, ELIMINATED
    }
    
    private Long id;
    private String name;
    private String username;
    private String email;
    private String password;
    private int lives;
    private boolean isDealer;
    private PlayerStatus status;
    private int totalGames;
    private int totalWins;
    private LocalDateTime createdAt;
    private List<Card> hand;
    
    public Player() {
        this.hand = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.totalGames = 0;
        this.totalWins = 0;
        this.lives = 5;
        this.isDealer = false;
        this.status = PlayerStatus.ACTIVE;
    }
    
    public Player(String username, String email, String password) {
        this();
        this.username = username;
        this.name = username;
        this.email = email;
        this.password = password;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPlayerId() { return id; }
    public void setPlayerId(Long playerId) { this.id = playerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }
    public boolean isDealer() { return isDealer; }
    public void setDealer(boolean dealer) { isDealer = dealer; }
    public PlayerStatus getStatus() { return status; }
    public void setStatus(PlayerStatus status) { this.status = status; }
    public int getTotalGames() { return totalGames; }
    public void setTotalGames(int totalGames) { this.totalGames = totalGames; }
    public int getTotalWins() { return totalWins; }
    public void setTotalWins(int totalWins) { this.totalWins = totalWins; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<Card> getHand() { return hand; }
    public void setHand(List<Card> hand) { this.hand = hand; }
    
    public void addCardToHand(Card card) { this.hand.add(card); }
    public void clearHand() { this.hand.clear(); }
    public void incrementGames() { this.totalGames++; }
    public void incrementWins() { this.totalWins++; }
}
