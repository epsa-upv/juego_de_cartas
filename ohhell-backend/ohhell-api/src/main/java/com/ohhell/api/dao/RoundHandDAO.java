package com.ohhell.api.dao;

import com.ohhell.api.db.Database;

import java.sql.*;

public class RoundHandDAO {

    public String getLeadSuit(long roundId) {
        // El lead suit es el palo de la primera carta jugada en la baza actual
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     """
                     SELECT SUBSTRING(card FROM 3) as suit
                     FROM oh_hell.round_plays
                     WHERE round_id = ?
                     ORDER BY play_order ASC
                     LIMIT 1
                     """)) {
            ps.setLong(1, roundId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("suit") : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // setLeadSuit y clearLeadSuit eliminados - el lead suit se calcula desde round_plays
}
