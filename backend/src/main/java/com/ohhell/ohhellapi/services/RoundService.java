    package com.ohhell.ohhellapi.services;

    import com.ohhell.ohhellapi.dao.RoundDAO;
    import com.ohhell.ohhellapi.dao.PlayerDAO;
    import com.ohhell.ohhellapi.dao.GameDAO;
    import com.ohhell.ohhellapi.dao.BidDAO;
    import com.ohhell.ohhellapi.dao.TrickDAO;
    import com.ohhell.ohhellapi.models.Round;
    import com.ohhell.ohhellapi.models.Player;
    import com.ohhell.ohhellapi.models.Game;
    import com.ohhell.ohhellapi.models.Bid;
    import com.ohhell.ohhellapi.models.Trick;
    import com.ohhell.ohhellapi.models.Card;

    import java.sql.SQLException;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;

    /**
     * RoundService - Servicio para gestión de rondas
     *
     * Oh Hell! Card Game - UPV
     * Autor: Tomás Criado García
     *
     * Orquesta el flujo completo de una ronda:
     * - Creación y reparto de cartas
     * - Fases de apuestas y juego
     * - Cálculo de resultados y vidas
     */
    public class RoundService {

        private final RoundDAO roundDAO;
        private final PlayerDAO playerDAO;
        private final GameDAO gameDAO;
        private final BidDAO bidDAO;
        private final TrickDAO trickDAO;
        private final CardService cardService;
        private final BidService bidService;
        private final TrickService trickService;

        // Configuración según las reglas
        private static final int STARTING_CARDS = 5;
        private static final int TOTAL_ROUNDS = 5;

        public RoundService() {
            this.roundDAO = new RoundDAO();
            this.playerDAO = new PlayerDAO();
            this.gameDAO = new GameDAO();
            this.bidDAO = new BidDAO();
            this.trickDAO = new TrickDAO();
            this.cardService = new CardService();
            this.bidService = new BidService();
            this.trickService = new TrickService();
        }

        /**
         * Crea una nueva ronda
         *
         * @param gameId ID de la partida
         * @param roundNumber Número de la ronda (1-5)
         * @param dealerId ID del dealer
         * @return Round creado
         * @throws SQLException si hay error en BD
         */
        public Round createRound(Long gameId, int roundNumber, Long dealerId) throws SQLException {
            // Validaciones
            Game game = gameDAO.getGameById(gameId);
            if (game == null) {
                throw new IllegalArgumentException("La partida no existe");
            }

            if (!game.getStatus().equalsIgnoreCase("IN_PROGRESS")) {
                throw new IllegalStateException("La partida no está en progreso");
            }

            if (roundNumber < 1 || roundNumber > TOTAL_ROUNDS) {
                throw new IllegalArgumentException("Número de ronda inválido: " + roundNumber);
            }

            // Calcular número de cartas según la ronda
            // Ronda 1: 5 cartas, Ronda 2: 4 cartas, ..., Ronda 5: 1 carta
            int numCards = STARTING_CARDS - (roundNumber - 1);

            // Obtener jugadores activos
            List<Player> activePlayers = playerDAO.getActivePlayersByGameId(gameId);

            // Obtener carta de triunfo
            Card trumpCard = cardService.getTrumpCard(numCards, activePlayers.size());

            // Crear ronda
            Round round = new Round();
            round.setGameId(gameId);
            round.setRoundNumber(roundNumber);
            round.setNumCards(numCards);
            round.setDealerId(dealerId);
            round.setStatus("BIDDING");

            if (trumpCard != null) {
                round.setTrump(trumpCard);
            }

            Round createdRound = roundDAO.createRound(round);

            System.out.println("\n🎲 Ronda " + roundNumber + " creada");
            System.out.println("   Cartas: " + numCards);
            System.out.println("   Triunfo: " +
                    (trumpCard != null ? cardService.cardToString(trumpCard) : "ninguno"));
            System.out.println("   Dealer: Player " + dealerId);

            return createdRound;
        }

        /**
         * Reparte las cartas a todos los jugadores
         *
         * @param roundId ID de la ronda
         * @return Map de playerId -> List<Card>
         * @throws SQLException si hay error en BD
         */
        public Map<Long, List<Card>> dealCardsToPlayers(Long roundId) throws SQLException {
            Round round = roundDAO.getRoundById(roundId);
            if (round == null) {
                throw new IllegalArgumentException("La ronda no existe");
            }

            // Obtener jugadores activos
            List<Player> activePlayers = playerDAO.getActivePlayersByGameId(round.getGameId());
            List<Long> playerIds = activePlayers.stream()
                    .map(Player::getId)
                    .toList();

            // Repartir cartas usando CardService
            Map<Long, List<Card>> hands = cardService.dealCards(round.getNumCards(), playerIds);

            // Actualizar las manos de cada jugador en la BD
            for (Map.Entry<Long, List<Card>> entry : hands.entrySet()) {
                Player player = playerDAO.getPlayerById(entry.getKey());
                player.setHand(entry.getValue());
                playerDAO.updatePlayer(player);
            }

            System.out.println("🃏 Cartas repartidas a " + activePlayers.size() + " jugadores");

            return hands;
        }

        /**
         * Inicia la fase de apuestas
         *
         * @param roundId ID de la ronda
         * @return true si se inició correctamente
         * @throws SQLException si hay error en BD
         */
        public boolean startBiddingPhase(Long roundId) throws SQLException {
            Round round = roundDAO.getRoundById(roundId);
            if (round == null) {
                throw new IllegalArgumentException("La ronda no existe");
            }

            // Cambiar estado a BIDDING
            roundDAO.updateRoundStatus(roundId, "BIDDING");

            System.out.println("🎲 Fase de apuestas iniciada");
            System.out.println("   Orden de apuestas: derecha del dealer → ... → dealer (último)");

            return true;
        }

        /**
         * Inicia la fase de juego
         *
         * Verifica que todos hayan apostado y cambia el estado a PLAYING
         *
         * @param roundId ID de la ronda
         * @return true si se inició correctamente
         * @throws SQLException si hay error en BD
         */
        public boolean startPlayingPhase(Long roundId) throws SQLException {
            Round round = roundDAO.getRoundById(roundId);
            if (round == null) {
                throw new IllegalArgumentException("La ronda no existe");
            }

            if (!round.getStatus().equalsIgnoreCase("BIDDING")) {
                throw new IllegalStateException("La ronda no está en fase de apuestas");
            }

            // Verificar que todos apostaron
            if (!bidService.allPlayersHaveBid(roundId)) {
                throw new IllegalStateException("No todos los jugadores han apostado");
            }

            // Cambiar estado a PLAYING
            roundDAO.updateRoundStatus(roundId, "PLAYING");

            // Crear primera baza
            Trick firstTrick = trickService.createTrick(roundId);

            System.out.println("🎮 Fase de juego iniciada");
            System.out.println("   Primera baza creada (Trick " + firstTrick.getTrickNumber() + ")");

            return true;
        }

        /**
         * Verifica si una ronda está completa
         *
         * @param roundId ID de la ronda
         * @return true si todas las bazas están completadas
         * @throws SQLException si hay error en BD
         */
        public boolean isRoundComplete(Long roundId) throws SQLException {
            Round round = roundDAO.getRoundById(roundId);
            if (round == null) return false;

            // Contar bazas completadas
            int completedTricks = trickDAO.countCompletedTricksInRound(roundId);

            // Comparar con número de cartas (= número de bazas)
            return completedTricks == round.getNumCards();
        }

        /**
         * Calcula los resultados de la ronda
         *
         * Compara las apuestas con las bazas ganadas y determina
         * cuántas vidas pierde cada jugador
         *
         * @param roundId ID de la ronda
         * @return Lista de resultados
         * @throws SQLException si hay error en BD
         */
        public List<RoundResult> calculateRoundResults(Long roundId) throws SQLException {
            Round round = roundDAO.getRoundById(roundId);
            if (round == null) {
                throw new IllegalArgumentException("La ronda no existe");
            }

            // Verificar que la ronda está completa
            if (!isRoundComplete(roundId)) {
                throw new IllegalStateException("La ronda no está completa");
            }

            // Obtener todas las apuestas
            List<Bid> bids = bidDAO.getBidsByRoundId(roundId);
            List<RoundResult> results = new ArrayList<>();

            System.out.println("\n📊 Resultados Ronda " + round.getRoundNumber());
            System.out.println("─────────────────────────────────────");

            for (Bid bid : bids) {
                Player player = playerDAO.getPlayerById(bid.getPlayerId());

                boolean success = bid.getBidAmount() == bid.getTricksWon();
                int difference = Math.abs(bid.getBidAmount() - bid.getTricksWon());
                int livesLost = success ? 0 : difference;

                RoundResult result = new RoundResult(
                        player.getId(),
                        player.getName(),
                        bid.getBidAmount(),
                        bid.getTricksWon(),
                        success,
                        livesLost
                );

                results.add(result);

                // Mostrar resultado
                String status = success ? "✅ ACERTÓ" : "❌ FALLÓ";
                System.out.printf("   %s: apostó %d, ganó %d %s → -%d vidas%n",
                        player.getName(),
                        bid.getBidAmount(),
                        bid.getTricksWon(),
                        status,
                        livesLost
                );
            }

            System.out.println("─────────────────────────────────────");

            return results;
        }

        /**
         * Procesa el final de la ronda
         *
         * Calcula resultados, actualiza vidas de jugadores,
         * elimina jugadores sin vidas, y marca la ronda como completada
         *
         * @param roundId ID de la ronda
         * @return Lista de resultados
         * @throws SQLException si hay error en BD
         */
        public List<RoundResult> processRoundEnd(Long roundId) throws SQLException {
            Round round = roundDAO.getRoundById(roundId);
            if (round == null) {
                throw new IllegalArgumentException("La ronda no existe");
            }

            // Calcular resultados
            List<RoundResult> results = calculateRoundResults(roundId);

            System.out.println("\n💔 Actualizando vidas...");

            // Actualizar vidas de cada jugador
            for (RoundResult result : results) {
                Player player = playerDAO.getPlayerById(result.getPlayerId());

                int currentLives = player.getLives();
                int newLives = Math.max(0, currentLives - result.getLivesLost());

                player.setLives(newLives);

                // Si se quedó sin vidas, eliminar
                if (newLives == 0) {
                    player.setStatus(Player.PlayerStatus.ELIMINATED);
                    System.out.println("   💀 " + player.getName() + " ELIMINADO (0 vidas)");
                } else {
                    System.out.println("   " + player.getName() + ": " +
                            currentLives + " → " + newLives + " vidas");
                }

                playerDAO.updatePlayer(player);
            }

            // Marcar ronda como completada
            roundDAO.updateRoundStatus(roundId, "COMPLETED");

            // Verificar cuántos jugadores quedan
            List<Player> activePlayers = playerDAO.getActivePlayersByGameId(round.getGameId());
            System.out.println("\n👥 Jugadores restantes: " + activePlayers.size());

            return results;
        }

        /**
         * Obtiene el estado completo de una ronda
         *
         * @param roundId ID de la ronda
         * @return RoundState con toda la información
         * @throws SQLException si hay error en BD
         */
        public RoundState getRoundState(Long roundId) throws SQLException {
            Round round = roundDAO.getRoundById(roundId);
            if (round == null) {
                throw new IllegalArgumentException("La ronda no existe");
            }

            List<Player> activePlayers = playerDAO.getActivePlayersByGameId(round.getGameId());
            List<Bid> bids = bidDAO.getBidsByRoundId(roundId);
            List<Trick> tricks = trickDAO.getTricksByRoundId(roundId);

            // Determinar siguiente jugador
            Long nextPlayerId = null;
            if (round.getStatus().equalsIgnoreCase("PLAYING")) {
                Trick currentTrick = trickDAO.getCurrentTrick(roundId);
                if (currentTrick != null) {
                    nextPlayerId = trickService.getNextPlayer(roundId, currentTrick.getId());
                }
            }

            return new RoundState(
                    round,
                    activePlayers,
                    bids,
                    tricks,
                    round.getStatus(),
                    nextPlayerId
            );
        }

        /**
         * Obtiene todas las rondas de una partida
         *
         * @param gameId ID de la partida
         * @return Lista de rondas
         * @throws SQLException si hay error en BD
         */
        public List<Round> getAllRoundsInGame(Long gameId) throws SQLException {
            return roundDAO.getRoundsByGameId(gameId);
        }

        /**
         * Obtiene la ronda actual de una partida
         *
         * @param gameId ID de la partida
         * @return Round actual, o null si no hay
         * @throws SQLException si hay error en BD
         */
        public Round getCurrentRound(Long gameId) throws SQLException {
            return roundDAO.getCurrentRound(gameId);
        }

        /**
         * Determina el orden de apuestas
         *
         * Empieza el jugador a la DERECHA del dealer
         * y continúa en orden horario hasta el dealer (último)
         *
         * @param roundId ID de la ronda
         * @return Lista ordenada de Player IDs
         * @throws SQLException si hay error en BD
         */
        public List<Long> getBiddingOrder(Long roundId) throws SQLException {
            Round round = roundDAO.getRoundById(roundId);
            if (round == null) {
                throw new IllegalArgumentException("La ronda no existe");
            }

            List<Player> activePlayers = playerDAO.getActivePlayersByGameId(round.getGameId());
            List<Long> order = new ArrayList<>();

            // Encontrar índice del dealer
            int dealerIndex = -1;
            for (int i = 0; i < activePlayers.size(); i++) {
                if (activePlayers.get(i).getId().equals(round.getDealerId())) {
                    dealerIndex = i;
                    break;
                }
            }

            if (dealerIndex == -1) {
                // Si no se encuentra dealer, orden normal
                return activePlayers.stream().map(Player::getId).toList();
            }

            // Empezar por el jugador a la derecha del dealer
            // (siguiente en orden horario)
            int startIndex = (dealerIndex + 1) % activePlayers.size();

            // Construir orden
            for (int i = 0; i < activePlayers.size(); i++) {
                int index = (startIndex + i) % activePlayers.size();
                order.add(activePlayers.get(index).getId());
            }

            return order;
        }

        /**
         * Clase para representar el resultado de un jugador en una ronda
         */
        public static class RoundResult {
            private final Long playerId;
            private final String playerName;
            private final int bidAmount;
            private final int tricksWon;
            private final boolean success;
            private final int livesLost;

            public RoundResult(Long playerId, String playerName, int bidAmount,
                               int tricksWon, boolean success, int livesLost) {
                this.playerId = playerId;
                this.playerName = playerName;
                this.bidAmount = bidAmount;
                this.tricksWon = tricksWon;
                this.success = success;
                this.livesLost = livesLost;
            }

            public Long getPlayerId() { return playerId; }
            public String getPlayerName() { return playerName; }
            public int getBidAmount() { return bidAmount; }
            public int getTricksWon() { return tricksWon; }
            public boolean isSuccess() { return success; }
            public int getLivesLost() { return livesLost; }

            @Override
            public String toString() {
                return String.format("%s: bid=%d, won=%d, %s, -%d vidas",
                        playerName, bidAmount, tricksWon,
                        success ? "ACERTÓ" : "FALLÓ", livesLost);
            }
        }

        /**
         * Clase para representar el estado completo de una ronda
         */
        public static class RoundState {
            private final Round round;
            private final List<Player> players;
            private final List<Bid> bids;
            private final List<Trick> tricks;
            private final String currentPhase;
            private final Long nextPlayerId;

            public RoundState(Round round, List<Player> players, List<Bid> bids,
                              List<Trick> tricks, String currentPhase, Long nextPlayerId) {
                this.round = round;
                this.players = players;
                this.bids = bids;
                this.tricks = tricks;
                this.currentPhase = currentPhase;
                this.nextPlayerId = nextPlayerId;
            }

            public Round getRound() { return round; }
            public List<Player> getPlayers() { return players; }
            public List<Bid> getBids() { return bids; }
            public List<Trick> getTricks() { return tricks; }
            public String getCurrentPhase() { return currentPhase; }
            public Long getNextPlayerId() { return nextPlayerId; }
        }
    }