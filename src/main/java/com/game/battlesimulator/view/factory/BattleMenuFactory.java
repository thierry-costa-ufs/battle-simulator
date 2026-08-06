package com.game.battlesimulator.view.factory;

import com.game.battlesimulator.model.payload.CombatantRecord;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.util.function.Consumer;

public class BattleMenuFactory {
    public static GridPane createTargetGrid(CombatantRecord[] enemies, Consumer<CombatantRecord> onTargetSelected) {
        GridPane targetGrid = new GridPane();
        targetGrid.setHgap(8);
        targetGrid.setVgap(8);
        targetGrid.setAlignment(Pos.CENTER);

        int aliveCount = 0;
        for (int i = 0; i < enemies.length; i++) {
            if (!enemies[i].isDead()) {
                aliveCount++;
            }
        }

        if (aliveCount == 0) return targetGrid;

        int maxColumns = (aliveCount <= 3) ? aliveCount : 3;
        int maxRows = (int) Math.ceil((double) aliveCount / maxColumns);

        int row=0;
        int col=0;

        for (int i = 0; i < maxColumns; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setHgrow(Priority.ALWAYS);
            colConst.setPercentWidth(100.0 / maxColumns);
            targetGrid.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i < maxRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setVgrow(Priority.ALWAYS);
            targetGrid.getRowConstraints().add(rowConst);
        }

        for (int i = 0; i < enemies.length; i++) {
            CombatantRecord enemy = enemies[i];

            if (!enemy.isDead()) {
                Button btnTarget = new Button(enemy.name());

                btnTarget.getStyleClass().add("target-button");

                btnTarget.setMaxWidth(Double.MAX_VALUE);
                btnTarget.setMaxHeight(Double.MAX_VALUE);

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