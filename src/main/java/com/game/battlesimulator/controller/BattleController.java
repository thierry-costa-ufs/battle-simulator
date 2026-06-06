package com.game.battlesimulator.controller;

import com.game.battlesimulator.datastructure.CircularQueue;
import com.game.battlesimulator.model.domain.Combatant;
import com.game.battlesimulator.model.domain.Enemy;
import com.game.battlesimulator.model.domain.Player;
import com.game.battlesimulator.model.engine.BattleEngine;
import com.game.battlesimulator.model.factory.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

public class BattleController {

    //BATTLE ENGINE
    private final BattleEngine battleEngine = new BattleEngine();

    //ENEMY
    @FXML private VBox enemyStatusContainer;
    @FXML private VBox enemySpritesContainer;
    List<Combatant> enemiesList = battleEngine.getEnemiesList();

    //PLAYER
    @FXML private VBox playerStatusContainer;
    @FXML private VBox playerSpriteContainer;
    List<Combatant> playersList = battleEngine.getPlayersList();

    //QUEUE
    CircularQueue turnQueue = battleEngine.getTurnQueue();

    //CONTROLS & LOG
    @FXML private ListView<String> battleLogView;
    @FXML private Button attackButton;
    @FXML private HBox turnOrderContainer;
    @FXML private VBox controlsContainer;

    @FXML
    public void initialize() {

        battleEngine.startBattle();

        int enemiesQuantity = enemiesList.size();
        int playersQuantity = playersList.size();

        battleLogView.getItems().add("Cuidado! Uma horda com " + enemiesQuantity + " goblins emboscou você!");
        battleLogView.getItems().add("O que o herói vai fazer?");

        updatePlayerLifeBar();
        updateEnemyLifeBars();
        updateTurnOrder(turnQueue);
    }

    private void startNextRound(){
        battleEngine.prepareNextRound();

        updatePlayerLifeBar();
        updateEnemyLifeBars();
        updateTurnOrder(turnQueue);
    }

    private void updateEnemyLifeBars() {
        enemyStatusContainer.getChildren().clear();
        for (Combatant enemy : enemiesList) {
            VBox enemyBox = CombatantStatusFactory.createStatusNode(enemy, Pos.TOP_LEFT, false);
            enemyStatusContainer.getChildren().add(enemyBox);
        }
    }

    private void updatePlayerLifeBar() {
        playerStatusContainer.getChildren().clear();
        for(Combatant player : playersList){
            VBox playerBox = CombatantStatusFactory.createStatusNode(player, Pos.BOTTOM_RIGHT, true);
            playerStatusContainer.getChildren().add(playerBox);
        }
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        Combatant currentAttacker = turnQueue.getCombatantOnIndex(0);

        if (currentAttacker instanceof Player) {
            showTargetSelectionGrid();
        }
        else {
            executeEnemyAttack();
        }
    }

    private void showTargetSelectionGrid() {
        controlsContainer.getChildren().clear();
        GridPane targetGrid = BattleMenuFactory.createTargetGrid(enemiesList, this::executePlayerAttack);
        controlsContainer.getChildren().add(targetGrid);
    }

    private void executeEnemyAttack(){
        Combatant currentAttacker = turnQueue.getCombatantOnIndex(0);

        Combatant target = battleEngine.executeEnemyTurn();
        battleLogView.getItems().add(currentAttacker.getName() + " atacou " + target.getName());
        updateEnemyLifeBars();
        updatePlayerLifeBar();
        updateTurnOrder(turnQueue);
        resetControls();
        battleLogView.scrollTo(battleLogView.getItems().size() - 1);
    }

    private void executePlayerAttack(Combatant target) {
        Combatant currentAttacker = battleEngine.getCurrentAttacker();

        battleEngine.executeAttack(target);
        battleLogView.getItems().add(currentAttacker.getName() + " atacou " + target.getName() + "!");

        updateEnemyLifeBars();
        updatePlayerLifeBar();
        updateTurnOrder(turnQueue);
        resetControls();
        battleLogView.scrollTo(battleLogView.getItems().size() - 1);
    }

    private void resetControls() {
        controlsContainer.getChildren().clear();
        controlsContainer.getChildren().add(attackButton);
    }

    public void updateTurnOrder(CircularQueue combatantQueue) {
        turnOrderContainer.getChildren().clear();
        List<Label> carouselLabels = TurnOrderFactory.createCarouselNodes(combatantQueue);
        turnOrderContainer.getChildren().addAll(carouselLabels);
    }
}
