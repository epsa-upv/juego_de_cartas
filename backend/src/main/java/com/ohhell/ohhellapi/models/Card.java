package com.ohhell.ohhellapi.models;

public class Card {
    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES;
        
        public static Suit fromString(String s) {
            if (s == null) return HEARTS;
            switch (s.toLowerCase()) {
                case "hearts": return HEARTS;
                case "diamonds": return DIAMONDS;
                case "clubs": return CLUBS;
                case "spades": return SPADES;
                default: return HEARTS;
            }
        }
        
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
    
    public enum Rank {
        TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE;
        
        public static Rank fromString(String s) {
            if (s == null) return ACE;
            switch (s.toUpperCase()) {
                case "2": return TWO;
                case "3": return THREE;
                case "4": return FOUR;
                case "5": return FIVE;
                case "6": return SIX;
                case "7": return SEVEN;
                case "8": return EIGHT;
                case "9": return NINE;
                case "10": return TEN;
                case "J": case "JACK": return JACK;
                case "Q": case "QUEEN": return QUEEN;
                case "K": case "KING": return KING;
                case "A": case "ACE": return ACE;
                default: return ACE;
            }
        }
        
        @Override
        public String toString() {
            switch (this) {
                case TWO: return "2";
                case THREE: return "3";
                case FOUR: return "4";
                case FIVE: return "5";
                case SIX: return "6";
                case SEVEN: return "7";
                case EIGHT: return "8";
                case NINE: return "9";
                case TEN: return "10";
                case JACK: return "J";
                case QUEEN: return "Q";
                case KING: return "K";
                case ACE: return "A";
                default: return "A";
            }
        }
    }
    
    private String suit;
    private String rank;
    
    public Card() {}
    
    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }
    
    public String getSuit() { return suit; }
    public void setSuit(String suit) { this.suit = suit; }
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
    
    public int getValue() {
        if (rank == null) return 0;
        switch (rank.toUpperCase()) {
            case "A": return 14;
            case "K": return 13;
            case "Q": return 12;
            case "J": return 11;
            default:
                try {
                    return Integer.parseInt(rank);
                } catch (NumberFormatException e) {
                    return 0;
                }
        }
    }
    
    @Override
    public String toString() {
        return rank + " of " + suit;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return suit.equals(card.suit) && rank.equals(card.rank);
    }
}
