package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Card;
import com.cervinschi.marin.javafx.briscola.models.Hand;
import javafx.animation.PauseTransition;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

public class Bot {
    private final GameObjects gameObjects;
    private final String mode;
    private boolean isPauseActive = false;
    private final Hand hand = new Hand();
    private boolean hasPlayed = false;

    public Bot(GameObjects gameObjects, String mode) {
        this.gameObjects = gameObjects;
        this.mode = mode;
    }

    public void move() {
        if (isPauseActive || hasPlayed) return;
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            if (!gameObjects.getBoard().tableIsFull()) {
                isPauseActive = false;
                hasPlayed = true;

                Rectangle card = switch (mode) {
                    case "easy" -> easyMove();
                    case "medium" -> mediumMove();
                    default -> hardMove();
                };

                gameObjects.getBotHandBox().getChildren().remove(card);
                for (int i = 0; i < 3; i++) {
                    if (hand.getCards()[i] != null) {
                        if (hand.getCards()[i].toString().equals(card.getId())) {
                            gameObjects.getBoard().addCardToTable(hand.getCards()[i]);
                            gameObjects.getBoard().removeCardFromHand(hand.getCards()[i], card);
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

    private Rectangle easyMove() {
        return (Rectangle) gameObjects.getBotHandBox().getChildren().get(new Random().nextInt(3));
    }

    private Rectangle mediumMove() {
        Rectangle card;
        /* bot move first */
        if (gameObjects.getBoard().tableIsEmpty()) {
            card = findMinCardName();
            if (card == null) {
                card = findMinCardNameBriscola();
            }
        } else {
            /* bot move second */
            Card tableCard = gameObjects.getBoard().getTable(0);
            if (tableCard.isBriscola()) {
                Rectangle maxCard = findMaxCardNameBriscola();
                if (tableCard.getValue() == 10 && maxCard != null) {
                    if (Integer.parseInt(maxCard.getId().split(" ")[3]) > 10) {
                        card = maxCard;
                    } else {
                        card = findMinCardName();
                        if (card == null) {
                            card = findMinCardNameBriscola();
                        }
                    }
                } else {
                    card = findMinCardName();
                    if (card == null) {
                        card = findMinCardNameBriscola();
                    }
                }
            } else {
                if (tableCard.getValue() == 10 || tableCard.getValue() == 11) {
                    Rectangle maxCard = findMaxCardName(tableCard.getSeed());
                    if (maxCard != null) {
                        if (Integer.parseInt(maxCard.getId().split(" ")[3]) > 10) {
                            card = maxCard;
                        } else {
                            card = findMinCardName();
                            if (card == null) {
                                card = findMinCardNameBriscola();
                            }
                        }
                    } else {
                        card = findMinCardName();
                        if (card == null) {
                            card = findMinCardNameBriscola();
                        }
                    }
                } else {
                    card = findMaxCardName(tableCard.getSeed());
                    if (card == null || Integer.parseInt(card.getId().split(" ")[3]) <= tableCard.getValue()) {
                        card = findMinCardName();
                        if (card == null) {
                            card = findMinCardNameBriscola();
                        }
                    }
                }
            }
        }

        return card;
    }

    private Rectangle findMinCardName() {
        int min = -1;
        Card card = null;
        for (int i = 0; i < 3; i++) {
            if (hand.getCards()[i] != null && !hand.getCards()[i].isBriscola()) {
                if (Integer.parseInt(hand.getCards()[i].getName()) < min) {
                    min = Integer.parseInt(hand.getCards()[i].getName());
                    card = hand.getCards()[i];
                }
            }
        }
        return getRectangle(card);
    }

    private Rectangle findMaxCardName(String seed) {
        int max = -1;
        Card card = null;
        for (int i = 0; i < 3; i++) {
            if (hand.getCards()[i] != null && !hand.getCards()[i].isBriscola() && hand.getCards()[i].getSeed().equals(seed)) {
                if (Integer.parseInt(hand.getCards()[i].getName()) > max) {
                    max = Integer.parseInt(hand.getCards()[i].getName());
                    card = hand.getCards()[i];
                }
            }
        }
        return getRectangle(card);
    }

    private Rectangle findMinCardNameBriscola() {
        int min = -1;
        Card card = null;
        for (int i = 0; i < 3; i++) {
            if (hand.getCards()[i] != null && hand.getCards()[i].isBriscola()) {
                if (Integer.parseInt(hand.getCards()[i].getName()) < min) {
                    min = Integer.parseInt(hand.getCards()[i].getName());
                    card = hand.getCards()[i];
                }
            }
        }
        return getRectangle(card);
    }

    private Rectangle findMaxCardNameBriscola() {
        int max = -1;
        Card card = null;
        for (int i = 0; i < 3; i++) {
            if (hand.getCards()[i] != null && hand.getCards()[i].isBriscola()) {
                if (Integer.parseInt(hand.getCards()[i].getName()) > max) {
                    max = Integer.parseInt(hand.getCards()[i].getName());
                    card = hand.getCards()[i];
                }
            }
        }
        return getRectangle(card);
    }

    private Rectangle getRectangle(Card card) {
        for (int i = 0; i < 3; i++) {
            if (hand.getCards()[i] != null) {
                if (hand.getCards()[i].equals(card)) {
                    return (Rectangle) gameObjects.getBotHandBox().getChildren().get(i);
                }
            }
        }
        return null;
    }

    private Rectangle hardMove() {
        return (Rectangle) gameObjects.getBotHandBox().getChildren().getFirst();
    }

    public Hand getHand() {
        return hand;
    }

    public void setHasPlayed(boolean hasPlayed) {
        this.hasPlayed = hasPlayed;
    }

}
