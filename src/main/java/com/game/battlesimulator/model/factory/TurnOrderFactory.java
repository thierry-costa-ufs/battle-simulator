package com.game.battlesimulator.model.factory;

import com.game.battlesimulator.datastructure.CircularQueue;

import java.util.ArrayList;
import java.util.List;

import com.game.battlesimulator.model.domain.Combatant;
import javafx.scene.control.Label;

public class TurnOrderFactory {
    public static List<Label> createCarouselNodes(CircularQueue combatantQueue) {
        List<Label> nodes = new ArrayList<>();

        Label title = new Label("TURNOS");
        title.getStyleClass().add("turn-title-label");
        nodes.add(title);

        for (int i=0; i < combatantQueue.getSize(); i++) {
            Combatant combatant = combatantQueue.getCombatantOnIndex(i);
            String combatantName = combatant.getName();

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
