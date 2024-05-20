package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Board;
import com.cervinschi.marin.javafx.briscola.models.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Font;

import java.util.ArrayDeque;
import java.util.Objects;

import static com.cervinschi.marin.javafx.briscola.utils.Const.CHEIGHT;
import static com.cervinschi.marin.javafx.briscola.utils.Const.CWIDTH;

public class GameObjects {
    private final BorderPane boardPane;
    private final AnchorPane root;

    private Text playerPoints;
    private Text botPoints;
    private Text deckCards;

    private BorderPane tablePane;

    private HBox playerHandBox;
    private HBox botHandBox;
    private HBox tableBox;

    private Board board;
    private ArrayDeque<Rectangle> deckObject;


    public GameObjects(BorderPane boardPane, AnchorPane root) {
        this.boardPane = boardPane;
        this.root = root;
    }

    public void createGameObjects() {
        botPoints = createText(740.0, 121.0, 35);
        playerPoints = createText(740.0, 607.0, 35);
        deckCards = createText(110, 500, 25);
        createBorderPane();

        root.getChildren().addAll(botPoints, playerPoints, deckCards, tablePane);
    }

    public void showBackground() {
        String path = "/com/cervinschi/marin/javafx/briscola/media/background.png";
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, true);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        boardPane.setBackground(background);
    }

    public void initializeGameObjects() {
        // remove sprites from eventual former match
        tablePane.getChildren().clear();

        playerHandBox = new HBox();
        botHandBox = new HBox();
        tableBox = new HBox();

        board = new Board();

        deckObject = new ArrayDeque<>();

        for (Card card : board.getDeck()) {
            Rectangle rectangle = createCardObject(card);
            deckObject.add(rectangle);
        }

        initializeDeckBox();
    }

    public void appendHandsObject() {
        playerHandBox.setSpacing(10);
        botHandBox.setSpacing(10);
        tableBox.setSpacing(10);

        playerHandBox.setAlignment(Pos.CENTER);
        botHandBox.setAlignment(Pos.CENTER);
        tableBox.setAlignment(Pos.CENTER);

        tablePane.setTop(botHandBox);
        tablePane.setBottom(playerHandBox);
        tablePane.setCenter(tableBox);
    }

    public void initializePoints(String playerPoints, String botPoints, String deckCards) {
        this.playerPoints.setText(playerPoints);
        this.botPoints.setText(botPoints);
        this.deckCards.setText(deckCards);
    }

    private void initializeDeckBox() {
        HBox deckBox = new HBox();

        deckBox.setAlignment(Pos.CENTER);

        StackPane stack = new StackPane();

        Rectangle briscola = Objects.requireNonNull(deckObject.poll());
        board.setBriscolaToCards(board.getDeck().getFirst().getSeed());
        board.setBriscolaObject(briscola);
        board.setBriscola(Objects.requireNonNull(board.getDeck().poll()));
        Objects.requireNonNull(briscola).setTranslateY(-50);
        Objects.requireNonNull(briscola).setTranslateX(25);

        Rectangle backDeck = createCardObject(new Card("1", "back", 0, false));
        Objects.requireNonNull(backDeck).setTranslateY(50);

        stack.getChildren().addAll(briscola, backDeck);

        deckBox.getChildren().add(stack);

        tablePane.setLeft(deckBox);
        tablePane.setPadding(new Insets(5));
    }

    protected Rectangle createCardObject(Card card) {
        Rectangle rectangle = new Rectangle(CWIDTH, CHEIGHT);
        setCardStyle(rectangle);
        rectangle.setId(card.toString());

        String path = "/com/cervinschi/marin/javafx/briscola/media/cards/" + card.getSeed() + card.getName() + ".png";
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));

        ImagePattern imagePattern = new ImagePattern(image);
        rectangle.setFill(imagePattern);
        return rectangle;
    }

    private void setCardStyle(Rectangle rectangle) {
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

    private Text createText(double layoutX, double layoutY, double fontSize) {
        Text text = new Text();
        text.setFill(Color.WHITE);
        text.setLayoutX(layoutX);
        text.setLayoutY(layoutY);
        text.setStroke(Color.WHITE);
        text.setStrokeWidth(0.0);
        text.setFont(Font.font("Arial Bold", FontWeight.BOLD, fontSize));
        return text;
    }

    private void createBorderPane() {
        tablePane = new BorderPane();
        tablePane.setPrefHeight(665);
        tablePane.setPrefWidth(800);
        AnchorPane.setBottomAnchor(tablePane, 0.0);
        AnchorPane.setLeftAnchor(tablePane, 0.0);
        AnchorPane.setRightAnchor(tablePane, 0.0);
        AnchorPane.setTopAnchor(tablePane, 0.0);
    }

    public Text getPlayerPoints() {
        return playerPoints;
    }

    public Text getBotPoints() {
        return botPoints;
    }

    public Text getDeckCards() {
        return deckCards;
    }

    public BorderPane getTablePane() {
        return tablePane;
    }

    public HBox getPlayerHandBox() {
        return playerHandBox;
    }

    public HBox getBotHandBox() {
        return botHandBox;
    }

    public HBox getTableBox() {
        return tableBox;
    }

    public Board getBoard() {
        return board;
    }

    public ArrayDeque<Rectangle> getDeckObject() {
        return deckObject;
    }
}
