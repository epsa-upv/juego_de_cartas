package com.ohhell.api.dao;

import com.ohhell.api.db.Database;
import com.ohhell.api.models.GamePlayerView;

import java.sql.*;
import java.util.*;

public class GamePlayerDAO {

    private static final int MAX_PLAYERS = 4;

    public void addHost(UUID gameId, UUID playerId) {

        String sql = """
            INSERT INTO oh_hell.game_players
            (game_id, player_id, seat_position, is_host, status)
            VALUES (?, ?, 0, true, CAST('ACTIVE' AS player_status))
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ps.setObject(2, playerId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setReady(UUID gameId, UUID playerId, boolean ready) {

        String sql = """
            UPDATE oh_hell.game_players
            SET status = CAST(? AS player_status)
            WHERE game_id = ? AND player_id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ready ? "ACTIVE" : "PENDING");
            ps.setObject(2, gameId);
            ps.setObject(3, playerId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<GamePlayerView> getLobbyPlayers(UUID gameId) {

        String sql = """
            SELECT
                gp.player_id,
                p.nickname,
                gp.seat_position,
                gp.is_host,
                gp.status
            FROM oh_hell.game_players gp
            JOIN oh_hell.players p ON p.id = gp.player_id
            WHERE gp.game_id = ?
            ORDER BY gp.seat_position
        """;

        List<GamePlayerView> list = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new GamePlayerView(
                        (UUID) rs.getObject("player_id"),
                        rs.getString("nickname"),
                        rs.getInt("seat_position"),
                        rs.getBoolean("is_host"),
                        "ACTIVE".equals(rs.getString("status"))
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public boolean isHost(UUID gameId, UUID playerId) {

        String sql = """
            SELECT is_host
            FROM oh_hell.game_players
            WHERE game_id = ? AND player_id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ps.setObject(2, playerId);

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getBoolean("is_host");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean areAllPlayersReady(UUID gameId) {

        String sql = """
            SELECT COUNT(*) = SUM(
                CASE WHEN status = 'ACTIVE' THEN 1 ELSE 0 END
            )
            FROM oh_hell.game_players
            WHERE game_id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getBoolean(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getGamePlayerId(UUID gameId, UUID playerId) {

        String sql = """
        SELECT id
        FROM oh_hell.game_players
        WHERE game_id = ? AND player_id = ?
    """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ps.setObject(2, playerId);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("Jugador no está en la partida");
            }

            return rs.getLong("id");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
