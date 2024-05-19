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
    private final Bot bot;
    
    private final Hand playerHand = new Hand();

    private boolean canFill = true;
    private boolean canSelect = true;
    private boolean gameEnded = false;
    private boolean botWonLastHand = false;


    public GameInit(GameObjects gameObjects, String mode) {
        this.gameObjects = gameObjects;
        this.bot = new Bot(gameObjects, mode);
        gameObjects.appendHandsObject();
    }

    public void mainLoop() {

        if (!gameObjects.getDeckObject().isEmpty() && !gameEnded) {
            fillHands();
            if (botWonLastHand) {
                bot.move();
            }
        }
        if (gameObjects.getBoard().tableIsFull()) {
            checkTable();
            gameObjects.getTableBox().setAlignment(Pos.CENTER);
        }
        if (bot.getHand().isEmptyObject() && playerHand.isEmptyObject()) {
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
        while (bot.getHand().isCardsObjectFull()) {
            Rectangle card = gameObjects.getDeckObject().poll();
            assert card != null;
            bot.getHand().addCard(gameObjects.getBoard().getDeck().poll());
            bot.getHand().addCardObject(card);
            gameObjects.getBotHandBox().getChildren().add(card);
        }
        gameObjects.getBoard().setHand(playerHand);
        gameObjects.getBoard().setHand(bot.getHand());
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

            bot.move();
        });
    }


    private void checkTable() {
        if (!gameObjects.getBoard().tableIsFull()) return;

        int[] points = new int[2];
        Rectangle[] results = findWinningCard(getCardsFromTable(), points);
        Rectangle winningCard = results[0];
        Rectangle losingCard = results[1];

        boolean botWon = winningCard.equals(getRectangleFromTable()[1]);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.2), winningCard);

        tt.setToX(losingCard.getLayoutX() - winningCard.getLayoutX());
        tt.setToY(losingCard.getLayoutY() - winningCard.getLayoutY());

        tt.play();

        PauseTransition pause = getPauseTransition(points[0], points[1], botWon);
        pause.play();
    }

    private Rectangle[] findWinningCard(Card[] cards, int[] points) {
        Rectangle[] winningCard = new Rectangle[2];

        if (cards[0].isBriscola() && !cards[1].isBriscola()) {
            winningCard[0] = getRectangleFromTable()[0];
            winningCard[1] = getRectangleFromTable()[1];
            points[0] = cards[0].getValue() + cards[1].getValue();
            points[1] = 0;
        } else if (!cards[0].isBriscola() && cards[1].isBriscola()) {
            winningCard[0] = getRectangleFromTable()[1];
            winningCard[1] = getRectangleFromTable()[0];
            points[0] = 0;
            points[1] = cards[0].getValue() + cards[1].getValue();
        } else if (cards[0].getSeed().equals(cards[1].getSeed())) {
            winningCard = checkForName(cards, points);
        } else {
            if (cards[0].equals(gameObjects.getBoard().getTable(0))) {
                winningCard[0] = getRectangleFromTable()[0];
                winningCard[1] = getRectangleFromTable()[1];
                points[0] = cards[0].getValue() + cards[1].getValue();
                points[1] = 0;
            } else {
                winningCard[0] = getRectangleFromTable()[1];
                winningCard[1] = getRectangleFromTable()[0];
                points[0] = 0;
                points[1] = cards[0].getValue() + cards[1].getValue();
            }
        }

        return winningCard;
    }

    private Rectangle[] checkForName(Card[] cards, int[] points) {
        Rectangle[] sameSeed = new Rectangle[2];
        int playerValue = Integer.parseInt(cards[0].getName());
        int botValue = Integer.parseInt(cards[1].getName());

        if (playerValue == 1) {
            playerValue = 11;
        } else if (playerValue == 3) {
            playerValue = 10;
        }
        if (botValue == 1) {
            botValue = 11;
        } else if (botValue == 3) {
            botValue = 10;
        }

        if (playerValue > botValue) {
            sameSeed[0] = getRectangleFromTable()[0];
            sameSeed[1] = getRectangleFromTable()[1];
            points[0] = cards[0].getValue() + cards[1].getValue();
            points[1] = 0;
        } else if (playerValue < botValue) {
            sameSeed[0] = getRectangleFromTable()[1];
            sameSeed[1] = getRectangleFromTable()[0];
            points[0] = 0;
            points[1] = cards[0].getValue() + cards[1].getValue();
        } else {
            points[0] = cards[0].getValue() + cards[1].getValue();
            points[1] = 0;
            return getRectangleFromTable();
        }
        return sameSeed;
    }

    private Card[] getCardsFromTable() {
        Card[] cards = new Card[2];
        if (botWonLastHand) {
            cards[0] = gameObjects.getBoard().getTable(1);
            cards[1] = gameObjects.getBoard().getTable(0);
        } else {
            cards[0] = gameObjects.getBoard().getTable(0);
            cards[1] = gameObjects.getBoard().getTable(1);
        }
        return cards;
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

    private PauseTransition getPauseTransition(int pointsFirst, int pointsSecond, boolean botWon) {
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> {
            if (gameObjects.getBoard().tableIsFull()) {
                updatePoints(gameObjects.getPlayerPoints(), pointsFirst);
                updatePoints(gameObjects.getBotPoints(), pointsSecond);

                gameObjects.getTableBox().getChildren().clear();
                gameObjects.getBoard().clearTable();
                canFill = true;
                canSelect = true;
                bot.setHasPlayed(false);
                botWonLastHand = botWon;
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
