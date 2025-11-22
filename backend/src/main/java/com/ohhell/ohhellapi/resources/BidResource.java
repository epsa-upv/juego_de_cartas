package com.ohhell.ohhellapi.resources;

import com.ohhell.ohhellapi.models.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;
import java.util.Map;

@Path("v1/games/{gameId}/rounds/{roundNumber}/bids")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BidResource {
    
    /**
     * GET /api/v1/games/{gameId}/rounds/{roundNumber}/bids
     * Obtener todas las apuestas de una ronda
     */
    @GET
    public Response getAllBids(
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
        List<Bid> bids = round.getBids();
        
        return Response.ok(bids).build();
    }
    
    /**
     * POST /api/v1/games/{gameId}/rounds/{roundNumber}/bids
     * Hacer una apuesta
     */
    @POST
    public Response makeBid(
            @PathParam("gameId") Long gameId,
            @PathParam("roundNumber") int roundNumber,
            Map<String, Object> bidRequest,
            @Context UriInfo uriInfo) {
        
        Game game = GameResource.getGamesMap().get(gameId);
        
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Game not found\"}")
                    .build();
        }
        
        // Verificar que el juego está activo
        if (!game.getStatus().equals("active")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Game is not active\"}")
                    .build();
        }
        
        List<Round> rounds = game.getRounds();
        
        if (roundNumber < 1 || roundNumber > rounds.size()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Round not found\"}")
                    .build();
        }
        
        Round round = rounds.get(roundNumber - 1);
        
        // Verificar que la ronda está en fase de apuestas
        if (!round.getStatus().equals("bidding")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Round is not in bidding phase\"}")
                    .build();
        }
        
        Long playerId = ((Number) bidRequest.get("player_id")).longValue();
        Integer bidValue = (Integer) bidRequest.get("bid_value");
        
        // Validaciones
        if (!game.getPlayerIds().contains(playerId)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Player is not in this game\"}")
                    .build();
        }
        
        if (bidValue < 0 || bidValue > round.getCardsPerPlayer()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid bid value\"}")
                    .build();
        }
        
        // Verificar si el jugador ya apostó
        boolean alreadyBid = round.getBids().stream()
                .anyMatch(b -> b.getPlayerId().equals(playerId));
        
        if (alreadyBid) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Player has already bid\"}")
                    .build();
        }
        
        // Validar regla de Oh Hell (suma de apuestas != número de cartas)
        if (!round.isValidBid(bidValue, game.getPlayerIds().size())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid bid - total bids cannot equal cards per player (Oh Hell rule)\"}")
                    .build();
        }
        
        // Crear y añadir apuesta
        Bid bid = new Bid(playerId, bidValue);
        round.addBid(bid);
        
        // Si todos han apostado, cambiar fase a "playing"
        if (round.getBids().size() == game.getPlayerIds().size()) {
            round.setStatus("playing");
            round.startNewTrick();  // Iniciar primera baza
        }
        
        return Response.status(Response.Status.CREATED)
                .entity(bid)
                .build();
    }
}
