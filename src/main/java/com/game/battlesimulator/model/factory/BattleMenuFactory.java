package com.game.battlesimulator.model.factory;

import com.game.battlesimulator.model.domain.Combatant;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.function.Consumer;

public class BattleMenuFactory {
    public static GridPane createTargetGrid(List<Combatant> enemies, Consumer<Combatant> onTargetSelected) {
        GridPane targetGrid = new GridPane();
        targetGrid.setHgap(10);
        targetGrid.setVgap(10);
        targetGrid.setAlignment(Pos.BOTTOM_RIGHT);

        int maxColumns=2;
        int row=0;
        int col=0;

        for (Combatant enemy : enemies) {
            if (enemy.isAlive()) {
                Button btnTarget = new Button(enemy.getName());
                btnTarget.setOnAction(e -> onTargetSelected.accept(enemy));

                targetGrid.add(btnTarget, col, row);
                col++;

                if (col >= maxColumns) {
                    col=0;
                    row++;
                }
            }
        }
        return targetGrid;
    }
}
