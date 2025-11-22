package com.ohhell.ohhellapi.dao;

import com.ohhell.ohhellapi.models.Bid;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * BidDAO - Data Access Object para la entidad Bid
 * 
 * Oh Hell! Card Game - UPV
 * Autor: Tomás Criado García
 * 
 * Gestiona todas las operaciones CRUD con la tabla bids en PostgreSQL
 */
public class BidDAO {
    
    /**
     * Obtiene todas las apuestas de una ronda
     * 
     * @param roundId ID de la ronda
     * @return Lista de apuestas de la ronda
     * @throws SQLException si hay error en la consulta
     */
    public List<Bid> getBidsByRoundId(Long roundId) throws SQLException {
        List<Bid> bids = new ArrayList<>();
        String query = "SELECT * FROM bids WHERE round_id = ? ORDER BY timestamp";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, roundId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bids.add(mapResultSetToBid(rs));
                }
            }
        }
        
        return bids;
    }
    
    /**
     * Obtiene una apuesta por su ID
     * 
     * @param id ID de la apuesta
     * @return Bid objeto con los datos de la apuesta, o null si no existe
     * @throws SQLException si hay error en la consulta
     */
    public Bid getBidById(Long id) throws SQLException {
        String query = "SELECT * FROM bids WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBid(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Obtiene la apuesta de un jugador en una ronda específica
     * 
     * @param playerId ID del jugador
     * @param roundId ID de la ronda
     * @return Bid objeto con la apuesta, o null si no existe
     * @throws SQLException si hay error en la consulta
     */
    public Bid getBidByPlayerAndRound(Long playerId, Long roundId) throws SQLException {
        String query = "SELECT * FROM bids WHERE player_id = ? AND round_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, playerId);
            pstmt.setLong(2, roundId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBid(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Crea una nueva apuesta en la base de datos
     * 
     * @param bid Objeto Bid con los datos de la nueva apuesta
     * @return Bid objeto con el ID asignado
     * @throws SQLException si hay error en la inserción
     */
    public Bid createBid(Bid bid) throws SQLException {
        String query = "INSERT INTO bids (player_id, round_id, bid_amount, tricks_won, timestamp) " +
                      "VALUES (?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, bid.getPlayerId());
            pstmt.setLong(2, bid.getRoundId());
            pstmt.setInt(3, bid.getBidAmount());
            pstmt.setInt(4, bid.getTricksWon());
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    bid.setId(rs.getLong("id"));
                    bid.setTimestamp(LocalDateTime.now());
                    return bid;
                }
            }
        }
        
        throw new SQLException("Error al crear apuesta: No se obtuvo ID");
    }
    
    /**
     * Actualiza los datos de una apuesta existente
     * 
     * @param bid Objeto Bid con los datos actualizados
     * @return true si se actualizó correctamente, false si no existe
     * @throws SQLException si hay error en la actualización
     */
    public boolean updateBid(Bid bid) throws SQLException {
        String query = "UPDATE bids SET bid_amount = ?, tricks_won = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, bid.getBidAmount());
            pstmt.setInt(2, bid.getTricksWon());
            pstmt.setLong(3, bid.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Incrementa el contador de bazas ganadas de una apuesta
     * 
     * @param bidId ID de la apuesta
     * @return true si se actualizó correctamente
     * @throws SQLException si hay error en la actualización
     */
    public boolean incrementTricksWon(Long bidId) throws SQLException {
        String query = "UPDATE bids SET tricks_won = tricks_won + 1 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, bidId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Elimina una apuesta de la base de datos
     * 
     * @param id ID de la apuesta a eliminar
     * @return true si se eliminó correctamente, false si no existe
     * @throws SQLException si hay error en la eliminación
     */
    public boolean deleteBid(Long id) throws SQLException {
        String query = "DELETE FROM bids WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Calcula la suma total de apuestas en una ronda
     * 
     * @param roundId ID de la ronda
     * @return Suma total de las apuestas
     * @throws SQLException si hay error en la consulta
     */
    public int getTotalBidsForRound(Long roundId) throws SQLException {
        String query = "SELECT COALESCE(SUM(bid_amount), 0) as total FROM bids WHERE round_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, roundId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Cuenta el número de apuestas en una ronda
     * 
     * @param roundId ID de la ronda
     * @return Número de apuestas
     * @throws SQLException si hay error en la consulta
     */
    public int countBidsInRound(Long roundId) throws SQLException {
        String query = "SELECT COUNT(*) as bid_count FROM bids WHERE round_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, roundId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("bid_count");
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Verifica si todos los jugadores de una ronda han apostado
     * 
     * @param roundId ID de la ronda
     * @param expectedPlayers Número esperado de jugadores
     * @return true si todos han apostado
     * @throws SQLException si hay error en la consulta
     */
    public boolean allPlayersHaveBid(Long roundId, int expectedPlayers) throws SQLException {
        int bidCount = countBidsInRound(roundId);
        return bidCount == expectedPlayers;
    }
    
    /**
     * Mapea un ResultSet a un objeto Bid
     * 
     * @param rs ResultSet con los datos de la apuesta
     * @return Bid objeto mapeado
     * @throws SQLException si hay error al leer los datos
     */
    private Bid mapResultSetToBid(ResultSet rs) throws SQLException {
        Bid bid = new Bid();
        bid.setId(rs.getLong("id"));
        bid.setPlayerId(rs.getLong("player_id"));
        bid.setRoundId(rs.getLong("round_id"));
        bid.setBidAmount(rs.getInt("bid_amount"));
        bid.setTricksWon(rs.getInt("tricks_won"));
        
        Timestamp timestamp = rs.getTimestamp("timestamp");
        if (timestamp != null) {
            bid.setTimestamp(timestamp.toLocalDateTime());
        }
        
        return bid;
    }
}
