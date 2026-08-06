package com.game.battlesimulator.controller;

import com.game.battlesimulator.view.SceneManager;
import javafx.fxml.FXML;

public class MenuController {

    @FXML
    private void handleStartBattle() {
        SceneManager.switchScene("/fxml/battle-view.fxml");
    }
}
