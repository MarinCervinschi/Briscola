package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Board;
import com.cervinschi.marin.javafx.briscola.models.Card;
import com.cervinschi.marin.javafx.briscola.models.Hand;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.Cursor;
import javafx.util.Duration;

import java.util.Deque;
import java.util.Objects;

public class InitGame {
    private final Deque<Rectangle> deckObject;
    private final Board board;
    private final BorderPane boardPaneHands;
    private final HBox playerHandBox = new HBox();
    private final HBox botHandBox = new HBox();
    private final HBox tableBox = new HBox();
    private final Hand playerHand = new Hand();
    private final Hand botHand = new Hand();
    private boolean playerTurn = true;


    public InitGame(Deque<Rectangle> deckObject, Board board, BorderPane boardPaneHands) {
        this.deckObject = deckObject;
        this.board = board;
        this.boardPaneHands = boardPaneHands;
        appendHandsObject();
    }

    public void appendHandsObject() {

        playerHandBox.setSpacing(10);
        botHandBox.setSpacing(10);
        tableBox.setSpacing(10);

        playerHandBox.setAlignment(Pos.CENTER);
        botHandBox.setAlignment(Pos.CENTER);
        tableBox.setAlignment(Pos.CENTER);

        boardPaneHands.setTop(botHandBox);
        boardPaneHands.setBottom(playerHandBox);
        boardPaneHands.setCenter(tableBox);
    }

    public void mainLoop() {

        if (!deckObject.isEmpty()) {
            fillHands();
            Rectangle[] tableCards = new Rectangle[tableBox.getChildren().size()];
            for (int i = 0; i < tableBox.getChildren().size(); i++) {
                tableCards[i] = (Rectangle) tableBox.getChildren().get(i);
            }
            board.setTable(tableCards);
        } else {
            endGame();
        }
    }

    public void fillHands() {
        while (!playerHand.isFull()) {
            Rectangle card = deckObject.poll();
            playerHand.addCard(card);
            playerHandBox.getChildren().add(card);
            showHoverEffect(Objects.requireNonNull(card));
            selectCard(Objects.requireNonNull(card));
        }
        while (!botHand.isFull()) {
            Rectangle card = deckObject.poll();
            botHand.addCard(card);
            botHandBox.getChildren().add(card);
        }
        if (!board.handsAreFull()) {
            board.setHand(playerHand);
            board.setHand(botHand);
        }
    }

    public void selectCard(Rectangle card) {
        card.setOnMouseClicked(e -> {
            playerHandBox.getChildren().remove(card);

            card.setOnMouseEntered(null);
            card.setOnMouseExited(null);

            tableBox.getChildren().add(card);

            playerTurn = false;

            botMove();
        });
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

    public void botMove() {
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            Rectangle card = (Rectangle) botHandBox.getChildren().getFirst();
            botHandBox.getChildren().remove(card);
            tableBox.getChildren().add(card);

            playerTurn = true;
        });
        pause.play();
    }

    public void endGame() {
        // end game
    }

    public Board getBoard() {
        return board;
    }
}
