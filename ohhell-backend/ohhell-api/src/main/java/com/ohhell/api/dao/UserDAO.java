package com.ohhell.api.dao;

import com.ohhell.api.db.Database;
import com.ohhell.api.models.User;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public class UserDAO {

    public Optional<User> findByEmail(String email) {
        String sql = """
            SELECT id, email, password_hash, created_at
            FROM oh_hell.users
            WHERE email = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public User create(String email, String passwordHash) {
        String sql = """
            INSERT INTO oh_hell.users (email, password_hash)
            VALUES (?, ?)
            RETURNING id, created_at
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, passwordHash);

            ResultSet rs = ps.executeQuery();
            rs.next();

            User user = new User();
            user.setId((UUID) rs.getObject("id"));
            user.setEmail(email);
            user.setPasswordHash(passwordHash);
            user.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));

            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
                (UUID) rs.getObject("id"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getObject("created_at", OffsetDateTime.class)
        );
    }
}
