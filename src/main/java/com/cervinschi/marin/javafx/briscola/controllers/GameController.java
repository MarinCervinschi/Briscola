package com.cervinschi.marin.javafx.briscola.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;


public class GameController {

    @FXML protected BorderPane boardPane;
    @FXML protected AnchorPane root;

    protected GameObjects gameObjects;
    protected GameInit gameInit;

    private AnimationTimer timer;
    private boolean gameStarted = false;

    @FXML
    public void initialize() {
        gameObjects = new GameObjects(boardPane, root);
        gameObjects.createGameObjects();
        gameObjects.showBackground();
        gameObjects.initializeGameObjects();
    }

    @FXML
    protected void startGame() {
        if (gameStarted) return;
        gameStarted = true;

        gameInit = new GameInit(gameObjects);
        start();
        gameObjects.initializePoints("0", "0", "34");
    }

    @FXML
    protected void newGame() {
        if (!gameStarted) return;
        gameStarted = false;

        gameObjects.initializeGameObjects();
        gameObjects.initializePoints(" ", " ", "40");
    }

    void start() {
        if (timer != null) timer.stop();

        timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                gameInit.mainLoop();
                if (gameInit.isGameEnded()) timer.stop();
            }
        };
        timer.start();
    }

    @FXML
    protected void showRules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rules");
        alert.setHeaderText("Briscola rules");
        alert.setContentText("The game is played with a deck of 40 cards. " +
                "The game is played by two players, or four players in fixed partnerships. " +
                "Players face each other, and the game is played clockwise. The player who " +
                "did not deal the cards starts the game. The player who wins the trick leads " +
                "the next trick. The game is won by the first player to reach 61 points. " +
                "If both players reach 61 points in the same hand, the game is a tie.");
        alert.showAndWait();
    }
    @FXML
    protected void exit() {
        System.exit(0);
    }
}