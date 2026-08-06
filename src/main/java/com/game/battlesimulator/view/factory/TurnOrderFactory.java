package com.game.battlesimulator.view.factory;

import com.game.battlesimulator.model.payload.CombatantRecord;
import javafx.scene.control.Label;

public class TurnOrderFactory {
    public static Label[] createCarouselNodes(CombatantRecord[] turnOrder) {

        Label[] nodes = new Label[turnOrder.length + 1];

        Label title = new Label("TURNOS");
        title.getStyleClass().add("turn-title-label");

        nodes[0] = title;

        for (int i = 0; i < turnOrder.length; i++) {
            CombatantRecord combatant = turnOrder[i];
            String combatantName = combatant.name();

            Label carouselItem = new Label(combatantName);
            carouselItem.getStyleClass().add("carousel-item");

            if (i == 0) {
                carouselItem.getStyleClass().add("carousel-item-active");
                carouselItem.setText("-> " + combatantName);
            } else {
                carouselItem.getStyleClass().add("carousel-item-queue");
            }

            nodes[i + 1] = carouselItem;
        }

        return nodes;
    }
}