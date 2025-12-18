package com.ohhell.api.models;

import java.util.List;
import java.util.UUID;

public class RoundBetsView {

    private long roundId;
    private List<BetView> bets;

    public RoundBetsView(long roundId, List<BetView> bets) {
        this.roundId = roundId;
        this.bets = bets;
    }

    public long getRoundId() {
        return roundId;
    }

    public List<BetView> getBets() {
        return bets;
    }

    // ------------------

    public static class BetView {
        private UUID playerId;
        private String nickname;
        private int bet;
        private int order;

        public BetView(UUID playerId, String nickname, int bet, int order) {
            this.playerId = playerId;
            this.nickname = nickname;
            this.bet = bet;
            this.order = order;
        }

        public UUID getPlayerId() {
            return playerId;
        }

        public String getNickname() {
            return nickname;
        }

        public int getBet() {
            return bet;
        }

        public int getOrder() {
            return order;
        }
    }
}

