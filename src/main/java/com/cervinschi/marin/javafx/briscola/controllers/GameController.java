package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Board;
import com.cervinschi.marin.javafx.briscola.models.Card;
import static com.cervinschi.marin.javafx.briscola.utils.Const.*;
import javafx.animation.AnimationTimer;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.*;


public class GameController {

    @FXML protected BorderPane boardPane;
    @FXML protected AnchorPane root;

    protected GameObjects gameObjects;
    AnimationTimer timer;

    private boolean gameStarted = false;
    private InitGame initGame;

    @FXML
    public void initialize() {
        gameObjects = new GameObjects(boardPane, root);
        gameObjects.createGameObjects();
        gameObjects.showBackground();
        gameObjects.initializeGameObjects();
    }

    @FXML
    protected void newGame() {
        if (gameStarted) {
            gameStarted = false;
        } else {
            return;
        }
        gameObjects.initializeGameObjects();
        gameObjects.initializePoints(" ", " ", "40");
    }

    @FXML
    protected void startGame() {
        if (gameStarted) {
            return;
        } else {
            gameStarted = true;
        }
        initializeTimer();
        gameObjects.initializePoints("0", "0", "34");
    }


    void initializeTimer() {
        if (timer != null) {
            timer.stop();
        }
        timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                initGame.mainLoop();
                if (initGame.isGameEnded()) {
                    timer.stop();
                }
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

