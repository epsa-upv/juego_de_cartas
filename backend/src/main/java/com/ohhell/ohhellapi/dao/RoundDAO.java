package com.ohhell.ohhellapi.dao;

import com.ohhell.ohhellapi.models.Round;
import com.ohhell.ohhellapi.models.Card;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RoundDAO - Data Access Object para la entidad Round
 *
 * Oh Hell! Card Game - UPV
 * Autor: Tomás Criado García
 *
 * Gestiona todas las operaciones CRUD con la tabla rounds en PostgreSQL
 */
public class RoundDAO {

    /**
     * Obtiene todas las rondas de una partida
     *
     * @param gameId ID de la partida
     * @return Lista de rondas de la partida
     * @throws SQLException si hay error en la consulta
     */
    public List<Round> getRoundsByGameId(Long gameId) throws SQLException {
        List<Round> rounds = new ArrayList<>();
        String query = "SELECT * FROM rounds WHERE game_id = ? ORDER BY round_number";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, gameId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rounds.add(mapResultSetToRound(rs));
                }
            }
        }

        return rounds;
    }

    /**
     * Obtiene una ronda por su ID
     *
     * @param id ID de la ronda
     * @return Round objeto con los datos de la ronda, o null si no existe
     * @throws SQLException si hay error en la consulta
     */
    public Round getRoundById(Long id) throws SQLException {
        String query = "SELECT * FROM rounds WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRound(rs);
                }
            }
        }

        return null;
    }

    /**
     * Crea una nueva ronda en la base de datos
     *
     * @param round Objeto Round con los datos de la nueva ronda
     * @return Round objeto con el ID asignado
     * @throws SQLException si hay error en la inserción
     */
    public Round createRound(Round round) throws SQLException {
        String query = "INSERT INTO rounds (game_id, round_number, num_cards, trump_suit, trump_rank, dealer_id, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, round.getGameId());
            pstmt.setInt(2, round.getRoundNumber());
            pstmt.setInt(3, round.getNumCards());

            if (round.getTrump() != null) {
                pstmt.setString(4, round.getTrump().getSuit());
                pstmt.setString(5, round.getTrump().getRank());
            } else {
                pstmt.setNull(4, Types.VARCHAR);
                pstmt.setNull(5, Types.VARCHAR);
            }

            if (round.getDealerId() != null) {
                pstmt.setLong(6, round.getDealerId());
            } else {
                pstmt.setNull(6, Types.BIGINT);
            }

            pstmt.setString(7, "BIDDING");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    round.setId(rs.getLong("id"));
                    return round;
                }
            }
        }

        throw new SQLException("Error al crear ronda: No se obtuvo ID");
    }

    /**
     * Actualiza los datos de una ronda existente
     *
     * @param round Objeto Round con los datos actualizados
     * @return true si se actualizó correctamente, false si no existe
     * @throws SQLException si hay error en la actualización
     */
    public boolean updateRound(Round round) throws SQLException {
        String query = "UPDATE rounds SET trump_suit = ?, trump_rank = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (round.getTrump() != null) {
                pstmt.setString(1, round.getTrump().getSuit());
                pstmt.setString(2, round.getTrump().getRank());
            } else {
                pstmt.setNull(1, Types.VARCHAR);
                pstmt.setNull(2, Types.VARCHAR);
            }

            pstmt.setString(3, round.getStatus());
            pstmt.setLong(4, round.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Actualiza el estado de una ronda
     *
     * @param roundId ID de la ronda
     * @param status Nuevo estado
     * @return true si se actualizó correctamente
     * @throws SQLException si hay error en la actualización
     */
    public boolean updateRoundStatus(Long roundId, String status) throws SQLException {
        String query = "UPDATE rounds SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status);
            pstmt.setLong(2, roundId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Elimina una ronda de la base de datos
     *
     * @param id ID de la ronda a eliminar
     * @return true si se eliminó correctamente, false si no existe
     * @throws SQLException si hay error en la eliminación
     */
    public boolean deleteRound(Long id) throws SQLException {
        String query = "DELETE FROM rounds WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Obtiene la ronda actual de una partida
     *
     * @param gameId ID de la partida
     * @return Round objeto de la ronda actual, o null si no hay
     * @throws SQLException si hay error en la consulta
     */
    public Round getCurrentRound(Long gameId) throws SQLException {
        String query = "SELECT * FROM rounds WHERE game_id = ? AND status IN ('BIDDING', 'PLAYING') " +
                "ORDER BY round_number DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, gameId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRound(rs);
                }
            }
        }

        return null;
    }

    /**
     * Cuenta el número de rondas de una partida
     *
     * @param gameId ID de la partida
     * @return Número de rondas
     * @throws SQLException si hay error en la consulta
     */
    public int countRoundsInGame(Long gameId) throws SQLException {
        String query = "SELECT COUNT(*) as round_count FROM rounds WHERE game_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, gameId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("round_count");
                }
            }
        }

        return 0;
    }

    /**
     * Mapea un ResultSet a un objeto Round
     *
     * @param rs ResultSet con los datos de la ronda
     * @return Round objeto mapeado
     * @throws SQLException si hay error al leer los datos
     */
    private Round mapResultSetToRound(ResultSet rs) throws SQLException {
        Round round = new Round();
        round.setId(rs.getLong("id"));
        round.setGameId(rs.getLong("game_id"));
        round.setRoundNumber(rs.getInt("round_number"));
        round.setNumCards(rs.getInt("num_cards"));

        String trumpSuit = rs.getString("trump_suit");
        String trumpRank = rs.getString("trump_rank");

        if (trumpSuit != null && trumpRank != null) {
            Card trump = new Card(
                    Card.Suit.valueOf(trumpSuit).toString().toString(),
                    Card.Rank.valueOf(trumpRank).toString().toString()
            );
            round.setTrump(trump);
        }

        Long dealerId = rs.getLong("dealer_id");
        if (!rs.wasNull()) {
            round.setDealerId(dealerId);
        }

        round.setStatus(rs.getString("status"));

        return round;
    }
    /**
     * Obtiene el número de la última ronda de una partida
     *
     * @param gameId ID de la partida
     * @return Número de la última ronda, o 0 si no hay rondas
     * @throws SQLException si hay error en la consulta
     */
    public int getLastRoundNumber(Long gameId) throws SQLException {
        String query = "SELECT COALESCE(MAX(round_number), 0) as last_round FROM rounds WHERE game_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, gameId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("last_round");
                }
            }
        }

        return 0;
    }
    public Round findById(Long id) throws SQLException {
        return getRoundById(id);
    }

    public Round save(Round round) throws SQLException {
        return createRound(round);
    }
    /**
     * Obtiene la última ronda completada de una partida
     *
     * @param gameId ID de la partida
     * @return Round objeto de la última ronda completada, o null si no hay
     * @throws SQLException si hay error en la consulta
     */
    public Round getLastCompletedRound(Long gameId) throws SQLException {
        String query = "SELECT * FROM rounds WHERE game_id = ? AND status = 'COMPLETED' " +
                "ORDER BY round_number DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, gameId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRound(rs);
                }
            }
        }

        return null;
    }
}