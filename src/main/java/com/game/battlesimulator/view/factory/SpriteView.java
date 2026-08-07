package com.game.battlesimulator.view.factory;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class SpriteView extends StackPane {

    private final Region sprite;
    private final Label damageLabel;

    public SpriteView(boolean isPlayer, int number) {
        sprite = new Region();
        sprite.getStyleClass().add(isPlayer ? "player-sprite" : "enemy-sprite");
        sprite.getStyleClass().add((isPlayer ? "player-sprite-" : "enemy-sprite-") + number);
        damageLabel = new Label();
        damageLabel.getStyleClass().add("damage-float");
        damageLabel.setVisible(false);
        damageLabel.setManaged(false);
        getChildren().addAll(sprite, damageLabel);
    }

    public static int safeIndex(int number, boolean isPlayer) {
        int max = isPlayer ? 4 : 5;
        if (number < 1) return 1;
        return ((number - 1) % max) + 1;
    }

    public void showHit(int damage) {
        if (damage <= 0) return;

        damageLabel.setText("-" + damage);
        damageLabel.setVisible(true);
        damageLabel.setOpacity(1);
        damageLabel.setTranslateY(0);

        TranslateTransition rise = new TranslateTransition(Duration.millis(700), damageLabel);
        rise.setByY(-28);
        FadeTransition fade = new FadeTransition(Duration.millis(700), damageLabel);
        fade.setToValue(0);
        ParallelTransition floatUp = new ParallelTransition(rise, fade);
        floatUp.setOnFinished(e -> damageLabel.setVisible(false));
        floatUp.play();

        TranslateTransition shake = new TranslateTransition(Duration.millis(120), sprite);
        shake.setFromX(-6);
        shake.setToX(6);
        shake.setCycleCount(3);
        shake.setAutoReverse(true);
        shake.playFromStart();
    }

    public void markDead() {
        sprite.getStyleClass().add("sprite-dead");
    }
}
