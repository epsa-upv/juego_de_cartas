package com.ohhell.api.models;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Game {

    private UUID id;
    private String code;
    private String title;
    private String status;

    private int startingCards;
    private int maxRounds;

    private OffsetDateTime createdAt;
    private OffsetDateTime startedAt;

    public Game() {}

    public Game(
            UUID id,
            String code,
            String title,
            String status,
            int startingCards,
            int maxRounds,
            OffsetDateTime createdAt,
            OffsetDateTime startedAt
    ) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.status = status;
        this.startingCards = startingCards;
        this.maxRounds = maxRounds;
        this.createdAt = createdAt;
        this.startedAt = startedAt;
    }

    // =========================
    // Getters / setters
    // =========================

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public int getStartingCards() {
        return startingCards;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStartingCards(int startingCards) {
        this.startingCards = startingCards;
    }

    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }
}
