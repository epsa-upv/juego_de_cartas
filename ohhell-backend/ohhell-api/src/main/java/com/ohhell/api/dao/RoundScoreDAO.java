package com.ohhell.api.dao;

import com.ohhell.api.db.Database;

import java.sql.*;
import java.util.*;
import java.util.UUID;

public class RoundScoreDAO {

    // =========================
    // SAVE SCORE (PASO 9–10)
    // =========================
    public void saveScore(
            long roundId,
            long gamePlayerId,
            int tricks,
            int bet,
            int points
    ) {

        String sql = """
            INSERT INTO oh_hell.round_scores
            (round_id, game_player_id, lives_change, points_earned, notes)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ps.setLong(2, gamePlayerId);
            ps.setInt(3, tricks);
            ps.setInt(4, points);
            ps.setString(5, "bet=" + bet);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // READ SCORES (PASO 10)
    // =========================
    public static class ScoreRow {

        private final long gamePlayerId;
        private final int totalScore;
        private final int totalTricks;

        public ScoreRow(long gamePlayerId, int totalScore, int totalTricks) {
            this.gamePlayerId = gamePlayerId;
            this.totalScore = totalScore;
            this.totalTricks = totalTricks;
        }

        public long getGamePlayerId() {
            return gamePlayerId;
        }

        public int getTotalScore() {
            return totalScore;
        }

        public int getTotalTricks() {
            return totalTricks;
        }
    }

    public List<ScoreRow> getScoresForGame(UUID gameId) {

        String sql = """
            SELECT
                rs.game_player_id,
                SUM(rs.points_earned) AS total_score,
                SUM(rs.lives_change) AS total_tricks
            FROM oh_hell.round_scores rs
            JOIN oh_hell.rounds r ON r.id = rs.round_id
            WHERE r.game_id = ?
            GROUP BY rs.game_player_id
            ORDER BY total_score DESC
        """;

        List<ScoreRow> list = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new ScoreRow(
                        rs.getLong("game_player_id"),
                        rs.getInt("total_score"),
                        rs.getInt("total_tricks")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    // =========================
    // GET PLAYER TOTAL SCORE
    // =========================
    public int getPlayerTotalScore(UUID gameId, UUID playerId) {
        String sql = """
            SELECT COALESCE(SUM(rs.points_earned), 0)
            FROM oh_hell.round_scores rs
            JOIN oh_hell.rounds r ON r.id = rs.round_id
            JOIN oh_hell.game_players gp ON gp.id = rs.game_player_id
            WHERE r.game_id = ?
            AND gp.player_id = ?
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ps.setObject(2, playerId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // GET PLAYER TOTAL TRICKS
    // =========================
    public int getPlayerTotalTricks(UUID gameId, UUID playerId) {
        String sql = """
            SELECT COALESCE(SUM(rs.lives_change), 0)
            FROM oh_hell.round_scores rs
            JOIN oh_hell.rounds r ON r.id = rs.round_id
            JOIN oh_hell.game_players gp ON gp.id = rs.game_player_id
            WHERE r.game_id = ?
            AND gp.player_id = ?
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ps.setObject(2, playerId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}