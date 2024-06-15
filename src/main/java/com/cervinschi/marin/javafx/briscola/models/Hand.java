package com.cervinschi.marin.javafx.briscola.models;

import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private final List<Rectangle> cardsObject;
    private final List<Card> cards;

    public Hand() {
        cardsObject = new ArrayList<>();
        cards = new ArrayList<>();
    }

    public Card[] getCards() {
        return cards.toArray(new Card[0]);
    }

    public Rectangle[] getCardsObject() {
        return cardsObject.toArray(new Rectangle[0]);
    }

    public int getLength() {
        return cards.size();
    }

    public void addCard(Card card, Rectangle cardObject) {
        cards.add(card);
        cardsObject.add(cardObject);
    }

    public void removeCard(Card card, Rectangle cardObject) {
        cards.remove(card);
        cardsObject.remove(cardObject);
    }

    public boolean isCardsObjectFull() {
        return cardsObject.size() != 3;
    }

    public boolean isEmptyObject() {
        return cardsObject.isEmpty();
    }
}