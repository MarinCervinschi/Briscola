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

        if (!deckObject.isEmpty() && !gameEnded) {
            fillHands();
        }
        if (board.tableIsFull()) {
            checkTable();
            tableBox.setAlignment(Pos.CENTER);
        }
        if (botHand.isEmptyObject() && playerHand.isEmptyObject()) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> endGame());
            pause.play();
        }
    }

    private boolean canFill = true;
    private boolean canSelect = true;

    public void fillHands() {
        if (!canFill) {
            return;
        }
        canFill = false;
        while (playerHand.isCardsObjectFull()) {
            Rectangle card;
            Card selectedCard;
            if (deckObject.size() == 1) {
                card = board.getBriscolaObject();
                selectedCard = board.getBriscola();
            } else {
                card = deckObject.poll();
                selectedCard = board.getDeck().poll();
            }

            assert card != null;
            playerHand.addCard(selectedCard);
            playerHand.addCardObject(card);

            playerHandBox.getChildren().add(card);
            if (card.equals(board.getBriscolaObject())) {
                card.setTranslateX(0);
                card.setTranslateY(0);
                boardPaneHands.getChildren().remove(boardPaneHands.getLeft());
                deckCards.setText(" ");
            }

            showHoverEffect(Objects.requireNonNull(card));
            selectCard(card, selectedCard);
        }
        while (botHand.isCardsObjectFull()) {
            Rectangle card = deckObject.poll();
            assert card != null;
            botHand.addCard(board.getDeck().poll());
            botHand.addCardObject(card);
            botHandBox.getChildren().add(card);
        }
        board.setHand(playerHand);
        board.setHand(botHand);
    }

    public void selectCard(Rectangle card, Card selectedCard) {
        card.setOnMouseClicked(e -> {
            if (!canSelect) {
                return;
            }
            canSelect = false;
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
            if (!board.tableIsFull()) {
                Rectangle card = (Rectangle) botHandBox.getChildren().getFirst();
                botHandBox.getChildren().remove(card);
                for (int i = 0; i < 3; i++) {
                    if (botHand.getCards()[i] != null) {
                        if (botHand.getCards()[i].toString().equals(card.getId())) {
                            board.addCardToTable(botHand.getCards()[i]);
                            board.removeCardFromHand(botHand.getCards()[i], card);
                            break;
                        }
                    }
                }
                if (board.getTable(0).getValue() > board.getTable(1).getValue()) {
                    tableBox.getChildren().addFirst(card);
                } else {
                    tableBox.getChildren().addLast(card);
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

        Rectangle firstCard = (Rectangle) tableBox.getChildren().getLast();
        Rectangle secondCard = (Rectangle) tableBox.getChildren().getFirst();
        if (pointsFirst > pointsSecond) {
            pointsFirst += pointsSecond;
            pointsSecond = 0;
        } else {
            pointsSecond += pointsFirst;
            pointsFirst = 0;
        }

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.2), firstCard);

        tt.setToX(secondCard.getLayoutX() - firstCard.getLayoutX());
        tt.setToY(secondCard.getLayoutY() - firstCard.getLayoutY());

        tt.play();

        PauseTransition pause = getPauseTransition(pointsFirst, pointsSecond);
        pause.play();
    }

    private PauseTransition getPauseTransition(int pointsFirst, int pointsSecond) {
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> {
            if (board.tableIsFull()) {
                updatePoints(playerPoints, pointsFirst);
                updatePoints(botPoints, pointsSecond);

                tableBox.getChildren().clear();
                board.clearTable();
                canFill = true;
                canSelect = true;
            }
        });
        return pause;
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
        if (gameEnded || player.getText().equals(" ")){
            return;
        }
        int points = Integer.parseInt(player.getText()) + newPoints;
        player.setText(String.valueOf(points));
        if (deckObject.size() > 1) {
            deckCards.setText(String.valueOf(deckObject.size() - 1));
        }
    }

    public void endGame() {
        if (gameEnded) {
            return;
        }
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
        String message;
        if (playerScore > 61) {
            message = "You won!";
        } else if (botScore > 61) {
            message = "You lost!";
        } else {
            message = "Draw!";
        }

        endGameMessage.setText(playerScore + " - " + botScore + "\n" + message);
        return endGameMessage;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }
}
