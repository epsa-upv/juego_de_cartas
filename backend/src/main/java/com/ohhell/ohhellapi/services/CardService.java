package com.ohhell.ohhellapi.services;

import com.ohhell.ohhellapi.models.Card;
import java.util.*;


public class CardService {

    // Constantes para los palos y rangos
    private static final String[] SUITS = {"hearts", "diamonds", "clubs", "spades"};
    private static final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};


    public List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();

        // Por cada palo (4 palos)
        for (String suit : SUITS) {
            // Por cada rango (13 rangos)
            for (String rank : RANKS) {
                deck.add(new Card(suit, rank));
            }
        }

        return deck; // Devuelve 52 cartas
    }


    public List<Card> shuffleDeck(List<Card> deck) {
        if (deck == null || deck.isEmpty()) {
            return new ArrayList<>();
        }

        // Usa Collections.shuffle para mezclar aleatoriamente
        Collections.shuffle(deck);
        return deck;
    }

    public Map<Long, List<Card>> dealCards(int numCards, List<Long> playerIds) {
        // Validaciones
        if (numCards < 1 || numCards > 10) {
            throw new IllegalArgumentException("Número de cartas inválido: " + numCards);
        }
        if (playerIds == null || playerIds.isEmpty()) {
            throw new IllegalArgumentException("Lista de jugadores vacía");
        }

        int totalCardsNeeded = numCards * playerIds.size();
        if (totalCardsNeeded > 52) {
            throw new IllegalArgumentException("No hay suficientes cartas en la baraja");
        }

        // Crear y barajar el mazo
        List<Card> deck = createDeck();
        deck = shuffleDeck(deck);

        // Mapa para guardar las manos de cada jugador
        Map<Long, List<Card>> hands = new HashMap<>();

        // Inicializar manos vacías
        for (Long playerId : playerIds) {
            hands.put(playerId, new ArrayList<>());
        }

        // Repartir cartas en orden rotatorio
        int cardIndex = 0;
        for (int i = 0; i < numCards; i++) {
            for (Long playerId : playerIds) {
                hands.get(playerId).add(deck.get(cardIndex));
                cardIndex++;
            }
        }

        return hands;
    }

    public Card getTrumpCard(int numCards, int numPlayers) {
        List<Card> deck = createDeck();
        deck = shuffleDeck(deck);

        int cardsDealt = numCards * numPlayers;

        // Si se repartaron todas las cartas, no hay triunfo
        if (cardsDealt >= 52) {
            return null;
        }

        // La carta de triunfo es la siguiente después de repartir
        return deck.get(cardsDealt);
    }


    public boolean isValidPlay(List<Card> playerHand, Card cardToPlay, String leadSuit) {
        // Validaciones básicas
        if (playerHand == null || playerHand.isEmpty()) {
            return false;
        }
        if (cardToPlay == null) {
            return false;
        }

        // Verificar que el jugador tiene la carta
        boolean hasCard = playerHand.stream()
                .anyMatch(c -> c.getSuit().equals(cardToPlay.getSuit())
                        && c.getRank().equals(cardToPlay.getRank()));

        if (!hasCard) {
            return false; // No tiene esa carta
        }

        // Si es el primero en jugar (no hay leadSuit)
        if (leadSuit == null || leadSuit.isEmpty()) {
            return true; // Puede jugar cualquier carta
        }

        // Verificar si tiene cartas del palo inicial
        boolean hasLeadSuit = playerHand.stream()
                .anyMatch(c -> c.getSuit().equals(leadSuit));

        // Si tiene del palo inicial, DEBE jugar ese palo
        if (hasLeadSuit) {
            return cardToPlay.getSuit().equals(leadSuit);
        }

        // Si NO tiene del palo inicial, puede jugar cualquiera
        return true;
    }


    public Card determineCardWinner(Card card1, Card card2, String trumpSuit, String leadSuit) {
        if (card1 == null) return card2;
        if (card2 == null) return card1;

        boolean card1IsTrump = card1.getSuit().equals(trumpSuit);
        boolean card2IsTrump = card2.getSuit().equals(trumpSuit);

        // Caso 1: Una es triunfo y la otra no
        if (card1IsTrump && !card2IsTrump) {
            return card1; // El triunfo siempre gana
        }
        if (card2IsTrump && !card1IsTrump) {
            return card2; // El triunfo siempre gana
        }

        // Caso 2: Ambas son triunfo
        if (card1IsTrump && card2IsTrump) {
            return card1.getValue() > card2.getValue() ? card1 : card2;
        }

        // Caso 3: Ninguna es triunfo
        // Solo cuenta si es del palo inicial (leadSuit)
        boolean card1IsLeadSuit = card1.getSuit().equals(leadSuit);
        boolean card2IsLeadSuit = card2.getSuit().equals(leadSuit);

        if (card1IsLeadSuit && !card2IsLeadSuit) {
            return card1; // Solo card1 es del palo inicial
        }
        if (card2IsLeadSuit && !card1IsLeadSuit) {
            return card2; // Solo card2 es del palo inicial
        }

        // Ambas son del palo inicial o ninguna lo es
        if (card1IsLeadSuit && card2IsLeadSuit) {
            return card1.getValue() > card2.getValue() ? card1 : card2;
        }

        // Ninguna es del palo inicial ni triunfo → gana la primera
        return card1;
    }

    public Long determineTrickWinner(Map<Long, Card> cardsPlayed, String trumpSuit, String leadSuit) {
        if (cardsPlayed == null || cardsPlayed.isEmpty()) {
            return null;
        }

        Long winnerId = null;
        Card winningCard = null;

        for (Map.Entry<Long, Card> entry : cardsPlayed.entrySet()) {
            if (winningCard == null) {
                winnerId = entry.getKey();
                winningCard = entry.getValue();
            } else {
                Card currentWinner = determineCardWinner(
                        winningCard,
                        entry.getValue(),
                        trumpSuit,
                        leadSuit
                );

                if (!currentWinner.equals(winningCard)) {
                    winnerId = entry.getKey();
                    winningCard = entry.getValue();
                }
            }
        }

        return winnerId;
    }

    public String cardToString(Card card) {
        if (card == null) return "null";

        String suitSymbol;
        switch (card.getSuit().toLowerCase()) {
            case "hearts": suitSymbol = "♥"; break;
            case "diamonds": suitSymbol = "♦"; break;
            case "clubs": suitSymbol = "♣"; break;
            case "spades": suitSymbol = "♠"; break;
            default: suitSymbol = card.getSuit();
        }

        return card.getRank() + suitSymbol;
    }
}