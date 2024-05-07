package com.cervinschi.marin.javafx.briscola.models;

import javafx.scene.shape.Rectangle;

import java.util.*;

import static com.cervinschi.marin.javafx.briscola.utils.Const.*;

public class Board {
    private final Deque<Hand> hands = new ArrayDeque<>(2);
    private Deque<Card> deck = new ArrayDeque<>();
    private final Card[] table = new Card[2];
    private Card briscola;
    private int playerPoints;
    private int botPoints;

    public Board() {
        createDeck();
        shuffleDeck();
        playerPoints = 0;
        botPoints = 0;
    }

    public Deque<Hand> getHands() {
        return hands;
    }
    public boolean handsAreFull() {
        for (Hand hand : hands) {
            if (hand.isEmpty()) {
                return false;
            }
        }
        return true;
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

    public void setPlayerPoints(int points) {
        playerPoints = points;
    }

    public void setBotPoints(int points) {
        botPoints = points;
    }

    public int getPlayerPoints() {
        return playerPoints;
    }

    public int getBotPoints() {
        return botPoints;
    }

    public void addCardToTable(Card card) {
        if (table[0] == null) {
            table[0] = card;
        } else {
            table[1] = card;
        }
    }

    public void removeCardFromHand(Card card, Rectangle cardObject) {
        for (Hand hand : hands) {
            if (hand.containsCard(card) && hand.containsCardObject(cardObject)) {
                hand.removeCard(card);
                hand.removeCardObject(cardObject);
            }

        }
    }



    public boolean tableIsFull() {
        return table[0] != null && table[1] != null;
    }

    public Card getTable(int index) {
        return table[index];
    }

    public void clearTable() {
        table[0] = null;
        table[1] = null;
    }

}
