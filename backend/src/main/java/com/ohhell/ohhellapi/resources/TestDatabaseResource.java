package com.ohhell.ohhellapi.resources;

import com.ohhell.ohhellapi.utils.DatabaseHelper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.sql.Connection;

@Path("testdb")
public class TestDatabaseResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String testConnection() {
        try {
            Connection conn = DatabaseHelper.getConnection();
            if (conn != null) {
                DatabaseHelper.closeConnection(conn);
                return "Conexion exitosa a PostgreSQL en Render";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return "Error desconocido";
    }
}