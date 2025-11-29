package com.ohhell.ohhellapi.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - Gestión de conexión a PostgreSQL
 *
 * Oh Hell! Card Game - UPV
 * Autor: Tomás Criado García
 *
 * Esta clase proporciona conexiones independientes para evitar problemas de concurrencia
 */
public class DatabaseConnection {

    // Configuración de conexión a PostgreSQL en Render
    private static final String DB_URL = "jdbc:postgresql://dpg-ct3g5bdsvqrc73874o10-a.oregon-postgres.render.com:5432/ohhell_db";
    private static final String DB_USER = "ohhell_user";
    private static final String DB_PASSWORD = "iMi5lFilip6ih2K0b8xygiM13EyQfbkT";

    /**
     * Constructor privado para evitar instanciación
     */
    private DatabaseConnection() {
        // Impedir instanciación
    }

    /**
     * Obtiene una NUEVA conexión a la base de datos PostgreSQL
     * Cada llamada devuelve una conexión independiente para evitar conflictos de concurrencia
     *
     * @return Connection objeto de conexión a la BD
     * @throws SQLException si hay error en la conexión
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Cargar el driver de PostgreSQL
            Class.forName("org.postgresql.Driver");

            // Crear y devolver NUEVA conexión cada vez
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ Nueva conexión establecida con PostgreSQL");
            return conn;

        } catch (ClassNotFoundException e) {
            System.err.println("✗ Error: Driver PostgreSQL no encontrado");
            throw new SQLException("PostgreSQL driver not found", e);
        } catch (SQLException e) {
            System.err.println("✗ Error al conectar con PostgreSQL: " + e.getMessage());
            throw e;
        }
    }
}