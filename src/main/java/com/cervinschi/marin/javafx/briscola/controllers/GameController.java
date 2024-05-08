package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Board;
import com.cervinschi.marin.javafx.briscola.models.Card;
import static com.cervinschi.marin.javafx.briscola.utils.Const.*;
import javafx.animation.AnimationTimer;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import java.util.*;


public class GameController {

    @FXML private BorderPane boardPane;
    @FXML private BorderPane boardPaneHands;
    @FXML private AnchorPane root;
    @FXML private Text playerPoints;
    @FXML private Text botPoints;
    @FXML private Text deckCards;
    AnimationTimer timer;

    private Board board;
    private boolean gameStarted = false;
    private Deque<Rectangle> deckObject;
    private InitGame initGame;

    @FXML
    public void initialize() {
        showBackground();
        initializeGameObjects();
    }

    @FXML
    protected void newGame() {
        if (gameStarted) {
            gameStarted = false;
        } else {
            return;
        }
        initializeGameObjects();
        initializePoints();
    }

    @FXML
    protected void startGame() {
        if (gameStarted) {
            return;
        } else {
            gameStarted = true;
        }
        initGame = new InitGame(deckObject, board, boardPaneHands, playerPoints, botPoints, deckCards);
        initializeTimer();
        initializePoints();
    }

    public void showBackground() {
        String path = "/com/cervinschi/marin/javafx/briscola/media/background.png";
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, true);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        boardPane.setBackground(background);
    }

    private void initializeGameObjects() {
        // remove sprites from eventual former match
        boardPaneHands.getChildren().removeAll(boardPaneHands.getBottom(), boardPaneHands.getTop(), boardPaneHands.getCenter());

        board = new Board();

        deckObject = new ArrayDeque<>();

        for (Card card : board.getDeck()) {
            Rectangle rectangle = createCardObject(card);
            deckObject.add(rectangle);
        }

        HBox deckBox = new HBox();

        deckBox.setAlignment(Pos.CENTER);

        StackPane stack = new StackPane();

        Rectangle briscola = Objects.requireNonNull(deckObject.pollLast());
        board.setBriscola(board.getDeck().poll());
        board.setBriscolaToCards(board.getBriscola().getSeed());
        Objects.requireNonNull(briscola).setTranslateY(-50);
        Objects.requireNonNull(briscola).setTranslateX(25);

        Rectangle backDeck = createCardObject(new Card("1", "back", 0, false));
        Objects.requireNonNull(backDeck).setTranslateY(50);

        stack.getChildren().addAll(briscola, backDeck);

        deckBox.getChildren().add(stack);

        boardPaneHands.setLeft(deckBox);
        boardPaneHands.setPadding(new Insets(5));

    }
    void initializeTimer() {
        if (timer != null) {
            timer.stop();
        }
        timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                initGame.mainLoop();
            }
        };
        timer.start();
    }

    private Rectangle createCardObject(Card card) {
        Rectangle rectangle = new Rectangle(CWIDTH, CHEIGHT);
        setCardStyle(rectangle);

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

    private void initializePoints() {
        playerPoints.setText("0");
        botPoints.setText("0");
        deckCards.setText("33");
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

