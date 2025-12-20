package com.ohhell.api.dao;

import com.ohhell.api.db.Database;
import com.ohhell.api.models.RoundView;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.*;

public class RoundDAO {

    public void createFirstRound(UUID gameId, int cardsPerPlayer, int dealerSeat) {

        String sql = """
            INSERT INTO oh_hell.rounds
            (game_id, number, cards_per_player, dealer_seat, phase, started_at)
            VALUES (?, 1, ?, ?, 'BETTING', now())
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ps.setInt(2, cardsPerPlayer);
            ps.setInt(3, dealerSeat);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePhase(long roundId, String phase) {

        String sql = "UPDATE oh_hell.rounds SET phase = ? WHERE id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, phase);
            ps.setLong(2, roundId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public RoundView findCurrentRound(UUID gameId) {

        String sql = """
            SELECT id, number, cards_per_player, dealer_seat, phase, started_at
            FROM oh_hell.rounds
            WHERE game_id = ?
              AND finished_at IS NULL
            ORDER BY number DESC
            LIMIT 1
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            return new RoundView(
                    rs.getLong("id"),
                    rs.getInt("number"),
                    rs.getInt("cards_per_player"),
                    rs.getInt("dealer_seat"),
                    rs.getString("phase"),
                    rs.getObject("started_at", OffsetDateTime.class)
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // REPARTO + TRIUNFO (FIX ENUMS)
    // =========================
    public void dealCards(long roundId, List<Long> gamePlayerIds, int cardsPerPlayer) {

        PlayerCardDAO playerCardDAO = new PlayerCardDAO();
        CardDAO cardDAO = new CardDAO();

        List<String> deck = buildDeck();
        Collections.shuffle(deck);

        Iterator<String> it = deck.iterator();

        // repartir cartas
        for (int i = 0; i < cardsPerPlayer; i++) {
            for (long gpId : gamePlayerIds) {
                playerCardDAO.addCard(roundId, gpId, it.next());
            }
        }

        // carta de triunfo
        String trump = it.next();
        String[] parts = trump.split("_");

        String rankEnum = mapRank(parts[0]);
        String suitEnum = mapSuit(parts[1]);

        int trumpCardId = cardDAO.findCardId(rankEnum, suitEnum);

        String sql = "UPDATE oh_hell.rounds SET trump_card_id = ? WHERE id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, trumpCardId);
            ps.setLong(2, roundId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> buildDeck() {

        String[] suits = {"H", "D", "C", "S"};
        String[] ranks = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};

        List<String> deck = new ArrayList<>();

        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(rank + "_" + suit);
            }
        }

        return deck;
    }

    // =========================
    // ENUM MAPPERS (CLAVE)
    // =========================
    private String mapSuit(String s) {
        return switch (s) {
            case "H" -> "HEARTS";
            case "D" -> "DIAMONDS";
            case "C" -> "CLUBS";
            case "S" -> "SPADES";
            default -> throw new IllegalArgumentException("Palo inválido: " + s);
        };
    }

    private String mapRank(String r) {
        return switch (r) {
            case "A" -> "ACE";
            case "K" -> "KING";
            case "Q" -> "QUEEN";
            case "J" -> "JACK";
            default -> r; // 2–10
        };
    }

    public int getTrumpCardId(long roundId) {

        String sql = "SELECT trump_card_id FROM oh_hell.rounds WHERE id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
