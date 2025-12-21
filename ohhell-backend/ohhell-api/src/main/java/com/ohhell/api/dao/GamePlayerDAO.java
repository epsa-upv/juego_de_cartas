package com.ohhell.api.dao;

import com.ohhell.api.db.Database;
import com.ohhell.api.models.GamePlayerView;

import java.sql.*;
import java.util.*;

public class GamePlayerDAO {

    private static final int MAX_PLAYERS = 4;

    // =========================
    // HOST
    // =========================
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

    // =========================
    // JOIN GAME
    // =========================
    public void joinGame(UUID gameId, UUID playerId) {

        if (countPlayers(gameId) >= MAX_PLAYERS) {
            throw new RuntimeException("La partida está llena");
        }

        int seat = nextSeat(gameId);

        String sql = """
            INSERT INTO oh_hell.game_players
            (game_id, player_id, seat_position, is_host, status)
            VALUES (?, ?, ?, false, CAST('PENDING' AS player_status))
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ps.setObject(2, playerId);
            ps.setInt(3, seat);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // READY / UNREADY
    // =========================
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

    // =========================
    // LOBBY
    // =========================
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

    // =========================
    // CHECKS
    // =========================
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

    public int countPlayers(UUID gameId) {

        String sql = """
            SELECT COUNT(*)
            FROM oh_hell.game_players
            WHERE game_id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int nextSeat(UUID gameId) {

        String sql = """
            SELECT seat_position
            FROM oh_hell.game_players
            WHERE game_id = ?
        """;

        Set<Integer> usedSeats = new HashSet<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                usedSeats.add(rs.getInt("seat_position"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < MAX_PLAYERS; i++) {
            if (!usedSeats.contains(i)) {
                return i;
            }
        }

        throw new RuntimeException("No hay asientos disponibles");
    }

    public int getSeat(UUID gameId, UUID playerId) {
        String sql = """
        SELECT seat_position
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

            return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getSeatByGamePlayerId(long gamePlayerId) {

        String sql = """
        SELECT seat_position
        FROM oh_hell.game_players
        WHERE id = ?
    """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, gamePlayerId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("GamePlayer no encontrado");
            }

            return rs.getInt("seat_position");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getPlayerIdBySeat(UUID gameId, int seat) {
        String sql = """
        SELECT player_id
        FROM oh_hell.game_players
        WHERE game_id = ? AND seat_position = ?
    """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ps.setInt(2, seat);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new RuntimeException("Jugador no encontrado");

            return (UUID) rs.getObject("player_id");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UUID getPlayerIdByGamePlayerId(long gpId) {
        String sql = """
        SELECT player_id
        FROM oh_hell.game_players
        WHERE id = ?
    """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, gpId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return (UUID) rs.getObject("player_id");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public record PlayerInfo(UUID playerId, String nickname) {}

    public PlayerInfo getPlayerInfo(long gamePlayerId) {

        String sql = """
        SELECT p.id, p.nickname
        FROM oh_hell.game_players gp
        JOIN oh_hell.players p ON p.id = gp.player_id
        WHERE gp.id = ?
    """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, gamePlayerId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("GamePlayer no encontrado");
            }

            return new PlayerInfo(
                    rs.getObject("id", UUID.class),
                    rs.getString("nickname")
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
// GET GAME PLAYERS
// =========================
    public List<PlayerInfo> getGamePlayers(UUID gameId) {
        String sql = """
        SELECT p.id, p.nickname
        FROM oh_hell.game_players gp
        JOIN oh_hell.players p ON p.id = gp.player_id
        WHERE gp.game_id = ?
        ORDER BY gp.seat_position
    """;

        List<PlayerInfo> list = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new PlayerInfo(
                        (UUID) rs.getObject("id"),
                        rs.getString("nickname")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public List<Long> getGamePlayerIds(UUID gameId) {
        String sql = """
        SELECT id
        FROM oh_hell.game_players
        WHERE game_id = ?
        ORDER BY seat_position
    """;

        List<Long> ids = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ids.add(rs.getLong("id"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ids;
    }
}
