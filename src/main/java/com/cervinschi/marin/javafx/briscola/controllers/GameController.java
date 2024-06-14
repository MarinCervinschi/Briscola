package com.cervinschi.marin.javafx.briscola.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class GameController {

    @FXML protected BorderPane boardPane;
    @FXML protected AnchorPane root;
    @FXML protected VBox menuVBox;
    @FXML protected ComboBox<String> difficultyComboBox;

    protected GameObjects gameObjects;
    protected GameInit gameInit;

    private AnimationTimer timer;

    @FXML
    protected void initialize() {
        gameObjects = new GameObjects(boardPane, root);
        gameObjects.showBackground();
    }

    @FXML
    protected void startGame() {
        String difficulty = difficultyComboBox.getValue();
        if (difficulty == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please select a difficulty");
            alert.showAndWait();
            return;
        }
        root.getChildren().clear();

        gameObjects.createGameObjects();
        gameObjects.initializeGameObjects();

        addMenuIconAction();
        gameInit = new GameInit(gameObjects, difficulty);
        Bot bot = new Bot(gameObjects, difficulty);
        gameInit.setBot(bot);
        start();
        gameObjects.initializePoints("0", "0", "34");
    }

    private void addMenuIconAction() {
        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(e -> startGame());
        MenuItem home = new MenuItem("Home");
        home.setOnAction(e -> {
            root.getChildren().clear();
            root.getChildren().add(menuVBox);
            initialize();
        });
        MenuItem rules = new MenuItem("Rules");
        rules.setOnAction(e -> showRules());
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> exit());

        gameObjects.getMenuIcon().getItems().addAll(newGame, home, rules, exit);
    }

    private void start() {
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