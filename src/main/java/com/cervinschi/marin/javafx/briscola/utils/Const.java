package com.cervinschi.marin.javafx.briscola.utils;

/* Constants used in the game */
public final class Const {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    public static final int CWIDTH = 100;
    public static final int CHEIGHT = 150;

    // Prevents instantiation
    private Const() {
        throw new AssertionError("Cannot instantiate " + getClass().getSimpleName());
    }
}
