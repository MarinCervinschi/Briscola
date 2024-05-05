package com.cervinschi.marin.javafx.briscola.utils;

public final class Const {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 700;
    public static final int DECK = 40;
    public static final int SEED = 4;
    public static final int CWIDTH = 80;
    public static final int CHEIGHT = 120;

    // Prevents instantiation
    private Const() {
        throw new AssertionError("Cannot instantiate " + getClass().getSimpleName());
    }
}
