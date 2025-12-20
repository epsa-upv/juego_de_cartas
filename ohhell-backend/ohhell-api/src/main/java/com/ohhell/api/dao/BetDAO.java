package com.ohhell.api.dao;

import com.ohhell.api.db.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BetDAO {

    // =========================
    // PLACE BET
    // =========================
    public void placeBet(long roundId, long gamePlayerId, int value, int order) {

        String sql = """
            INSERT INTO oh_hell.bets
            (round_id, game_player_id, bet_value, bet_order)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ps.setLong(2, gamePlayerId);
            ps.setInt(3, value);
            ps.setInt(4, order);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // CHECKS
    // =========================
    public boolean hasBet(long roundId, long gamePlayerId) {

        String sql = """
            SELECT 1
            FROM oh_hell.bets
            WHERE round_id = ? AND game_player_id = ?
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ps.setLong(2, gamePlayerId);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

        String sql = """
            SELECT COALESCE(SUM(bet_value), 0)
            FROM oh_hell.bets
            WHERE round_id = ?
        """;

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

    public int nextBetOrder(long roundId) {

        String sql = """
            SELECT COALESCE(MAX(bet_order), -1) + 1
            FROM oh_hell.bets
            WHERE round_id = ?
        """;

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

    // =========================
    // PARA CIERRE DE RONDA
    // =========================
    public List<Long> getGamePlayersForRound(long roundId) {

        String sql = """
            SELECT game_player_id
            FROM oh_hell.bets
            WHERE round_id = ?
            ORDER BY bet_order
        """;

        List<Long> list = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getLong("game_player_id"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public int getBet(long roundId, long gamePlayerId) {

        String sql = """
            SELECT bet_value
            FROM oh_hell.bets
            WHERE round_id = ? AND game_player_id = ?
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ps.setLong(2, gamePlayerId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("Apuesta no encontrada");
            }

            return rs.getInt("bet_value");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public record BetRow(
            long gamePlayerId,
            int betValue,
            int order
    ) {}


    public List<BetRow> getBetsForRound(long roundId) {

        String sql = """
        SELECT game_player_id, bet_value, bet_order
        FROM oh_hell.bets
        WHERE round_id = ?
        ORDER BY bet_order
    """;

        List<BetRow> list = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new BetRow(
                        rs.getLong("game_player_id"),
                        rs.getInt("bet_value"),
                        rs.getInt("bet_order")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

}
