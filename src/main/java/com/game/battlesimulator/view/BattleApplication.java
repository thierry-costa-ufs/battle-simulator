package com.game.battlesimulator.view;

import javafx.application.Application;
import javafx.stage.Stage;

public class BattleApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Battle Simulator");
        SceneManager.init(primaryStage);
        SceneManager.switchScene("/fxml/menu-view.fxml");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}