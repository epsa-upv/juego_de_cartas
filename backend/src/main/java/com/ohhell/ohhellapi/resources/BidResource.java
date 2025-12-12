package com.ohhell.ohhellapi.resources;

import com.ohhell.ohhellapi.services.BidService;
import com.ohhell.ohhellapi.models.Bid;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("v1/games/{gameId}/rounds/{roundNumber}/bids")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BidResource {

    @Inject
    private BidService bidService;

    // ==================== DTOs para las peticiones ====================

    /**
     * DTO para crear una nueva apuesta
     */
    public static class CreateBidRequest {
        private Long playerId;
        private Integer bidValue;

        // Getters y Setters
        public Long getPlayerId() { return playerId; }
        public void setPlayerId(Long playerId) { this.playerId = playerId; }

        public Integer getBidValue() { return bidValue; }
        public void setBidValue(Integer bidValue) { this.bidValue = bidValue; }
    }

    /**
     * DTO para actualizar una apuesta
     */
    public static class UpdateBidRequest {
        private Integer newBidAmount;

        public Integer getNewBidAmount() { return newBidAmount; }
        public void setNewBidAmount(Integer newBidAmount) { this.newBidAmount = newBidAmount; }
    }

    // ==================== ENDPOINTS ====================

    /**
     * GET /api/v1/games/{gameId}/rounds/{roundNumber}/bids
     * Obtiene TODAS las apuestas de una ronda específica
     */
    @GET
    public Response getAllBids(
            @PathParam("gameId") Long gameId,
            @PathParam("roundNumber") Integer roundNumber) {

        try {
            // Convertir roundNumber a roundId
            // NOTA: En una implementación real, necesitarías buscar el roundId
            // basado en gameId y roundNumber. Por ahora, asumimos que roundNumber = roundId
            Long roundId = roundNumber.longValue();

            List<Bid> bids = bidService.getBidsByRound(roundId);

            if (bids.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No hay apuestas en esta ronda");
                response.put("gameId", gameId);
                response.put("roundNumber", roundNumber);
                response.put("roundId", roundId);
                return Response.ok(response).build();
            }

            return Response.ok(bids).build();

        } catch (Exception e) {
            return buildErrorResponse(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al obtener apuestas: " + e.getMessage()
            );
        }
    }

    /**
     * POST /api/v1/games/{gameId}/rounds/{roundNumber}/bids
     * Un jugador hace una apuesta en una ronda
     *
     * Body JSON esperado:
     * {
     *   "playerId": 1,
     *   "bidValue": 3
     * }
     */
    @POST
    public Response makeBid(
            @PathParam("gameId") Long gameId,
            @PathParam("roundNumber") Integer roundNumber,
            CreateBidRequest bidRequest) {

        try {
            // ========== VALIDACIONES BÁSICAS DEL REQUEST ==========
            if (bidRequest == null) {
                return buildErrorResponse(
                        Response.Status.BAD_REQUEST,
                        "El cuerpo de la solicitud no puede estar vacío"
                );
            }

            if (bidRequest.getPlayerId() == null) {
                return buildErrorResponse(
                        Response.Status.BAD_REQUEST,
                        "El campo 'playerId' es requerido"
                );
            }

            if (bidRequest.getBidValue() == null) {
                return buildErrorResponse(
                        Response.Status.BAD_REQUEST,
                        "El campo 'bidValue' es requerido"
                );
            }

            // Convertir roundNumber a roundId
            Long roundId = roundNumber.longValue();

            // ========== DELEGAR TODA LA LÓGICA AL SERVICE ==========
            Bid bid = bidService.placeBid(
                    bidRequest.getPlayerId(),
                    roundId,
                    bidRequest.getBidValue()
            );

            // ========== CONSTRUIR RESPUESTA DE ÉXITO ==========
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Apuesta realizada exitosamente");
            response.put("bidId", bid.getId());
            response.put("playerId", bid.getPlayerId());
            response.put("bidAmount", bid.getBidAmount());
            response.put("timestamp", bid.getTimestamp());
            response.put("gameId", gameId);
            response.put("roundNumber", roundNumber);

            return Response
                    .status(Response.Status.CREATED)
                    .entity(response)
                    .build();

        } catch (IllegalArgumentException e) {
            // Errores de validación (datos incorrectos)
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            // Errores de estado (juego/ronda no en estado correcto)
            return buildErrorResponse(Response.Status.CONFLICT, e.getMessage());
        } catch (Exception e) {
            // Errores inesperados
            return buildErrorResponse(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "Error interno al procesar apuesta: " + e.getMessage()
            );
        }
    }

    /**
     * GET /api/v1/games/{gameId}/rounds/{roundNumber}/bids/player/{playerId}
     * Obtiene la apuesta de un jugador específico en una ronda
     */
    @GET
    @Path("/player/{playerId}")
    public Response getPlayerBid(
            @PathParam("gameId") Long gameId,
            @PathParam("roundNumber") Integer roundNumber,
            @PathParam("playerId") Long playerId) {

        try {
            Long roundId = roundNumber.longValue();
            Bid bid = bidService.getBidByPlayerAndRound(playerId, roundId);

            if (bid == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "El jugador no ha apostado en esta ronda");
                response.put("playerId", playerId);
                response.put("gameId", gameId);
                response.put("roundNumber", roundNumber);
                response.put("roundId", roundId);
                return Response.ok(response).build();
            }

            return Response.ok(bid).build();

        } catch (Exception e) {
            return buildErrorResponse(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al obtener apuesta del jugador: " + e.getMessage()
            );
        }
    }

    /**
     * GET /api/v1/games/{gameId}/rounds/{roundNumber}/bids/total
     * Obtiene la suma total de apuestas en una ronda
     */
    @GET
    @Path("/total")
    public Response getTotalBids(
            @PathParam("gameId") Long gameId,
            @PathParam("roundNumber") Integer roundNumber) {

        try {
            Long roundId = roundNumber.longValue();
            int total = bidService.getTotalBidAmountForRound(roundId);

            Map<String, Object> response = new HashMap<>();
            response.put("totalBids", total);
            response.put("gameId", gameId);
            response.put("roundNumber", roundNumber);
            response.put("roundId", roundId);

            return Response.ok(response).build();

        } catch (Exception e) {
            return buildErrorResponse(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al calcular total de apuestas: " + e.getMessage()
            );
        }
    }

    /**
     * PUT /api/v1/games/{gameId}/rounds/{roundNumber}/bids/{bidId}
     * Actualiza una apuesta existente
     *
     * Body JSON esperado:
     * {
     *   "newBidAmount": 5
     * }
     */
    @PUT
    @Path("/{bidId}")
    public Response updateBid(
            @PathParam("gameId") Long gameId,
            @PathParam("roundNumber") Integer roundNumber,
            @PathParam("bidId") Long bidId,
            UpdateBidRequest updateRequest) {

        try {
            if (updateRequest == null || updateRequest.getNewBidAmount() == null) {
                return buildErrorResponse(
                        Response.Status.BAD_REQUEST,
                        "El campo 'newBidAmount' es requerido"
                );
            }

            if (updateRequest.getNewBidAmount() < 0) {
                return buildErrorResponse(
                        Response.Status.BAD_REQUEST,
                        "El valor de la apuesta no puede ser negativo"
                );
            }

            boolean updated = bidService.updateBid(bidId, updateRequest.getNewBidAmount());

            if (updated) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Apuesta actualizada exitosamente");
                response.put("bidId", bidId);
                response.put("newBidAmount", updateRequest.getNewBidAmount());
                response.put("gameId", gameId);
                response.put("roundNumber", roundNumber);
                return Response.ok(response).build();
            } else {
                return buildErrorResponse(
                        Response.Status.NOT_FOUND,
                        "Apuesta no encontrada (ID: " + bidId + ")"
                );
            }

        } catch (IllegalArgumentException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al actualizar apuesta: " + e.getMessage()
            );
        }
    }

    /**
     * POST /api/v1/games/{gameId}/rounds/{roundNumber}/bids/{bidId}/increment-trick
     * Incrementa las bazas ganadas de una apuesta (cuando un jugador gana una baza)
     */
    @POST
    @Path("/{bidId}/increment-trick")
    public Response incrementTricksWon(
            @PathParam("gameId") Long gameId,
            @PathParam("roundNumber") Integer roundNumber,
            @PathParam("bidId") Long bidId) {

        try {
            boolean incremented = bidService.incrementTricksWon(bidId);

            if (incremented) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Baza registrada exitosamente");
                response.put("bidId", bidId);
                response.put("gameId", gameId);
                response.put("roundNumber", roundNumber);
                return Response.ok(response).build();
            } else {
                return buildErrorResponse(
                        Response.Status.NOT_FOUND,
                        "Apuesta no encontrada (ID: " + bidId + ")"
                );
            }

        } catch (Exception e) {
            return buildErrorResponse(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al registrar baza: " + e.getMessage()
            );
        }
    }

    /**
     * DELETE /api/v1/games/{gameId}/rounds/{roundNumber}/bids/{bidId}
     * Elimina una apuesta (solo para administración o testing)
     */
    @DELETE
    @Path("/{bidId}")
    public Response deleteBid(
            @PathParam("gameId") Long gameId,
            @PathParam("roundNumber") Integer roundNumber,
            @PathParam("bidId") Long bidId) {

        try {
            boolean deleted = bidService.deleteBid(bidId);

            if (deleted) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Apuesta eliminada exitosamente");
                response.put("bidId", bidId);
                response.put("gameId", gameId);
                response.put("roundNumber", roundNumber);
                return Response.ok(response).build();
            } else {
                return buildErrorResponse(
                        Response.Status.NOT_FOUND,
                        "Apuesta no encontrada (ID: " + bidId + ")"
                );
            }

        } catch (Exception e) {
            return buildErrorResponse(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al eliminar apuesta: " + e.getMessage()
            );
        }
    }

    /**
     * GET /api/v1/games/{gameId}/rounds/{roundNumber}/bids/status
     * Verifica si todos los jugadores han apostado en esta ronda
     */
    @GET
    @Path("/status")
    public Response getBiddingStatus(
            @PathParam("gameId") Long gameId,
            @PathParam("roundNumber") Integer roundNumber,
            @QueryParam("totalPlayers") Integer totalPlayers) {

        try {
            Long roundId = roundNumber.longValue();

            if (totalPlayers == null || totalPlayers <= 0) {
                return buildErrorResponse(
                        Response.Status.BAD_REQUEST,
                        "El parámetro 'totalPlayers' es requerido y debe ser > 0"
                );
            }

            boolean allHaveBid = bidService.allPlayersHaveBid(roundId, totalPlayers);

            Map<String, Object> response = new HashMap<>();
            response.put("allPlayersHaveBid", allHaveBid);
            response.put("gameId", gameId);
            response.put("roundNumber", roundNumber);
            response.put("roundId", roundId);
            response.put("totalPlayers", totalPlayers);

            return Response.ok(response).build();

        } catch (Exception e) {
            return buildErrorResponse(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al verificar estado de apuestas: " + e.getMessage()
            );
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Método auxiliar para construir respuestas de error consistentes
     */
    private Response buildErrorResponse(Response.Status status, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        error.put("status", status.getStatusCode() + " " + status.getReasonPhrase());
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        return Response.status(status).entity(error).build();
    }

    /**
     * Método auxiliar para validar IDs
     */
    private boolean isValidId(Long id) {
        return id != null && id > 0;
    }
}