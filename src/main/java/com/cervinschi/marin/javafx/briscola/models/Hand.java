package com.cervinschi.marin.javafx.briscola.models;

import javafx.scene.shape.Rectangle;

public class Hand {
    private final Rectangle[] cardsObject;
    private final Card[] cards;

    public Hand() {
        cardsObject = new Rectangle[3];
        cards = new Card[3];
    }

    public void addCard(Card card) {
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] == null) {
                cards[i] = card;
                break;
            }
        }
    }

    public Card[] getCards() {
        return cards;
    }

    public Rectangle[] getCardsObject() {
        return cardsObject;
    }


    public boolean isEmptyObject() {
        for (Rectangle card : cardsObject) {
            if (card != null) {
                return false;
            }
        }
        return true;
    }

    public void addCardObject(Rectangle card) {
        for (int i = 0; i < cards.length; i++) {
            if (cardsObject[i] == null) {
                cardsObject[i] = card;
                break;
            }
        }
    }

    public void removeCardObject(Rectangle card) {
        for (int i = 0; i < cards.length; i++) {
            if (cardsObject[i] != null && cardsObject[i].equals(card)) {
                cardsObject[i] = null;
                break;
            }
        }
    }

    public void removeCard(Card card) {
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] != null && cards[i].equals(card)) {
                cards[i] = null;
                break;
            }
        }
    }

    public boolean containsCard(Card card) {
        for (Card c : cards) {
            if (c != null && c.equals(card)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsCardObject(Rectangle card) {
        for (Rectangle c : cardsObject) {
            if (c != null && c.equals(card)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCardsObjectFull() {
        for (Rectangle card : cardsObject) {
            if (card == null) {
                return true;
            }
        }
        return false;
    }
}
