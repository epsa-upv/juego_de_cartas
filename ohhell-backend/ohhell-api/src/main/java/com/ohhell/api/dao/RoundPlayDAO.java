package com.ohhell.api.dao;

import com.ohhell.api.db.Database;

import java.sql.*;
import java.util.*;

public class RoundPlayDAO {

    public record PlayedCard(long gamePlayerId, String card, int order) {}

    public int countPlays(long roundId) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) FROM oh_hell.round_plays WHERE round_id = ?")) {
            ps.setLong(1, roundId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void playCard(long roundId, long gpId, String card, int order) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO oh_hell.round_plays VALUES (DEFAULT,?,?,?,?)")) {
            ps.setLong(1, roundId);
            ps.setLong(2, gpId);
            ps.setString(3, card);
            ps.setInt(4, order);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PlayedCard> getPlays(long roundId) {
        List<PlayedCard> list = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT game_player_id, card, play_order FROM oh_hell.round_plays WHERE round_id = ? ORDER BY play_order")) {
            ps.setLong(1, roundId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new PlayedCard(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getInt(3)
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
