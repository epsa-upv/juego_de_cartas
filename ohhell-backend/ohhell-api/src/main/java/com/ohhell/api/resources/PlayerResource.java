package com.ohhell.api.resources;

import com.ohhell.api.dao.PlayerDAO;
import com.ohhell.api.models.Player;
import com.ohhell.api.security.UserPrincipal;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.Map;
import java.util.UUID;

@Path("/players")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PlayerResource {

    private final PlayerDAO playerDAO = new PlayerDAO();

    // 🔐 Crear player (usuario autenticado por JWT)
    @POST
    public Response createPlayer(
            @Context SecurityContext securityContext,
            Map<String, String> body
    ) {
        UUID userId = getUserId(securityContext);

        if (playerDAO.findByUserId(userId).isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("PLAYER_ALREADY_EXISTS")
                    .build();
        }

        String nickname = body.get("nickname");
        if (nickname == null || nickname.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("NICKNAME_REQUIRED")
                    .build();
        }

        Player player = playerDAO.create(userId, nickname);
        return Response.ok(player).build();
    }

    // 🔐 Obtener mi player
    @GET
    @Path("/me")
    public Response getMyPlayer(@Context SecurityContext securityContext) {
        UUID userId = getUserId(securityContext);

        return playerDAO.findByUserId(userId)
                .map(player -> Response.ok(player).build())
                .orElseGet(() ->
                        Response.status(Response.Status.NOT_FOUND)
                                .entity("PLAYER_NOT_FOUND")
                                .build()
                );
    }

    // =========================
    // Helper interno
    // =========================
    private UUID getUserId(SecurityContext securityContext) {
        if (securityContext.getUserPrincipal() == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        return ((UserPrincipal) securityContext.getUserPrincipal()).getUserId();
    }
}
