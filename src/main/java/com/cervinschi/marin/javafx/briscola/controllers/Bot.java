package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Card;
import com.cervinschi.marin.javafx.briscola.models.Hand;
import javafx.animation.PauseTransition;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.*;

public class Bot extends GameInit {
    private final GameObjects gameObjects;
    private final String mode;

    private final Hand hand;

    private boolean hasPlayed = false;

    public Bot(GameObjects gameObjects, String mode) {
        super(gameObjects);
        this.mode = mode;
        this.gameObjects = gameObjects;
        this.hand = gameObjects.getBoard().getBotHand();
    }

    public Hand getHand() {
        return hand;
    }

    public void setHasPlayed(boolean hasPlayed) {
        this.hasPlayed = hasPlayed;
    }

    public boolean isHasPlayed() {
        return hasPlayed;
    }

    public void move() {
        if (isPauseActive() || hasPlayed || (gameObjects.getBotHandBox().getChildren().isEmpty())) return;
        PauseTransition pause = new PauseTransition(Duration.seconds(1.8));
        pause.setOnFinished(e -> {
            if (!gameObjects.getBoard().tableIsFull()) {
                setPauseActive(false);
                hasPlayed = true;

                /* bot move */
                Rectangle card = switch (mode) {
                    case "Easy" -> easyMove();
                    case "Medium" -> mediumMove();
                    default -> hardMove();
                };

                /* remove card from bot hand */
                gameObjects.getBotHandBox().getChildren().remove(new Random().nextInt(hand.getLength()));

                Card cardToPlay = getCard(card);

                createTransition(card, 100, -200);
                playSound("card-sound");

                /* add card to table */
                gameObjects.getTableBox().getChildren().add(card);

                gameObjects.getBoard().addCardToTable(cardToPlay);
                hand.removeCard(cardToPlay, card);
                getPlayedCards().add(cardToPlay);
            }
        });
        setPauseActive(true);
        pause.play();
    }

    private Card getCard(Rectangle card) {
        return Arrays.stream(hand.getCards())
                .filter(Objects::nonNull)
                .filter(handCard -> handCard.toString().equals(card.getId()))
                .findFirst()
                .orElse(null);
    }

    private Rectangle easyMove() {
        return Arrays.stream(hand.getCardsObject())
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(() -> hand.getCardsObject()[new Random().nextInt(3)]);
    }

    private Rectangle mediumMove() {
        Card tableCard = gameObjects.getBoard().getTable(0);

        /* bot move first */
        if (tableCard == null) {
            return Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
        } else {
            /* bot move second */
            return findBestCard(tableCard);
        }
    }

    private Rectangle hardMove() {
        Card tableCard = gameObjects.getBoard().getTable(0);

        /* calculate best card to play */
        Pair<Card, Double> bestCard = null;
        if (tableCard == null) {
            bestCard = calcBestCardToPlay(Arrays.asList(hand.getCards()), hand.getLength(), getPlayedCards());
        }

        /* bot move first */
        if (tableCard == null) {
            /* if the probability that player (has a briscola card + prob. to have a higher card) is less than 50% */
            if (bestCard.getValue() < 0.5 && !bestCard.getKey().isBriscola()) {
                return getRectangle(bestCard.getKey());
            } else {
                Rectangle card = Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
                return checkValue(card);
            }
        } else {
            /* bot move second */
            return findBestCard(tableCard);
        }
    }

    private Rectangle findBestCard(Card tableCard) {
        Rectangle card;
        if (tableCard.isBriscola()) {
            /* if table card is briscola */
            Rectangle maxBriscolaCard = findMaxCardName(null, true);
            if (tableCard.getValue() == 10 && maxBriscolaCard != null) {
                if (getCard(maxBriscolaCard).getValue() == 11) {
                    card = maxBriscolaCard;
                } else {
                    card = Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
                    card = mode.equals("Hard") ? checkValue(card) : card;
                }
            } else {
                card = Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
                card = mode.equals("Hard") ? checkValue(card) : card;
            }
        } else {
            /* if table card is 10 or 11 (higher points) */
            if (tableCard.getValue() == 10 || tableCard.getValue() == 11) {
                Rectangle maxCard = findMaxCardName(tableCard.getSeed(), false);
                if (maxCard != null) {
                    if (getCard(maxCard).getValue() == 11) {
                        card = maxCard;
                    } else {
                        card = Optional.ofNullable(findMinCardName(true)).orElseGet(() -> findMinCardName(false));
                    }
                } else {
                    card = Optional.ofNullable(findMinCardName(true)).orElseGet(() -> findMinCardName(false));
                }
            } else {
                /* check for the higher card of the same seed else give the smallest*/
                card = findMaxCardName(tableCard.getSeed(), false);
                if (card == null) {
                    card = Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
                    card = mode.equals("Hard") ? checkValue(card) : card;
                }
            }
        }
        return card;
    }

