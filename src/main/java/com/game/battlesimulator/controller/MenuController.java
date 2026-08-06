package com.game.battlesimulator.controller;

import com.game.battlesimulator.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MenuController {

    private int selectedPartySize = 1;

    @FXML
    private Label partySizeLabel;

    @FXML
    private void selectPartySize(int size) {
        selectedPartySize = size;
        partySizeLabel.setText("Heróis: " + size);
    }

    @FXML
    private void handlePartySize1() { selectPartySize(1); }
    @FXML
    private void handlePartySize2() { selectPartySize(2); }
    @FXML
    private void handlePartySize3() { selectPartySize(3); }
    @FXML
    private void handlePartySize4() { selectPartySize(4); }

    @FXML
    private void handleStartBattle() {
        SceneManager.setPartySize(selectedPartySize);
        SceneManager.switchScene("/fxml/battle-view.fxml");
    }
}
