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

    private final String briscolaSeed;

    private final Hand hand;

    private boolean hasPlayed = false;

    public Bot(GameObjects gameObjects, String mode) {
        super(gameObjects, mode);
        this.mode = mode;
        this.gameObjects = gameObjects;
        this.hand = gameObjects.getBoard().getBotHand();
        this.briscolaSeed = gameObjects.getBoard().getBriscolaCard().getSeed();
    }

    public void move() {
        if (isPauseActive() || hasPlayed || (gameObjects.getBotHandBox().getChildren().isEmpty())) return;
        PauseTransition pause = new PauseTransition(Duration.seconds(1.8));
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

                Card cardToPlay = getCard(card);

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
                    if (getCard(maxCard).getValue() == 11) {
                        card = maxCard;
                    } else {
                        card = Optional.ofNullable(findMinCardName(true)).orElseGet(() -> findMinCardName(false));
                    }
                } else {
                    card = Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
                }
            } else {
                /* if table card is 10 or 11 (higher points) */
                card = findBestNoBriscolaCard(tableCard);
            }
        }

        return card;
    }

    private Rectangle hardMove() {
        Card tableCard = gameObjects.getBoard().getTable(0);
        Rectangle maxCard = findMaxCardName(null, false);
        Rectangle minBriscolaCard = findMinCardName(true);
        Rectangle minCard = findMinCardName(false);
        
        Rectangle card;
        double probabilityToHaveBriscola = 0;
        double probabilityToHaveSeed = 0;
        double probabilityToHaveHigherCardSameSeed = 0;

        if (tableCard == null && maxCard != null) {
            probabilityToHaveBriscola = probabilityToHaveBriscola(getPlayedCards(), hand.getLength()) * 100;
            probabilityToHaveSeed = probabilityToHaveSeed(getPlayedCards(), hand.getLength(), getCard(maxCard).getSeed()) * 100;
            probabilityToHaveHigherCardSameSeed = probabilityToHaveHigherCardSameSeed(getPlayedCards(), hand.getLength(), getCard(maxCard)) * 100;
        }

        /* bot move first */
        if (tableCard == null) {
            if (probabilityToHaveBriscola < 30 && probabilityToHaveSeed < 30 && probabilityToHaveHigherCardSameSeed < 30) {
                card = maxCard != null ? maxCard : minBriscolaCard;
            } else {
                card = minCard != null ? minCard : minBriscolaCard;
                if (getCard(minCard).getValue() > 9) {
                    card = minBriscolaCard != null ? minBriscolaCard : minCard;
                }
            }
        } else {
            /* bot move second */
            if (tableCard.isBriscola()) {
                /* if table card is briscola */
                Rectangle maxBriscolaCard = findMaxCardName(tableCard.getSeed(), false);
                if (tableCard.getValue() == 10 && maxBriscolaCard != null) {
                    if (getCard(maxBriscolaCard).getValue() == 11) {
                        card = maxBriscolaCard;
                    } else {
                        card = minCard != null ? minCard : minBriscolaCard;
                        card = findMinBriscolaOrMinCard(card);
                    }
                } else {
                    card = minCard != null ? minCard : minBriscolaCard;
                    card = findMinBriscolaOrMinCard(card);
                }
            } else {
                /* if table card is 10 or 11 (higher points) */
                card = findBestNoBriscolaCard(tableCard);
                card = findMinBriscolaOrMinCard(card);
            }
        }

        return card;
    }

    private Rectangle findBestNoBriscolaCard(Card tableCard) {
        Rectangle card;
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
            if (card == null || getCard(card).getValue() <= getCardValue(tableCard)) {
                card = Optional.ofNullable(findMinCardName(false)).orElseGet(() -> findMinCardName(true));
            }
        }
        return card;
    }

    private Rectangle findMinBriscolaOrMinCard(Rectangle card) {
        int cardValue = getCard(card).getValue();
        if (cardValue > 9 && getCardValue(gameObjects.getBoard().getTable(0)) < cardValue) {
            card = Optional.ofNullable(findMinCardName(true)).orElseGet(() -> findMinCardName(false));
        }
        return card;
    }

    private double probabilityToHaveBriscola(List<Card> playedCards, int playerCardsCount) {
        int playedBriscolaCount = (int) playedCards.stream().filter(Card::isBriscola).count();
        int cardsLeft = gameObjects.getDeckObject().size() + getPlayerHand().getLength() - 1;
        int myBriscolaCount = (int) Arrays.stream(hand.getCards()).filter(Card::isBriscola).count();

        double probabilityToBriscolaInGame = calcProbability(9, playedBriscolaCount, myBriscolaCount, cardsLeft);

        return complementaryProbability(probabilityToBriscolaInGame, playerCardsCount);
    }

    private double probabilityToHaveSeed(List<Card> playedCards, int playerCardsCount, String seed) {
        long playedSeedCount = playedCards.stream().filter(card -> card.getSeed().equals(seed)).count();
        int rest = briscolaSeed.equals(seed) ? 1 : 0;
        int cardsLeft = gameObjects.getDeckObject().size() + getPlayerHand().getLength() - rest;

        int mySeedCount = (int) Arrays.stream(hand.getCards()).filter(card -> card.getSeed().equals(seed)).count();
        
        double probabilityToSeedInGame = calcProbability(10, (int)playedSeedCount, mySeedCount, cardsLeft);
        
        return complementaryProbability(probabilityToSeedInGame, playerCardsCount);
    }

    private double probabilityToHaveHigherCardSameSeed(List<Card> playedCards, int playerCardsCount, Card tableCard) {
        long playedHigherCardCount = playedCards.stream().filter(card -> card.getSeed().equals(tableCard.getSeed()) && card.getValue() > getCardValue(tableCard)).count();

        int cardsLeft = gameObjects.getDeckObject().size() + getPlayerHand().getLength();
        int myHigherCardCount = (int) Arrays.stream(hand.getCards()).filter(card -> card.getSeed().equals(tableCard.getSeed()) && card.getValue() > getCardValue(tableCard)).count();

        int countHigherCards = Math.max((10 - getCardValue(tableCard)), 0);
        if (tableCard.getValue() == 10) countHigherCards = 1;
        double probabilityToHigherCardInGame = calcProbability(countHigherCards, (int)playedHigherCardCount, myHigherCardCount, cardsLeft);

        return complementaryProbability(probabilityToHigherCardInGame, playerCardsCount);
    }

    private double complementaryProbability(double probability, int opponentCardsCount) {
        double probabilityCalc = Math.pow((1 - probability), opponentCardsCount);
        return 1 - probabilityCalc;
    }
    
    private double calcProbability(int cardsInTheGame, int playedCards, int myCards, int cardsLeft) {
        return (double)(cardsInTheGame - playedCards - myCards) / cardsLeft;
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
