package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Hand;
import javafx.animation.PauseTransition;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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
                Rectangle card = (Rectangle) gameObjects.getBotHandBox().getChildren().getFirst();
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

    public Hand getHand() {
        return hand;
    }

    public boolean isHasPlayed() {
        return hasPlayed;
    }

    public void setHasPlayed(boolean hasPlayed) {
        this.hasPlayed = hasPlayed;
    }


}
