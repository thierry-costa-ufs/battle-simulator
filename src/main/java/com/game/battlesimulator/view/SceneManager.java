package com.game.battlesimulator.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class SceneManager {
    private static Stage stage;
    private static int partySize = 1;
    private static String theme = "theme-yellow";

    private SceneManager() {}

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void setPartySize(int size) {
        partySize = size;
    }

    public static int getPartySize() {
        return partySize;
    }

    public static void setTheme(String themeClass) {
        theme = themeClass;
    }

    public static String getTheme() {
        return theme;
    }

    public static void applyTheme(Parent root) {
        root.getStyleClass().removeIf(c -> c.startsWith("theme-"));
        root.getStyleClass().add(theme);
    }

    public static void refreshTheme() {
        if (stage != null && stage.getScene() != null && stage.getScene().getRoot() != null) {
            applyTheme(stage.getScene().getRoot());
        }
    }

    public static void switchScene(String fxml) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxml));
            applyTheme(root);
            if (stage.getScene() == null) {
                stage.setScene(new Scene(root, 800, 600));
            } else {
                stage.getScene().setRoot(root);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao carregar tela: " + fxml, e);
        }
    }
}
