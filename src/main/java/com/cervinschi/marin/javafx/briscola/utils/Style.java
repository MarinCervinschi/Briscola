package com.cervinschi.marin.javafx.briscola.utils;

import com.cervinschi.marin.javafx.briscola.controllers.GameObjects;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.control.MenuButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Style {
    public static void setTurnStyle(boolean botWon, GameObjects gameObjects) {
        if (botWon) {
            gameObjects.getPlayerTurn().setStyle("-fx-background-color: rgba(255, 255, 255, 0.3)");
            gameObjects.getBotTurn().setStyle("-fx-background-color: #2a2a2a");
        } else {
            gameObjects.getBotTurn().setStyle("-fx-background-color: rgba(255, 255, 255, 0.3)");
            gameObjects.getPlayerTurn().setStyle("-fx-background-color: #2a2a2a");
        }
    }

    public static void setCardStyle(Rectangle rectangle) {
        /* Set bord radius*/
        rectangle.setArcHeight(16);
        rectangle.setArcWidth(16);

        /* Set border */
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1);

        /* Set shadow */
        DropShadow dropShadow1 = new DropShadow();
        dropShadow1.setRadius(12.0);
        dropShadow1.setOffsetX(0.0);
        dropShadow1.setOffsetY(6.0);
        dropShadow1.setColor(Color.rgb(50, 50, 93, 0.25));

        DropShadow dropShadow2 = new DropShadow();
        dropShadow2.setRadius(7.0);
        dropShadow2.setOffsetX(0.0);
        dropShadow2.setOffsetY(3.0);
        dropShadow2.setColor(Color.rgb(0, 0, 0, 0.3));

        /* add shadow */
        rectangle.setEffect(dropShadow1);
        rectangle.setEffect(dropShadow2);
    }

    public static void showHoverEffect(Rectangle card) {
        card.setOnMouseEntered(e -> {
            card.setTranslateY(-20);
            card.setCursor(Cursor.HAND);
        });
        card.setOnMouseExited(e -> {
            card.setTranslateY(0);
            card.setCursor(Cursor.DEFAULT);
        });
    }

    public static void setMenuIconHover(MenuButton menuIcon, TranslateTransition tt) {
        menuIcon.setOnMouseEntered(e -> {
            tt.setToX(10);
            tt.setToY(10);
            tt.playFromStart();
        });

        menuIcon.setOnMouseExited(e -> {
            tt.setToX(0);
            tt.setToY(0);
            tt.playFromStart();
        });
    }
}
