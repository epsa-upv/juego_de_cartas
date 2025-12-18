package com.ohhell.api.dao;

import com.ohhell.api.db.Database;
import com.ohhell.api.models.RoundView;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.UUID;

public class RoundDAO {

    public void createFirstRound(UUID gameId, int cardsPerPlayer, int dealerSeat) {

        String sql = """
            INSERT INTO oh_hell.rounds
            (game_id, number, cards_per_player, dealer_seat, phase, started_at)
            VALUES (?, 1, ?, ?, 'BETTING', now())
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ps.setInt(2, cardsPerPlayer);
            ps.setInt(3, dealerSeat);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error creando la primera ronda", e);
        }
    }

    public RoundView findCurrentRound(UUID gameId) {

        String sql = """
            SELECT
                id,
                number,
                cards_per_player,
                dealer_seat,
                phase,
                started_at
            FROM oh_hell.rounds
            WHERE game_id = ?
              AND finished_at IS NULL
            ORDER BY number DESC
            LIMIT 1
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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
}
