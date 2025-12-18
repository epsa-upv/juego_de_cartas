package com.ohhell.api.dao;

import com.ohhell.api.db.Database;
import com.ohhell.api.models.Player;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public class PlayerDAO {

    public Optional<Player> findByUserId(UUID userId) {
        String sql = """
            SELECT id, user_id, nickname, created_at
            FROM oh_hell.players
            WHERE user_id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, userId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public Player create(UUID userId, String nickname) {
        String sql = """
            INSERT INTO oh_hell.players (user_id, nickname)
            VALUES (?, ?)
            RETURNING id, created_at
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, userId);
            ps.setString(2, nickname);

            ResultSet rs = ps.executeQuery();
            rs.next();

            Player p = new Player();
            p.setId((UUID) rs.getObject("id"));
            p.setUserId(userId);
            p.setNickname(nickname);
            p.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));

            return p;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Player map(ResultSet rs) throws SQLException {
        return new Player(
                (UUID) rs.getObject("id"),
                (UUID) rs.getObject("user_id"),
                rs.getString("nickname"),
                rs.getObject("created_at", OffsetDateTime.class)
        );
    }
}
