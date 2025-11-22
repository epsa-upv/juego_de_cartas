package com.ohhell.ohhellapi.resources;

import com.ohhell.ohhellapi.models.Player;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Path("players")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlayerResource {
    
    // Simulación de base de datos en memoria
    private static ConcurrentHashMap<Long, Player> players = new ConcurrentHashMap<>();
    private static AtomicLong idCounter = new AtomicLong(1);
    
    /**
     * GET /api/v1/players
     * Obtener todos los jugadores
     */
    @GET
    public Response getAllPlayers(
            @QueryParam("limit") @DefaultValue("100") int limit,
            @QueryParam("offset") @DefaultValue("0") int offset) {
        
        List<Player> playerList = new ArrayList<>(players.values());
        
        // Aplicar paginación
        int start = Math.min(offset, playerList.size());
        int end = Math.min(start + limit, playerList.size());
        List<Player> paginatedList = playerList.subList(start, end);
        
        return Response.ok(paginatedList).build();
    }
    
    /**
     * GET /api/v1/players/{id}
     * Obtener un jugador específico
     */
    @GET
    @Path("{id}")
    public Response getPlayer(@PathParam("id") Long id) {
        Player player = players.get(id);
        
        if (player == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Player not found\"}")
                    .build();
        }
        
        return Response.ok(player).build();
    }
    
    /**
     * POST /api/v1/players
     * Crear un nuevo jugador
     */
    @POST
    public Response createPlayer(Player player, @Context UriInfo uriInfo) {
        
        // Validaciones
        if (player.getUsername() == null || player.getUsername().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Username is required\"}")
                    .build();
        }
        
        if (player.getEmail() == null || player.getEmail().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Email is required\"}")
                    .build();
        }
        
        // Verificar si el username ya existe
        boolean usernameExists = players.values().stream()
                .anyMatch(p -> p.getUsername().equals(player.getUsername()));
        
        if (usernameExists) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Username already exists\"}")
                    .build();
        }
        
        // Asignar ID y guardar
        Long newId = idCounter.getAndIncrement();
        player.setPlayerId(newId);
        players.put(newId, player);
        
        // Construir URI del recurso creado
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Long.toString(newId));
        
        return Response.created(builder.build())
                .entity(player)
                .build();
    }
    
    /**
     * PUT /api/v1/players/{id}
     * Actualizar un jugador
     */
    @PUT
    @Path("{id}")
    public Response updatePlayer(@PathParam("id") Long id, Player updatedPlayer) {
        
        Player existingPlayer = players.get(id);
        
        if (existingPlayer == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Player not found\"}")
                    .build();
        }
        
        // Actualizar campos
        if (updatedPlayer.getUsername() != null) {
            existingPlayer.setUsername(updatedPlayer.getUsername());
        }
        if (updatedPlayer.getEmail() != null) {
            existingPlayer.setEmail(updatedPlayer.getEmail());
        }
        if (updatedPlayer.getPassword() != null) {
            existingPlayer.setPassword(updatedPlayer.getPassword());
        }
        
        players.put(id, existingPlayer);
        
        return Response.ok(existingPlayer).build();
    }
    
    /**
     * DELETE /api/v1/players/{id}
     * Eliminar un jugador
     */
    @DELETE
    @Path("{id}")
    public Response deletePlayer(@PathParam("id") Long id) {
        
        Player removed = players.remove(id);
        
        if (removed == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Player not found\"}")
                    .build();
        }
        
        return Response.noContent().build();
    }
    
    // Método helper para obtener el mapa de jugadores (útil para otros resources)
    public static ConcurrentHashMap<Long, Player> getPlayersMap() {
        return players;
    }
}
