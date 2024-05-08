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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import java.awt.*;
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
    private final Text playerPoints;
    private final Text botPoints;
    private final Text deckCards;
    private boolean update = false;
    private boolean gameEnded = false;



    public InitGame(Deque<Rectangle> deckObject, Board board, BorderPane boardPaneHands, Text playerPoints, Text botPoints, Text deckCards) {
        this.deckObject = deckObject;
        this.board = board;
        this.boardPaneHands = boardPaneHands;
        this.playerPoints = playerPoints;
        this.botPoints = botPoints;
        this.deckCards = deckCards;
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
        int pointsFirst = board.getTable(0).getValue();
        int pointsSecond = board.getTable(1).getValue();
        if (pointsFirst > pointsSecond) {
            //tableBox.getChildren().getFirst().setTranslateX(-50);
            updatePoints(playerPoints, pointsFirst + pointsSecond);
        } else {
            //tableBox.getChildren().getFirst().setTranslateX(50);
            updatePoints(botPoints, pointsFirst + pointsSecond);
        }
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e -> {
            tableBox.getChildren().clear();
            board.clearTable();
            update = true;
        });
        pause.play();
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
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
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

    public void updatePoints(Text player, int newPoints) {
        if (update) {
            int points = Integer.parseInt(player.getText()) + newPoints;
            player.setText(String.valueOf(points));
            update = false;
        }
        deckCards.setText(String.valueOf(board.getDeck().size()));
    }

    public void endGame() {
        int playerScore = Integer.parseInt(playerPoints.getText());
        int botScore = Integer.parseInt(botPoints.getText());
        playerPoints.setText(" ");
        botPoints.setText(" ");
        deckCards.setText(" ");

        Text endGameMessage = getText(playerScore, botScore);

        // Add the message to the board
        boardPaneHands.getChildren().clear();
        boardPaneHands.setCenter(endGameMessage);

        gameEnded = true;
    }

    private Text getText(int playerScore, int botScore) {
        Text endGameMessage = new Text();
        endGameMessage.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 90));
        endGameMessage.setFill(Color.YELLOWGREEN);

        endGameMessage.setTextAlignment(TextAlignment.CENTER);

        if (playerScore > 61) {
            endGameMessage.setText("You won");
        } else if (botScore > 61) {
            endGameMessage.setText("You lose");
        } else {
            endGameMessage.setText("Draw");
        }
        return endGameMessage;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }
}
