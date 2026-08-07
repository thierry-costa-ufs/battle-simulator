package com.game.battlesimulator.model.engine;

import com.game.battlesimulator.datastructure.CircularQueue;
import com.game.battlesimulator.model.domain.Combatant;
import com.game.battlesimulator.model.domain.Enemy;
import com.game.battlesimulator.model.domain.Player;
import com.game.battlesimulator.model.factory.EnemyFactory;
import com.game.battlesimulator.model.factory.PlayerFactory;
import com.game.battlesimulator.model.payload.BattleStatusRecord;
import com.game.battlesimulator.model.payload.CombatantRecord;

import java.util.Random;

public class BattleEngine {

    private int currentRound = 1;
    private int partySize = 1;
    private final Random random = new Random();

    // QUEUE
    private CircularQueue turnQueue;

    // ENEMIES
    private final EnemyFactory enemyFactory = new EnemyFactory();
    private CircularQueue enemiesList;

    // PLAYERS
    private final PlayerFactory playerFactory = new PlayerFactory();
    private CircularQueue playersList;

    // CONSTRUTORES
    public BattleEngine(){
        this.turnQueue = new CircularQueue();
        this.enemiesList = new CircularQueue();
        this.playersList = new CircularQueue();
    }

    // MÉTODOS DE CONTROLE DA BATALHA
    public void startBattle(){
        startBattle(this.partySize);
    }

    public void startBattle(int playersQty){
        this.partySize = playersQty;
        turnQueue.clear();
        enemiesList.clear();
        playersList.clear();

        loadPlayersIntoBattle(playersQty);
        loadHordeIntoBattle();
    }

    private int loadPlayersIntoBattle(int playersQty){
        Player[] players = playerFactory.generatePlayers(playersQty);
        int playersQuantity = players.length;

        for(int i = 0; i < playersQuantity; i++){
            turnQueue.enqueue(players[i]);
            playersList.enqueue(players[i]);
        }
        return playersQuantity;
    }

    private int loadHordeIntoBattle(){
        Enemy[] horde = enemyFactory.generateHorde(currentRound);
        int enemiesQuantity = horde.length;

        for(int i = 0; i < enemiesQuantity; i++){
            turnQueue.enqueue(horde[i]);
            enemiesList.enqueue(horde[i]);
        }
        return enemiesQuantity;
    }

    // MÉTODOS DE AÇÃO
    public BattleStatusRecord executeAttack(String targetId) {
        Combatant attacker = turnQueue.getCombatantOnIndex(0);
        Combatant target = findCombatantById(targetId);

        if (attacker == null || target == null) {
            throw new IllegalArgumentException("Atacante ou alvo inválido!!");
        }

        int damage = attacker.getAttackDamage();

        target.takeDamage(damage);

        CombatantRecord killedInThisTurn = null;
        String actionLog = target.getName() + " Foi atacado por " + attacker.getName();
        boolean isGameOver = false;
        boolean isVictory = true;

        if (!target.isAlive()) {
            killedInThisTurn = toRecord(target);
            turnQueue.remove(target);
            enemiesList.remove(target);
            playersList.remove(target);
            actionLog = target.getName() + " Foi derrotado!!";
        }

        if (getEnemiesQuantity() > 0 && getPlayersQuantity() > 0) {
            if (turnQueue.getCombatantOnIndex(0) == attacker) {
                turnQueue.rotateTurn();
            }
        }

        if(enemiesList.isEmpty()){
            isGameOver = true;
            isVictory= true;
        }
        else if(playersList.isEmpty()){
            isGameOver = true;
            isVictory = false;
        }

        return new BattleStatusRecord(
                getPlayersRecords(),
                getEnemiesRecords(),
                actionLog,
                isGameOver,
                isVictory,
                killedInThisTurn,
                target.getId(),
                damage
        );
    }

    public BattleStatusRecord executeEnemyTurn(){
        if (playersList.isEmpty()) {
            return null;
        }

        int randomIndex = random.nextInt(playersList.getSize());
        Combatant target = playersList.getCombatantOnIndex(randomIndex);

        return executeAttack(target.getId());
    }

    private void playersLevelUp(){
        for(int i = 0; i < playersList.getSize(); i++){
            Player currentHero = (Player) playersList.getCombatantOnIndex(i);
            currentHero.levelUp();
            turnQueue.enqueue(playersList.getCombatantOnIndex(i));
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

    public void restartEngine(){
        this.currentRound = 1;

        this.turnQueue = new CircularQueue();
        this.enemiesList = new CircularQueue();
        this.playersList = new CircularQueue();

        this.startBattle();
    }

    // UTILS E BUSCAS INTERNAS
    private Combatant findCombatantById(String id) {
        if (id == null) return null;

        for (int i = 0; i < playersList.getSize(); i++) {
            Combatant c = playersList.getCombatantOnIndex(i);
            if (c != null && id.equals(c.getId())) return c;
        }

        for (int i = 0; i < enemiesList.getSize(); i++) {
            Combatant c = enemiesList.getCombatantOnIndex(i);
            if (c != null && id.equals(c.getId())) return c;
        }

        return null;
    }
    private CombatantRecord toRecord(Combatant c) {
        if (c == null) return null;
        return new CombatantRecord(
                c.getId(),
                c.getName(),
                c.getCurrentHealth(),
                c.getMaxHealth(),
                c instanceof Player,
                c.getSpriteIndex()
        );
    }

    // GETTERS
    public CombatantRecord getCurrentAttackerRecord(){
        return toRecord(turnQueue.getCombatantOnIndex(0));
    }

    public CombatantRecord[] getEnemiesRecords() {
        int size = enemiesList.getSize();
        CombatantRecord[] records = new CombatantRecord[size];

        for(int i = 0; i < size; i++ ){
            Combatant c = enemiesList.getCombatantOnIndex(i);
            records[i] = toRecord(c);
        }
        return records;
    }

    public CombatantRecord[] getPlayersRecords() {
        int size = playersList.getSize();
        CombatantRecord[] records = new CombatantRecord[size];

        for(int i = 0; i < size; i++ ){
            Combatant c = playersList.getCombatantOnIndex(i);
            records[i] = toRecord(c);
        }
        return records;
    }

    public CombatantRecord[] getTurnOrderRecords() {
        int size = turnQueue.getSize();
        CombatantRecord[] order = new CombatantRecord[size];

        for (int i = 0; i < size; i++) {
            Combatant c = turnQueue.getCombatantOnIndex(i);
            if (c != null) {
                order[i] = toRecord(c);
            }
        }
        return order;
    }

    public int getEnemiesQuantity(){
        return enemiesList.getSize();
    }

    public int getPlayersQuantity(){
        return playersList.getSize();
    }

    public int getCurrentRound(){
        return currentRound;
    }
}