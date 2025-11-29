package com.ohhell.ohhellapi.dao;

import com.ohhell.ohhellapi.models.Trick;
import com.ohhell.ohhellapi.models.Card;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TrickDAO - Data Access Object para la entidad Trick
 *
 * Oh Hell! Card Game - UPV
 * Autor: Tomás Criado García
 *
 * Gestiona todas las operaciones CRUD con la tabla tricks en PostgreSQL
 */
public class TrickDAO {

    /**
     * Obtiene todas las bazas de una ronda
     *
     * @param roundId ID de la ronda
     * @return Lista de bazas de la ronda
     * @throws SQLException si hay error en la consulta
     */
    public List<Trick> getTricksByRoundId(Long roundId) throws SQLException {
        List<Trick> tricks = new ArrayList<>();
        String query = "SELECT * FROM tricks WHERE round_id = ? ORDER BY trick_number";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, roundId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tricks.add(mapResultSetToTrick(rs));
                }
            }
        }

        return tricks;
    }

    /**
     * Obtiene una baza por su ID
     *
     * @param id ID de la baza
     * @return Trick objeto con los datos de la baza, o null si no existe
     * @throws SQLException si hay error en la consulta
     */
    public Trick getTrickById(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de baza inválido: " + id);
        }

        String query = "SELECT * FROM tricks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrick(rs);
                }
            }
        }

        return null;
    }

    /**
     * Obtiene una baza específica de una ronda por su número
     *
     * @param roundId ID de la ronda
     * @param trickNumber Número de la baza
     * @return Trick objeto, o null si no existe
     * @throws SQLException si hay error en la consulta
     */
    public Trick getTrickByNumber(Long roundId, int trickNumber) throws SQLException {
        String query = "SELECT * FROM tricks WHERE round_id = ? AND trick_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, roundId);
            pstmt.setInt(2, trickNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrick(rs);
                }
            }
        }

        return null;
    }

    /**
     * Crea una nueva baza en la base de datos
     *
     * @param trick Objeto Trick con los datos de la nueva baza
     * @return Trick objeto con el ID asignado
     * @throws SQLException si hay error en la inserción
     */
    public Trick createTrick(Trick trick) throws SQLException {
        String query = "INSERT INTO tricks (round_id, trick_number, lead_suit, lead_rank, winner_id, completed) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, trick.getRoundId());
            pstmt.setInt(2, trick.getTrickNumber());

            // ✅ CORREGIDO: Ahora guarda lead_suit y lead_rank correctamente
            if (trick.getLeadCard() != null) {
                pstmt.setString(3, trick.getLeadCard().getSuit());
                pstmt.setString(4, trick.getLeadCard().getRank());
            } else if (trick.getLeadSuit() != null) {
                // Si solo tienes el suit guardado en el modelo actual
                pstmt.setString(3, trick.getLeadSuit());
                pstmt.setNull(4, Types.VARCHAR);
            } else {
                pstmt.setNull(3, Types.VARCHAR);
                pstmt.setNull(4, Types.VARCHAR);
            }

            if (trick.getWinnerId() != null) {
                pstmt.setLong(5, trick.getWinnerId());
            } else {
                pstmt.setNull(5, Types.BIGINT);
            }

            pstmt.setBoolean(6, trick.isCompleted());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    trick.setId(rs.getLong("id"));
                    return trick;
                }
            }
        }

        throw new SQLException("Error al crear baza: No se obtuvo ID");
    }

    /**
     * Actualiza los datos de una baza existente
     *
     * @param trick Objeto Trick con los datos actualizados
     * @return true si se actualizó correctamente, false si no existe
     * @throws SQLException si hay error en la actualización
     */
    public boolean updateTrick(Trick trick) throws SQLException {
        String query = "UPDATE tricks SET lead_suit = ?, lead_rank = ?, winner_id = ?, completed = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (trick.getLeadCard() != null) {
                pstmt.setString(1, trick.getLeadCard().getSuit());
                pstmt.setString(2, trick.getLeadCard().getRank());
            } else if (trick.getLeadSuit() != null) {
                pstmt.setString(1, trick.getLeadSuit());
                pstmt.setNull(2, Types.VARCHAR);
            } else {
                pstmt.setNull(1, Types.VARCHAR);
                pstmt.setNull(2, Types.VARCHAR);
            }

            if (trick.getWinnerId() != null) {
                pstmt.setLong(3, trick.getWinnerId());
            } else {
                pstmt.setNull(3, Types.BIGINT);
            }

            pstmt.setBoolean(4, trick.isCompleted());
            pstmt.setLong(5, trick.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Actualiza la carta líder de una baza
     *
     * @param trickId ID de la baza
     * @param leadCard Carta líder
     * @return true si se actualizó correctamente
     * @throws SQLException si hay error en la actualización
     */
    public boolean updateLeadCard(Long trickId, Card leadCard) throws SQLException {
        String query = "UPDATE tricks SET lead_suit = ?, lead_rank = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            if (leadCard != null) {
                pstmt.setString(1, leadCard.getSuit());
                pstmt.setString(2, leadCard.getRank());
            } else {
                pstmt.setNull(1, Types.VARCHAR);
                pstmt.setNull(2, Types.VARCHAR);
            }

            pstmt.setLong(3, trickId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Marca una baza como completada y asigna el ganador
     *
     * @param trickId ID de la baza
     * @param winnerId ID del ganador
     * @return true si se actualizó correctamente
     * @throws SQLException si hay error en la actualización
     */
    public boolean completeTrick(Long trickId, Long winnerId) throws SQLException {
        String query = "UPDATE tricks SET winner_id = ?, completed = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, winnerId);
            pstmt.setBoolean(2, true);
            pstmt.setLong(3, trickId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Elimina una baza de la base de datos
     *
     * @param id ID de la baza a eliminar
     * @return true si se eliminó correctamente, false si no existe
     * @throws SQLException si hay error en la eliminación
     */
    public boolean deleteTrick(Long id) throws SQLException {
        String query = "DELETE FROM tricks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Elimina todas las bazas de una ronda
     *
     * @param roundId ID de la ronda
     * @return Número de bazas eliminadas
     * @throws SQLException si hay error en la eliminación
     */
    public int deleteTricksByRoundId(Long roundId) throws SQLException {
        String query = "DELETE FROM tricks WHERE round_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, roundId);
            return pstmt.executeUpdate();
        }
    }

    /**
     * Obtiene la baza actual (no completada) de una ronda
     *
     * @param roundId ID de la ronda
     * @return Trick objeto de la baza actual, o null si no hay
     * @throws SQLException si hay error en la consulta
     */
    public Trick getCurrentTrick(Long roundId) throws SQLException {
        String query = "SELECT * FROM tricks WHERE round_id = ? AND completed = false " +
                "ORDER BY trick_number DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, roundId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrick(rs);
                }
            }
        }

        return null;
    }

    /**
     * Obtiene la última baza completada de una ronda
     *
     * @param roundId ID de la ronda
     * @return Trick objeto de la última baza completada, o null si no hay
     * @throws SQLException si hay error en la consulta
     */
    public Trick getLastCompletedTrick(Long roundId) throws SQLException {
        String query = "SELECT * FROM tricks WHERE round_id = ? AND completed = true " +
                "ORDER BY trick_number DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, roundId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrick(rs);
                }
            }
        }

        return null;
    }

    /**
     * Cuenta el número de bazas completadas en una ronda
     *
     * @param roundId ID de la ronda
     * @return Número de bazas completadas
     * @throws SQLException si hay error en la consulta
     */
    public int countCompletedTricksInRound(Long roundId) throws SQLException {
        String query = "SELECT COUNT(*) as trick_count FROM tricks WHERE round_id = ? AND completed = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, roundId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("trick_count");
                }
            }
        }

        return 0;
    }

    /**
     * Cuenta el número total de bazas en una ronda
     *
     * @param roundId ID de la ronda
     * @return Número total de bazas
     * @throws SQLException si hay error en la consulta
     */
    public int countTotalTricksInRound(Long roundId) throws SQLException {
        String query = "SELECT COUNT(*) as trick_count FROM tricks WHERE round_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, roundId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("trick_count");
                }
            }
        }

        return 0;
    }

    /**
     * Obtiene el número de bazas ganadas por un jugador en una ronda
     *
     * @param playerId ID del jugador
     * @param roundId ID de la ronda
     * @return Número de bazas ganadas
     * @throws SQLException si hay error en la consulta
     */
    public int getTricksWonByPlayer(Long playerId, Long roundId) throws SQLException {
        String query = "SELECT COUNT(*) as tricks_won FROM tricks " +
                "WHERE round_id = ? AND winner_id = ? AND completed = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, roundId);
            pstmt.setLong(2, playerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("tricks_won");
                }
            }
        }

        return 0;
    }

    /**
     * Verifica si todas las bazas de una ronda están completadas
     *
     * @param roundId ID de la ronda
     * @param expectedTricks Número esperado de bazas
     * @return true si todas las bazas están completadas
     * @throws SQLException si hay error en la consulta
     */
    public boolean allTricksCompleted(Long roundId, int expectedTricks) throws SQLException {
        int completedCount = countCompletedTricksInRound(roundId);
        return completedCount == expectedTricks;
    }

    /**
     * Mapea un ResultSet a un objeto Trick
     *
     * @param rs ResultSet con los datos de la baza
     * @return Trick objeto mapeado
     * @throws SQLException si hay error al leer los datos
     */
    private Trick mapResultSetToTrick(ResultSet rs) throws SQLException {
        Trick trick = new Trick();
        trick.setId(rs.getLong("id"));
        trick.setRoundId(rs.getLong("round_id"));
        trick.setTrickNumber(rs.getInt("trick_number"));

        // ✅ CORREGIDO: Mapear lead_suit y lead_rank correctamente
        String leadSuit = rs.getString("lead_suit");
        String leadRank = rs.getString("lead_rank");

        if (leadSuit != null && leadRank != null) {
            // Crear la carta líder completa
            Card leadCard = new Card(leadSuit, leadRank);
            trick.setLeadCard(leadCard);
        } else if (leadSuit != null) {
            // Si solo hay suit (compatibilidad con modelo anterior)
            trick.setLeadSuit(leadSuit);
        }

        Long winnerId = rs.getLong("winner_id");
        if (!rs.wasNull()) {
            trick.setWinnerId(winnerId);
        }

        trick.setCompleted(rs.getBoolean("completed"));

        return trick;
    }
}