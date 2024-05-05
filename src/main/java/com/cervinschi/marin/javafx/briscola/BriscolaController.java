package com.cervinschi.marin.javafx.briscola;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BriscolaController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}