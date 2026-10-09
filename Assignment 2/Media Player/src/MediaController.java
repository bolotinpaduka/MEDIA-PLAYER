import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MediaController {

    private MediaPlayer mediaPlayer;

    private double volume = 0.5;

    // =========================================================
    // LOAD MEDIA
    // =========================================================

    public void loadMedia(File file) {

        if (file == null) {
            return;
        }

        // Dispose the previous player
        disposePlayer();

        try {

            Media media =
                    new Media(
                            file.toURI().toString()
                    );

            mediaPlayer =
                    new MediaPlayer(media);

            mediaPlayer.setVolume(volume);

        } catch (Exception exception) {

            mediaPlayer = null;

            System.out.println(
                    "Unable to load media: "
                            + exception.getMessage()
            );
        }
    }

    // =========================================================
    // PLAY
    // =========================================================

    public void play() {

        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    // =========================================================
    // PAUSE
    // =========================================================

    public void pause() {

        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    // =========================================================
    // STOP
    // =========================================================

    public void stop() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    // =========================================================
    // PLAY / PAUSE STATE
    // =========================================================

    public boolean isPlaying() {

        if (mediaPlayer == null) {
            return false;
        }

        return mediaPlayer.getStatus()
                == MediaPlayer.Status.PLAYING;
    }

    // =========================================================
    // SEEK
    // =========================================================

    public void seek(Duration duration) {

        if (mediaPlayer != null &&
                duration != null) {

            mediaPlayer.seek(duration);
        }
    }

    // =========================================================
    // SET VOLUME
    // =========================================================

    public void setVolume(double volume) {

        this.volume =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                volume
                        )
                );

        if (mediaPlayer != null) {

            mediaPlayer.setVolume(
                    this.volume
            );
        }
    }

    // =========================================================
    // INCREASE VOLUME
    // =========================================================

    public void increaseVolume() {

        setVolume(
                volume + 0.1
        );
    }

    // =========================================================
    // DECREASE VOLUME
    // =========================================================

    public void decreaseVolume() {

        setVolume(
                volume - 0.1
        );
    }

    // =========================================================
    // MUTE / UNMUTE
    // =========================================================

    public void toggleMute() {

        if (mediaPlayer != null) {

            mediaPlayer.setMute(
                    !mediaPlayer.isMute()
            );
        }
    }

    // =========================================================
    // CHECK MUTE STATUS
    // =========================================================

    public boolean isMuted() {

        if (mediaPlayer == null) {
            return false;
        }

        return mediaPlayer.isMute();
    }

    // =========================================================
    // GET MEDIA PLAYER
    // =========================================================

    public MediaPlayer getMediaPlayer() {

        return mediaPlayer;
    }

    // =========================================================
    // GET VOLUME
    // =========================================================

    public double getVolume() {

        return volume;
    }

    // =========================================================
    // DISPOSE PLAYER
    // =========================================================

    public void disposePlayer() {

        if (mediaPlayer != null) {

            mediaPlayer.stop();

            mediaPlayer.dispose();

            mediaPlayer = null;
        }
    }
}