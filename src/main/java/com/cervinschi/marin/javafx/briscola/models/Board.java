package com.cervinschi.marin.javafx.briscola.models;

public class Board {
    private final Hand[] hands;
    private final Card[] deck;
    private Card[] table = new Card[2];
    private final Card briscola;

    public Board(Hand[] hands, Card[] deck, Card briscola) {
        this.hands = hands;
        this.deck = deck;
        this.briscola = briscola;
    }

    public Hand[] getHands() {
        return hands;
    }

    public Hand getHand(int index) {
        return hands[index];
    }

    public void setHand(int index, Hand hand) {
        hands[index] = hand;
    }

    public Card[] getDeck() {
        return deck;
    }

    public Card getDeckCard(int index) {
        return deck[index];
    }

    public void setDeckCard(int index, Card card) {
        deck[index] = card;
    }

    public Card getBriscola() {
        return briscola;
    }

    public int getPoints() {
        return table[0].getValue() + table[1].getValue();
    }

    public Card[] getTable() {
        return table;
    }

    public void setTable(Card[] table) {
        this.table = table;
    }
}
