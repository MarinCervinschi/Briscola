package com.cervinschi.marin.javafx.briscola.models;
import static com.cervinschi.marin.javafx.briscola.utils.Const.*;

public class Card {
    private final String name;
    private final String seed;
    private final boolean isBriscola;
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

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + " of " + seed + " (" + value + ")";
    }

}
