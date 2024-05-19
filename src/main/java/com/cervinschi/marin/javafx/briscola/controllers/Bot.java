package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Card;
import com.cervinschi.marin.javafx.briscola.models.Hand;
import javafx.animation.PauseTransition;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;

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
        Card tableCard = gameObjects.getBoard().getTable(0);

        /* bot move first */
        if (tableCard == null) {
            card = Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
        } else {
            /* bot move second */
            if (tableCard.isBriscola()) {
                Rectangle maxCard = findMaxCardName(tableCard.getSeed(), true);
                if (tableCard.getValue() == 10 && maxCard != null) {
                    if (Integer.parseInt(maxCard.getId().split(" ")[3]) == 11) {
                        card = maxCard;
                    } else {
                        card = Optional.ofNullable(findMinCardName(true)).orElseGet(() -> findMinCardName(false));
                    }
                } else {
                    card = Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
                }
            } else {
                if (tableCard.getValue() == 10 || tableCard.getValue() == 11) {
                    Rectangle maxCard = findMaxCardName(tableCard.getSeed(), false);
                    if (maxCard != null) {
                        if (Integer.parseInt(maxCard.getId().split(" ")[3]) == 11) {
                            card = maxCard;
                        } else {
                            card = Optional.ofNullable(findMinCardName(true)).orElseGet(() -> findMinCardName(false));
                        }
                    } else {
                        card = Optional.ofNullable(findMinCardName(true)).orElseGet(() -> findMinCardName(false));
                    }
                } else {
                    card = findMaxCardName(tableCard.getSeed(), false);
                    if (card == null || Integer.parseInt(card.getId().split(" ")[3]) <= getCardValue(tableCard)) {
                        card = Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
                    }
                }
            }
        }

        return card;
    }

    private Rectangle findMinCardName(boolean isBriscola) {
        Card minCard = Arrays.stream(hand.getCards())
            .filter(Objects::nonNull)
            .filter(card -> card.isBriscola() == isBriscola)
            .min(Comparator.comparingInt(this::getCardValue))
            .orElse(null);

        return getRectangle(minCard);
    }

    private Rectangle findMaxCardName(String seed, boolean isBriscola) {
        Card maxCard = Arrays.stream(hand.getCards())
            .filter(Objects::nonNull)
            .filter(card -> card.getSeed().equals(seed) && card.isBriscola() == isBriscola)
            .max(Comparator.comparingInt(this::getCardValue))
            .orElse(null);

        return getRectangle(maxCard);
    }

    private int getCardValue(Card card) {
        return switch (card.getName()) {
            case "1" -> 12;
            case "3" -> 11;
            default -> Integer.parseInt(card.getName());
        };
    }

    private Rectangle getRectangle(Card card) {
        for (int i = 0; i < 3; i++) {
            if (hand.getCards()[i] != null) {
                if (hand.getCards()[i].equals(card)) {
                    return hand.getCardsObject()[i];
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
