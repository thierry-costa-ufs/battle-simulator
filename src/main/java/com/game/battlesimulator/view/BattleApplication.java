package com.game.battlesimulator.view;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;
import static javafx.fxml.FXMLLoader.load;

public class BattleApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = load(Objects.requireNonNull(getClass().getResource("/fxml/battle-view.fxml")));

            Scene scene = new Scene(root, 800, 600);

            primaryStage.setTitle("Battle Simulator");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    static void main(String[] args) {
        launch(args);
    }
}