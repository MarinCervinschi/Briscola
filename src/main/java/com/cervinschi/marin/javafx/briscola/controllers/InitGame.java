package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Card;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.Cursor;

import java.util.Deque;
import java.util.Objects;

public class InitGame {
    private final Deque<Rectangle> deckObject;
    private Deque<Card> deck;
    private final BorderPane boardPaneHands;
    private final HBox playerHandBox = new HBox();
    private final HBox botHandBox = new HBox();

    public InitGame(Deque<Rectangle> deckObject, Deque<Card> deck, BorderPane boardPaneHands) {
        this.deckObject = deckObject;
        this.deck = deck;
        this.boardPaneHands = boardPaneHands;
        distributeCards();
        appendHandsObject();
    }

    public void appendHandsObject() {

        playerHandBox.setSpacing(10);
        botHandBox.setSpacing(10);

        playerHandBox.setAlignment(Pos.CENTER);
        botHandBox.setAlignment(Pos.CENTER);

        boardPaneHands.setTop(botHandBox);
        boardPaneHands.setBottom(playerHandBox);
    }

    public void distributeCards() {
        for (int i = 0; i < 3; i++) {
            Rectangle card = deckObject.poll();
            showHoverEffect(Objects.requireNonNull(card));
            playerHandBox.getChildren().add(card);
        }
        for (int i = 0; i < 3; i++) {
            Rectangle card = deckObject.poll();
            botHandBox.getChildren().add(card);
        }
    }

    public void showHoverEffect(Rectangle card) {
        card.setOnMouseEntered(e -> {
            card.setTranslateY(-20);
            card.setCursor(Cursor.HAND);
        });
        card.setOnMouseExited(e -> {
            card.setTranslateY(0);
            card.setCursor(Cursor.DEFAULT);
        });
    }




}
