package com.ohhell.ohhellapi.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    public enum GameStatus {
        WAITING, IN_PROGRESS, FINISHED, CANCELLED
    }
    
    private Long id;
    private String name;
    private String status;
    private GameStatus gameStatus;
    private int currentRound;
    private int maxRounds;
    private int maxPlayers;
    private int initialLives;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private List<Long> playerIds;
    private Map<Long, Integer> playerScores;
    private Map<Long, Integer> playerLives;
    private List<Round> rounds;
    
    public Game() {
        this.playerIds = new ArrayList<>();
        this.playerScores = new HashMap<>();
        this.playerLives = new HashMap<>();
        this.rounds = new ArrayList<>();
        this.currentRound = 0;
        this.status = "waiting";
        this.gameStatus = GameStatus.WAITING;
        this.createdAt = LocalDateTime.now();
    }
    
    public Game(int maxPlayers, int initialLives, int maxRounds, Long createdBy) {
        this();
        this.maxPlayers = maxPlayers;
        this.initialLives = initialLives;
        this.maxRounds = maxRounds;
        this.createdBy = createdBy;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGameId() { return id; }
    public void setGameId(Long gameId) { this.id = gameId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        if (status != null) {
            switch (status.toUpperCase()) {
                case "WAITING": this.gameStatus = GameStatus.WAITING; break;
                case "ACTIVE":
                case "IN_PROGRESS": this.gameStatus = GameStatus.IN_PROGRESS; break;
                case "FINISHED": this.gameStatus = GameStatus.FINISHED; break;
                case "CANCELLED": this.gameStatus = GameStatus.CANCELLED; break;
            }
        }
    }
    
    public GameStatus getGameStatus() { return gameStatus; }
    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        this.status = gameStatus != null ? gameStatus.name().toLowerCase() : "waiting";
    }
    
    public int getCurrentRound() { return currentRound; }
    public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }
    public int getMaxRounds() { return maxRounds; }
    public void setMaxRounds(int maxRounds) { this.maxRounds = maxRounds; }
    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
    public int getInitialLives() { return initialLives; }
    public void setInitialLives(int initialLives) { this.initialLives = initialLives; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
    public List<Long> getPlayerIds() { return playerIds; }
    public void setPlayerIds(List<Long> playerIds) { this.playerIds = playerIds; }
    public Map<Long, Integer> getPlayerScores() { return playerScores; }
    public void setPlayerScores(Map<Long, Integer> playerScores) { this.playerScores = playerScores; }
    public Map<Long, Integer> getPlayerLives() { return playerLives; }
    public void setPlayerLives(Map<Long, Integer> playerLives) { this.playerLives = playerLives; }
    public List<Round> getRounds() { return rounds; }
    public void setRounds(List<Round> rounds) { this.rounds = rounds; }
    
    public boolean addPlayer(Long playerId) {
        if (playerIds.size() >= maxPlayers || !status.equals("waiting")) return false;
        playerIds.add(playerId);
        playerScores.put(playerId, 0);
        playerLives.put(playerId, initialLives);
        return true;
    }
    
    public boolean startGame() {
        if (playerIds.size() < 2 || !status.equals("waiting")) return false;
        this.status = "active";
        this.gameStatus = GameStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        startNewRound();
        return true;
    }
    
    public void startNewRound() {
        currentRound++;
        int cardsThisRound = Math.min(currentRound, 10);
        String[] suits = {"hearts", "diamonds", "clubs", "spades"};
        String trumpSuit = suits[(int) (Math.random() * 4)];
        Long dealerId = playerIds.get((currentRound - 1) % playerIds.size());
        Round round = new Round(currentRound, cardsThisRound, trumpSuit, dealerId);
        rounds.add(round);
    }
    
    public Round getCurrentRoundObject() {
        if (rounds.isEmpty() || currentRound > rounds.size()) return null;
        return rounds.get(currentRound - 1);
    }
    
    public void updatePlayerLives(Long playerId, int livesChange) {
        int currentLives = playerLives.getOrDefault(playerId, 0);
        playerLives.put(playerId, Math.max(0, currentLives + livesChange));
        if (playerLives.get(playerId) == 0) checkGameEnd();
    }
    
    public void addScore(Long playerId, int points) {
        int currentScore = playerScores.getOrDefault(playerId, 0);
        playerScores.put(playerId, currentScore + points);
    }
    
    public void checkGameEnd() {
        long playersAlive = playerLives.values().stream().filter(lives -> lives > 0).count();
        if (playersAlive <= 1 || currentRound >= maxRounds) {
            this.status = "finished";
            this.gameStatus = GameStatus.FINISHED;
            this.finishedAt = LocalDateTime.now();
        }
    }
    
    public Long getWinner() {
        if (!status.equals("finished")) return null;
        return playerScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
}
