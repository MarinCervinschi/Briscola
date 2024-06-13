package com.cervinschi.marin.javafx.briscola;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.cervinschi.marin.javafx.briscola.utils.Const.HEIGHT;
import static com.cervinschi.marin.javafx.briscola.utils.Const.WIDTH;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("board-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle("Briscola!");
        stage.setScene(scene);

        /* block the resizing of the window */
        stage.setMinWidth(WIDTH);
        stage.setMaxWidth(WIDTH);
        stage.setMinHeight(HEIGHT);
        stage.setMaxHeight(HEIGHT);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}