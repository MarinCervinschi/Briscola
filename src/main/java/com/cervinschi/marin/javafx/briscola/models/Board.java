package com.cervinschi.marin.javafx.briscola.models;

import java.util.Map;

import static com.cervinschi.marin.javafx.briscola.utils.Const.*;

public class Board {
    private Hand[] hands;
    private final Card[] deck = new Card[DECK];
    private Card[] table = new Card[2];
    private Card briscola;

    public Board() {
        createDeck();
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

    public void createDeck() {
        Map<String, Integer> values = Map.of("Ace", 11, "Two", 0, "Three", 10, "Four", 0, "Five", 0, "Six", 0, "Seven", 0, "Jack", 2, "Horse", 3, "King", 4);
        String[] seeds = {"denara", "coppe", "spade", "bastoni"};
        int index = 0;
        for (String seed : seeds) {
            for (Map.Entry<String, Integer> entry : values.entrySet()) {
                Card card = new Card(entry.getKey(), seed, entry.getValue(), false);
                deck[index] = card;
                index++;
            }
        }
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
