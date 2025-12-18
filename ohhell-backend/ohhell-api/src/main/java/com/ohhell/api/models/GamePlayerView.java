package com.ohhell.api.models;

import java.util.UUID;

public class GamePlayerView {

    private UUID playerId;
    private String nickname;
    private int seat;
    private boolean host;
    private boolean ready;

    public GamePlayerView(
            UUID playerId,
            String nickname,
            int seat,
            boolean host,
            boolean ready
    ) {
        this.playerId = playerId;
        this.nickname = nickname;
        this.seat = seat;
        this.host = host;
        this.ready = ready;
    }

    public UUID getPlayerId() { return playerId; }
    public String getNickname() { return nickname; }
    public int getSeat() { return seat; }
    public boolean isHost() { return host; }
    public boolean isReady() { return ready; }
}
