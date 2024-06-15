package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Card;
import com.cervinschi.marin.javafx.briscola.models.Hand;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameInit {
    private final GameObjects gameObjects;
    private Bot bot;

    private final Hand playerHand;

    private final List<Card> playedCards;

    private boolean canFill = true;
    private boolean canSelect = true;
    private boolean gameEnded = false;
    private boolean botWonLastHand = false;
    private boolean isPauseActive = false;

    private String turn = "player";

    public GameInit(GameObjects gameObjects, String mode) {
        this.gameObjects = gameObjects;
        this.playedCards = new ArrayList<>();
        this.playerHand = gameObjects.getBoard().getPlayerHand();
        gameObjects.appendHandsObject();
    }

    public void mainLoop() {

        if (!gameObjects.getDeckObject().isEmpty()) fillHands();
        if (!gameEnded && botWonLastHand) {
            bot.move();
            canSelect = bot.isHasPlayed();
        }
        if (gameObjects.getBoard().tableIsFull()) {
            checkTable();
            gameObjects.getTableBox().setAlignment(Pos.CENTER);
        }
        if (bot.getHand().isEmptyObject() && playerHand.isEmptyObject() && !isPauseActive) {
            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(e -> {
                isPauseActive = false;
                endGame();
            });
            isPauseActive = true;
            pause.play();
        }
    }

    private void fillHands() {
        if (!canFill || isPauseActive || bot.isHasPlayed()) return;
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e -> {
            isPauseActive = false;
            Rectangle cardObject;
            Card card;
            if (playerHand.isCardsObjectFull() || bot.getHand().isCardsObjectFull()) {

                if (gameObjects.getBoard().getDeck().size() == 1 && gameObjects.getBoard().getBriscolaCard() != null) {
                    cardObject = gameObjects.getBoard().getBriscolaObject();
                    card = gameObjects.getBoard().getBriscolaCard();
                    gameObjects.getBoard().setBriscolaCard(null);
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
            } else {
                canFill = false;
            }
        });
        isPauseActive = true;
        pause.play();
    }

    private void fillPlayerHand(Rectangle cardObject, Card card) {
        playerHand.addCard(card, cardObject);

        createTransition(cardObject, -300, -200);
        playSound("card-sound");

        gameObjects.getPlayerHandBox().getChildren().add(cardObject);

        showHoverEffect(Objects.requireNonNull(cardObject));
        selectCard(cardObject, card);
    }

    private void fillBotHand(Rectangle cardObject, Card card) {
        bot.getHand().addCard(card, cardObject);

        Rectangle backDeck = gameObjects.createCardObject(new Card("1", "back", 0, false));

        createTransition(backDeck, -300, 200);
        playSound("card-sound");

        gameObjects.getBotHandBox().getChildren().add(backDeck);
    }

    private void handleBriscolaCard(Rectangle card) {
        card.setTranslateX(0);
        card.setTranslateY(0);
        gameObjects.getTablePane().getChildren().remove(gameObjects.getTablePane().getLeft());
        gameObjects.getDeckCards().setVisible(false);
    }

    private void selectCard(Rectangle card, Card selectedCard) {
        card.setOnMouseClicked(e -> {
            if (!canSelect || isPauseActive) return;

            canSelect = false;
            gameObjects.getPlayerHandBox().getChildren().remove(card);

            card.setOnMouseEntered(null);
            card.setOnMouseExited(null);

            createTransition(card, 100, 200);
            if (selectedCard.toString().equals("1 of denara 11")) {
                playSound("eagle");
            } else {
                playSound("card-sound");
            }
            gameObjects.getTableBox().getChildren().add(card);

            playerHand.removeCard(selectedCard, card);
            playedCards.add(selectedCard);
            gameObjects.getBoard().addCardToTable(selectedCard);

            bot.move();
        });
    }

    protected void playSound(String name) {
        URL url = getClass().getResource("/com/cervinschi/marin/javafx/briscola/sounds/" + name + ".mp3");
        Media musicCard = new Media(Objects.requireNonNull(url).toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(musicCard);

        mediaPlayer.play();
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
        if (isPauseActive) return;
        PauseTransition pause = getPauseTransition(points[0], points[1], botWon);
        isPauseActive = true;
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
        return cardValue == 1 ? 12 : cardValue == 3 ? 11 : cardValue;
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

    private PauseTransition getPauseTransition(int pointsPlayer, int pointsBot, boolean botWon) {
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> {
            if (gameObjects.getBoard().tableIsFull()) {
                updatePoints(gameObjects.getPlayerPoints(), pointsPlayer);
                updatePoints(gameObjects.getBotPoints(), pointsBot);

                if (botWon) {
                    gameObjects.getPlayerTurn().setStyle("-fx-background-color: rgba(255, 255, 255, 0.3)");
                    gameObjects.getBotTurn().setStyle("-fx-background-color: #2a2a2a");
                } else {
                    gameObjects.getBotTurn().setStyle("-fx-background-color: rgba(255, 255, 255, 0.3)");
                    gameObjects.getPlayerTurn().setStyle("-fx-background-color: #2a2a2a");
                }

                if (pointsPlayer > 10) {
                    playSound("cry");
                } else if (pointsBot > 10) {
                    playSound("laugh");
                }

                resetGame(botWon);
            }
        });
        return pause;
    }

    private void resetGame(boolean botWon) {
        gameObjects.getTableBox().getChildren().clear();
        gameObjects.getBoard().clearTable();
        canFill = true;
        canSelect = !botWon;
        isPauseActive = false;
        bot.setHasPlayed(false);
        botWonLastHand = botWon;
        turn = botWon ? "bot" : "player";
    }

    protected void createTransition(Rectangle card, int x, int y) {
        card.setTranslateX(x);
        card.setTranslateY(y);

        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), card);

        tt.setToX(0);
        tt.setToY(0);

        tt.play();
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

    private void updatePoints(Label player, int newPoints) {
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
        gameObjects.getPlayerPoints().setVisible(false);
        gameObjects.getBotPoints().setVisible(false);
        gameObjects.getDeckCards().setVisible(false);
        gameObjects.getBotTurn().setVisible(false);
        gameObjects.getPlayerTurn().setVisible(false);

        Label endGameMessage = getEndGameMessage(playerScore, botScore);

        fadeTransition(endGameMessage);

        // Add the message to the board
        gameObjects.getTablePane().getChildren().clear();
        gameObjects.getTablePane().setCenter(endGameMessage);

        gameEnded = true;
    }

    public void fadeTransition(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(3000), node);

        ft.setFromValue(0.0);
        ft.setToValue(1.0);

        ft.play();
    }

    private Label getEndGameMessage(int playerScore, int botScore) {
        String victory = playerScore > 60 ? "You won!" : botScore > 60 ? "You lost!" : "Draw!";
        String message = playerScore + " - " + botScore + "\n" + victory;
        Label endGameMessage = new Label(message);
        switch (victory) {
            case "You won!":
                endGameMessage.getStyleClass().add("victory");
                playSound("won");
                break;
            case "You lost!":
                endGameMessage.getStyleClass().add("defeat");
                playSound("lost");
                break;
            default:
                endGameMessage.getStyleClass().add("draw");
                playSound("draw");
                break;
        }
        return endGameMessage;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public Hand getPlayerHand() {
        return playerHand;
    }

    public boolean isPauseActive() {
        return isPauseActive;
    }

    public void setPauseActive(boolean pauseActive) {
        isPauseActive = pauseActive;
    }

    public List<Card> getPlayedCards() {
        return playedCards;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }
}
