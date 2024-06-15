package com.cervinschi.marin.javafx.briscola.models;

import javafx.scene.shape.Rectangle;

import java.util.*;

public class Board {
    private final Hand playerHand;
    private final Hand botHand;
    private final Card[] table;
    private Deque<Card> deck;
    private Card briscolaCard;
    private Rectangle briscolaObject;

    public Board() {
        playerHand = new Hand();
        botHand = new Hand();
        deck = new ArrayDeque<>();
        table = new Card[2];
        createDeck();
        shuffleDeck();
    }

    public Hand getBotHand() {
        return botHand;
    }

    public Hand getPlayerHand() {
        return playerHand;
    }

    public Deque<Card> getDeck() {
        return deck;
    }

    public void setBriscolaToCards(String seed) {
        deck.stream().filter(card -> card.getSeed().equals(seed)).forEach(card -> card.setBriscola(true));
    }

    public Card getBriscolaCard() {
        return briscolaCard;
    }

    public void setBriscolaCard(Card briscola) {
        this.briscolaCard = briscola;
    }

    public Rectangle getBriscolaObject() {
        return briscolaObject;
    }

    public void setBriscolaObject(Rectangle briscolaObject) {
        this.briscolaObject = briscolaObject;
    }

    public Card getTable(int index) {
        return table[index];
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
        for (int i = 0; i < 100; i++) Collections.shuffle(deckList);
        deck = new ArrayDeque<>(deckList);
    }

    public void addCardToTable(Card card) {
        table[table[0] == null ? 0 : 1] = card;
    }

    public boolean tableIsFull() {
        return table[0] != null && table[1] != null;
    }

    public void clearTable() {
        table[0] = null;
        table[1] = null;
    }
}
