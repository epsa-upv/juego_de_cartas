package com.ohhell.api.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/mi_app";

    private static final String USER = "tommy";
    private static final String PASSWORD = "1234";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver no encontrado", e);
        }
    }

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
