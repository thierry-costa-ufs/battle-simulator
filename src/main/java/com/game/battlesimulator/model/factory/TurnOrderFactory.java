package com.game.battlesimulator.model.factory;

import com.game.battlesimulator.model.payload.CombatantRecord;
import javafx.scene.control.Label;
import java.util.ArrayList;
import java.util.List;

public class TurnOrderFactory {
    public static List<Label> createCarouselNodes(List<CombatantRecord> turnOrder) {
        List<Label> nodes = new ArrayList<>();

        Label title = new Label("TURNOS");
        title.getStyleClass().add("turn-title-label");
        nodes.add(title);

        for (int i=0; i < turnOrder.size(); i++) {
            CombatantRecord combatant = turnOrder.get(i);
            String combatantName = combatant.name();

            Label carouselItem = new Label(combatantName);
            carouselItem.getStyleClass().add("carousel-item");

            if (i == 0) {
                carouselItem.getStyleClass().add("carousel-item-active");
                carouselItem.setText("-> " + combatantName);
            }
            else {
                carouselItem.getStyleClass().add("carousel-item-queue");
            }
            nodes.add(carouselItem);
        }
        return nodes;
    }
}