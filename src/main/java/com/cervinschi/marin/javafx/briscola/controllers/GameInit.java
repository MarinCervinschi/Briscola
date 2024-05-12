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
    private boolean botWonLastHand = false;
    private boolean isPauseActive = false;


    public GameInit(GameObjects gameObjects) {
        this.gameObjects = gameObjects;
        gameObjects.appendHandsObject();
    }

    public void mainLoop() {

        if (!gameObjects.getDeckObject().isEmpty() && !gameEnded) {
            fillHands();
            if (botWonLastHand) {
                botMove();
            }
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

    private void fillHands() {
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
    private void selectCard(Rectangle card, Card selectedCard) {
        card.setOnMouseClicked(e -> {
            if (!canSelect) return;

            canSelect = false;
            gameObjects.getPlayerHandBox().getChildren().remove(card);

            card.setOnMouseEntered(null);
            card.setOnMouseExited(null);

            gameObjects.getTableBox().getChildren().add(card);

            gameObjects.getBoard().removeCardFromHand(selectedCard, card);
            gameObjects.getBoard().addCardToTable(selectedCard);

            botMove();
        });
    }

    boolean botPlayed = false;
    private void botMove() {
        if (isPauseActive || botPlayed) return;
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            if (!gameObjects.getBoard().tableIsFull()) {
                isPauseActive = false;
                botPlayed = true;
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
                gameObjects.getTableBox().getChildren().add(card);
            }
        });
        isPauseActive = true;
        pause.play();
    }

    private void checkTable() {
        if (!gameObjects.getBoard().tableIsFull()) return;

        int[] points = getPointsFromTable();
        int pointsFirst = points[0];
        int pointsSecond = points[1];

        Rectangle[] cards = getRectangleFromTable();
        Rectangle playerCard, botCard;

        if (pointsFirst > pointsSecond) {
            playerCard = cards[0];
            botCard = cards[1];
            pointsFirst += pointsSecond;
            pointsSecond = 0;
        } else {
            playerCard = cards[1];
            botCard = cards[0];
            pointsSecond += pointsFirst;
            pointsFirst = 0;
        }

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.2), playerCard);

        tt.setToX(botCard.getLayoutX() - playerCard.getLayoutX());
        tt.setToY(botCard.getLayoutY() - playerCard.getLayoutY());

        tt.play();

        PauseTransition pause = getPauseTransition(pointsFirst, pointsSecond);
        pause.play();
    }

    private int[] getPointsFromTable() {
        int[] points = new int[2];
        if (botWonLastHand) {
            points[0] = gameObjects.getBoard().getTable(1).getValue();
            points[1] = gameObjects.getBoard().getTable(0).getValue();
        } else {
            points[0] = gameObjects.getBoard().getTable(0).getValue();
            points[1] = gameObjects.getBoard().getTable(1).getValue();
        }
        return points;
    }

    private Rectangle[] getRectangleFromTable() {
        Rectangle[] cards = new Rectangle[2];
        if (botWonLastHand) {
            cards[0] = (Rectangle) gameObjects.getTableBox().getChildren().getLast();
            cards[1] = (Rectangle) gameObjects.getTableBox().getChildren().getFirst();
        } else {
            cards[0] = (Rectangle) gameObjects.getTableBox().getChildren().getFirst();
            cards[1] = (Rectangle) gameObjects.getTableBox().getChildren().getLast();
        }
        return cards;
    }

    private PauseTransition getPauseTransition(int pointsFirst, int pointsSecond) {
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> {
            if (gameObjects.getBoard().tableIsFull()) {
                updatePoints(gameObjects.getPlayerPoints(), pointsFirst);
                updatePoints(gameObjects.getBotPoints(), pointsSecond);

                gameObjects.getTableBox().getChildren().clear();
                gameObjects.getBoard().clearTable();
                canFill = true;
                canSelect = true;
                botPlayed = false;
                botWonLastHand = pointsFirst <= pointsSecond;
            }
        });
        return pause;
    }

    private void showHoverEffect(Rectangle card) {
        card.setOnMouseEntered(e -> {
            card.setTranslateY(-20);
            card.setCursor(Cursor.HAND);
        });
        card.setOnMouseExited(e -> {
            card.setTranslateY(0);
            card.setCursor(Cursor.DEFAULT);
        });
    }

    private void updatePoints(Text player, int newPoints) {
        if (gameEnded || player.getText().equals(" ")) return;

        int points = Integer.parseInt(player.getText()) + newPoints;
        player.setText(String.valueOf(points));
        if (gameObjects.getDeckObject().size() > 1) {
            gameObjects.getDeckCards().setText(String.valueOf(gameObjects.getDeckObject().size() - 1));
        }
    }

    private void endGame() {
        if (gameEnded) return;

        int playerScore = Integer.parseInt(gameObjects.getPlayerPoints().getText());
        int botScore = Integer.parseInt(gameObjects.getBotPoints().getText());
        gameObjects.getPlayerPoints().setText(" ");
        gameObjects.getBotPoints().setText(" ");
        gameObjects.getDeckCards().setText(" ");

        Text endGameMessage = getText(playerScore, botScore);

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
