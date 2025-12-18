package com.ohhell.api.models;

import java.time.OffsetDateTime;

public class RoundView {

    private long id;
    private int number;
    private int cardsPerPlayer;
    private int dealerSeat;
    private String phase;
    private OffsetDateTime startedAt;

    public RoundView(long id,
                     int number,
                     int cardsPerPlayer,
                     int dealerSeat,
                     String phase,
                     OffsetDateTime startedAt) {
        this.id = id;
        this.number = number;
        this.cardsPerPlayer = cardsPerPlayer;
        this.dealerSeat = dealerSeat;
        this.phase = phase;
        this.startedAt = startedAt;
    }

    public long getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public int getCardsPerPlayer() {
        return cardsPerPlayer;
    }

    public int getDealerSeat() {
        return dealerSeat;
    }

    public String getPhase() {
        return phase;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }
}
