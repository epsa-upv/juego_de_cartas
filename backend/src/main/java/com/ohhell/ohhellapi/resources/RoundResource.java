package com.ohhell.ohhellapi.resources;

import com.ohhell.ohhellapi.models.Game;
import com.ohhell.ohhellapi.models.Round;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;

@Path("v1/games/{gameId}/rounds")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoundResource {
    
    /**
     * GET /api/v1/games/{gameId}/rounds
     * Obtener todas las rondas de una partida
     */
    @GET
    public Response getAllRounds(@PathParam("gameId") Long gameId) {
        
        Game game = GameResource.getGamesMap().get(gameId);
        
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Game not found\"}")
                    .build();
        }
        
        List<Round> rounds = game.getRounds();
        return Response.ok(rounds).build();
    }
    
    /**
     * GET /api/v1/games/{gameId}/rounds/{roundNumber}
     * Obtener una ronda específica
     */
    @GET
    @Path("{roundNumber}")
    public Response getRound(
            @PathParam("gameId") Long gameId,
            @PathParam("roundNumber") int roundNumber) {
        
        Game game = GameResource.getGamesMap().get(gameId);
        
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Game not found\"}")
                    .build();
        }
        
        List<Round> rounds = game.getRounds();
        
        if (roundNumber < 1 || roundNumber > rounds.size()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Round not found\"}")
                    .build();
        }
        
        Round round = rounds.get(roundNumber - 1);
        return Response.ok(round).build();
    }
}
