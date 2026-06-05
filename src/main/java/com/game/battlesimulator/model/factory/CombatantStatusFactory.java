package com.game.battlesimulator.model.factory;

import com.game.battlesimulator.model.domain.Combatant;
import com.game.battlesimulator.model.domain.Player;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CombatantStatusFactory {
    public static VBox createStatusNode(Combatant combatant, Pos alignment, boolean textFirst) {
        VBox box = new VBox();

        boolean isPlayer = combatant instanceof Player;

        box.getStyleClass().add(isPlayer ? "player-hp-row" : "enemy-hp-row");
        box.setSpacing(6);
        box.setAlignment(alignment);

        Label lblName = new Label(combatant.getName());
        lblName.getStyleClass().add("label");

        double lifePercentage = (double) combatant.getCurrentHealth() / combatant.getMaxHealth();
        ProgressBar lifeBar = new ProgressBar(lifePercentage);
        lifeBar.getStyleClass().add("progress-bar");

        String hpText = "[" + combatant.getCurrentHealth() + "/" + combatant.getMaxHealth() + "]";

        Label lblHpText = new Label(hpText);
        lblHpText.getStyleClass().add("hp-text");

        HBox hpBarRow = new HBox();
        hpBarRow.setSpacing(6);
        hpBarRow.setAlignment(alignment == Pos.BOTTOM_RIGHT ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        if (textFirst) {
            hpBarRow.getChildren().addAll(lblHpText, lifeBar);
        }
        else {
            hpBarRow.getChildren().addAll(lifeBar, lblHpText);
        }

        if (!combatant.isAlive()) {
            box.setOpacity(0.4);
            lblName.setText("[DERROTADO] " + combatant.getName());
        }

        box.getChildren().addAll(lblName, hpBarRow);
        return box;
    }
}
