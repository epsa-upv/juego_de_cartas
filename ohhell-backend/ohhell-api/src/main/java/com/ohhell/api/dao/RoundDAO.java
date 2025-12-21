package com.ohhell.api.dao;

import com.ohhell.api.db.Database;
import com.ohhell.api.models.RoundView;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.*;

public class RoundDAO {

    public void createFirstRound(UUID gameId, int cardsPerPlayer, int dealerSeat) {
        System.out.println("🎲 Creando primera ronda para juego: " + gameId);

        String sql = """
            INSERT INTO oh_hell.rounds
            (game_id, number, cards_per_player, dealer_seat, phase, started_at)
            VALUES (?, 1, ?, ?, 'BETTING', now())
            RETURNING id
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ps.setInt(2, cardsPerPlayer);
            ps.setInt(3, dealerSeat);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long roundId = rs.getLong("id");
                System.out.println("✅ Ronda creada con ID: " + roundId);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error creando ronda: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void updatePhase(long roundId, String phase) {
        System.out.println("🔄 Actualizando fase de ronda " + roundId + " a: " + phase);

        String sql = "UPDATE oh_hell.rounds SET phase = ? WHERE id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, phase);
            ps.setLong(2, roundId);
            ps.executeUpdate();

            System.out.println("✅ Fase actualizada");

        } catch (SQLException e) {
            System.err.println("❌ Error actualizando fase: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public RoundView findCurrentRound(UUID gameId) {
        System.out.println("🔍 Buscando ronda actual para juego: " + gameId);

        String sql = """
            SELECT id, number, cards_per_player, dealer_seat, phase, started_at
            FROM oh_hell.rounds
            WHERE game_id = ?
              AND finished_at IS NULL
            ORDER BY number DESC
            LIMIT 1
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, gameId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("ℹ️ No hay ronda activa para juego: " + gameId);
                return null;
            }

            RoundView round = new RoundView(
                    rs.getLong("id"),
                    rs.getInt("number"),
                    rs.getInt("cards_per_player"),
                    rs.getInt("dealer_seat"),
                    rs.getString("phase"),
                    rs.getObject("started_at", OffsetDateTime.class)
            );

            System.out.println("✅ Ronda encontrada: ID=" + round.getId() +
                    ", Fase=" + round.getPhase() +
                    ", Cartas=" + round.getCardsPerPlayer());

