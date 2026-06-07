package com.game.battlesimulator.controller;

import com.game.battlesimulator.model.engine.BattleEngine;
import com.game.battlesimulator.model.factory.*;
import com.game.battlesimulator.model.payload.CombatantRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;

public class BattleController {

    //BATTLE ENGINE
    private final BattleEngine battleEngine = new BattleEngine();

    //ENEMY
    @FXML private VBox enemyStatusContainer;
    @FXML private VBox enemySpritesContainer;

    //PLAYER
    @FXML private VBox playerStatusContainer;
    @FXML private VBox playerSpriteContainer;

    //CONTROLS & LOG
    @FXML private ListView<String> battleLogView;
    @FXML private Button attackButton;
    @FXML private HBox turnOrderContainer;
    @FXML private VBox controlsContainer;

    @FXML
    public void initialize() {

        battleEngine.startBattle();

        int enemiesQuantity = battleEngine.getEnemiesQuantity();

        battleLogView.getItems().add("Cuidado! Uma horda com " + enemiesQuantity + " goblins emboscou você!");
        battleLogView.getItems().add("O que o herói vai fazer?");

        updateAllUI();
    }

    private void startNextRound(){
        battleEngine.prepareNextRound();
        updateAllUI();
    }

    private void updateAllUI() {
        updatePlayerLifeBar();
        updateEnemyLifeBars();
        updateTurnOrder();
        updateEnemySprites();
    }

    private void updateEnemyLifeBars() {
        enemyStatusContainer.getChildren().clear();
        List<CombatantRecord> enemies = battleEngine.getEnemiesRecords();
        for (CombatantRecord enemy : enemies) {
            VBox enemyBox = CombatantStatusFactory.createStatusNode(enemy, Pos.TOP_LEFT, false);
            enemyStatusContainer.getChildren().add(enemyBox);
        }
    }

    private void updatePlayerLifeBar() {
        playerStatusContainer.getChildren().clear();
        List<CombatantRecord> players = battleEngine.getPlayersRecords();
        for(CombatantRecord player : players){
            VBox playerBox = CombatantStatusFactory.createStatusNode(player, Pos.BOTTOM_RIGHT, true);
            playerStatusContainer.getChildren().add(playerBox);
        }
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        CombatantRecord currentAttacker = battleEngine.getCurrentAttackerRecord();

        if (currentAttacker.isPlayer()) {
            showTargetSelectionGrid();
        } else {
            executeEnemyAttack();
        }
    }

    private void showTargetSelectionGrid() {
        controlsContainer.getChildren().clear();
        List<CombatantRecord> enemies = battleEngine.getEnemiesRecords();
        GridPane targetGrid = BattleMenuFactory.createTargetGrid(enemies, this::executePlayerAttack);
        controlsContainer.getChildren().add(targetGrid);
    }

    private void executePlayerAttack(CombatantRecord target) {
        CombatantRecord currentAttacker = battleEngine.getCurrentAttackerRecord();

        CombatantRecord updatedTarget = battleEngine.executeAttack(target.id());
        battleLogView.getItems().add(currentAttacker.name() + " atacou " + updatedTarget.name() + "!");

        if (battleEngine.getEnemiesQuantity() == 0) {
            battleLogView.getItems().add("A horda foi derrotada! Avançando de round...");
            startNextRound();
        } else {
            updateAllUI();
            resetControls();
        }
        battleLogView.scrollTo(battleLogView.getItems().size() - 1);
    }

    private void executeEnemyAttack(){
        CombatantRecord currentAttacker = battleEngine.getCurrentAttackerRecord();

        CombatantRecord target = battleEngine.executeEnemyTurn();
        battleLogView.getItems().add(currentAttacker.name() + " atacou " + target.name());

        if (battleEngine.getPlayersQuantity() == 0) {
            battleLogView.getItems().add("O herói foi derrotado. Fim de jogo.");
            controlsContainer.getChildren().clear(); // Remove os controles
            updateAllUI();
        } else {
            updateAllUI();
            resetControls();
        }
        battleLogView.scrollTo(battleLogView.getItems().size() - 1);
    }

    private void resetControls() {
        controlsContainer.getChildren().clear();
        controlsContainer.getChildren().add(attackButton);
    }

    public void updateTurnOrder() {
        turnOrderContainer.getChildren().clear();
        List<CombatantRecord> turnOrder = battleEngine.getTurnOrderRecords();
        List<Label> carouselLabels = TurnOrderFactory.createCarouselNodes(turnOrder);
        turnOrderContainer.getChildren().addAll(carouselLabels);
    }

    private void updateEnemySprites() {
        enemySpritesContainer.getChildren().clear();

        List<CombatantRecord> enemies = battleEngine.getEnemiesRecords();

        for (CombatantRecord enemy : enemies) {
            if (!enemy.isDead()) {
                javafx.scene.layout.Region sprite = new javafx.scene.layout.Region();
                sprite.getStyleClass().add("enemy-sprite");
                enemySpritesContainer.getChildren().add(sprite);
            }
        }
    }
}