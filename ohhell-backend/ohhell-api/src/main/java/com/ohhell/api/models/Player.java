package com.ohhell.api.models;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Player {

    private UUID id;
    private UUID userId;
    private String nickname;
    private OffsetDateTime createdAt;

    public Player() {}

    public Player(UUID id, UUID userId, String nickname, OffsetDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