            return round;

        } catch (SQLException e) {
            System.err.println("❌ Error buscando ronda: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // =========================
    // REPARTO + TRIUNFO (FIX ENUMS)
    // =========================
    public void dealCards(long roundId, List<Long> gamePlayerIds, int cardsPerPlayer) {
        System.out.println("🎴 Repartiendo cartas para ronda: " + roundId);
        System.out.println("🎴 Jugadores: " + gamePlayerIds.size() +
                ", Cartas por jugador: " + cardsPerPlayer);

        PlayerCardDAO playerCardDAO = new PlayerCardDAO();
        CardDAO cardDAO = new CardDAO();

        List<String> deck = buildDeck();
        Collections.shuffle(deck);
        System.out.println("✅ Mazo creado y barajado: " + deck.size() + " cartas");

        Iterator<String> it = deck.iterator();

        // Repartir cartas
        int totalCards = 0;
        for (int i = 0; i < cardsPerPlayer; i++) {
            for (long gpId : gamePlayerIds) {
                if (it.hasNext()) {
                    String card = it.next();
                    playerCardDAO.addCard(roundId, gpId, card);
                    totalCards++;
                }
            }
        }
        System.out.println("✅ Cartas repartidas: " + totalCards + " cartas");

        // Carta de triunfo
        if (it.hasNext()) {
            String trump = it.next();
            System.out.println("🎯 Carta de triunfo: " + trump);

            String[] parts = trump.split("_");

            String rankEnum = mapRank(parts[0]);
            String suitEnum = mapSuit(parts[1]);

            int trumpCardId = cardDAO.findCardId(rankEnum, suitEnum);
            System.out.println("🎯 ID de triunfo en BD: " + trumpCardId);

            String sql = "UPDATE oh_hell.rounds SET trump_card_id = ? WHERE id = ?";

            try (Connection c = Database.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, trumpCardId);
                ps.setLong(2, roundId);
                ps.executeUpdate();
                System.out.println("✅ Triunfo guardado en BD");

            } catch (SQLException e) {
                System.err.println("❌ Error guardando triunfo: " + e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("⚠️ No hay cartas para triunfo");
        }
    }

    private List<String> buildDeck() {
        String[] suits = {"H", "D", "C", "S"};
        String[] ranks = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};

        List<String> deck = new ArrayList<>();

        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(rank + "_" + suit);
            }
        }

        return deck;
    }

    // =========================
    // ENUM MAPPERS (CLAVE)
    // =========================
    private String mapSuit(String s) {
        return switch (s) {
            case "H" -> "HEARTS";
            case "D" -> "DIAMONDS";
            case "C" -> "CLUBS";
            case "S" -> "SPADES";
            default -> throw new IllegalArgumentException("Palo inválido: " + s);
        };
    }

    private String mapRank(String r) {
        return switch (r) {
            case "A" -> "ACE";
            case "K" -> "KING";
            case "Q" -> "QUEEN";
            case "J" -> "JACK";
            default -> r; // 2–10
        };
    }

    public int getTrumpCardId(long roundId) {
        String sql = "SELECT trump_card_id FROM oh_hell.rounds WHERE id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void startPlayingPhase(long roundId) {
        System.out.println("🚀 Iniciando fase de juego para ronda: " + roundId);

        String sqlPlayers = """
            SELECT id
            FROM oh_hell.game_players
            WHERE game_id = (
                SELECT game_id FROM oh_hell.rounds WHERE id = ?
            )
            ORDER BY seat_position
        """;

        String sqlCards = """
            SELECT cards_per_player
            FROM oh_hell.rounds
            WHERE id = ?
        """;

        try (Connection c = Database.getConnection()) {

            // 1️⃣ Obtener jugadores ordenados por asiento
            List<Long> gamePlayerIds = new ArrayList<>();

            try (PreparedStatement ps = c.prepareStatement(sqlPlayers)) {
                ps.setLong(1, roundId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    gamePlayerIds.add(rs.getLong("id"));
                }
                System.out.println("👥 Jugadores encontrados: " + gamePlayerIds.size());
            }

            // 2️⃣ Nº de cartas por jugador
            int cardsPerPlayer;
            try (PreparedStatement ps = c.prepareStatement(sqlCards)) {
                ps.setLong(1, roundId);
                ResultSet rs = ps.executeQuery();
                rs.next();
                cardsPerPlayer = rs.getInt("cards_per_player");
                System.out.println("🎴 Cartas por jugador: " + cardsPerPlayer);
            }

            // 3️⃣ Repartir cartas y triunfo (si aún no se han repartido)
            // Verificar si ya hay cartas repartidas
            if (!hasCardsDealt(roundId)) {
                System.out.println("🃏 Repartiendo cartas...");
                dealCards(roundId, gamePlayerIds, cardsPerPlayer);
            } else {
                System.out.println("ℹ️ Cartas ya repartidas, omitiendo reparto");
            }

            // 4️⃣ Cambiar fase a PLAYING
            updatePhase(roundId, "PLAYING");
            System.out.println("✅ Fase cambiada a PLAYING");

        } catch (SQLException e) {
            System.err.println("❌ Error iniciando fase de juego: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private boolean hasCardsDealt(long roundId) {
        String sql = """
            SELECT COUNT(*) as count 
            FROM oh_hell.round_player_cards 
            WHERE round_id = ?
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roundId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt("count");
            return count > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error verificando cartas: " + e.getMessage());
            return false;
        }
    }
}