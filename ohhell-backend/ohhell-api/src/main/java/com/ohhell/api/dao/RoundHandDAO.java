package com.ohhell.api.dao;

import com.ohhell.api.db.Database;

import java.sql.*;

public class RoundHandDAO {

    public String getLeadSuit(long roundId) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT lead_suit FROM oh_hell.round_hands WHERE round_id = ?")) {
            ps.setLong(1, roundId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString(1) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLeadSuit(long roundId, String suit) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("""
                 INSERT INTO oh_hell.round_hands (round_id, lead_suit)
                 VALUES (?,?)
                 ON CONFLICT (round_id)
                 DO UPDATE SET lead_suit = EXCLUDED.lead_suit
             """)) {
            ps.setLong(1, roundId);
            ps.setString(2, suit);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearLeadSuit(long roundId) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM oh_hell.round_hands WHERE round_id = ?")) {
            ps.setLong(1, roundId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
