package com.cervinschi.marin.javafx.briscola.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.Objects;

public class Sound {
    public static void play(String name) {
        if (name.isEmpty()) return;
        URL url = Sound.class.getResource("/com/cervinschi/marin/javafx/briscola/sounds/" + name + ".mp3");
        Media musicCard = new Media(Objects.requireNonNull(url).toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(musicCard);

        mediaPlayer.play();
    }
}
