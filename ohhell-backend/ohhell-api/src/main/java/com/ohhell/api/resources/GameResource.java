package com.ohhell.api.resources;

import com.ohhell.api.dao.*;
import com.ohhell.api.models.*;
import com.ohhell.api.security.UserPrincipal;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.Map;
import java.util.UUID;

@Path("/games")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GameResource {

    private final GameDAO gameDAO = new GameDAO();
    private final PlayerDAO playerDAO = new PlayerDAO();
    private final GamePlayerDAO gamePlayerDAO = new GamePlayerDAO();
    private final RoundDAO roundDAO = new RoundDAO();


    @POST
    @Path("/{code}/rounds/current/bet")
    public Response placeBet(
            @PathParam("code") String code,
            BetRequest request,
            @Context SecurityContext securityContext
    ) {
        UUID userId = getUserId(securityContext);

        Game game = gameDAO.findByCode(code);
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Partida no encontrada")
                    .build();
        }

        RoundView round = roundDAO.findCurrentRound(game.getId());
        if (round == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("No hay ronda activa")
                    .build();
        }

        Player player = playerDAO.findByUserId(userId)
                .orElseThrow(() -> new WebApplicationException(400));

        long gamePlayerId =
                gamePlayerDAO.getGamePlayerId(game.getId(), player.getId());

        BetDAO betDAO = new BetDAO();

        if (betDAO.hasBet(round.getId(), gamePlayerId)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Ya has apostado en esta ronda")
                    .build();
        }

        int betOrder = betDAO.nextBetOrder(round.getId());

        betDAO.placeBet(
                round.getId(),
                gamePlayerId,
                request.getValue(),
                betOrder
        );

        return Response.ok(
                Map.of(
                        "bet", request.getValue(),
                        "order", betOrder
                )
        ).build();
    }


    // =========================
    // CREATE GAME  🔥 FIX CLAVE
    // =========================
    @POST
    public Response createGame(
            @Context SecurityContext securityContext,
            Map<String, String> body
    ) {
        UUID userId = getUserId(securityContext);

        Player player = playerDAO.findByUserId(userId)
                .orElseThrow(() ->
                        new WebApplicationException(
                                "Debes crear un player antes",
                                Response.Status.BAD_REQUEST
                        )
                );

        String title = body != null
                ? body.getOrDefault("title", "Oh Hell!")
                : "Oh Hell!";

        Game game = gameDAO.create(title);

        // 🔥 FUNDAMENTAL: crear el host
        gamePlayerDAO.addHost(game.getId(), player.getId());

        return Response.ok(game).build();
    }

    // =========================
    // LOBBY
    // =========================
    @GET
    @Path("/{code}/players")
    public Response getLobby(
            @PathParam("code") String code,
            @Context SecurityContext securityContext
    ) {
        getUserId(securityContext);

        Game game = gameDAO.findByCode(code);
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Partida no encontrada")
                    .build();
        }

        return Response.ok(
                new GameLobbyView(
                        game.getCode(),
                        game.getStatus(),
                        gamePlayerDAO.getLobbyPlayers(game.getId())
                )
        ).build();
    }

    // =========================
    // READY
    // =========================
    @POST
    @Path("/{code}/ready")
    public Response readyUp(
            @PathParam("code") String code,
            @Context SecurityContext securityContext
    ) {
        UUID userId = getUserId(securityContext);

        Game game = gameDAO.findByCode(code);
        if (game == null) return Response.status(404).build();

        Player player = playerDAO.findByUserId(userId)
                .orElseThrow(() -> new WebApplicationException(400));

        gamePlayerDAO.setReady(game.getId(), player.getId(), true);

        return Response.ok(Map.of("message", "READY")).build();
    }

    @POST
    @Path("/{code}/unready")
    public Response unready(
            @PathParam("code") String code,
            @Context SecurityContext securityContext
    ) {
        UUID userId = getUserId(securityContext);

        Game game = gameDAO.findByCode(code);
        if (game == null) return Response.status(404).build();

        Player player = playerDAO.findByUserId(userId)
                .orElseThrow(() -> new WebApplicationException(400));

        gamePlayerDAO.setReady(game.getId(), player.getId(), false);

        return Response.ok(Map.of("message", "UNREADY")).build();
    }

    // =========================
    // START GAME
    // =========================
    @POST
    @Path("/{code}/start")
    public Response startGame(
            @PathParam("code") String code,
            @Context SecurityContext securityContext
    ) {
        UUID userId = getUserId(securityContext);

        Game game = gameDAO.findByCode(code);
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Partida no encontrada")
                    .build();
        }

        if (!"WAITING".equals(game.getStatus())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La partida ya ha comenzado")
                    .build();
        }

        Player player = playerDAO.findByUserId(userId)
                .orElseThrow(() -> new WebApplicationException(400));

        if (!gamePlayerDAO.isHost(game.getId(), player.getId())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Solo el host puede iniciar")
                    .build();
        }

        if (!gamePlayerDAO.areAllPlayersReady(game.getId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("No todos los jugadores están READY")
                    .build();
        }

        // 1️⃣ Marcar juego como PLAYING
        gameDAO.markStarted(game.getId());

        // 2️⃣ Crear ronda 1
        roundDAO.createFirstRound(
                game.getId(),
                game.getStartingCards(),
                0
        );

        return Response.ok(
                Map.of("message", "Partida iniciada y ronda 1 creada")
        ).build();
    }

    // =========================
    // CURRENT ROUND
    // =========================
    @GET
    @Path("/{code}/rounds/current")
    public Response getCurrentRound(
            @PathParam("code") String code,
            @Context SecurityContext securityContext
    ) {
        getUserId(securityContext);

        Game game = gameDAO.findByCode(code);
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Partida no encontrada")
                    .build();
        }

        if ("WAITING".equals(game.getStatus())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La partida aún no ha comenzado")
                    .build();
        }

        RoundView round = roundDAO.findCurrentRound(game.getId());
        if (round == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No hay ronda activa")
                    .build();
        }

        return Response.ok(round).build();
    }

    // =========================
    // Helper
    // =========================
    private UUID getUserId(SecurityContext securityContext) {
        if (securityContext.getUserPrincipal() == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        return ((UserPrincipal) securityContext.getUserPrincipal()).getUserId();
    }

    @GET
    @Path("/{code}/rounds/current/bets")
    public Response getCurrentRoundBets(
            @PathParam("code") String code,
            @Context SecurityContext securityContext
    ) {
        getUserId(securityContext);

        Game game = gameDAO.findByCode(code);
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Partida no encontrada")
                    .build();
        }

        RoundView round = roundDAO.findCurrentRound(game.getId());
        if (round == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("No hay ronda activa")
                    .build();
        }

        BetDAO betDAO = new BetDAO();
        var bets = betDAO.getBetsForRound(round.getId());

        return Response.ok(
                new RoundBetsView(round.getId(), bets)
        ).build();
    }


}
