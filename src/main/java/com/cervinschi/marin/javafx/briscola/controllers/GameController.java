package com.cervinschi.marin.javafx.briscola.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;


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

        gameInit.fadeTransition(root);
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
        Stage rulesStage = new Stage();
        rulesStage.setTitle("Rules");

        WebView webView = new WebView();

        try {
            InputStream is = getClass().getResourceAsStream("/com/cervinschi/marin/javafx/briscola/briscola_rules.html");
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));
            String htmlContent = reader.lines().collect(Collectors.joining("\n"));
            webView.getEngine().loadContent(htmlContent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Scene rulesScene = new Scene(webView, 600, 600);
        rulesStage.setScene(rulesScene);
        rulesStage.show();
    }
    @FXML
    protected void exit() {
        System.exit(0);
    }
}