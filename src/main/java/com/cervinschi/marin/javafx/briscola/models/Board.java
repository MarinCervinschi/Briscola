package com.cervinschi.marin.javafx.briscola.models;

import javafx.scene.shape.Rectangle;

import java.util.*;

import static com.cervinschi.marin.javafx.briscola.utils.Const.*;

public class Board {
    private final Deque<Hand> hands = new ArrayDeque<>(2);
    private Deque<Card> deck = new ArrayDeque<>();
    private Rectangle[] table = new Rectangle[2];
    private Card briscola;

    public Board() {
        createDeck();
        shuffleDeck();
    }

    public Deque<Hand> getHands() {
        return hands;
    }


    public Deque<Card> getDeck() {
        return deck;
    }

    public void setHand(Hand hand) {
        hands.add(hand);
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

    public Card popHead() {
        return deck.poll();
    }

    public void setBriscolaToCards(String seed) {
        for (Card card : deck) {
            if (card.getSeed().equals(seed) && !card.isBriscola()) {
                card.setBriscola(true);
            }
        }
    }

    public Card getBriscola() {
        return briscola;
    }

    public void setBriscola(Card briscola) {
        this.briscola = briscola;
    }
    public int getPoints() {
        return 5;
    }

    public void setTable(Rectangle[] table) {
        this.table = table;
    }

}
