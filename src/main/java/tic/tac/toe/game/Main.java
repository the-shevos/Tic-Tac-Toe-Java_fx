package tic.tac.toe.game;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        new TicTacToeController().start(stage);

        stage.getIcons().clear();

        stage.getIcons().add(new Image(getClass().getResource("/images/icon.png").toExternalForm()));
    }

    public static void main(String[] args) {
        launch();
    }
}
