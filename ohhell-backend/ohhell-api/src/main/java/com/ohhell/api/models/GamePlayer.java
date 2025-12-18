package com.ohhell.api.models;

import java.util.UUID;

public class GamePlayer {

    private Long id;
    private UUID gameId;
    private UUID playerId;
    private int seatPosition;
    private int livesRemaining;
    private int totalPoints;
    private boolean isHost;
    private String status;

    public GamePlayer() {}

    public GamePlayer(
            Long id,
            UUID gameId,
            UUID playerId,
            int seatPosition,
            int livesRemaining,
            int totalPoints,
            boolean isHost,
            String status
    ) {
        this.id = id;
        this.gameId = gameId;
        this.playerId = playerId;
        this.seatPosition = seatPosition;
        this.livesRemaining = livesRemaining;
        this.totalPoints = totalPoints;
        this.isHost = isHost;
        this.status = status;
    }

    public Long getId() { return id; }
    public UUID getGameId() { return gameId; }
    public UUID getPlayerId() { return playerId; }
    public int getSeatPosition() { return seatPosition; }
    public int getLivesRemaining() { return livesRemaining; }
    public int getTotalPoints() { return totalPoints; }
    public boolean isHost() { return isHost; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setGameId(UUID gameId) { this.gameId = gameId; }
    public void setPlayerId(UUID playerId) { this.playerId = playerId; }
    public void setSeatPosition(int seatPosition) { this.seatPosition = seatPosition; }
    public void setLivesRemaining(int livesRemaining) { this.livesRemaining = livesRemaining; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    public void setHost(boolean host) { isHost = host; }
    public void setStatus(String status) { this.status = status; }
}
