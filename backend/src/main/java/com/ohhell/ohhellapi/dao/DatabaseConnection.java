package com.ohhell.ohhellapi.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - Gestión centralizada de conexión a PostgreSQL
 *
 * Oh Hell! Card Game - UPV
 * Autor: Tomás Criado García
 *
 * Esta clase proporciona una conexión singleton a la base de datos PostgreSQL
 * alojada en Render.
 */
public class DatabaseConnection {

    // Configuración de conexión a PostgreSQL en Render
    private static final String DB_URL = "jdbc:postgresql://dpg-ct3g5bdsvqrc73874o10-a.oregon-postgres.render.com:5432/ohhell_db";
    private static final String DB_USER = "ohhell_user";
    private static final String DB_PASSWORD = "iMi5lFilip6ih2K0b8xygiM13EyQfbkT";

    private static Connection connection = null;

    /**
     * Constructor privado para patrón Singleton
     */
    private DatabaseConnection() {
        // Impedir instanciación
    }

    /**
     * Obtiene una conexión a la base de datos PostgreSQL
     *
     * @return Connection objeto de conexión a la BD
     * @throws SQLException si hay error en la conexión
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Cargar el driver de PostgreSQL
            Class.forName("org.postgresql.Driver");

            // Si no hay conexión o está cerrada, crear nueva
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("✓ Conexión establecida con PostgreSQL en Render");
            }

            return connection;

        } catch (ClassNotFoundException e) {
            System.err.println("✗ Error: Driver PostgreSQL no encontrado");
            throw new SQLException("PostgreSQL driver not found", e);
        } catch (SQLException e) {
            System.err.println("✗ Error al conectar con PostgreSQL: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al cerrar conexión: " + e.getMessage());
        }
    }

    /**
     * Verifica si la conexión está activa
     *
     * @return true si la conexión está activa, false en caso contrario
     */
    public static boolean isConnectionActive() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}