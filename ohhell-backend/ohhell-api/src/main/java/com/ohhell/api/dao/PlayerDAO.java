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
        // Limitar nickname a 40 caracteres (límite de la BD)
        if (nickname.length() > 40) {
            nickname = nickname.substring(0, 40);
        }

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

            if (!rs.next()) {
                throw new RuntimeException("No se pudo crear el Player");
            }

            Player p = new Player();
            p.setId((UUID) rs.getObject("id"));
            p.setUserId(userId);
            p.setNickname(nickname);
            p.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));

            return p;

        } catch (SQLException e) {
            // Mejorar mensajes de error
            String errorMsg = e.getMessage();
            if (errorMsg.contains("players_nickname_key")) {
                throw new RuntimeException("El nickname '" + nickname + "' ya está en uso");
            } else if (errorMsg.contains("players_user_id_key")) {
                throw new RuntimeException("Ya existe un Player para este usuario");
            } else {
                throw new RuntimeException("Error de base de datos: " + errorMsg);
            }
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