    private Rectangle checkValue(Rectangle card) {
        if (getCard(card).getValue() > 9) {
            card = Optional.ofNullable(findMinCardName(true)).orElseGet(() -> findMinCardName(false));
        }
        return card;
    }

    private Pair<Card, Double> calcBestCardToPlay(List<Card> cards, int playerCardsCount, List<Card> playedCards) {
        return cards.stream()
                .map(card -> new Pair<>(
                        card,
                        probabilityToHaveBriscola(playedCards, playerCardsCount) +
                                probabilityToHaveHigherCardSameSeed(playedCards, playerCardsCount, card)
                ))
                .min(Comparator.comparingDouble(Pair::getValue))
                .orElse(null);
    }

    private double probabilityToHaveBriscola(List<Card> playedCards, int playerCardsCount) {
        int playedBriscolaCount = (int) playedCards.stream().filter(Card::isBriscola).count();
        int cardsLeft = gameObjects.getDeckObject().size() + getPlayerHand().getLength() - 1;
        int myBriscolaCount = (int) Arrays.stream(hand.getCards()).filter(Card::isBriscola).count();

        double probabilityToBriscolaInGame = calcProbability(9, playedBriscolaCount, myBriscolaCount, cardsLeft);

        return complementaryProbability(probabilityToBriscolaInGame, playerCardsCount);
    }

    private double probabilityToHaveHigherCardSameSeed(List<Card> playedCards, int playerCardsCount, Card tableCard) {
        long playedHigherCardCount = playedCards.stream().filter(card -> card.getSeed().equals(tableCard.getSeed()) && card.getValue() > tableCard.getValue()).count();

        int cardsLeft = gameObjects.getDeckObject().size() + getPlayerHand().getLength();
        int myHigherCardCount = (int) Arrays.stream(hand.getCards()).filter(card -> card.getSeed().equals(tableCard.getSeed()) && card.getValue() > tableCard.getValue()).count();

        int countHigherCards = switch (tableCard.getValue()) {
            case 1 -> 0;
            case 3 -> 1;
            default -> 12 - Integer.parseInt(tableCard.getName());
        };
        double probabilityToHigherCardInGame = calcProbability(countHigherCards, (int) playedHigherCardCount, myHigherCardCount, cardsLeft);

        return complementaryProbability(probabilityToHigherCardInGame, playerCardsCount);
    }

    private double complementaryProbability(double probability, int opponentCardsCount) {
        double probabilityCalc = Math.pow((1 - probability), opponentCardsCount);
        return 1 - probabilityCalc;
    }

    private double calcProbability(int cardsInTheGame, int playedCards, int myCards, int cardsLeft) {
        return (double) (cardsInTheGame - playedCards - myCards) / cardsLeft;
    }

    private Rectangle findMinCardName(boolean isBriscola) {
        Card minCard = Arrays.stream(hand.getCards())
                .filter(Objects::nonNull)
                .filter(card -> card.isBriscola() == isBriscola)
                .min(Comparator.comparingInt(this::getCardValue))
                .orElse(null);

        return getRectangle(minCard);
    }

    private Rectangle findMaxCardName(String seed, boolean isBriscola) {
        Card maxCard = Arrays.stream(hand.getCards())
                .filter(Objects::nonNull)
                .filter(card -> seed == null || card.getSeed().equals(seed))
                .filter(card -> card.isBriscola() == isBriscola)
                .max(Comparator.comparingInt(this::getCardValue))
                .orElse(null);

        return getRectangle(maxCard);
    }

    private int getCardValue(Card card) {
        return switch (card.getName()) {
            case "1" -> 12;
            case "3" -> 11;
            default -> Integer.parseInt(card.getName());
        };
    }

    private Rectangle getRectangle(Card card) {
        if (card == null) return null;
        return Arrays.stream(hand.getCardsObject())
                .filter(rectangle -> rectangle.getId().equals(card.toString()))
                .findFirst()
                .orElse(null);
    }
}
