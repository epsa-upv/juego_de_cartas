package com.ohhell.api.dao;

import com.ohhell.api.db.Database;
import com.ohhell.api.models.Game;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.*;

public class GameDAO {

    public Game create(String title) {

        String sql = """
            INSERT INTO oh_hell.games (code, title)
            VALUES (?, ?)
            RETURNING
                id,
                code,
                title,
                status,
                starting_cards,
                max_rounds,
                created_at,
                started_at
        """;

        String code = generateCode();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setString(2, title);

            ResultSet rs = ps.executeQuery();
            rs.next();

            Game game = new Game();
            game.setId((UUID) rs.getObject("id"));
            game.setCode(rs.getString("code"));
            game.setTitle(rs.getString("title"));
            game.setStatus(rs.getString("status"));
            game.setStartingCards(rs.getInt("starting_cards"));
            game.setMaxRounds(rs.getInt("max_rounds"));
            game.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            game.setStartedAt(rs.getObject("started_at", OffsetDateTime.class));

            return game;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Game findByCode(String code) {

        String sql = """
            SELECT
                id,
                code,
                title,
                status,
                starting_cards,
                max_rounds,
                created_at,
                started_at
            FROM oh_hell.games
            WHERE code = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            Game game = new Game();
            game.setId((UUID) rs.getObject("id"));
            game.setCode(rs.getString("code"));
            game.setTitle(rs.getString("title"));
            game.setStatus(rs.getString("status"));
            game.setStartingCards(rs.getInt("starting_cards"));
            game.setMaxRounds(rs.getInt("max_rounds"));
            game.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            game.setStartedAt(rs.getObject("started_at", OffsetDateTime.class));

            return game;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void markStarted(UUID gameId) {

        String sql = """
            UPDATE oh_hell.games
            SET status = 'PLAYING', started_at = now()
            WHERE id = ? AND status = 'WAITING'
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ✅ PASO 9 — FIN DE PARTIDA
    public void markFinished(UUID gameId) {

        String sql = """
            UPDATE oh_hell.games
            SET status = 'FINISHED'
            WHERE id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateCode() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }

    // =========================
    // FIND
    // =========================

    public List<Map<String, Object>> findAvailableGames(String status) {
        String sql = """
        SELECT id, code, title, status, starting_cards, created_at
        FROM oh_hell.games
        WHERE status = ?
        ORDER BY created_at DESC
        LIMIT 10
    """;

        List<Map<String, Object>> games = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> gameMap = new HashMap<>();
                gameMap.put("id", rs.getObject("id"));
                gameMap.put("code", rs.getString("code"));
                gameMap.put("title", rs.getString("title"));
                gameMap.put("status", rs.getString("status"));
                gameMap.put("startingCards", rs.getInt("starting_cards"));
                gameMap.put("createdAt", rs.getObject("created_at"));

                games.add(gameMap);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return games;
    }

}
