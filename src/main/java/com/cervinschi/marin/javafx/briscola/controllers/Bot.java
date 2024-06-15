package com.cervinschi.marin.javafx.briscola.controllers;

import com.cervinschi.marin.javafx.briscola.models.Card;
import com.cervinschi.marin.javafx.briscola.models.Hand;
import javafx.animation.PauseTransition;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;

public class Bot extends GameInit{
    private final GameObjects gameObjects;
    private final String mode;

    private final Hand hand;

    private boolean hasPlayed = false;

    public Bot(GameObjects gameObjects, String mode) {
        super(gameObjects, mode);
        this.mode = mode;
        this.gameObjects = gameObjects;
        this.hand = gameObjects.getBoard().getBotHand();
    }

    public void move() {
        if (isPauseActive() || hasPlayed || (gameObjects.getBotHandBox().getChildren().isEmpty())) return;
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            if (!gameObjects.getBoard().tableIsFull()) {
                setPauseActive(false);
                hasPlayed = true;

                Rectangle card = switch (mode) {
                    case "Easy" -> easyMove();
                    case "Medium" -> mediumMove();
                    default -> hardMove();
                };

                int index = gameObjects.getBoard().getDeck().isEmpty() ? 1 : 3;
                gameObjects.getBotHandBox().getChildren().remove(new Random().nextInt(index));

                Card cardToPlay = findCardToPlay(card);

                createTransition(card, 100, -200);
                playSound("card-sound");

                gameObjects.getTableBox().getChildren().add(card);

                gameObjects.getBoard().addCardToTable(cardToPlay);
                getPlayedCards().add(cardToPlay);
                hand.removeCard(cardToPlay, card);
            }
        });
        setPauseActive(true);
        pause.play();
    }

    private Card findCardToPlay(Rectangle card) {
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
        Rectangle card;
        Card tableCard = gameObjects.getBoard().getTable(0);

        /* bot move first */
        if (tableCard == null) {
            card = Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
        } else {
            /* bot move second */
            if (tableCard.isBriscola()) {
                /* if table card is briscola */
                Rectangle maxCard = findMaxCardName(tableCard.getSeed(), true);
                if (tableCard.getValue() == 10 && maxCard != null) {
                    if (Integer.parseInt(maxCard.getId().split(" ")[3]) == 11) {
                        card = maxCard;
                    } else {
                        card = Optional.ofNullable(findMinCardName(true)).orElseGet(() -> findMinCardName(false));
                    }
                } else {
                    card = Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
                }
            } else {
                /* if table card is 10 or 11 (higher points) */
                if (tableCard.getValue() == 10 || tableCard.getValue() == 11) {
                    Rectangle maxCard = findMaxCardName(tableCard.getSeed(), false);
                    if (maxCard != null) {
                        if (Integer.parseInt(maxCard.getId().split(" ")[3]) == 11) {
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
                    if (card == null || Integer.parseInt(card.getId().split(" ")[3]) <= getCardValue(tableCard)) {
                        card = Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
                    }
                }
            }
        }

        return card;
    }

    private Rectangle hardMove() {
        Rectangle card;
        Card tableCard = gameObjects.getBoard().getTable(0);
        double probabilityToHaveBriscola = probabilityToHaveBriscola(getPlayedCards(), hand.getLength());
        if (tableCard != null) {
            double probabilityToHaveSeed = probabilityToHaveSeed(getPlayedCards(), hand.getLength(), tableCard.getSeed());
            double probabilityToHaveHigherCardSameSeed = probabilityToHaveHigherCardSameSeed(getPlayedCards(), hand.getLength(), tableCard);
            double probabilityToHaveHigherCard = probabilityToHaveHigherCard(getPlayedCards(), hand.getLength(), tableCard);

        }

        /* bot move first */
        if (tableCard == null) {
            if (probabilityToHaveBriscola > 50) {
                card = findMaxCardName(null, false);
                if (card == null) {
                    card = findMinCardName(false);
                } else {
                    card = findMinCardName(false);
                }
            } else {
                card = findMinCardName(false);
            }
        } else {
            /* bot move second */
            if (tableCard.isBriscola()) {
                /* if table card is briscola */
                Rectangle maxCard = findMaxCardName(tableCard.getSeed(), true);
                if (tableCard.getValue() == 10 && maxCard != null) {
                    if (Integer.parseInt(maxCard.getId().split(" ")[3]) == 11) {
                        card = maxCard;
                    } else {
                        card = Optional.ofNullable(findMinCardName(true)).orElseGet(() -> findMinCardName(false));
                    }
                } else {
                    card = Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
                }
            } else {
                /* if table card is 10 or 11 (higher points) */
                if (tableCard.getValue() == 10 || tableCard.getValue() == 11) {
                    Rectangle maxCard = findMaxCardName(tableCard.getSeed(), false);
                    if (maxCard != null) {
                        if (Integer.parseInt(maxCard.getId().split(" ")[3]) == 11) {
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
                    if (card == null || Integer.parseInt(card.getId().split(" ")[3]) <= getCardValue(tableCard)) {
                        card = Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
                    }
                }
            }

        }

        return card;
    }

    private double probabilityToHaveBriscola(List<Card> playedCards, int playerCardsCount) {
        int playedBriscolaCount = (int) playedCards.stream().filter(Card::isBriscola).count();
        int cardsLeft = gameObjects.getDeckObject().size() + getPlayerHand().getLength();
        int myBriscolaCount = (int) Arrays.stream(hand.getCards()).filter(Objects::nonNull).filter(Card::isBriscola).count();

        double probabilityToBriscolaInGame = calcProbability(9, playedBriscolaCount, myBriscolaCount, cardsLeft) / 100;
        double probabilityNoBriscola = Math.pow((1 - probabilityToBriscolaInGame), playerCardsCount);
        return 1 - probabilityNoBriscola; // 40 : 100 : 9 : x
    }

    private double probabilityToHaveSeed(List<Card> playedCards, int playerCardsCount, String seed) {
        long playedSeedCount = playedCards.stream().filter(card -> card.getSeed().equals(seed)).count();

        int remainingSeedCount = 10 - (int)playedSeedCount;
        return (double)(playerCardsCount * 100) / remainingSeedCount;
    }

    private double probabilityToHaveHigherCardSameSeed(List<Card> playedCards, int playerCardsCount, Card tableCard) {
        long playedHigherCardCount = playedCards.stream().filter(card -> card.getSeed().equals(tableCard.getSeed()) && card.getValue() > tableCard.getValue()).count();

        int remainingHigherCardCount = 10 - (int)playedHigherCardCount;
        return (double)(playerCardsCount * 100) / remainingHigherCardCount;
    }

    private double probabilityToHaveHigherCard(List<Card> playedCards, int playerCardsCount, Card tableCard) {
        long playedHigherCardCount = playedCards.stream().filter(card -> card.getValue() > tableCard.getValue()).count();

        int remainingHigherCardCount = 10 - (int)playedHigherCardCount;
        return (double)(playerCardsCount * 100) / remainingHigherCardCount;
    }

    private double calcProbability(int cardsInTheGame, int playedCards, int myCards, int cardsLeft) {
        return (double)(cardsInTheGame - playedCards - myCards) * 100 / cardsLeft;
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
        for (int i = 0; i < 3; i++) {
            if (hand.getCards()[i] != null) {
                if (hand.getCards()[i].equals(card)) {
                    return hand.getCardsObject()[i];
                }
            }
        }
        return null;
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

}
