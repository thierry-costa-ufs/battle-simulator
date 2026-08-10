package com.game.battlesimulator.controller;

import com.game.battlesimulator.model.engine.BattleEngine;
import com.game.battlesimulator.view.factory.*;
import com.game.battlesimulator.model.payload.BattleStatusRecord;
import com.game.battlesimulator.model.payload.CombatantRecord;
import com.game.battlesimulator.view.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class BattleController {

    //BATTLE ENGINE
    private final BattleEngine battleEngine = new BattleEngine();

    //COMBATANT ROWS
    @FXML private HBox enemySpritesContainer;
    @FXML private HBox playerSpriteContainer;

    //CONTROLS & LOG
    @FXML private ListView<String> battleLogView;
    @FXML private Button attackButton;
    @FXML private HBox turnOrderContainer;
    @FXML private VBox controlsContainer;
    @FXML private StackPane overlayRoot;

    private final Map<String, SpriteView> playerSprites = new HashMap<>();
    private final Map<String, SpriteView> enemySprites = new HashMap<>();

    @FXML
    public void initialize() {

        battleEngine.startBattle(SceneManager.getPartySize());

        int enemiesQuantity = battleEngine.getEnemiesQuantity();

        battleLogView.getItems().add("Cuidado! Uma horda com " + enemiesQuantity + " goblins emboscou você!");
        battleLogView.getItems().add("O que o herói vai fazer?");

        updateAllUI();
    }

    private void startNextRound(){
        battleEngine.prepareNextRound();

        battleLogView.getItems().add("Novo round iniciado!");

        updateAllUI();
        resetControls();
    }

    private void updateAllUI() {
        updateEnemyRows();
        updatePlayerRows();
        updateTurnOrder();
    }

    private void updateEnemyRows() {
        enemySpritesContainer.getChildren().clear();
        enemySprites.clear();

        CombatantRecord[] enemies = battleEngine.getEnemiesRecords();
        for (CombatantRecord enemy : enemies) {
            SpriteView sprite = new SpriteView(false, enemy.spriteIndex());
            if (enemy.isDead()) {
                sprite.markDead();
            }
            enemySprites.put(enemy.id(), sprite);

            VBox status = CombatantStatusFactory.createStatusNode(enemy, Pos.TOP_CENTER, false);
            VBox unit = new VBox(8, sprite, status);
            unit.setAlignment(Pos.TOP_CENTER);
            enemySpritesContainer.getChildren().add(unit);
        }
    }

    private void updatePlayerRows() {
        playerSpriteContainer.getChildren().clear();
        playerSprites.clear();

        CombatantRecord[] players = battleEngine.getPlayersRecords();
        for (CombatantRecord player : players) {
            SpriteView sprite = new SpriteView(true, player.spriteIndex());
            if (player.isDead()) {
                sprite.markDead();
            }
            playerSprites.put(player.id(), sprite);

            VBox status = CombatantStatusFactory.createStatusNode(player, Pos.BOTTOM_CENTER, true);
            VBox unit = new VBox(8, sprite, status);
            unit.setAlignment(Pos.BOTTOM_CENTER);
            playerSpriteContainer.getChildren().add(unit);
        }
    }

    @FXML
    private void handleBackToMenu() {
        SceneManager.switchScene("/fxml/menu-view.fxml");
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {        CombatantRecord currentAttacker = battleEngine.getCurrentAttackerRecord();

        if (currentAttacker.isPlayer()) {
            showTargetSelectionGrid();
        } else {
            executeEnemyAttack();
        }
    }

    private void handleGameRestart() {
        battleEngine.restartEngine();

        battleLogView.getItems().clear();
        battleLogView.getItems().add("A jornada recomeça! Boa sorte desta vez.");

        updateAllUI();
        resetControls();
    }

    private void showTargetSelectionGrid() {
        controlsContainer.getChildren().clear();
        controlsContainer.setPrefHeight(220);

        CombatantRecord[] enemies = battleEngine.getEnemiesRecords();
        GridPane targetGrid = BattleMenuFactory.createTargetGrid(enemies, this::executePlayerAttack);

        VBox.setVgrow(targetGrid, Priority.ALWAYS);
        controlsContainer.getChildren().add(targetGrid);
    }

    private void executePlayerAttack(CombatantRecord target) {
        BattleStatusRecord status = battleEngine.executeAttack(target.id());

        battleLogView.getItems().add(status.actionLog());

        if (status.isGameOver() && status.isVictory()) {
            battleLogView.getItems().add("Vitória! A horda foi derrotada!");
            updateAllUI();
            showOverlay(true);
        }
        else {
            updateAllUI();
            resetControls();
            showHitOn(status.hitTargetId(), status.hitDamage());
        }
        battleLogView.scrollTo(battleLogView.getItems().size() - 1);
    }

    private void executeEnemyAttack(){
        BattleStatusRecord status = battleEngine.executeEnemyTurn();

        if (status == null) return;

        battleLogView.getItems().add(status.actionLog());

        if (status.isGameOver() && !status.isVictory()) {
            String fallen = (status.killedTarget() != null)
                    ? status.killedTarget().name() + " tombou em combate"
                    : "a jornada termina aqui";
            battleLogView.getItems().add("Derrota... " + fallen);
            updateAllUI();
            showOverlay(false);
        }
        else {
            updateAllUI();
            resetControls();
            showHitOn(status.hitTargetId(), status.hitDamage());
        }
        battleLogView.scrollTo(battleLogView.getItems().size() - 1);
    }

    private void showHitOn(String combatantId, int damage) {
        if (combatantId == null || damage <= 0) return;

        SpriteView sprite = playerSprites.get(combatantId);
        if (sprite == null) {
            sprite = enemySprites.get(combatantId);
        }
        if (sprite != null) {
            sprite.showHit(damage);
        }
    }

    private void showOverlay(boolean victory) {
        VBox overlay = OverlayViewFactory.createOverlay(
                victory,
                battleEngine.getCurrentRound(),
                battleEngine.getPlayerHealthGrowth(),
                battleEngine.getPlayerAttackGrowth(),
                this::handleOverlayAction
        );
        overlayRoot.getChildren().clear();
        overlayRoot.getChildren().add(overlay);
        overlayRoot.setVisible(true);
    }

    private void handleOverlayAction(OverlayViewFactory.OverlayAction action) {
        overlayRoot.setVisible(false);
        overlayRoot.getChildren().clear();

        switch (action) {
            case CONTINUE -> startNextRound();
            case RESTART -> handleGameRestart();
            case MENU -> SceneManager.switchScene("/fxml/menu-view.fxml");
        }
    }

    private void resetControls() {
        controlsContainer.getChildren().clear();
        controlsContainer.setPrefHeight(110);

        CombatantRecord current = battleEngine.getCurrentAttackerRecord();
        if (current != null && current.isPlayer()) {
            attackButton.setText("Atacar");
            attackButton.setOnAction(this::handleButtonClick);
        } else {
            attackButton.setText("Próximo Turno");
            attackButton.setOnAction(this::handleButtonClick);
        }

        controlsContainer.getChildren().add(attackButton);
    }

    public void updateTurnOrder() {
        turnOrderContainer.getChildren().clear();
        CombatantRecord[] turnOrder = battleEngine.getTurnOrderRecords();
        Label[] carouselLabels = TurnOrderFactory.createCarouselNodes(turnOrder);
        turnOrderContainer.getChildren().addAll(carouselLabels);
    }
}