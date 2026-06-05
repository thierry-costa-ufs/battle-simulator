package com.game.battlesimulator.controller;

import com.game.battlesimulator.datastructure.CircularQueue;
import com.game.battlesimulator.model.domain.Combatant;
import com.game.battlesimulator.model.domain.Enemy;
import com.game.battlesimulator.model.domain.Player;
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
import java.util.Random;

public class BattleController {
    // ENEMY
    @FXML private VBox enemyStatusContainer;
    @FXML private VBox enemySpritesContainer;
    private final EnemyFactory enemyFactory = new EnemyFactory();

    // PLAYER
    @FXML private VBox playerStatusContainer;
    @FXML private VBox playerSpriteContainer;
    private final PlayerFactory playerFactory = new PlayerFactory();

    // CONTROLS & LOG
    @FXML private ListView<String> battleLogView;
    @FXML private Button attackButton;
    @FXML private HBox turnOrderContainer;
    @FXML private VBox controlsContainer;

    private int currentRound = 1;
    private CircularQueue turnQueue;
    private List<Combatant> enemiesList;
    private List<Combatant> playersList;

    @FXML
    public void initialize() {
        turnQueue = new CircularQueue();
        enemiesList = new ArrayList<>();
        playersList = new ArrayList<>();

        int playersQuantity = loadPlayersIntoBattle();
        int enemiesQuantity = loadHordeIntoBattle();

        battleLogView.getItems().add("Cuidado! Uma horda com " + enemiesQuantity + " goblins emboscou você!");
        battleLogView.getItems().add("O que o herói vai fazer?");

        updatePlayerLifeBar();
        updateEnemyLifeBars();
        updateTurnOrder(turnQueue);
    }

    private void startNextRound(){
        enemiesList.clear();
        currentRound++;
        turnQueue.clear();

        playersLevelUp();
        loadHordeIntoBattle();

        updateTurnOrder(turnQueue);
    }

    private int loadHordeIntoBattle(){
        Enemy[] horde = enemyFactory.generateHorde(currentRound);
        int enemiesQuantity = horde.length;

        for (int i=0; i< enemiesQuantity; i++) {
            turnQueue.enqueue(horde[i]);
            enemiesList.add(horde[i]);
        }
        return enemiesQuantity;
    }

    private int loadPlayersIntoBattle(){
        Player[] players = playerFactory.generatePlayer(currentRound);
        int  playersQuantity = players.length;

        for(int i = 0; i < playersQuantity; i++){
            turnQueue.enqueue(players[i]);
            playersList.add(players[i]);
        }
        return playersQuantity;
    }

    private void playersLevelUp(){
        for(int i = 0; i < playersList.size(); i++){
            Player currentHero = (Player) playersList.get(i);
            currentHero.levelUp();
            turnQueue.enqueue(playersList.get(i));
        }
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
            battleLogView.getItems().add(currentAttacker.getName() + " está pensando...");
        }
    }

    private void showTargetSelectionGrid() {
        controlsContainer.getChildren().clear();
        GridPane targetGrid = BattleMenuFactory.createTargetGrid(enemiesList, this::executePlayerAttack);
        controlsContainer.getChildren().add(targetGrid);
    }

    private void executePlayerAttack(Combatant target) {
        Combatant currentAttacker = turnQueue.getCombatantOnIndex(0);
        battleLogView.getItems().add(currentAttacker.getName() + " atacou " + target.getName() + "!");
        turnQueue.rotateTurn();
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
