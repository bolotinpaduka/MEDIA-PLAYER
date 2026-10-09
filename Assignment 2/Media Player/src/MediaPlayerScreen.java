
import java.io.File;
import java.util.Locale;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MediaPlayerScreen {

    private final BorderPane root;
    private final Stage stage;

    private final PlaylistManager playlistManager;
    private final MediaController mediaController;

    private final ListView<String> playlistView;

    private final Label trackName;
    private final Label currentTime;
    private final Label totalTime;
    private final Label mediaStatus;

    private final Slider progress;
    private final Slider volumeSlider;

    private final StackPane mediaArea;

    private MediaView activeMediaView;

    private Button playPauseButton;
    private Button muteButton;
    private Button loopButton;
    private Button fullscreenButton;

    private boolean looping = false;
    private boolean seeking = false;


    private Scene keyboardScene;

    
    private final EventHandler<KeyEvent> keyboardHandler =
        this::handleKeyboardShortcut;

    private static final String PLAY_ICON =
        "play_arrow_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.png";

    private static final String PAUSE_ICON =
        "pause_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.png";

    private static final String STOP_ICON =
        "stop_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.png";

    private static final String PREVIOUS_ICON =
        "skip_previous_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.png";

    private static final String NEXT_ICON =
        "skip_next_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.png";

    private static final String VOLUME_ICON =
        "volume_up_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.png";

    private static final String MUTE_ICON =
        "volume_off_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.png";

    private static final String ADD_ICON =
        "playlist_add_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.png";

    private static final String REMOVE_ICON =
        "playlist_remove_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.png";

    private static final String LOOP_ICON =
        "repeat_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.png";

    private static final String LOOP_ACTIVE_ICON =
        "repeat_one_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.png";

    private static final String FULLSCREEN_ICON =
        "fullscreen_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.png";

    private static final String PROGRESS_COLOR = "#ff4f87";
    private static final String VOLUME_COLOR = "#36d9e8";
    private static final String TRACK_COLOR = "#343440";


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public MediaPlayerScreen(Stage stage) {

        this.stage = stage;

        playlistManager = new PlaylistManager();
        mediaController = new MediaController();

        root = new BorderPane();
        root.getStyleClass().add("root");
        root.setPadding(new Insets(16));
        root.setFocusTraversable(true);

        // HEADER

        Label title = new Label("NOVA PLAYER");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Your media, your space.");
        subtitle.getStyleClass().add("app-subtitle");

        VBox headerText = new VBox(4, title, subtitle);
        headerText.setAlignment(Pos.CENTER_LEFT);

        HBox header = new HBox(headerText);
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER_LEFT);

        // MEDIA DISPLAY

        mediaStatus =
            new Label("Select a song or video from your library");

        mediaStatus.getStyleClass().add("media-placeholder-text");

        Label mediaTitle = new Label("READY TO PLAY");
        mediaTitle.getStyleClass().add("media-placeholder-title");

        VBox placeholder = new VBox(10, mediaTitle, mediaStatus);
        placeholder.setAlignment(Pos.CENTER);

        mediaArea = new StackPane(placeholder);
        mediaArea.getStyleClass().add("media-area");

        Rectangle mediaClip = new Rectangle();
        mediaClip.widthProperty().bind(mediaArea.widthProperty());
        mediaClip.heightProperty().bind(mediaArea.heightProperty());
        mediaArea.setClip(mediaClip);

        mediaArea.widthProperty().addListener(
            (obs, oldValue, newValue) -> resizeVideo()
        );

        mediaArea.heightProperty().addListener(
            (obs, oldValue, newValue) -> resizeVideo()
        );

        // PLAYLIST

        Label playlistTitle = new Label("YOUR LIBRARY");
        playlistTitle.getStyleClass().add("section-title");

        playlistView = new ListView<>();
        playlistView.getStyleClass().add("playlist");
        playlistView.setPlaceholder(new Label("Your library is empty"));

        VBox.setVgrow(playlistView, Priority.ALWAYS);

        Button addButton = createIconButton(ADD_ICON, "Add media");
        addButton.getStyleClass().add("add-button");
        addButton.setOnAction(event -> addMedia());

        Button removeButton =
            createIconButton(REMOVE_ICON, "Remove selected media");

        removeButton.getStyleClass().add("remove-button");
        removeButton.setOnAction(event -> removeSelectedMedia());

        HBox playlistButtons = new HBox(10, addButton, removeButton);
        playlistButtons.setAlignment(Pos.CENTER_LEFT);

        VBox playlistPanel = new VBox(
            14,
            playlistTitle,
            playlistView,
            playlistButtons
        );

        playlistPanel.getStyleClass().add("playlist-panel");
        playlistPanel.setPrefWidth(260);
        playlistPanel.setMinWidth(210);
        playlistPanel.setMaxWidth(320);

        // MAIN CONTENT

        HBox mainArea = new HBox(16, mediaArea, playlistPanel);
        mainArea.getStyleClass().add("main-area");

        HBox.setHgrow(mediaArea, Priority.ALWAYS);
        mediaArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // NOW PLAYING

        Label nowPlaying = new Label("NOW PLAYING");
        nowPlaying.getStyleClass().add("now-playing-label");

        trackName = new Label("No media selected");
        trackName.getStyleClass().add("track-name");
        trackName.setMaxWidth(Double.MAX_VALUE);

        VBox trackInfo = new VBox(4, nowPlaying, trackName);

        // PROGRESS SLIDER

        currentTime = new Label("00:00");
        currentTime.getStyleClass().add("time-label");

        totalTime = new Label("00:00");
        totalTime.getStyleClass().add("time-label");

        progress = new Slider(0, 1, 0);
        progress.getStyleClass().add("progress-slider");
        progress.setMaxWidth(Double.MAX_VALUE);
        progress.setDisable(true);

        HBox progressRow = new HBox(
            10,
            currentTime,
            progress,
            totalTime
        );

        progressRow.setAlignment(Pos.CENTER);
        HBox.setHgrow(progress, Priority.ALWAYS);

        progress.valueProperty().addListener(
            (obs, oldValue, newValue) ->
                updateSliderFill(progress, PROGRESS_COLOR, TRACK_COLOR)
        );

        progress.skinProperty().addListener(
            (obs, oldSkin, newSkin) ->
                updateSliderFill(progress, PROGRESS_COLOR, TRACK_COLOR)
        );

        progress.sceneProperty().addListener(
            (obs, oldScene, newScene) ->
                updateSliderFill(progress, PROGRESS_COLOR, TRACK_COLOR)
        );

        progress.setOnMousePressed(event -> {
            if (mediaController.getMediaPlayer() != null) {
                seeking = true;
            }
        });

        progress.setOnMouseReleased(event -> {
            if (seeking) {
                seekMedia();
                seeking = false;
            }
        });

        progress.valueChangingProperty().addListener(
            (obs, wasChanging, isChanging) -> {
                if (wasChanging && !isChanging && seeking) {
                    seekMedia();
                    seeking = false;
                }
            }
        );

        // PREVIOUS

        Button previousButton =
            createIconButton(PREVIOUS_ICON, "Previous track (P)");

        previousButton.getStyleClass().add("previous-button");
        previousButton.setOnAction(event -> playPrevious());

        // PLAY / PAUSE

        playPauseButton = createIconButton(PLAY_ICON, "Play (Space)");
        playPauseButton.getStyleClass().add("play-button");
        playPauseButton.setOnAction(event -> togglePlayPause());

        // STOP

        Button stopButton = createIconButton(STOP_ICON, "Stop (S)");
        stopButton.getStyleClass().add("stop-button");

        stopButton.setOnAction(event -> stopPlayback());

        // NEXT

        Button nextButton =
            createIconButton(NEXT_ICON, "Next track (N)");

        nextButton.getStyleClass().add("next-button");
        nextButton.setOnAction(event -> playNext());

        HBox playbackControls = new HBox(
            10,
            previousButton,
            playPauseButton,
            stopButton,
            nextButton
        );

        playbackControls.setAlignment(Pos.CENTER_LEFT);

        // REPEAT BUTTON

        loopButton = createIconButton(LOOP_ICON, "Repeat: Off (L)");
        loopButton.getStyleClass().add("loop-button");
        loopButton.setOnAction(event -> toggleLoop());

        // VOLUME SLIDER

        volumeSlider = new Slider(0, 100, 50);

        volumeSlider.getStyleClass().add("volume-slider");
        volumeSlider.setPrefWidth(105);
        volumeSlider.setMinWidth(65);
        volumeSlider.setMaxWidth(130);
        volumeSlider.setTooltip(new Tooltip("Adjust volume (Up/Down)"));

        mediaController.setVolume(0.5);

        volumeSlider.valueProperty().addListener(
            (obs, oldValue, newValue) -> {
                mediaController.setVolume(newValue.doubleValue() / 100.0);
                updateSliderFill(volumeSlider, VOLUME_COLOR, TRACK_COLOR);
            }
        );

        volumeSlider.skinProperty().addListener(
            (obs, oldSkin, newSkin) ->
                updateSliderFill(volumeSlider, VOLUME_COLOR, TRACK_COLOR)
        );

        volumeSlider.sceneProperty().addListener(
            (obs, oldScene, newScene) ->
                updateSliderFill(volumeSlider, VOLUME_COLOR, TRACK_COLOR)
        );

        // MUTE BUTTON

        muteButton = createIconButton(VOLUME_ICON, "Mute (M)");
        muteButton.getStyleClass().add("mute-button");
        muteButton.setOnAction(event -> toggleMute());

        HBox volumeControls = new HBox(10, muteButton, volumeSlider);
        volumeControls.setAlignment(Pos.CENTER_RIGHT);

        // FULLSCREEN

        fullscreenButton =
            createIconButton(FULLSCREEN_ICON, "Enter fullscreen (F)");

        fullscreenButton.getStyleClass().add("fullscreen-button");
        fullscreenButton.setOnAction(event -> toggleFullscreen());

        // RIGHT CONTROLS

        HBox rightControls = new HBox(
            12,
            loopButton,
            volumeControls,
            fullscreenButton
        );

        rightControls.setAlignment(Pos.CENTER_RIGHT);

        // BOTTOM TOOLBAR

        HBox bottomToolbar = new HBox(playbackControls, rightControls);
        bottomToolbar.setAlignment(Pos.CENTER_LEFT);
        bottomToolbar.setSpacing(12);

        HBox.setHgrow(rightControls, Priority.ALWAYS);

        // CONTROLS PANEL

        VBox controls = new VBox(14, trackInfo, progressRow, bottomToolbar);
        controls.getStyleClass().add("controls");

        // ROOT LAYOUT

        root.setTop(header);
        root.setCenter(mainArea);
        root.setBottom(controls);

        BorderPane.setMargin(header, new Insets(0, 0, 14, 0));
        BorderPane.setMargin(mainArea, new Insets(0, 0, 14, 0));

        // PLAYLIST SELECTION

        playlistView.getSelectionModel()
            .selectedIndexProperty()
            .addListener((obs, oldValue, newValue) -> {
                int index = newValue.intValue();

                if (index >= 0) {
                    selectMedia(index);
                }
            });

      
        setupKeyboardControls();

        // FULLSCREEN STATE

        stage.fullScreenProperty().addListener(
            (obs, wasFullscreen, isFullscreen) -> updateFullscreenIcon()
        );

        Platform.runLater(() -> {
            updateSliderFill(progress, PROGRESS_COLOR, TRACK_COLOR);
            updateSliderFill(volumeSlider, VOLUME_COLOR, TRACK_COLOR);
        });
    }


    
    //      TRACK FILL
    

    private void updateSliderFill(
        Slider slider,
        String fillColor,
        String emptyColor
    ) {
        if (slider == null) {
            return;
        }

        Node track = slider.lookup(".track");

        if (track == null) {
            return;
        }

        double min = slider.getMin();
        double max = slider.getMax();
        double value = slider.getValue();

        double percentage = 0;

        if (max > min) {
            percentage = ((value - min) / (max - min)) * 100.0;
        }

        percentage = Math.max(0, Math.min(100, percentage));

        String fillStyle = String.format(
            Locale.ROOT,
            "-fx-background-color: linear-gradient(to right, "
                + "%s 0%%, %s %.2f%%, "
                + "%s %.2f%%, %s 100%%);",
            fillColor,
            fillColor,
            percentage,
            emptyColor,
            percentage,
            emptyColor
        );

        track.setStyle(fillStyle);
    }


    // =====================================================
    // CREATE ICON
    // =====================================================

    private ImageView createIcon(String fileName, double size) {

        var resource = getClass().getResource("icons/" + fileName);

        if (resource == null) {
            System.out.println("Icon not found: icons/" + fileName);
            return new ImageView();
        }

        Image image = new Image(resource.toExternalForm());
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        return imageView;
    }


    // =====================================================
    // CREATE ICON BUTTON
    // =====================================================

    private Button createIconButton(String iconFile, String tooltipText) {

        Button button = new Button();

        button.setGraphic(createIcon(iconFile, 18));
        button.setTooltip(new Tooltip(tooltipText));
        button.setFocusTraversable(false);
        button.getStyleClass().add("icon-button");

        return button;
    }


    // =====================================================
    // PLAY / PAUSE ICON
    // =====================================================

    private void updatePlayPauseIcon() {

        if (playPauseButton == null) {
            return;
        }

        if (mediaController.isPlaying()) {
            playPauseButton.setGraphic(createIcon(PAUSE_ICON, 20));
            playPauseButton.setTooltip(new Tooltip("Pause (Space)"));
        } else {
            playPauseButton.setGraphic(createIcon(PLAY_ICON, 20));
            playPauseButton.setTooltip(new Tooltip("Play (Space)"));
        }
    }


    // =====================================================
    // PLAY / PAUSE
    // =====================================================

    private void togglePlayPause() {

        if (mediaController.getMediaPlayer() == null) {
            return;
        }

        if (mediaController.isPlaying()) {
            mediaController.pause();
        } else {
            mediaController.play();
        }

        updatePlayPauseIcon();
    }


    // =====================================================
    // STOP
    // =====================================================

    private void stopPlayback() {

        mediaController.stop();
        progress.setValue(0);
        currentTime.setText("00:00");
        updatePlayPauseIcon();
    }


    // =====================================================
    // MUTE
    // =====================================================

    private void toggleMute() {
        mediaController.toggleMute();
        updateMuteIcon();
    }


    private void updateMuteIcon() {

        if (muteButton == null) {
            return;
        }

        if (mediaController.isMuted()) {
            muteButton.setGraphic(createIcon(MUTE_ICON, 18));
            muteButton.setTooltip(new Tooltip("Unmute (M)"));
        } else {
            muteButton.setGraphic(createIcon(VOLUME_ICON, 18));
            muteButton.setTooltip(new Tooltip("Mute (M)"));
        }
    }


    // =====================================================
    // REPEAT
    // =====================================================

    private void toggleLoop() {

        looping = !looping;

        MediaPlayer player = mediaController.getMediaPlayer();

        if (player != null) {
            player.setCycleCount(
                looping ? MediaPlayer.INDEFINITE : 1
            );
        }

        loopButton.getStyleClass().remove("active");

        if (looping) {
            loopButton.getStyleClass().add("active");
            loopButton.setGraphic(createIcon(LOOP_ACTIVE_ICON, 18));
            loopButton.setTooltip(new Tooltip("Repeat: On (L)"));

        } else {
            loopButton.setGraphic(createIcon(LOOP_ICON, 18));
            loopButton.setTooltip(new Tooltip("Repeat: Off (L)"));
        }
    }


    // =====================================================
    // FULLSCREEN
    // =====================================================

    private void toggleFullscreen() {
        stage.setFullScreen(!stage.isFullScreen());
        updateFullscreenIcon();
    }


    private void updateFullscreenIcon() {

        if (fullscreenButton == null) {
            return;
        }

        fullscreenButton.setTooltip(
            new Tooltip(
                stage.isFullScreen()
                    ? "Exit fullscreen (F)"
                    : "Enter fullscreen (F)"
            )
        );
    }


    // =====================================================
    // KEYBOARD CONTROLS
    // =====================================================

    private void setupKeyboardControls() {

        root.setFocusTraversable(true);

        root.sceneProperty().addListener(
            (observable, oldScene, newScene) -> {

                
                if (keyboardScene != null) {
                    keyboardScene.removeEventFilter(
                        KeyEvent.KEY_PRESSED,
                        keyboardHandler
                    );
                }

                keyboardScene = newScene;

                
                if (keyboardScene != null) {
                    keyboardScene.addEventFilter(
                        KeyEvent.KEY_PRESSED,
                        keyboardHandler
                    );
                }
            }
        );

        
        if (root.getScene() != null) {
            keyboardScene = root.getScene();

            keyboardScene.addEventFilter(
                KeyEvent.KEY_PRESSED,
                keyboardHandler
            );
        }
    }


    private void handleKeyboardShortcut(KeyEvent event) {

        KeyCode key = event.getCode();

        switch (key) {

            case SPACE:
                togglePlayPause();
                event.consume();
                break;

            case S:
                stopPlayback();
                event.consume();
                break;

            case N:
                playNext();
                event.consume();
                break;

            case P:
                playPrevious();
                event.consume();
                break;

            case UP:
                adjustVolume(5);
                event.consume();
                break;

            case DOWN:
                adjustVolume(-5);
                event.consume();
                break;

            case M:
                toggleMute();
                event.consume();
                break;

            case F:
                toggleFullscreen();
                event.consume();
                break;

            case L:
                toggleLoop();
                event.consume();
                break;

            default:
                break;
        }
    }


    // =====================================================
    // KEYBOARD VOLUME CONTROL
    // =====================================================

    private void adjustVolume(double amount) {

        double newValue = volumeSlider.getValue() + amount;

        newValue = Math.max(
            volumeSlider.getMin(),
            Math.min(volumeSlider.getMax(), newValue)
        );

        
        volumeSlider.setValue(newValue);

        updateSliderFill(volumeSlider, VOLUME_COLOR, TRACK_COLOR);
    }


    // =====================================================
    // ADD MEDIA
    // =====================================================

    private void addMedia() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Add media to your library");

        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(
                "Supported Media Files",
                "*.mp4", "*.m4v", "*.avi", "*.mov",
                "*.mp3", "*.wav", "*.m4a", "*.aac", "*.aiff"
            )
        );

        File file = chooser.showOpenDialog(stage);

        if (file == null) {
            return;
        }

        playlistManager.addMedia(file);
        refreshPlaylist();

        int index = playlistManager.size() - 1;

        playlistView.getSelectionModel().select(index);
        playlistView.scrollTo(index);
    }


    // =====================================================
    // REFRESH PLAYLIST
    // =====================================================

    private void refreshPlaylist() {

        ObservableList<String> names = FXCollections.observableArrayList();

        for (File file : playlistManager.getPlaylist()) {
            names.add(file.getName());
        }

        playlistView.setItems(names);
    }


    // =====================================================
    // SELECT MEDIA
    // =====================================================

    private void selectMedia(int index) {

        File file = playlistManager.getMedia(index);

        if (file == null) {
            return;
        }

        if (index == playlistManager.getCurrentIndex()
                && mediaController.getMediaPlayer() != null) {
            return;
        }

        playlistManager.setCurrentIndex(index);
        loadMedia(file);
    }


    // =====================================================
    // LOAD MEDIA
    // =====================================================

    private void loadMedia(File file) {

        if (file == null) {
            return;
        }

        mediaController.loadMedia(file);

        MediaPlayer player = mediaController.getMediaPlayer();

        if (player == null) {
            mediaStatus.setText("Unable to load this media file");
            return;
        }

        trackName.setText(file.getName());
        mediaStatus.setText(file.getName());

        currentTime.setText("00:00");
        totalTime.setText("00:00");

        progress.setMin(0);
        progress.setMax(1);
        progress.setValue(0);
        progress.setDisable(true);

        updateSliderFill(progress, PROGRESS_COLOR, TRACK_COLOR);

        player.setCycleCount(looping ? MediaPlayer.INDEFINITE : 1);

        updatePlayPauseIcon();
        updateMuteIcon();

        player.setOnReady(() -> {

            Duration duration = player.getTotalDuration();
            totalTime.setText(formatTime(duration));

            double durationSeconds = duration.toSeconds();

            if (!Double.isFinite(durationSeconds) || durationSeconds <= 0) {
                durationSeconds = 1;
            }

            progress.setMin(0);
            progress.setMax(durationSeconds);
            progress.setValue(0);
            progress.setDisable(false);

            updateSliderFill(progress, PROGRESS_COLOR, TRACK_COLOR);
            resizeVideo();
        });

        player.currentTimeProperty().addListener(
            (obs, oldValue, newValue) -> {

                if (!progress.isValueChanging() && !seeking) {
                    progress.setValue(newValue.toSeconds());
                    updateSliderFill(progress, PROGRESS_COLOR, TRACK_COLOR);
                }

                currentTime.setText(formatTime(newValue));
            }
        );

        player.statusProperty().addListener(
            (obs, oldValue, newValue) -> updatePlayPauseIcon()
        );

        player.setOnEndOfMedia(() -> {

            updatePlayPauseIcon();

            if (!looping) {
                playNext();
            }
        });

        player.setOnError(() -> {
            mediaStatus.setText("Unable to play this media file");
            updatePlayPauseIcon();
        });

        if (isAudioFile(file)) {
            showAudioPlayer(file);
        } else {
            showVideoPlayer(player);
        }
    }


    // =====================================================
    // VIDEO DISPLAY
    // =====================================================

    private void showVideoPlayer(MediaPlayer player) {

        MediaView mediaView = new MediaView(player);

        mediaView.setPreserveRatio(true);
        mediaView.setSmooth(true);
        mediaView.setManaged(false);

        activeMediaView = mediaView;

        mediaArea.getChildren().setAll(mediaView);

        resizeVideo();
    }


    // =====================================================
    // RESIZE VIDEO
    // =====================================================

    private void resizeVideo() {

        if (activeMediaView == null) {
            return;
        }

        double areaWidth = mediaArea.getWidth();
        double areaHeight = mediaArea.getHeight();

        if (areaWidth <= 0 || areaHeight <= 0) {
            return;
        }

        double availableWidth = Math.max(0, areaWidth - 24);
        double availableHeight = Math.max(0, areaHeight - 24);

        activeMediaView.setFitWidth(availableWidth);
        activeMediaView.setFitHeight(availableHeight);

        Bounds bounds = activeMediaView.getLayoutBounds();

        double videoWidth = bounds.getWidth();
        double videoHeight = bounds.getHeight();

        activeMediaView.setLayoutX((areaWidth - videoWidth) / 2.0);
        activeMediaView.setLayoutY((areaHeight - videoHeight) / 2.0);
    }


    // =====================================================
    // AUDIO DISPLAY
    // =====================================================

    private void showAudioPlayer(File file) {

        activeMediaView = null;

        Label audioTitle = new Label("AUDIO");
        audioTitle.getStyleClass().add("audio-title");

        Label audioFileName = new Label(file.getName());
        audioFileName.getStyleClass().add("audio-file-name");
        audioFileName.setWrapText(true);
        audioFileName.setMaxWidth(400);

        Label audioStatus = new Label("READY TO PLAY");
        audioStatus.getStyleClass().add("audio-status");

        VBox audioDisplay = new VBox(
            12,
            audioTitle,
            audioFileName,
            audioStatus
        );

        audioDisplay.setAlignment(Pos.CENTER);
        audioDisplay.getStyleClass().add("audio-display");

        mediaArea.getChildren().setAll(audioDisplay);
    }


    // =====================================================
    // AUDIO FILE CHECK
    // =====================================================

    private boolean isAudioFile(File file) {

        String name = file.getName().toLowerCase(Locale.ROOT);

        return name.endsWith(".mp3")
            || name.endsWith(".wav")
            || name.endsWith(".m4a")
            || name.endsWith(".aac")
            || name.endsWith(".aiff");
    }


    // =====================================================
    // SEEK
    // =====================================================

    private void seekMedia() {

        MediaPlayer player = mediaController.getMediaPlayer();

        if (player == null) {
            return;
        }

        mediaController.seek(Duration.seconds(progress.getValue()));
    }


    // =====================================================
    // REMOVE SELECTED MEDIA
    // =====================================================

    private void removeSelectedMedia() {

        int selectedIndex =
            playlistView.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0) {
            return;
        }

        int currentIndex = playlistManager.getCurrentIndex();
        boolean removingCurrent = selectedIndex == currentIndex;

        mediaController.stop();
        playlistManager.removeMedia(selectedIndex);

        refreshPlaylist();

        if (playlistManager.isEmpty()) {

            mediaController.disposePlayer();
            activeMediaView = null;

            trackName.setText("No media selected");
            mediaStatus.setText("Select a song or video from your library");

            Label title = new Label("READY TO PLAY");
            title.getStyleClass().add("media-placeholder-title");

            Label message =
                new Label("Select a song or video from your library");

            message.getStyleClass().add("media-placeholder-text");

            VBox placeholder = new VBox(10, title, message);
            placeholder.setAlignment(Pos.CENTER);

            mediaArea.getChildren().setAll(placeholder);

            progress.setValue(0);
            progress.setDisable(true);

            currentTime.setText("00:00");
            totalTime.setText("00:00");

            updateSliderFill(progress, PROGRESS_COLOR, TRACK_COLOR);
            updatePlayPauseIcon();

            return;
        }

        if (removingCurrent) {

            int newIndex = Math.min(selectedIndex, playlistManager.size() - 1);

            playlistManager.setCurrentIndex(-1);
            playlistView.getSelectionModel().clearSelection();

            File replacement = playlistManager.getMedia(newIndex);

            if (replacement != null) {
                playlistManager.setCurrentIndex(newIndex);
                loadMedia(replacement);

                playlistView.getSelectionModel().select(newIndex);
                playlistView.scrollTo(newIndex);
            }

        } else {

            int updatedIndex = playlistManager.getCurrentIndex();

            if (updatedIndex >= 0 && updatedIndex < playlistManager.size()) {
                playlistView.getSelectionModel().select(updatedIndex);
            }
        }
    }


    // =====================================================
    // NEXT TRACK
    // =====================================================

    private void playNext() {

        File next = playlistManager.getNext();

        if (next == null) {
            updatePlayPauseIcon();
            return;
        }

        int index = playlistManager.getCurrentIndex();

        playlistView.getSelectionModel().select(index);
        playlistView.scrollTo(index);

        loadMedia(next);
        mediaController.play();

        updatePlayPauseIcon();
    }


    // =====================================================
    // PREVIOUS TRACK
    // =====================================================

    private void playPrevious() {

        File previous = playlistManager.getPrevious();

        if (previous == null) {
            updatePlayPauseIcon();
            return;
        }

        int index = playlistManager.getCurrentIndex();

        playlistView.getSelectionModel().select(index);
        playlistView.scrollTo(index);

        loadMedia(previous);
        mediaController.play();

        updatePlayPauseIcon();
    }


    // =====================================================
    // FORMAT TIME
    // =====================================================

    private String formatTime(Duration duration) {

        if (duration == null
                || duration.isUnknown()
                || duration.lessThan(Duration.ZERO)) {
            return "00:00";
        }

        double secondsValue = duration.toSeconds();

        if (!Double.isFinite(secondsValue)) {
            return "00:00";
        }

        int totalSeconds = (int) secondsValue;

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        return String.format("%02d:%02d", minutes, seconds);
    }


    // =====================================================
    // GET VIEW
    // =====================================================

    public BorderPane getView() {
        return root;
    }
}