package com.ohhell.ohhellapi.resources;

import com.ohhell.ohhellapi.models.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;
import java.util.Map;

@Path("v1/games/{gameId}/rounds/{roundNumber}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TrickResource {
    
    /**
     * GET /api/v1/games/{gameId}/rounds/{roundNumber}/current-trick
     * Obtener el estado actual de la baza
     */
    @GET
    @Path("current-trick")
    public Response getCurrentTrick(
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
        Trick currentTrick = round.getCurrentTrickObject();
        
        if (currentTrick == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"No active trick\"}")
                    .build();
        }
        
        return Response.ok(currentTrick).build();
    }
    
    /**
     * POST /api/v1/games/{gameId}/rounds/{roundNumber}/play
     * Jugar una carta
     */
    @POST
    @Path("play")
    public Response playCard(
            @PathParam("gameId") Long gameId,
            @PathParam("roundNumber") int roundNumber,
            Map<String, Object> playRequest) {
        
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
        
        // Verificar que la ronda está en fase de juego
        if (!round.getStatus().equals("playing")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Round is not in playing phase\"}")
                    .build();
        }
        
        Long playerId = ((Number) playRequest.get("player_id")).longValue();
        Map<String, String> cardData = (Map<String, String>) playRequest.get("card");
        
        String suit = cardData.get("suit");
        String rank = cardData.get("rank");
        
        // Validaciones básicas
        if (!game.getPlayerIds().contains(playerId)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Player is not in this game\"}")
                    .build();
        }
        
        Card card = new Card(suit, rank);
        Trick currentTrick = round.getCurrentTrickObject();
        
        if (currentTrick == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"No active trick\"}")
                    .build();
        }
        
        // Jugar la carta
        currentTrick.playCard(playerId, card);
        
        // Verificar si la baza está completa
        if (currentTrick.isComplete(game.getPlayerIds().size())) {
            // Determinar ganador
            Long winnerId = currentTrick.determineWinner(round.getTrumpSuit());
            
            // Actualizar contador de bazas ganadas
            for (Bid bid : round.getBids()) {
                if (bid.getPlayerId().equals(winnerId)) {
                    bid.incrementTricksWon();
                    break;
                }
            }
            
            // Si hay más bazas por jugar, iniciar la siguiente
            if (round.getCurrentTrick() < round.getCardsPerPlayer()) {
                round.startNewTrick();
            } else {
                // Ronda terminada, calcular scores
                round.calculateScores();
                
                // Actualizar vidas y puntos
                for (Bid bid : round.getBids()) {
                    int score = round.getScores().getOrDefault(bid.getPlayerId(), 0);
                    game.addScore(bid.getPlayerId(), score);
                    
                    // Si falló la apuesta, pierde una vida
                    if (!bid.isBidMet()) {
                        game.updatePlayerLives(bid.getPlayerId(), -1);
                    }
                }
                
                // Verificar si el juego debe terminar
                game.checkGameEnd();
                
                // Si el juego no ha terminado, iniciar siguiente ronda
                if (!game.getStatus().equals("finished") && 
                    game.getCurrentRound() < game.getMaxRounds()) {
                    game.startNewRound();
                }
            }
        }
        
        return Response.ok()
                .entity("{\"message\": \"Card played successfully\", \"trick_winner\": " + 
                        (currentTrick.getWinnerId() != null ? currentTrick.getWinnerId() : "null") + "}")
                .build();
    }
}
