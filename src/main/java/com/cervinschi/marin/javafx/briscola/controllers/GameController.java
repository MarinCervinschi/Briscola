package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Board;
import com.cervinschi.marin.javafx.briscola.models.Card;
import com.cervinschi.marin.javafx.briscola.models.Hand;
import static com.cervinschi.marin.javafx.briscola.utils.Const.*;
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

    private Board board;

    @FXML
    public void initialize() {
        showBackground();
        initializeGameObjects();
    }

    @FXML
    protected void newGame() {
        System.out.println("Game started");
        initializeGameObjects();
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
        root.getChildren().removeAll();

        board = new Board();

        Deque<Rectangle> deckObject = new ArrayDeque<>(List.of(new Rectangle()));

        for (int i = 0; i < DECK; i++) {
            Card card = board.getDeckCard(i);
            Rectangle rectangle = createCardObject(card, String.valueOf((i + 1) % 10));
            deckObject.add(rectangle);
        }

        deckObject = shuffleDeck(deckObject);

        Hand playerHand = new Hand();
        Hand botHand = new Hand();

        HBox playerHandBox = new HBox();
        HBox botHandBox = new HBox();

        playerHandBox.setAlignment(Pos.CENTER);
        botHandBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < 6; i++) {
            Rectangle card = deckObject.poll();
            if (i % 2 == 0) {
                playerHand.addCard(card);
                playerHandBox.getChildren().add(card);
            } else {
                botHand.addCard(card);
                botHandBox.getChildren().add(card);
            }
        }
        playerHandBox.setSpacing(10);
        botHandBox.setSpacing(10);

        boardPaneHands.setBottom(playerHandBox);
        boardPaneHands.setTop(botHandBox);
        boardPaneHands.setPadding(new Insets(5));

    }

    private Deque<Rectangle> shuffleDeck(Deque<Rectangle> deck) {
        List<Rectangle> deckList = new ArrayList<>(deck);
        Collections.shuffle(deckList);
        return new ArrayDeque<>(deckList);
    }

    private Rectangle createCardObject(Card card, String value) {
        if (value.equals("0")) {
            value = "10";
        }
        Rectangle rectangle = new Rectangle(CWIDTH, CHEIGHT);
        setStyle(rectangle);

        String path = "/com/cervinschi/marin/javafx/briscola/media/cards/" + card.getSeed() + value + ".png";
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));

        ImagePattern imagePattern = new ImagePattern(image);
        rectangle.setFill(imagePattern);
        return rectangle;
    }

    private void setStyle(Rectangle rectangle) {
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
    }

    private void updatePoints(Text player) {
        int points = Integer.parseInt(player.getText());
        points += board.getPoints();
        player.setText(String.valueOf(points));
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

