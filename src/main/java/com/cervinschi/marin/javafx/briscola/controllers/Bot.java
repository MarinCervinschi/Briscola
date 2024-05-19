package com.cervinschi.marin.javafx.briscola.controllers;

public class Bot {
    private final GameObjects gameObjects;
    private final boolean easyMode;
    private final boolean mediumMode;
    private final boolean hardMode;


    public Bot(GameObjects gameObjects, boolean easyMode, boolean mediumMode, boolean hardMode) {
        this.gameObjects = gameObjects;
        this.easyMode = easyMode;
        this.mediumMode = mediumMode;
        this.hardMode = hardMode;
    }
}
