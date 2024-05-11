package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Card;
import com.cervinschi.marin.javafx.briscola.models.Hand;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.shape.Rectangle;
import javafx.scene.Cursor;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.util.Objects;

public class GameInit {
    private final GameObjects gameObjects;
    
    private final Hand playerHand = new Hand();
    private final Hand botHand = new Hand();


    private boolean canFill = true;
    private boolean canSelect = true;
    private boolean gameEnded = false;

    public GameInit(GameObjects gameObjects) {
        this.gameObjects = gameObjects;
        gameObjects.appendHandsObject();
    }

    public void mainLoop() {

        if (!gameObjects.getDeckObject().isEmpty() && !gameEnded) {
            fillHands();
        }
        if (gameObjects.getBoard().tableIsFull()) {
            checkTable();
            gameObjects.getTableBox().setAlignment(Pos.CENTER);
        }
        if (botHand.isEmptyObject() && playerHand.isEmptyObject()) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> endGame());
            pause.play();
        }
    }

    public void fillHands() {
        if (!canFill) return;

        canFill = false;
        while (playerHand.isCardsObjectFull()) {
            Rectangle card;
            Card selectedCard;
            if (gameObjects.getDeckObject().size() == 1) {
                card = gameObjects.getBoard().getBriscolaObject();
                selectedCard = gameObjects.getBoard().getBriscola();
            } else {
                card = gameObjects.getDeckObject().poll();
                selectedCard = gameObjects.getBoard().getDeck().poll();
            }

            assert card != null;
            playerHand.addCard(selectedCard);
            playerHand.addCardObject(card);

            gameObjects.getPlayerHandBox().getChildren().add(card);
            if (card.equals(gameObjects.getBoard().getBriscolaObject())) {
                card.setTranslateX(0);
                card.setTranslateY(0);
                gameObjects.getTablePane().getChildren().remove(gameObjects.getTablePane().getLeft());
                gameObjects.getDeckCards().setText(" ");
            }

            showHoverEffect(Objects.requireNonNull(card));
            selectCard(card, selectedCard);
        }
        while (botHand.isCardsObjectFull()) {
            Rectangle card = gameObjects.getDeckObject().poll();
            assert card != null;
            botHand.addCard(gameObjects.getBoard().getDeck().poll());
            botHand.addCardObject(card);
            gameObjects.getBotHandBox().getChildren().add(card);
        }
        gameObjects.getBoard().setHand(playerHand);
        gameObjects.getBoard().setHand(botHand);
    }

    public void selectCard(Rectangle card, Card selectedCard) {
        card.setOnMouseClicked(e -> {
            if (!canSelect) return;

            canSelect = false;
            gameObjects.getPlayerHandBox().getChildren().remove(card);

            card.setOnMouseEntered(null);
            card.setOnMouseExited(null);

            gameObjects.getTableBox().getChildren().addFirst(card);

            gameObjects.getBoard().removeCardFromHand(selectedCard, card);
            gameObjects.getBoard().addCardToTable(selectedCard);

            botMove();
        });
    }

    public void botMove() {
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            if (!gameObjects.getBoard().tableIsFull()) {
                Rectangle card = (Rectangle) gameObjects.getBotHandBox().getChildren().getFirst();
                gameObjects.getBotHandBox().getChildren().remove(card);
                for (int i = 0; i < 3; i++) {
                    if (botHand.getCards()[i] != null) {
                        if (botHand.getCards()[i].toString().equals(card.getId())) {
                            gameObjects.getBoard().addCardToTable(botHand.getCards()[i]);
                            gameObjects.getBoard().removeCardFromHand(botHand.getCards()[i], card);
                            break;
                        }
                    }
                }
                if (gameObjects.getBoard().getTable(0).getValue() > gameObjects.getBoard().getTable(1).getValue()) {
                    gameObjects.getTableBox().getChildren().addFirst(card);
                } else {
                    gameObjects.getTableBox().getChildren().addLast(card);
                }
            }

        });
        pause.play();
    }

    public void checkTable() {
        if (!gameObjects.getBoard().tableIsFull()) return;

        int pointsFirst = gameObjects.getBoard().getTable(0).getValue();
        int pointsSecond = gameObjects.getBoard().getTable(1).getValue();

        Rectangle firstCard = (Rectangle) gameObjects.getTableBox().getChildren().getLast();
        Rectangle secondCard = (Rectangle) gameObjects.getTableBox().getChildren().getFirst();
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
            if (gameObjects.getBoard().tableIsFull()) {
                updatePoints(gameObjects.getPlayerPoints(), pointsSecond);
                updatePoints(gameObjects.getBotPoints(), pointsFirst);

                gameObjects.getTableBox().getChildren().clear();
                gameObjects.getBoard().clearTable();
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
        if (gameEnded || player.getText().equals(" ")) return;

        int points = Integer.parseInt(player.getText()) + newPoints;
        player.setText(String.valueOf(points));
        if (gameObjects.getDeckObject().size() > 1) {
            gameObjects.getDeckCards().setText(String.valueOf(gameObjects.getDeckObject().size() - 1));
        }
    }

    public void endGame() {
        if (gameEnded) return;

        int playerScore = Integer.parseInt(gameObjects.getPlayerPoints().getText());
        int botScore = Integer.parseInt(gameObjects.getBotPoints().getText());
        gameObjects.getPlayerPoints().setText(" ");
        gameObjects.getBotPoints().setText(" ");
        gameObjects.getDeckCards().setText(" ");

        Text endGameMessage = getText(botScore, playerScore);

        // Add the message to the board
        gameObjects.getTablePane().getChildren().clear();
        gameObjects.getTablePane().setCenter(endGameMessage);

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
