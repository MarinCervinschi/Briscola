package com.cervinschi.marin.javafx.briscola.models;

public class Hand {
    private final Card[] cards;

    public Hand(Card[] cards) {
        this.cards = cards;
    }

    public Card[] getCards() {
        return cards;
    }

    public Card getCard(int index) {
        return cards[index];
    }

    public void setCard(int index, Card card) {
        cards[index] = card;
    }

    public void removeCard(int index) {
        cards[index] = null;
    }

    public int size() {
        return cards.length;
    }

    public boolean isEmpty() {
        for (Card card : cards) {
            if (card != null) {
                return false;
            }
        }
        return true;
    }

    public boolean isFull() {
        for (Card card : cards) {
            if (card == null) {
                return false;
            }
        }
        return true;
    }
}
