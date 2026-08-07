package com.game.battlesimulator.view.factory;

import com.game.battlesimulator.model.payload.CombatantRecord;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BattleMenuFactory {
    public static GridPane createTargetGrid(CombatantRecord[] enemies, Consumer<CombatantRecord> onTargetSelected) {
        GridPane targetGrid = new GridPane();
        targetGrid.setHgap(10);
        targetGrid.setVgap(10);
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

        int row = 0;
        int col = 0;

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
                int number = extractNumber(enemy.id());
                SpriteView sprite = new SpriteView(false, number);

                Label name = new Label(enemy.name());
                name.getStyleClass().add("target-card-name");

                VBox cardContent = new VBox(6, sprite, name);
                cardContent.setAlignment(Pos.CENTER);

                Button btnTarget = new Button();
                btnTarget.setGraphic(cardContent);
                btnTarget.getStyleClass().add("target-button");
                btnTarget.getStyleClass().add("target-card");

                btnTarget.setMaxWidth(Double.MAX_VALUE);
                btnTarget.setMaxHeight(Double.MAX_VALUE);

                btnTarget.setOnAction(e -> onTargetSelected.accept(enemy));

                targetGrid.add(btnTarget, col, row);
                col++;

                if (col >= maxColumns) {
                    col = 0;
                    row++;
                }
            }
        }
        return targetGrid;
    }

    private static int extractNumber(String id) {
        if (id == null) return 1;
        Matcher matcher = Pattern.compile("(\\d+)$").matcher(id);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 1;
    }
}
