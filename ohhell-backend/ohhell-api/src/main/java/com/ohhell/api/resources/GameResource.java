package com.ohhell.api.resources;

import com.ohhell.api.dao.*;
import com.ohhell.api.models.*;
import com.ohhell.api.security.UserPrincipal;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.*;

@Path("/games")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GameResource {

    private final GameDAO gameDAO = new GameDAO();
    private final PlayerDAO playerDAO = new PlayerDAO();
    private final GamePlayerDAO gamePlayerDAO = new GamePlayerDAO();
    private final RoundDAO roundDAO = new RoundDAO();
    private final BetDAO betDAO = new BetDAO();
    private final RoundPlayDAO roundPlayDAO = new RoundPlayDAO();
    private final RoundHandDAO roundHandDAO = new RoundHandDAO();
    private final PlayerCardDAO playerCardDAO = new PlayerCardDAO();
    private final RoundScoreDAO roundScoreDAO = new RoundScoreDAO();

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
                .orElseThrow(() -> new WebApplicationException("Crea un player antes", 400));

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
    public Response ready(
            @PathParam("code") String code,
            @Context SecurityContext ctx
    ) {
        UUID userId = getUserId(ctx);

        Game game = gameDAO.findByCode(code);
        Player player = playerDAO.findByUserId(userId)
                .orElseThrow(() -> new WebApplicationException(400));

        gamePlayerDAO.setReady(game.getId(), player.getId(), true);
        return Response.ok(Map.of("message", "READY")).build();
    }

    // =========================
    // START GAME - CORREGIDO
    // =========================
    @POST
    @Path("/{code}/start")
    public Response start(
            @PathParam("code") String code,
            @Context SecurityContext ctx
    ) {
        UUID userId = getUserId(ctx);

        Game game = gameDAO.findByCode(code);
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Partida no encontrada")
                    .build();
        }

        Player player = playerDAO.findByUserId(userId)
                .orElseThrow(() -> new WebApplicationException(400));

        if (!gamePlayerDAO.isHost(game.getId(), player.getId())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Solo el host puede iniciar la partida")
                    .build();
        }

        if (!gamePlayerDAO.areAllPlayersReady(game.getId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("No todos están READY")
                    .build();
        }

        // Marcar juego como iniciado
        gameDAO.markStarted(game.getId());

        // Crear primera ronda y repartir cartas inmediatamente
        roundDAO.createFirstRound(game.getId(), game.getStartingCards(), 0);

        // Obtener la ronda recién creada
        RoundView round = roundDAO.findCurrentRound(game.getId());
        if (round == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al crear la ronda")
                    .build();
        }

        // Obtener jugadores y repartir cartas inmediatamente
        List<Long> gamePlayerIds = gamePlayerDAO.getGamePlayerIds(game.getId());
        roundDAO.dealCards(round.getId(), gamePlayerIds, game.getStartingCards());

        return Response.ok(Map.of(
                "message", "GAME_STARTED",
                "roundId", round.getId()
        )).build();
    }

    // =========================
    // GET CURRENT ROUND BETS
    // =========================
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
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No hay ronda activa")
                    .build();
        }

        List<BetDAO.BetRow> rows = betDAO.getBetsForRound(round.getId());

        List<RoundBetsView.BetView> bets = rows.stream()
                .map(r -> {
                    var info = gamePlayerDAO.getPlayerInfo(r.gamePlayerId());
                    return new RoundBetsView.BetView(
                            info.playerId(),
                            info.nickname(),
                            r.betValue(),
                            r.order()
                    );
                })
                .toList();

        return Response.ok(new RoundBetsView(round.getId(), bets)).build();
    }

    // =========================
    // PLACE BET
    // =========================
    @POST
    @Path("/{code}/rounds/current/bets")
    public Response placeBet(
            @PathParam("code") String code,
            Map<String, Integer> body,
            @Context SecurityContext ctx
    ) {
        UUID userId = getUserId(ctx);

        Game game = gameDAO.findByCode(code);
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Partida no encontrada")
                    .build();
        }

        RoundView round = roundDAO.findCurrentRound(game.getId());
        if (round == null || !"BETTING".equals(round.getPhase())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("No se puede apostar ahora")
                    .build();
        }

        Player player = playerDAO.findByUserId(userId)
                .orElseThrow(() -> new WebApplicationException(400));

        long gpId = gamePlayerDAO.getGamePlayerId(game.getId(), player.getId());

        // No permitir apostar dos veces
        if (betDAO.hasBet(round.getId(), gpId)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Ya has apostado")
                    .build();
        }

        Integer value = body.get("value");
        if (value == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Apuesta inválida")
                    .build();
        }

        int cardsPerPlayer = round.getCardsPerPlayer();
        int totalPlayers = gamePlayerDAO.countPlayers(game.getId());
        int betsSoFar = betDAO.countBets(round.getId());
        int sumSoFar = betDAO.sumBets(round.getId());

        // Validación básica
        if (value < 0 || value > cardsPerPlayer) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Apuesta fuera de rango")
                    .build();
        }

        // Regla Oh Hell
        boolean isLastBetter = (betsSoFar == totalPlayers - 1);
        if (isLastBetter && (sumSoFar + value == cardsPerPlayer)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Apuesta inválida: no puede cerrar la suma")
                    .build();
        }

        // Orden de apuesta
        int order = betDAO.nextBetOrder(round.getId());

        // Insertar apuesta
        betDAO.placeBet(round.getId(), gpId, value, order);

        // ¿Han apostado todos?
        int totalBets = betDAO.countBets(round.getId());

        if (totalBets == totalPlayers) {
            // Empieza la fase de juego
            roundDAO.startPlayingPhase(round.getId());
        }

        return Response.ok(Map.of(
                "message", "BET_PLACED",
                "value", value
        )).build();
    }

    // =========================
    // PLAY CARD
    // =========================
    @POST
    @Path("/{code}/rounds/current/play")
    public Response play(
            @PathParam("code") String code,
            PlayCardRequest req,
            @Context SecurityContext ctx
    ) {
        UUID userId = getUserId(ctx);

        Game game = gameDAO.findByCode(code);
        RoundView round = roundDAO.findCurrentRound(game.getId());

        if (round == null || !"PLAYING".equals(round.getPhase())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("No se puede jugar ahora")
                    .build();
        }

        Player player = playerDAO.findByUserId(userId)
                .orElseThrow(() -> new WebApplicationException(400));

        long gpId = gamePlayerDAO.getGamePlayerId(game.getId(), player.getId());

        int total = gamePlayerDAO.countPlayers(game.getId());
        int plays = roundPlayDAO.countPlays(round.getId());

        int firstSeat = (round.getDealerSeat() + 1) % total;
        int expectedSeat = (firstSeat + plays) % total;
        int seat = gamePlayerDAO.getSeat(game.getId(), player.getId());

        if (seat != expectedSeat) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("No es tu turno")
                    .build();
        }

        String card = req.getCard();
        String suit = card.split("_")[1];

        String leadSuit = roundHandDAO.getLeadSuit(round.getId());
        if (leadSuit == null) {
            roundHandDAO.setLeadSuit(round.getId(), suit);
        } else if (!suit.equals(leadSuit)
                && playerCardDAO.playerHasSuit(round.getId(), gpId, leadSuit)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debes seguir el palo")
                    .build();
        }

        playerCardDAO.removeCard(round.getId(), gpId, card);
        roundPlayDAO.playCard(round.getId(), gpId, card, plays);

        return Response.ok(Map.of("card", card)).build();
    }

    // =========================
    // TRICK STATE
    // =========================
    @GET
    @Path("/{code}/rounds/current/trick")
    public Response trick(
            @PathParam("code") String code,
            @Context SecurityContext ctx
    ) {
        getUserId(ctx);

        Game game = gameDAO.findByCode(code);
        RoundView round = roundDAO.findCurrentRound(game.getId());

        if (round == null || !"PLAYING".equals(round.getPhase())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La ronda no está en juego")
                    .build();
        }

        int total = gamePlayerDAO.countPlayers(game.getId());
        int plays = roundPlayDAO.countPlays(round.getId());

        int firstSeat = (round.getDealerSeat() + 1) % total;
        int seat = (firstSeat + plays) % total;

        UUID currentPlayer =
                gamePlayerDAO.getPlayerIdBySeat(game.getId(), seat);

        List<TrickStateView.PlayedCardView> cards = new ArrayList<>();
        for (var p : roundPlayDAO.getPlays(round.getId())) {
            cards.add(new TrickStateView.PlayedCardView(
                    gamePlayerDAO.getPlayerIdByGamePlayerId(p.gamePlayerId()),
                    p.card(),
                    p.order()
            ));
        }

        return Response.ok(new TrickStateView(
                currentPlayer,
                roundHandDAO.getLeadSuit(round.getId()),
                cards
        )).build();
    }

    private UUID getUserId(SecurityContext ctx) {
        return ((UserPrincipal) ctx.getUserPrincipal()).getUserId();
    }

    // =========================
    // GET CURRENT ROUND
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

        RoundView round = roundDAO.findCurrentRound(game.getId());
        if (round == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No hay ronda activa")
                    .build();
        }

        return Response.ok(round).build();
    }

    // =========================
    // HAND - CORREGIDO: Permitir BETTING y PLAYING
    // =========================
    @GET
    @Path("/{code}/hand")
    public Response hand(
            @PathParam("code") String code,
            @Context SecurityContext ctx
    ) {
        UUID userId = getUserId(ctx);

        Game game = gameDAO.findByCode(code);
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Partida no encontrada")
                    .build();
        }

        RoundView round = roundDAO.findCurrentRound(game.getId());

        // Permitir obtener mano en fase BETTING y PLAYING
        Set<String> validPhases = Set.of("BETTING", "PLAYING");
        if (round == null || !validPhases.contains(round.getPhase())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La ronda no está en juego o apuestas")
                    .build();
        }

        Player player = playerDAO.findByUserId(userId)
                .orElseThrow(() -> new WebApplicationException(400));

        long gpId = gamePlayerDAO.getGamePlayerId(game.getId(), player.getId());

        List<String> cards = playerCardDAO.getHand(round.getId(), gpId);

        return Response.ok(
                Map.of(
                        "roundId", round.getId(),
                        "cards", cards,
                        "count", cards.size()
                )
        ).build();
    }

    // =========================
    // LIST AVAILABLE GAMES
    // =========================
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAvailableGames(
            @QueryParam("status") String status,
            @Context SecurityContext securityContext
    ) {
        getUserId(securityContext);

        String gameStatus = status != null ? status : "WAITING";

        List<Map<String, Object>> games = gameDAO.findAvailableGames(gameStatus);

        return Response.ok(games).build();
    }

    // =========================
    // GET GAME RESULTS
    // =========================
    @GET
    @Path("/{code}/results")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGameResults(
            @PathParam("code") String code,
            @Context SecurityContext securityContext
    ) {
        getUserId(securityContext);

        Game game = gameDAO.findByCode(code);
        if (game == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<GamePlayerDAO.PlayerInfo> gameInfos = gamePlayerDAO.getGamePlayers(game.getId());

        List<Map<String, Object>> players = gameInfos.stream()
                .map(info -> {
                    int totalPoints = roundScoreDAO.getPlayerTotalScore(game.getId(), info.playerId());
                    int totalTricks = roundScoreDAO.getPlayerTotalTricks(game.getId(), info.playerId());

                    Map<String, Object> playerMap = new HashMap<>();
                    playerMap.put("playerId", info.playerId());
                    playerMap.put("nickname", info.nickname());
                    playerMap.put("points", totalPoints);
                    playerMap.put("tricks", totalTricks);

                    return playerMap;
                })
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("players", players);

        return Response.ok(result).build();
    }
}