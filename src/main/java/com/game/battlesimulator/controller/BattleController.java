package com.game.battlesimulator.controller;

import com.game.battlesimulator.datastructure.CircularQueue;
import com.game.battlesimulator.model.domain.Combatant;
import com.game.battlesimulator.model.domain.Enemy;
import com.game.battlesimulator.model.domain.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleController {
    // ENEMY
    @FXML private VBox enemyStatusContainer;
    @FXML private VBox enemySpritesContainer;
    @FXML private Label enemyNameLabel;
    @FXML private Label enemyHealthLabel;

    // PLAYER
    @FXML private VBox playerStatusContainer;
    @FXML private VBox playerSpriteContainer;
    @FXML private Label playerNameLabel;
    @FXML private Label playerHealthLabel;

    @FXML private ListView<String> battleLogView;
    @FXML private Button attackButton;

    @FXML private HBox turnOrderContainer;

    private CircularQueue turnQueue;

    private List<Combatant> enemiesList;

    @FXML
    public void initialize() {
        turnQueue = new CircularQueue();
        enemiesList = new ArrayList<>();
        Random random = new Random();

        Combatant hero = new Player("Herói", 20,2);
        turnQueue.enqueue(hero);

        int goblinQuantity = random.nextInt(4) + 2;
        char sufixo = 'A';

        for (int i=0; i< goblinQuantity; i++) {
            Combatant goblin = new Enemy("Goblin " + sufixo, 10, 1) ;
            turnQueue.enqueue(goblin);
            enemiesList.add(goblin);
            sufixo++;
        }

        playerNameLabel.setText(hero.getName());
        playerHealthLabel.setText("VIDA: " + hero.getCurrentHealth() + "/" + hero.getMaxHealth());

        battleLogView.getItems().add("Cuidado! Uma horda com " + goblinQuantity + " goblins emboscou você!");
        battleLogView.getItems().add("O que o herói vai fazer?");

        updateEnemyLifeBars();
        updateTurnOrder(turnQueue);
    }

    private void updateEnemyLifeBars() {
        enemyStatusContainer.getChildren().clear();

        for (Combatant goblin : enemiesList) {
            HBox goblinLine = new HBox();
            goblinLine.getStyleClass().add("enemy-hp-row");

            Label lblName = new Label(goblin.getName() + " ");
            lblName.getStyleClass().add("label");

            double lifePercentage = (double) goblin.getCurrentHealth() / goblin.getMaxHealth();
            ProgressBar lifeBar = new ProgressBar(lifePercentage);

            Label lblHpText = new Label(" [" + goblin.getCurrentHealth() + "/" + goblin.getMaxHealth() + "]");
            lblHpText.getStyleClass().add("enemy-hp-text");

            if (!goblin.isAlive()) {
                goblinLine.setOpacity(0.4);
                lblName.setText("[DERROTADO]" + goblin.getName());
            }

            goblinLine.getChildren().addAll(lblName, lifeBar, lblHpText);
            enemyStatusContainer.getChildren().add(goblinLine);
        }
    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        Combatant currentAttacker = turnQueue.getCombatantOnIndex(0);
        String attackerName = currentAttacker.getName();
        battleLogView.getItems().add(attackerName + " atacou!");
        turnQueue.rotateTurn();
        updateTurnOrder(turnQueue);
        battleLogView.scrollTo(battleLogView.getItems().size()-1);
    }

    public void updateTurnOrder(CircularQueue combatantQueue) {
        turnOrderContainer.getChildren().clear();

        Label titulo = new Label("TURNOS");
        titulo.getStyleClass().add("turn-title-label");
        turnOrderContainer.getChildren().add(titulo);

        for (int i = 0; i < combatantQueue.getSize(); i++) {
            Combatant combatant = combatantQueue.getCombatantOnIndex(i);
            String combatantName = combatant.getName();

            Label itemCarrossel = new Label(combatantName);
            itemCarrossel.getStyleClass().add("carousel-item");

            if (i == 0) {
                itemCarrossel.getStyleClass().add("carousel-item-active");
                itemCarrossel.setText("► " + combatantName);
            } else if (i == 1) {
                itemCarrossel.getStyleClass().add("carousel-item-next");
            } else {
                itemCarrossel.getStyleClass().add("carousel-item-queue");
            }

            turnOrderContainer.getChildren().add(itemCarrossel);
        }
    }
}
