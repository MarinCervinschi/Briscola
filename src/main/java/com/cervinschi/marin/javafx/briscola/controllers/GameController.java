package com.cervinschi.marin.javafx.briscola.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.Objects;


public class GameController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    public void initialize() {
        showBackground();
    }

    public void showBackground() {
        String path = "/com/cervinschi/marin/javafx/briscola/media/background.png";
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, true);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        anchorPane.setBackground(background);
    }

    @FXML
    protected void newGame() {
        System.out.println("Game started");
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

