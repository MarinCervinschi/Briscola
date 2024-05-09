package com.cervinschi.marin.javafx.briscola.models;

public class Card {
    private final String name;
    private final String seed;
    private boolean isBriscola;
    private final int value;

    public Card(String name, String seed, int value, boolean isBriscola) {
        this.name = name;
        this.seed = seed;
        this.isBriscola = isBriscola;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getSeed() {
        return seed;
    }

    public boolean isBriscola() {
        return isBriscola;
    }

    public void setBriscola(boolean isBriscola) {
        this.isBriscola = isBriscola;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + " of " + seed + " (" + value + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Card card = (Card) obj;
        return value == card.value && isBriscola == card.isBriscola && name.equals(card.name) && seed.equals(card.seed);
    }

}
