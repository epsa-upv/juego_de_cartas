package com.ohhell.api.dao;

import com.ohhell.api.db.Database;

import java.sql.*;
import java.util.*;

public class PlayerCardDAO {

    // =========================
    // REPARTO
    // =========================

    public void addCard(long roundId, long gpId, String card) {

        String sql = """
            INSERT INTO oh_hell.round_player_cards
            (round_id, game_player_id, card)
            VALUES (?, ?, ?)
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ps.setLong(2, gpId);
            ps.setString(3, card);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getHand(long roundId, long gpId) {

        String sql = """
            SELECT card
            FROM oh_hell.round_player_cards
            WHERE round_id = ? AND game_player_id = ?
            ORDER BY card
        """;

        List<String> hand = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ps.setLong(2, gpId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                hand.add(rs.getString("card"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return hand;
    }

    // =========================
    // VALIDACIONES
    // =========================

    public boolean playerHasSuit(long roundId, long gpId, String suit) {

        String sql = """
            SELECT 1
            FROM oh_hell.round_player_cards
            WHERE round_id = ?
              AND game_player_id = ?
              AND card LIKE ?
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ps.setLong(2, gpId);
            ps.setString(3, "%_" + suit);

            return ps.executeQuery().next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeCard(long roundId, long gpId, String card) {

        String sql = """
            DELETE FROM oh_hell.round_player_cards
            WHERE round_id = ?
              AND game_player_id = ?
              AND card = ?
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ps.setLong(2, gpId);
            ps.setString(3, card);

            if (ps.executeUpdate() == 0) {
                throw new RuntimeException("La carta no está en la mano");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearHand(long roundId) {

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM oh_hell.round_player_cards WHERE round_id = ?")) {

            ps.setLong(1, roundId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
