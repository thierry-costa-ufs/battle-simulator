package com.game.battlesimulator.model.engine;

import com.game.battlesimulator.datastructure.CircularQueue;
import com.game.battlesimulator.model.domain.Combatant;
import com.game.battlesimulator.model.domain.Enemy;
import com.game.battlesimulator.model.domain.Player;
import com.game.battlesimulator.model.factory.EnemyFactory;
import com.game.battlesimulator.model.factory.PlayerFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleEngine {

    private int currentRound = 1;
    private final Random random = new Random();
    //QUEUE
    private CircularQueue turnQueue;
    //ENEMIES
    private final EnemyFactory enemyFactory = new EnemyFactory();
    private List<Combatant> enemiesList;
    //PLAYERS
    private final PlayerFactory playerFactory = new PlayerFactory();
    private List<Combatant> playersList;

    //CONSTRUTORES
    public BattleEngine(){
        this.turnQueue = new CircularQueue();
        this.enemiesList = new ArrayList<>();
        this.playersList = new ArrayList<>();
    }

    //MÉTODOS
    public void startBattle(){
        turnQueue.clear();
        enemiesList.clear();
        playersList.clear();

        loadPlayersIntoBattle();
        loadHordeIntoBattle();
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

    private int loadHordeIntoBattle(){
        Enemy[] horde = enemyFactory.generateHorde(currentRound);
        int enemiesQuantity = horde.length;

        for(int i = 0; i < enemiesQuantity; i++){
            turnQueue.enqueue(horde[i]);
            enemiesList.add(horde[i]);
        }

        return enemiesQuantity;
    }

    public void executeAttack(Combatant target){
        Combatant attacker = getCurrentAttacker();

        if(attacker == null || target == null){
            throw new IllegalArgumentException("Atacante ou alvo inválido!!");
        }

        int currentHealth = target.getCurrentHealth();
        int damage = attacker.getAttackDamage() ;
        int newHealth = currentHealth - damage;

        target.setCurrentHealth(newHealth);

        turnQueue.rotateTurn();
    }

    public Combatant executeEnemyTurn(){
        int randomIndex = random.nextInt(playersList.size());
        Combatant target = playersList.get(randomIndex);

        executeAttack(target);
        return target;
    }

    private void playersLevelUp(){
        for(int i = 0; i < playersList.size(); i++){
            Player currentHero = (Player) playersList.get(i);
            currentHero.levelUp();
            turnQueue.enqueue(playersList.get(i));
        }
    }

    private int passRound(){
        return currentRound++;
    }

    public void prepareNextRound(){
        turnQueue.clear();
        enemiesList.clear();

       passRound();

        playersLevelUp();
        loadHordeIntoBattle();
    }

    //GETTERS
    public Combatant getCurrentAttacker(){
        return turnQueue.getCombatantOnIndex(0);
    }
    public List<Combatant> getEnemiesList() {
        return enemiesList;
    }
    public List<Combatant> getPlayersList() {
        return playersList;
    }
    public CircularQueue getTurnQueue() {
        return turnQueue;
    }
    public int getEnemiesQuantity(){
        return enemiesList.size();
    }
    public int getPlayersQuantity(){
        return playersList.size();
    }
    public int getCurrentRound(){
        return currentRound;
    }

}
