package com.game.battlesimulator.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BattleController {
    @FXML
    private Label statusLabel;

    @FXML
    private void handleButtonClick() {
        statusLabel.setText("Hello! The button was clicked successfully.");
    }
}
