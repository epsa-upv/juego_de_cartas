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

    // =========================
    // CREATE GAME
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
        gamePlayerDAO.addHost(game.getId(), player.getId());

        return Response.ok(game).build();
    }

    // =========================
    // JOIN GAME
    // =========================
    @POST
    @Path("/{code}/join")
    public Response joinGame(
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
                .orElseThrow(() ->
                        new WebApplicationException(
                                "Jugador no existe",
                                Response.Status.BAD_REQUEST
                        )
                );

        // Evitar doble join
        try {
            gamePlayerDAO.getGamePlayerId(game.getId(), player.getId());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Ya estás en la partida")
                    .build();
        } catch (RuntimeException ignored) {
        }

        gamePlayerDAO.joinGame(game.getId(), player.getId());

        return Response.ok(Map.of("message", "JOINED")).build();
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
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!"WAITING".equals(game.getStatus())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La partida ya ha comenzado")
                    .build();
        }

        Player player = playerDAO.findByUserId(userId)
                .orElseThrow(() -> new WebApplicationException(400));

        if (!gamePlayerDAO.isHost(game.getId(), player.getId())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (!gamePlayerDAO.areAllPlayersReady(game.getId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("No todos están READY")
                    .build();
        }

        gameDAO.markStarted(game.getId());
        roundDAO.createFirstRound(
                game.getId(),
                game.getStartingCards(),
                0
        );

        return Response.ok(Map.of("message", "GAME_STARTED")).build();
    }

    // =========================
    // BET
    // =========================
    @POST
    @Path("/{code}/rounds/current/bet")
    public Response placeBet(
            @PathParam("code") String code,
            BetRequest request,
            @Context SecurityContext securityContext
    ) {
        UUID userId = getUserId(securityContext);

        Game game = gameDAO.findByCode(code);
        RoundView round = roundDAO.findCurrentRound(game.getId());

        if (round == null || !"BETTING".equals(round.getPhase())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (request.getValue() < 0 || request.getValue() > round.getCardsPerPlayer()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Player player = playerDAO.findByUserId(userId)
                .orElseThrow(() -> new WebApplicationException(400));

        long gpId = gamePlayerDAO.getGamePlayerId(game.getId(), player.getId());
        BetDAO betDAO = new BetDAO();

        int players = gamePlayerDAO.countPlayers(game.getId());
        int betsPlaced = betDAO.countBets(round.getId());
        int sum = betDAO.sumBets(round.getId());

        if (betsPlaced == players - 1 &&
                sum + request.getValue() == round.getCardsPerPlayer()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El último jugador no puede cerrar la suma exacta")
                    .build();
        }

        if (betDAO.hasBet(round.getId(), gpId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        int order = betDAO.nextBetOrder(round.getId());
        betDAO.placeBet(round.getId(), gpId, request.getValue(), order);

        return Response.ok(Map.of("bet", request.getValue(), "order", order)).build();
    }

    // =========================
    // CURRENT ROUND BETS
    // =========================
    @GET
    @Path("/{code}/rounds/current/bets")
    public Response getCurrentRoundBets(
            @PathParam("code") String code,
            @Context SecurityContext securityContext
    ) {
        getUserId(securityContext);

        Game game = gameDAO.findByCode(code);
        RoundView round = roundDAO.findCurrentRound(game.getId());

        BetDAO betDAO = new BetDAO();
        var bets = betDAO.getBetsForRound(round.getId());

        return Response.ok(new RoundBetsView(round.getId(), bets)).build();
    }

    // =========================
    // HELPER
    // =========================
    private UUID getUserId(SecurityContext securityContext) {
        if (securityContext.getUserPrincipal() == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        return ((UserPrincipal) securityContext.getUserPrincipal()).getUserId();
    }
}
