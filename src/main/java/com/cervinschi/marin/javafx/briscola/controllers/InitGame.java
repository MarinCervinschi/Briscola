package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Board;
import com.cervinschi.marin.javafx.briscola.models.Card;
import com.cervinschi.marin.javafx.briscola.models.Hand;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.Cursor;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.scene.paint.Color;
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
    private final Text playerPoints;
    private final Text botPoints;
    private final Text deckCards;
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
                tableBox.setAlignment(Pos.CENTER);
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

            playerHandBox.getChildren().add(card);

            showHoverEffect(Objects.requireNonNull(card));
            selectCard(card, selectedCard);
        }
        while (!botHand.isCardsObjectFull()) {
            Rectangle card = deckObject.poll();
            botHand.addCard(board.getDeck().poll());
            botHand.addCardObject(card);
            botHandBox.getChildren().add(card);
        }
        board.setHand(playerHand);
        board.setHand(botHand);
    }

    public void selectCard(Rectangle card, Card selectedCard) {
        card.setOnMouseClicked(e -> {
            playerHandBox.getChildren().remove(card);

            card.setOnMouseEntered(null);
            card.setOnMouseExited(null);

            tableBox.getChildren().addFirst(card);

            board.removeCardFromHand(selectedCard, card);
            board.addCardToTable(selectedCard);

            botMove();
        });
    }

    public void botMove() {
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            Rectangle card = (Rectangle) botHandBox.getChildren().getFirst();
            botHandBox.getChildren().remove(card);
            tableBox.getChildren().addLast(card);
            for (int i = 0; i < 3; i++) {
                if (botHand.getCards()[i].toString().equals(card.getId())) {
                    board.addCardToTable(botHand.getCards()[i]);
                    board.removeCardFromHand(botHand.getCards()[i], card);
                    break;
                }
            }

        });
        pause.play();
    }

    public void checkTable() {
        if (!board.tableIsFull()) {
            return;
        }
        int pointsFirst = board.getTable(0).getValue();
        int pointsSecond = board.getTable(1).getValue();

        Rectangle firstCard = null;
        Rectangle secondCard = null;
        if (pointsFirst > pointsSecond) {
            pointsFirst += pointsSecond;
            pointsSecond = 0;
            firstCard = (Rectangle) tableBox.getChildren().getFirst();
            secondCard = (Rectangle) tableBox.getChildren().getLast();
        } else {
            pointsSecond += pointsFirst;
            pointsFirst = 0;
            firstCard = (Rectangle) tableBox.getChildren().getLast();
            secondCard = (Rectangle) tableBox.getChildren().getFirst();
        }

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.3), firstCard);

        tt.setToX(secondCard.getLayoutX() - firstCard.getLayoutX());
        tt.setToY(secondCard.getLayoutY() - firstCard.getLayoutY());

        tt.play();

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        int finalPointsFirst = pointsFirst;
        int finalPointsSecond = pointsSecond;
        pause.setOnFinished(e -> {
            if (board.tableIsFull()) {
                updatePoints(playerPoints, finalPointsFirst);
                updatePoints(botPoints, finalPointsSecond);

                tableBox.getChildren().clear();
                board.clearTable();
            }
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

    public void updatePoints(Text player, int newPoints) {
        int points = Integer.parseInt(player.getText()) + newPoints;
        player.setText(String.valueOf(points));
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
