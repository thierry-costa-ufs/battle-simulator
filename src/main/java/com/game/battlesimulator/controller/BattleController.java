package com.game.battlesimulator.controller;

import com.game.battlesimulator.datastructure.CircularQueue;
import com.game.battlesimulator.model.domain.Combatant;
import com.game.battlesimulator.model.domain.Enemy;
import com.game.battlesimulator.model.domain.Player;
import com.game.battlesimulator.model.factory.BattleMenuFactory;
import com.game.battlesimulator.model.factory.CombatantStatusFactory;
import com.game.battlesimulator.model.factory.TurnOrderFactory;
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

    // PLAYER
    @FXML private VBox playerStatusContainer;
    @FXML private VBox playerSpriteContainer;
    private Combatant hero;

    // CONTROLS & LOG
    @FXML private ListView<String> battleLogView;
    @FXML private Button attackButton;
    @FXML private HBox turnOrderContainer;
    @FXML private VBox controlsContainer;

    private CircularQueue turnQueue;
    private List<Combatant> enemiesList;

    @FXML
    public void initialize() {
        turnQueue = new CircularQueue();
        enemiesList = new ArrayList<>();
        Random random = new Random();

        hero = new Player("Herói", 20,2);
        turnQueue.enqueue(hero);

        int goblinQuantity = random.nextInt(4) + 2;
        char sufixo = 'A';

        for (int i=0; i< goblinQuantity; i++) {
            Combatant goblin = new Enemy("Goblin " + sufixo, 10, 1) ;
            turnQueue.enqueue(goblin);
            enemiesList.add(goblin);
            sufixo++;
        }

        battleLogView.getItems().add("Cuidado! Uma horda com " + goblinQuantity + " goblins emboscou você!");
        battleLogView.getItems().add("O que o herói vai fazer?");

        updatePlayerLifeBar();
        updateEnemyLifeBars();
        updateTurnOrder(turnQueue);
    }

    private void updateEnemyLifeBars() {
        enemyStatusContainer.getChildren().clear();
        for (Combatant goblin : enemiesList) {
            VBox goblinBox = CombatantStatusFactory.createStatusNode(goblin, Pos.TOP_LEFT, false);
            enemyStatusContainer.getChildren().add(goblinBox);
        }
    }

    private void updatePlayerLifeBar() {
        playerStatusContainer.getChildren().clear();
        VBox playerBox = CombatantStatusFactory.createStatusNode(hero, Pos.BOTTOM_RIGHT, true);
        playerStatusContainer.getChildren().add(playerBox);
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
        battleLogView.getItems().add(hero.getName() + " atacou " + target.getName() + "!");
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
