package com.ohhell.api.dao;
import com.ohhell.api.models.RoundBetsView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ohhell.api.db.Database;

import java.sql.*;
import java.util.UUID;

public class BetDAO {

    // =========================
    // CREATE BET
    // =========================
    public void placeBet(
            long roundId,
            long gamePlayerId,
            int betValue,
            int betOrder
    ) {

        String sql = """
            INSERT INTO oh_hell.bets
            (round_id, game_player_id, bet_value, bet_order)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ps.setLong(2, gamePlayerId);
            ps.setInt(3, betValue);
            ps.setInt(4, betOrder);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error creando la apuesta", e);
        }
    }

    // =========================
    // NEXT BET ORDER
    // =========================
    public int nextBetOrder(long roundId) {

        String sql = """
            SELECT COUNT(*)
            FROM oh_hell.bets
            WHERE round_id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ResultSet rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // CHECK ALREADY BET
    // =========================
    public boolean hasBet(long roundId, long gamePlayerId) {

        String sql = """
            SELECT 1
            FROM oh_hell.bets
            WHERE round_id = ? AND game_player_id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ps.setLong(2, gamePlayerId);

            return ps.executeQuery().next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<RoundBetsView.BetView> getBetsForRound(long roundId) {

        String sql = """
        SELECT
            gp.player_id,
            p.nickname,
            b.bet_value,
            b.bet_order
        FROM oh_hell.bets b
        JOIN oh_hell.game_players gp ON gp.id = b.game_player_id
        JOIN oh_hell.players p ON p.id = gp.player_id
        WHERE b.round_id = ?
        ORDER BY b.bet_order
    """;

        List<RoundBetsView.BetView> bets = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bets.add(new RoundBetsView.BetView(
                        (UUID) rs.getObject("player_id"),
                        rs.getString("nickname"),
                        rs.getInt("bet_value"),
                        rs.getInt("bet_order")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo apuestas", e);
        }

        return bets;
    }

    public int countBets(long roundId) {
        String sql = "SELECT COUNT(*) FROM oh_hell.bets WHERE round_id = ?";
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

    public int sumBets(long roundId) {
        String sql = "SELECT COALESCE(SUM(bet_value), 0) FROM oh_hell.bets WHERE round_id = ?";
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
