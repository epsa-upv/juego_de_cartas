package com.ohhell.ohhellapi.dao;

import com.ohhell.ohhellapi.models.Player;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PlayerDAO - Data Access Object para la entidad Player
 *
 * Oh Hell! Card Game - UPV
 * Autor: Tomás Criado García
 *
 * Gestiona todas las operaciones CRUD con la tabla players en PostgreSQL
 */
public class PlayerDAO {

    /**
     * Obtiene todos los jugadores de la base de datos
     *
     * @return Lista de todos los jugadores
     * @throws SQLException si hay error en la consulta
     */
    public List<Player> getAllPlayers() throws SQLException {
        List<Player> players = new ArrayList<>();
        String query = "SELECT * FROM players ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Player player = mapResultSetToPlayer(rs);
                players.add(player);
            }
        }

        return players;
    }

    /**
     * Obtiene un jugador por su ID
     *
     * @param id ID del jugador
     * @return Player objeto con los datos del jugador, o null si no existe
     * @throws SQLException si hay error en la consulta
     */
    public Player getPlayerById(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de jugador inválido: " + id);
        }

        String query = "SELECT * FROM players WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlayer(rs);
                }
            }
        }

        return null;
    }

    /**
     * Obtiene un jugador por su nombre
     *
     * @param name Nombre del jugador
     * @return Player objeto con los datos del jugador, o null si no existe
     * @throws SQLException si hay error en la consulta
     */
    public Player getPlayerByName(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre de jugador inválido");
        }

        String query = "SELECT * FROM players WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlayer(rs);
                }
            }
        }

        return null;
    }

    /**
     * Crea un nuevo jugador en la base de datos
     *
     * @param player Objeto Player con los datos del nuevo jugador
     * @return Player objeto con el ID asignado
     * @throws SQLException si hay error en la inserción
     */
    public Player createPlayer(Player player) throws SQLException {
        String query = "INSERT INTO players (name, lives, status, is_dealer) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, player.getName());
            pstmt.setInt(2, player.getLives());
            pstmt.setString(3, player.getStatus().name());
            pstmt.setBoolean(4, player.isDealer());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    player.setId(rs.getLong("id"));
                    return player;
                }
            }
        }

        throw new SQLException("Error al crear jugador: No se obtuvo ID");
    }

    /**
     * Actualiza los datos de un jugador existente
     *
     * @param player Objeto Player con los datos actualizados
     * @return true si se actualizó correctamente, false si no existe
     * @throws SQLException si hay error en la actualización
     */
    public boolean updatePlayer(Player player) throws SQLException {
        String query = "UPDATE players SET name = ?, lives = ?, status = ?, is_dealer = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, player.getName());
            pstmt.setInt(2, player.getLives());
            pstmt.setString(3, player.getStatus().name());
            pstmt.setBoolean(4, player.isDealer());
            pstmt.setLong(5, player.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Elimina un jugador de la base de datos
     *
     * @param id ID del jugador a eliminar
     * @return true si se eliminó correctamente, false si no existe
     * @throws SQLException si hay error en la eliminación
     */
    public boolean deletePlayer(Long id) throws SQLException {
        String query = "DELETE FROM players WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Actualiza las vidas de un jugador
     *
     * @param playerId ID del jugador
     * @param lives Nuevas vidas
     * @return true si se actualizó correctamente
     * @throws SQLException si hay error en la actualización
     */
    public boolean updatePlayerLives(Long playerId, int lives) throws SQLException {
        String query = "UPDATE players SET lives = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, lives);
            pstmt.setLong(2, playerId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Decrementa una vida de un jugador
     *
     * @param playerId ID del jugador
     * @return true si se actualizó correctamente
     * @throws SQLException si hay error en la actualización
     */
    public boolean decrementPlayerLife(Long playerId) throws SQLException {
        String query = "UPDATE players SET lives = lives - 1 WHERE id = ? AND lives > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, playerId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Actualiza el estado de un jugador
     *
     * @param playerId ID del jugador
     * @param status Nuevo estado
     * @return true si se actualizó correctamente
     * @throws SQLException si hay error en la actualización
     */
    public boolean updatePlayerStatus(Long playerId, Player.PlayerStatus status) throws SQLException {
        String query = "UPDATE players SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status.name());
            pstmt.setLong(2, playerId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Obtiene jugadores por estado
     *
     * @param status Estado del jugador (ACTIVE, ELIMINATED, WAITING)
     * @return Lista de jugadores con ese estado
     * @throws SQLException si hay error en la consulta
     */
    public List<Player> getPlayersByStatus(String status) throws SQLException {
        List<Player> players = new ArrayList<>();
        String query = "SELECT * FROM players WHERE status = ? ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    players.add(mapResultSetToPlayer(rs));
                }
            }
        }

        return players;
    }

    /**
     * Obtiene todos los jugadores de una partida específica
     *
     * @param gameId ID de la partida
     * @return Lista de jugadores en la partida
     * @throws SQLException si hay error en la consulta
     */
    public List<Player> getPlayersByGameId(Long gameId) throws SQLException {
        List<Player> players = new ArrayList<>();
        String query = "SELECT p.* FROM players p " +
                "INNER JOIN game_players gp ON p.id = gp.player_id " +
                "WHERE gp.game_id = ? " +
                "ORDER BY gp.join_order";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, gameId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    players.add(mapResultSetToPlayer(rs));
                }
            }
        }

        return players;
    }

    /**
     * Obtiene jugadores activos de una partida
     *
     * @param gameId ID de la partida
     * @return Lista de jugadores activos
     * @throws SQLException si hay error en la consulta
     */
    public List<Player> getActivePlayersByGameId(Long gameId) throws SQLException {
        List<Player> players = new ArrayList<>();
        String query = "SELECT p.* FROM players p " +
                "INNER JOIN game_players gp ON p.id = gp.player_id " +
                "WHERE gp.game_id = ? AND p.status = 'ACTIVE' " +
                "ORDER BY gp.join_order";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, gameId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    players.add(mapResultSetToPlayer(rs));
                }
            }
        }

        return players;
    }

    /**
     * Establece el dealer de una partida
     *
     * @param gameId ID de la partida
     * @param playerId ID del jugador que será dealer
     * @return true si se actualizó correctamente
     * @throws SQLException si hay error en la actualización
     */
    public boolean setDealer(Long gameId, Long playerId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Quitar dealer a todos los jugadores de la partida
            String query1 = "UPDATE players p SET is_dealer = false " +
                    "FROM game_players gp " +
                    "WHERE p.id = gp.player_id AND gp.game_id = ?";
            try (PreparedStatement pstmt1 = conn.prepareStatement(query1)) {
                pstmt1.setLong(1, gameId);
                pstmt1.executeUpdate();
            }

            // Establecer nuevo dealer
            String query2 = "UPDATE players SET is_dealer = true WHERE id = ?";
            try (PreparedStatement pstmt2 = conn.prepareStatement(query2)) {
                pstmt2.setLong(1, playerId);
                pstmt2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Verifica si un nombre de jugador ya existe
     *
     * @param name Nombre del jugador
     * @return true si el nombre ya existe
     * @throws SQLException si hay error en la consulta
     */
    public boolean existsPlayerByName(String name) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM players WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }

        return false;
    }

    /**
     * Mapea un ResultSet a un objeto Player
     *
     * @param rs ResultSet con los datos del jugador
     * @return Player objeto mapeado
     * @throws SQLException si hay error al leer los datos
     */
    private Player mapResultSetToPlayer(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setId(rs.getLong("id"));
        player.setName(rs.getString("name"));
        player.setLives(rs.getInt("lives"));
        player.setStatus(Player.PlayerStatus.valueOf(rs.getString("status")));
        player.setDealer(rs.getBoolean("is_dealer"));
        return player;
    }
}