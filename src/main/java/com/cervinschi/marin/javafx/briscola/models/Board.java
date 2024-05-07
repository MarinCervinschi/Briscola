package com.cervinschi.marin.javafx.briscola.models;

import java.util.*;

import static com.cervinschi.marin.javafx.briscola.utils.Const.*;

public class Board {
    private final Hand[] hands = new Hand[2];
    private Deque<Card> deck = new ArrayDeque<>();
    private Card[] table = new Card[2];

    public Board() {
        createDeck();
        shuffleDeck();
    }

    public Hand[] getHands() {
        return hands;
    }

    public Hand getHand(int index) {
        return hands[index];
    }
    public Deque<Card> getDeck() {
        return deck;
    }

    public void setHand(int index, Hand hand) {
        hands[index] = hand;
    }

    public void createDeck() {
        Map<String, Integer> values = Map.of("1", 11, "2", 0, "3", 10, "4", 0, "5", 0, "6", 0, "7", 0, "8", 2, "9", 3, "10", 4);
        String[] seeds = {"denara", "coppe", "spade", "bastoni"};
        for (String seed : seeds) {
            for (Map.Entry<String, Integer> entry : values.entrySet()) {
                Card card = new Card(entry.getKey(), seed, entry.getValue(), false);
                deck.add(card);
            }
        }
    }

    public void shuffleDeck() {
        List<Card> deckList = new ArrayList<>(List.copyOf(deck));
        Collections.shuffle(deckList);
        deck = new ArrayDeque<>(deckList);
    }

    public void popHead() {
        deck.poll();
    }

    public void setBriscola(String seed) {
        for (Card card : deck) {
            if (card.getSeed().equals(seed) && !card.isBriscola()) {
                card.setBriscola(true);
            }
        }
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
