package com.ohhell.ohhellapi.resources;

import com.ohhell.ohhellapi.models.Game;
import com.ohhell.ohhellapi.models.Player;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Path("games")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GameResource {
    
    private static ConcurrentHashMap<Long, Game> games = new ConcurrentHashMap<>();
    private static AtomicLong idCounter = new AtomicLong(1);
    
    /**
     * GET /api/v1/games
     * Obtener todas las partidas
     */
    @GET
    public Response getAllGames(
            @QueryParam("status") String status,
            @QueryParam("limit") @DefaultValue("100") int limit,
            @QueryParam("offset") @DefaultValue("0") int offset) {
        
        List<Game> gameList = new ArrayList<>(games.values());
        
        // Filtrar por status si se proporciona
        if (status != null && !status.isEmpty()) {
            gameList = gameList.stream()
                    .filter(g -> g.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        
        // Aplicar paginación
        int start = Math.min(offset, gameList.size());
        int end = Math.min(start + limit, gameList.size());
        List<Game> paginatedList = gameList.subList(start, end);
        
        return Response.ok(paginatedList).build();
    }
    
    /**
     * GET /api/v1/games/{id}
     * Obtener una partida específica
     */
    @GET
    @Path("{id}")
    public Response getGame(@PathParam("id") Long id) {
        Game game = games.get(id);
        
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Game not found\"}")
                    .build();
        }
        
        return Response.ok(game).build();
    }
    
    /**
     * POST /api/v1/games
     * Crear una nueva partida
     */
    @POST
    public Response createGame(Map<String, Object> gameConfig, @Context UriInfo uriInfo) {
        
        // Extraer configuración
        Integer maxPlayers = (Integer) gameConfig.get("max_players");
        Integer initialLives = (Integer) gameConfig.get("initial_lives");
        Integer maxRounds = (Integer) gameConfig.get("max_rounds");
        Long createdBy = ((Number) gameConfig.get("created_by_player_id")).longValue();
        
        // Validaciones
        if (maxPlayers == null || maxPlayers < 2 || maxPlayers > 8) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"max_players must be between 2 and 8\"}")
                    .build();
        }
        
        if (initialLives == null || initialLives < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"initial_lives must be at least 1\"}")
                    .build();
        }
        
        if (maxRounds == null || maxRounds < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"max_rounds must be at least 1\"}")
                    .build();
        }
        
        // Verificar que el creador existe
        if (!PlayerResource.getPlayersMap().containsKey(createdBy)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Creator player not found\"}")
                    .build();
        }
        
        // Crear partida
        Game game = new Game(maxPlayers, initialLives, maxRounds, createdBy);
        Long newId = idCounter.getAndIncrement();
        game.setGameId(newId);
        
        // Añadir al creador automáticamente
        game.addPlayer(createdBy);
        
        games.put(newId, game);
        
        // Construir URI
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Long.toString(newId));
        
        return Response.created(builder.build())
                .entity(game)
                .build();
    }
    
    /**
     * POST /api/v1/games/{id}/join
     * Unirse a una partida
     */
    @POST
    @Path("{id}/join")
    public Response joinGame(@PathParam("id") Long gameId, Map<String, Object> joinRequest) {
        
        Game game = games.get(gameId);
        
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Game not found\"}")
                    .build();
        }
        
        Long playerId = ((Number) joinRequest.get("player_id")).longValue();
        
        // Verificar que el jugador existe
        if (!PlayerResource.getPlayersMap().containsKey(playerId)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Player not found\"}")
                    .build();
        }
        
        // Intentar añadir jugador
        boolean added = game.addPlayer(playerId);
        
        if (!added) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Cannot join game - game is full or already started\"}")
                    .build();
        }
        
        return Response.ok()
                .entity("{\"message\": \"Successfully joined game\", \"game_id\": " + gameId + "}")
                .build();
    }
    
    /**
     * POST /api/v1/games/{id}/start
     * Iniciar una partida
     */
    @POST
    @Path("{id}/start")
    public Response startGame(@PathParam("id") Long gameId) {
        
        Game game = games.get(gameId);
        
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Game not found\"}")
                    .build();
        }
        
        boolean started = game.startGame();
        
        if (!started) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Cannot start game - need at least 2 players and game must be in waiting status\"}")
                    .build();
        }
        
        return Response.ok(game).build();
    }
    
    /**
     * DELETE /api/v1/games/{id}
     * Eliminar una partida
     */
    @DELETE
    @Path("{id}")
    public Response deleteGame(@PathParam("id") Long id) {
        
        Game removed = games.remove(id);
        
        if (removed == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Game not found\"}")
                    .build();
        }
        
        return Response.noContent().build();
    }
    
    // Método helper
    public static ConcurrentHashMap<Long, Game> getGamesMap() {
        return games;
    }
}
