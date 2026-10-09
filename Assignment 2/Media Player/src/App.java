import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        MediaPlayerScreen screen =
                new MediaPlayerScreen(stage);

        Scene scene = new Scene(
                screen.getView(),
                1000,
                650
        );

        scene.getStylesheets().add(
                getClass()
                        .getResource("style.css")
                        .toExternalForm()
        );

        stage.setTitle("Java Media Player");
        stage.setScene(scene);

        stage.setMinWidth(850);
        stage.setMinHeight(550);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}