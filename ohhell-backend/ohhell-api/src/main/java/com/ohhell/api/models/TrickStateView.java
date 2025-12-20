package com.ohhell.api.models;

import java.util.List;
import java.util.UUID;

public class TrickStateView {

    private UUID currentPlayer;
    private String leadSuit;
    private List<PlayedCardView> plays;

    public TrickStateView(UUID currentPlayer, String leadSuit, List<PlayedCardView> plays) {
        this.currentPlayer = currentPlayer;
        this.leadSuit = leadSuit;
        this.plays = plays;
    }

    public static class PlayedCardView {
        public UUID playerId;
        public String card;
        public int order;

        public PlayedCardView(UUID playerId, String card, int order) {
            this.playerId = playerId;
            this.card = card;
            this.order = order;
        }
    }
}

