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

    private String turn = "player";

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
            PauseTransition pause = new PauseTransition(Duration.seconds(1.6));
            pause.setOnFinished(e -> endGame());
            pause.play();
        }
    }

    private void fillHands() {
        if (!canFill) return;

        canFill = false;

        Rectangle cardObject;
        Card card;

        while (playerHand.isCardsObjectFull() || bot.getHand().isCardsObjectFull()) {

            if (gameObjects.getBoard().getDeck().size() == 1 && gameObjects.getBoard().getBriscola() != null) {
                cardObject = gameObjects.getBoard().getBriscolaObject();
                card = gameObjects.getBoard().getBriscola();
                gameObjects.getBoard().setBriscola(null);
                handleBriscolaCard(cardObject);

                turn = botWonLastHand ? "player" : "bot";
            } else {
                cardObject = gameObjects.getDeckObject().poll();
                card = gameObjects.getBoard().getDeck().poll();
            }

            if (turn.equals("bot") && bot.getHand().isCardsObjectFull()) {
                fillBotHand(cardObject, card);
                turn = "player";
            } else if (turn.equals("player") && playerHand.isCardsObjectFull()) {
                fillPlayerHand(cardObject, card);
                turn = "bot";
            }
        }

    }

    private void fillPlayerHand(Rectangle cardObject, Card card) {
        playerHand.addCard(card);
        playerHand.addCardObject(cardObject);

        gameObjects.getPlayerHandBox().getChildren().add(cardObject);

        showHoverEffect(Objects.requireNonNull(cardObject));
        selectCard(cardObject, card);

        gameObjects.getBoard().setHand(playerHand);
    }

    private void fillBotHand(Rectangle cardObject, Card card) {
        bot.getHand().addCard(card);
        bot.getHand().addCardObject(cardObject);

        Rectangle backDeck = gameObjects.createCardObject(new Card("1", "back", 0, false));
        gameObjects.getBotHandBox().getChildren().add(backDeck);

        gameObjects.getBoard().setHand(bot.getHand());
    }

    private void handleBriscolaCard(Rectangle card) {
        card.setTranslateX(0);
        card.setTranslateY(0);
        gameObjects.getTablePane().getChildren().remove(gameObjects.getTablePane().getLeft());
        gameObjects.getDeckCards().setText(" ");
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
        boolean playerWins;

        if (cards[0].isBriscola() && !cards[1].isBriscola()) {
            playerWins = true;
        } else if (!cards[0].isBriscola() && cards[1].isBriscola()) {
            playerWins = false;
        } else if (cards[0].getSeed().equals(cards[1].getSeed())) {
            return checkForName(cards, points);
        } else {
            playerWins = cards[0].equals(gameObjects.getBoard().getTable(0));
        }

        winningCard[0] = getRectangleFromTable()[playerWins ? 0 : 1];
        winningCard[1] = getRectangleFromTable()[playerWins ? 1 : 0];
        points[0] = playerWins ? cards[0].getValue() + cards[1].getValue() : 0;
        points[1] = playerWins ? 0 : cards[0].getValue() + cards[1].getValue();

        return winningCard;
    }

    private Rectangle[] checkForName(Card[] cards, int[] points) {
        int playerValue = adjustCardValue(Integer.parseInt(cards[0].getName()));
        int botValue = adjustCardValue(Integer.parseInt(cards[1].getName()));

        if (playerValue == botValue) {
            points[0] = cards[0].getValue() + cards[1].getValue();
            points[1] = 0;
            return getRectangleFromTable();
        }

        boolean playerWins = playerValue > botValue;
        points[0] = playerWins ? cards[0].getValue() + cards[1].getValue() : 0;
        points[1] = playerWins ? 0 : cards[0].getValue() + cards[1].getValue();

        return new Rectangle[]{
            getRectangleFromTable()[playerWins ? 0 : 1],
            getRectangleFromTable()[playerWins ? 1 : 0]
        };
    }

    private int adjustCardValue(int cardValue) {
        return cardValue == 1 ? 11 : cardValue == 3 ? 10 : cardValue;
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

                resetGame(botWon);
            }
        });
        return pause;
    }

    private void resetGame(boolean botWon) {
        gameObjects.getTableBox().getChildren().clear();
        gameObjects.getBoard().clearTable();
        canFill = true;
        canSelect = true;
        bot.setHasPlayed(false);
        botWonLastHand = botWon;
        turn = botWon ? "bot" : "player";
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

        Text endGameMessage = getEndGameMessage(playerScore, botScore);

        // Add the message to the board
        gameObjects.getTablePane().getChildren().clear();
        gameObjects.getTablePane().setCenter(endGameMessage);

        gameEnded = true;
    }

    private Text getEndGameMessage(int playerScore, int botScore) {
        String message = playerScore > 60 ? "You won!" : botScore > 60 ? "You lost!" : "Draw!";
        return createText(playerScore + " - " + botScore + "\n" + message);
    }

    private Text createText(String message) {
        Text text = new Text(message);
        text.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 90));
        text.setFill(Color.YELLOWGREEN);
        text.setTextAlignment(TextAlignment.CENTER);
        return text;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }
}
