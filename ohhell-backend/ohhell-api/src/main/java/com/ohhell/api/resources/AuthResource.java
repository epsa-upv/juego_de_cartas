package com.ohhell.api.resources;

import com.ohhell.api.dao.UserDAO;
import com.ohhell.api.models.LoginRequest;
import com.ohhell.api.models.User;
import com.ohhell.api.security.JwtUtil;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;
import java.util.Optional;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final UserDAO userDAO = new UserDAO();

    @POST
    @Path("/register")
    public Response register(Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Email y password obligatorios")
                    .build();
        }

        if (userDAO.findByEmail(email).isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Email ya registrado")
                    .build();
        }

        // ⚠️ MVP: sin hash todavía
        User user = userDAO.create(email, password);

        return Response.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail()
        )).build();
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {

        Optional<User> optUser = userDAO.findByEmail(request.getEmail());

        if (optUser.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciales inválidas")
                    .build();
        }

        User user = optUser.get();

        // ⚠️ MVP: comparación directa
        if (!user.getPasswordHash().equals(request.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciales inválidas")
                    .build();
        }

        String token = JwtUtil.generateToken(user.getId(), user.getEmail());

        return Response.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "token", token
        )).build();
    }
}
