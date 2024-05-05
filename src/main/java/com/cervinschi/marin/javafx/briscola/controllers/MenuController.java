package com.cervinschi.marin.javafx.briscola.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class MenuController {

    @FXML
    protected void startGame() {
        // Qui dovresti avviare il gioco
    }

    @FXML
    protected void showRules() {
        new Alert(Alert.AlertType.INFORMATION, "Le regole del gioco sono...").showAndWait();
    }
    @FXML
    protected void exit() {
        System.exit(0);
    }
}