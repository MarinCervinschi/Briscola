package com.cervinschi.marin.javafx.briscola.utils;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Sound {
    /* players must stay referenced while playing: if one is garbage collected mid-playback the native player crashes */
    private static final Set<MediaPlayer> playing = ConcurrentHashMap.newKeySet();

    public static void play(String name) {
        if (name.isEmpty()) return;
        URL url = Sound.class.getResource("/com/cervinschi/marin/javafx/briscola/sounds/" + name + ".mp3");
        Media musicCard = new Media(Objects.requireNonNull(url).toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(musicCard);

        playing.add(mediaPlayer);
        /* dispose outside the media callback, once the native player is done with it */
        Runnable release = () -> Platform.runLater(() -> {
            if (playing.remove(mediaPlayer)) mediaPlayer.dispose();
        });
        mediaPlayer.setOnEndOfMedia(release);
        mediaPlayer.setOnError(release);
        mediaPlayer.setOnHalted(release);
        mediaPlayer.play();
    }
}
