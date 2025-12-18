package com.ohhell.api.models;

import java.util.List;

public class GameLobbyView {

    private String code;
    private String status;
    private List<GamePlayerView> players;

    public GameLobbyView(String code, String status, List<GamePlayerView> players) {
        this.code = code;
        this.status = status;
        this.players = players;
    }

    public String getCode() { return code; }
    public String getStatus() { return status; }
    public List<GamePlayerView> getPlayers() { return players; }
}
