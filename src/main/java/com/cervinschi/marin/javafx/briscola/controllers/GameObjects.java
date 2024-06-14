package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Board;
import com.cervinschi.marin.javafx.briscola.models.Card;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.Objects;

import static com.cervinschi.marin.javafx.briscola.utils.Const.CHEIGHT;
import static com.cervinschi.marin.javafx.briscola.utils.Const.CWIDTH;

public class GameObjects {
    private final BorderPane boardPane;

    private final AnchorPane root;

    private Label playerPoints;

    private Label botPoints;
    private Label deckCards;
    private Label botTurn;
    private Label playerTurn;
    private BorderPane tablePane;

    private HBox playerHandBox;

    private HBox botHandBox;
    private HBox tableBox;
    private MenuButton menuIcon;


    private Board board;

    private ArrayDeque<Rectangle> deckObject;

    public GameObjects(BorderPane boardPane, AnchorPane root) {
        this.boardPane = boardPane;
        this.root = root;
    }

    public void createGameObjects() {
        botPoints = createLabel(720.0, 250.0, 30);
        playerPoints = createLabel(720.0, 450.0, 30);
        deckCards = createLabel(110, 500, 25);
        botTurn = createLabel(600.0, 30.0, 20);
        playerTurn = createLabel(600.0, 700.0, 20);
        menuIcon = createMenuIcon();

        createBorderPane();

        root.getChildren().addAll(botPoints, playerPoints, deckCards, tablePane, menuIcon);
    }

    private MenuButton createMenuIcon() {
        MenuButton menuIcon = new MenuButton();
        Image menuImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/cervinschi/marin/javafx/briscola/media/menu.png")));

        ImageView menuImageView = new ImageView(menuImage);
        menuImageView.setFitHeight(25);
        menuImageView.setFitWidth(25);
        menuIcon.setGraphic(menuImageView);
        menuIcon.setPrefSize(25, 25);

        menuIcon.getStyleClass().add("menu-button");

        TranslateTransition tt = new TranslateTransition(Duration.millis(200), menuIcon);

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

        AnchorPane.setTopAnchor(menuIcon, 15.0);
        AnchorPane.setLeftAnchor(menuIcon, 15.0);
        return menuIcon;
    }

    private void setTurnIcon(String seed, Label label) {
        Image turnImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/cervinschi/marin/javafx/briscola/media/turns/" + seed + ".png")));
        ImageView turnImageView = new ImageView(turnImage);
        turnImageView.setFitHeight(25);
        turnImageView.setFitWidth(25);
        label.setGraphic(turnImageView);
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
        setTurnIcon(board.getBriscola().getSeed(), botTurn);
        setTurnIcon(board.getBriscola().getSeed(), playerTurn);
        botTurn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3)");
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

    private Label createLabel(double layoutX, double layoutY, double fontSize) {
        Label label = new Label();
        label.setLayoutX(layoutX);
        label.setLayoutY(layoutY);
        label.setFont(Font.font("SansSerif", fontSize));
        label.getStyleClass().add("points-label");
        return label;
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

    public Label getPlayerPoints() {
        return playerPoints;
    }

    public Label getBotPoints() {
        return botPoints;
    }

    public Label getDeckCards() {
        return deckCards;
    }

    public Label getPlayerTurn() {
        return playerTurn;
    }

    public Label getBotTurn() {
        return botTurn;
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

    public MenuButton getMenuIcon() {
        return menuIcon;
    }

    public ArrayDeque<Rectangle> getDeckObject() {
        return deckObject;
    }
}
