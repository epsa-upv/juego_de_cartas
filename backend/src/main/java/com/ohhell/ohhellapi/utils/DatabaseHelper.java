package com.ohhell.ohhellapi.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:postgresql://dpg-d4eg2uv5r7bs73fp28t0-a.oregon-postgres.render.com:5432/ohhell_db?sslmode=require";
    private static final String DB_USER = "ohhell_db_user";
    private static final String DB_PASSWORD = "zu9hytYEKXTiEEVRnSVOVQU4cx6jKe4r";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}