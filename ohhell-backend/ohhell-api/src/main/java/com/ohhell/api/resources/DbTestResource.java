package com.ohhell.api.resources;

import com.ohhell.api.db.Database;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.sql.Connection;

@Path("/db-test")
public class DbTestResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        try (Connection conn = Database.getConnection()) {
            if (conn.isValid(2)) {
                return "DB OK";
            }
            return "DB INVALID";
        } catch (Exception e) {
            e.printStackTrace();
            return "DB ERROR: " + e.getMessage();
        }
    }
}
