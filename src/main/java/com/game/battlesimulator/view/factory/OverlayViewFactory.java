package com.game.battlesimulator.view.factory;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class OverlayViewFactory {

    public enum OverlayAction { CONTINUE, RESTART, MENU }

    private static final String VICTORY_TITLE = "VITÓRIA!";
    private static final String DEFEAT_TITLE = "DERROTA...";

    private OverlayViewFactory() {}

    public static VBox createOverlay(boolean victory, int round, int healthGrowth, int attackGrowth, Consumer<OverlayAction> onAction) {
        Label title = new Label(victory ? VICTORY_TITLE : DEFEAT_TITLE);
        title.getStyleClass().add("overlay-title");

        VBox content = new VBox(8);
        content.setAlignment(Pos.CENTER);

        if (victory) {
            Label roundCleared = new Label("Round " + round + " vencido!");
            Label levelUp = new Label("Heróis: +" + healthGrowth + " HP, +" + attackGrowth + " dano");
            Label horde = new Label("Nova horda se aproxima...");
            for (Label l : new Label[]{roundCleared, levelUp, horde}) {
                l.getStyleClass().add("overlay-subtitle");
                content.getChildren().add(l);
            }
        } else {
            Label fallen = new Label("O herói tombou em combate.");
            fallen.getStyleClass().add("overlay-subtitle");
            content.getChildren().add(fallen);
        }

        Button actionButton = new Button(victory ? "Continuar" : "Jogar Novamente");
        actionButton.getStyleClass().add(victory ? "action-button" : "action-button-defeat");
        actionButton.setOnAction(e -> onAction.accept(victory ? OverlayAction.CONTINUE : OverlayAction.RESTART));

        VBox overlay = new VBox(16, title, content, actionButton);
        overlay.setAlignment(Pos.CENTER);
        overlay.setFillWidth(false);
        overlay.getStyleClass().add("battle-overlay");
        if (!victory) {
            overlay.getStyleClass().add("battle-overlay-full");
        }

        if (!victory) {
            Button menuButton = new Button("Voltar ao Menu");
            menuButton.getStyleClass().add("overlay-menu-button");
            menuButton.setOnAction(e -> onAction.accept(OverlayAction.MENU));
            overlay.getChildren().add(menuButton);
        }

        StackPane.setAlignment(overlay, Pos.CENTER);
        return overlay;
    }
}
