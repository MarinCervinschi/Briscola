package com.cervinschi.marin.javafx.briscola.utils;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Animation {
    public static void createTransition(Rectangle card, int x, int y) {
        card.setTranslateX(x);
        card.setTranslateY(y);

        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), card);

        tt.setToX(0);
        tt.setToY(0);

        tt.play();
    }

    public static void fadeTransition(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(3000), node);

        ft.setFromValue(0.0);
        ft.setToValue(1.0);

        ft.play();
    }
}
