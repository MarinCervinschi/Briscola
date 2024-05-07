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
            if (board.tableIsFull()) {
                checkTable();
            }
        } else {
            endGame();
        }
    }

    public void fillHands() {
        while (!playerHand.isCardsObjectFull()) {
            Rectangle card = deckObject.poll();
            Card selectedCard = board.getDeck().poll();
            playerHand.addCard(selectedCard);
            playerHand.addCardObject(card);

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> playerHandBox.getChildren().add(card));
            pause.play();

            showHoverEffect(Objects.requireNonNull(card));
            selectCard(card, selectedCard);
        }
        while (!botHand.isCardsObjectFull()) {
            Rectangle card = deckObject.poll();
            botHand.addCard(board.getDeck().poll());
            botHand.addCardObject(card);
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> botHandBox.getChildren().add(card));
            pause.play();
        }
        board.setHand(playerHand);
        board.setHand(botHand);
    }

    public void selectCard(Rectangle card, Card selectedCard) {
        card.setOnMouseClicked(e -> {
            playerHandBox.getChildren().remove(card);

            card.setOnMouseEntered(null);
            card.setOnMouseExited(null);

            tableBox.getChildren().add(card);

            board.removeCardFromHand(selectedCard, card);
            board.addCardToTable(selectedCard);

            playerTurn = false;

            botMove();
        });
    }

    public void checkTable() {
        if (board.tableIsFull()) {
            if (board.getTable(0).getValue() > board.getTable(1).getValue()) {
                tableBox.getChildren().getFirst().setTranslateX(-50);
                board.setPlayerPoints(board.getTable(0).getValue() + board.getTable(1).getValue());
            } else {
                tableBox.getChildren().getFirst().setTranslateX(50);
                board.setBotPoints(board.getTable(0).getValue() + board.getTable(1).getValue());
            }
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> {
                tableBox.getChildren().clear();
                board.clearTable();
            });
            pause.play();
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

    public void botMove() {
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            Rectangle card = (Rectangle) botHandBox.getChildren().getFirst();
            botHandBox.getChildren().remove(card);
            tableBox.getChildren().add(card);
            board.addCardToTable(botHand.getCards()[0]);

            board.removeCardFromHand(botHand.getCards()[0], card);

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
