package com.ohhell.ohhellapi.dao;

import com.ohhell.ohhellapi.models.Game;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * GameDAO - Data Access Object para la entidad Game
 *
 * Oh Hell! Card Game - UPV
 * Autor: Tomás Criado García
 *
 * Gestiona todas las operaciones CRUD con la tabla games en PostgreSQL
 */
public class GameDAO {

    /**
     * Obtiene todas las partidas de la base de datos
     *
     * @return Lista de todas las partidas
     * @throws SQLException si hay error en la consulta
     */
    public List<Game> getAllGames() throws SQLException {
        List<Game> games = new ArrayList<>();
        String query = "SELECT * FROM games ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Game game = mapResultSetToGame(rs);
                games.add(game);
            }
        }

        return games;
    }

    /**
     * Obtiene una partida por su ID
     *
     * @param id ID de la partida
     * @return Game objeto con los datos de la partida, o null si no existe
     * @throws SQLException si hay error en la consulta
     */
    public Game getGameById(Long id) throws SQLException {
        String query = "SELECT * FROM games WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGame(rs);
                }
            }
        }

        return null;
    }

    /**
     * Crea una nueva partida en la base de datos
     *
     * @param game Objeto Game con los datos de la nueva partida
     * @return Game objeto con el ID asignado
     * @throws SQLException si hay error en la inserción
     */
    public Game createGame(Game game) throws SQLException {
        String query = "INSERT INTO games (name, max_players, initial_lives, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, game.getName());
            pstmt.setInt(2, game.getMaxPlayers());
            pstmt.setInt(3, game.getInitialLives());
            pstmt.setString(4, game.getStatus());
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    game.setId(rs.getLong("id"));
                    game.setCreatedAt(LocalDateTime.now());
                    return game;
                }
            }
        }

        throw new SQLException("Error al crear partida: No se obtuvo ID");
    }

    /**
     * Actualiza los datos de una partida existente
     *
     * @param game Objeto Game con los datos actualizados
     * @return true si se actualizó correctamente, false si no existe
     * @throws SQLException si hay error en la actualización
     */
    public boolean updateGame(Game game) throws SQLException {
        String query = "UPDATE games SET name = ?, status = ?, finished_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, game.getName());
            pstmt.setString(2, game.getStatus());

            if (game.getFinishedAt() != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(game.getFinishedAt()));
            } else {
                pstmt.setNull(3, Types.TIMESTAMP);
            }

            pstmt.setLong(4, game.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Elimina una partida de la base de datos
     *
     * @param id ID de la partida a eliminar
     * @return true si se eliminó correctamente, false si no existe
     * @throws SQLException si hay error en la eliminación
     */
    public boolean deleteGame(Long id) throws SQLException {
        String query = "DELETE FROM games WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Actualiza el estado de una partida
     *
     * @param gameId ID de la partida
     * @param status Nuevo estado
     * @return true si se actualizó correctamente
     * @throws SQLException si hay error en la actualización
     */
    public boolean updateGameStatus(Long gameId, Game.GameStatus status) throws SQLException {
        String query = "UPDATE games SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status.name());
            pstmt.setLong(2, gameId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Finaliza una partida estableciendo la fecha de finalización
     *
     * @param gameId ID de la partida
     * @param winnerId ID del ganador (puede ser null)
     * @return true si se actualizó correctamente
     * @throws SQLException si hay error en la actualización
     */
    public boolean finishGame(Long gameId, Long winnerId) throws SQLException {
        String query = "UPDATE games SET status = ?, finished_at = ?, winner_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, Game.GameStatus.FINISHED.name());
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));

            if (winnerId != null) {
                pstmt.setLong(3, winnerId);
            } else {
                pstmt.setNull(3, Types.BIGINT);
            }

            pstmt.setLong(4, gameId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Obtiene partidas por estado
     *
     * @param status Estado de la partida (WAITING, IN_PROGRESS, FINISHED)
     * @return Lista de partidas con ese estado
     * @throws SQLException si hay error en la consulta
     */
    public List<Game> getGamesByStatus(String status) throws SQLException {
        List<Game> games = new ArrayList<>();
        String query = "SELECT * FROM games WHERE status = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    games.add(mapResultSetToGame(rs));
                }
            }
        }

        return games;
    }

    /**
     * Cuenta el número de jugadores en una partida
     *
     * @param gameId ID de la partida
     * @return Número de jugadores
     * @throws SQLException si hay error en la consulta
     */
    public int countPlayersInGame(Long gameId) throws SQLException {
        String query = "SELECT COUNT(*) as player_count FROM game_players WHERE game_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, gameId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("player_count");
                }
            }
        }

        return 0;
    }

    /**
     * Verifica si una partida está llena
     *
     * @param gameId ID de la partida
     * @return true si la partida está llena
     * @throws SQLException si hay error en la consulta
     */
    public boolean isGameFull(Long gameId) throws SQLException {
        Game game = getGameById(gameId);
        if (game == null) return false;

        int playerCount = countPlayersInGame(gameId);
        return playerCount >= game.getMaxPlayers();
    }

    /**
     * Mapea un ResultSet a un objeto Game
     *
     * @param rs ResultSet con los datos de la partida
     * @return Game objeto mapeado
     * @throws SQLException si hay error al leer los datos
     */
    private Game mapResultSetToGame(ResultSet rs) throws SQLException {
        Game game = new Game();
        game.setId(rs.getLong("id"));
        game.setName(rs.getString("name"));
        game.setMaxPlayers(rs.getInt("max_players"));
        game.setInitialLives(rs.getInt("initial_lives"));
        game.setStatus((rs.getString("status")));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            game.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp finishedAt = rs.getTimestamp("finished_at");
        if (finishedAt != null) {
            game.setFinishedAt(finishedAt.toLocalDateTime());
        }

        return game;
    }
    /**
     * Obtiene partidas disponibles para unirse (WAITING y no llenas)
     *
     * @return Lista de partidas disponibles
     * @throws SQLException si hay error en la consulta
     */
    public List<Game> getAvailableGames() throws SQLException {
        List<Game> games = new ArrayList<>();
        String query = "SELECT g.* FROM games g " +
                "LEFT JOIN (SELECT game_id, COUNT(*) as player_count " +
                "           FROM game_players GROUP BY game_id) gp " +
                "ON g.id = gp.game_id " +
                "WHERE g.status = ? AND (gp.player_count < g.max_players OR gp.player_count IS NULL) " +
                "ORDER BY g.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "WAITING");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    games.add(mapResultSetToGame(rs));
                }
            }
        }

        return games;
    }
}