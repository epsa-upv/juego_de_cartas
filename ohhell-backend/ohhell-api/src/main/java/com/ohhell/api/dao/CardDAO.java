package com.ohhell.api.dao;

import com.ohhell.api.db.Database;

import java.sql.*;

public class CardDAO {

    public int findCardId(String rankEnum, String suitEnum) {

        String sql = """
            SELECT id
            FROM oh_hell.cards
            WHERE rank = ?::card_rank
              AND suit = ?::card_suit
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, rankEnum);
            ps.setString(2, suitEnum);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException(
                        "Carta no existe en catálogo: " + rankEnum + "_" + suitEnum
                );
            }

            return rs.getInt("id");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSuitById(int cardId) {

        String sql = """
            SELECT suit::text
            FROM oh_hell.cards
            WHERE id = ?
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, cardId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getString(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
