package com.cervinschi.marin.javafx.briscola.models;

import javafx.scene.shape.Rectangle;

public class Hand {
    private final Rectangle[] cards;

    public Hand() {
        cards = new Rectangle[3];
    }

    public Rectangle[] getCards() {
        return cards;
    }

    public Rectangle getCard(int index) {
        return cards[index];
    }

    public void setCard(int index, Rectangle card) {
        cards[index] = card;
    }

    public void removeCard(int index) {
        cards[index] = null;
    }

    public void addCard(Rectangle card) {
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] == null) {
                cards[i] = card;
                break;
            }
        }
    }

    public boolean isFull() {
        for (Rectangle card : cards) {
            if (card == null) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        for (Rectangle card : cards) {
            if (card != null) {
                return false;
            }
        }
        return true;
    }


}
