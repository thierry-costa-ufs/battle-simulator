package com.game.battlesimulator.model.engine;

import com.game.battlesimulator.datastructure.CircularQueue;
import com.game.battlesimulator.model.domain.Combatant;
import com.game.battlesimulator.model.domain.Enemy;
import com.game.battlesimulator.model.domain.Player;
import com.game.battlesimulator.model.factory.EnemyFactory;
import com.game.battlesimulator.model.factory.PlayerFactory;
import com.game.battlesimulator.model.payload.CombatantRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BattleEngine {

    private int currentRound = 1;
    private final Random random = new Random();

    // QUEUE
    private CircularQueue turnQueue;

    // ENEMIES
    private final EnemyFactory enemyFactory = new EnemyFactory();
    private List<Combatant> enemiesList;

    // PLAYERS
    private final PlayerFactory playerFactory = new PlayerFactory();
    private List<Combatant> playersList;

    // CONSTRUTORES
    public BattleEngine(){
        this.turnQueue = new CircularQueue();
        this.enemiesList = new ArrayList<>();
        this.playersList = new ArrayList<>();
    }

    // MÉTODOS DE CONTROLE DA BATALHA
    public void startBattle(){
        turnQueue.clear();
        enemiesList.clear();
        playersList.clear();

        loadPlayersIntoBattle();
        loadHordeIntoBattle();
    }

    private int loadPlayersIntoBattle(){
        Player[] players = playerFactory.generatePlayer(currentRound);
        int playersQuantity = players.length;

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

    // MÉTODOS DE AÇÃO (REFATORADOS PARA USAR IDENTIFICADORES E RETORNAR RECORDS)
    public CombatantRecord executeAttack(String targetId){
        Combatant attacker = turnQueue.getCombatantOnIndex(0);
        Combatant target = findCombatantById(targetId);

        if(attacker == null || target == null){
            throw new IllegalArgumentException("Atacante ou alvo inválido!!");
        }

        int currentHealth = target.getCurrentHealth();
        int damage = attacker.getAttackDamage();
        int newHealth = Math.max(0, currentHealth - damage);

        target.setCurrentHealth(newHealth);

        if (!target.isAlive()) {
            turnQueue.remove(target);
            enemiesList.remove(target);
            playersList.remove(target);
        }

        if (getEnemiesQuantity() > 0 && getPlayersQuantity() > 0) {
            if (turnQueue.getCombatantOnIndex(0) == attacker) {
                turnQueue.rotateTurn();
            }
        }

        return toRecord(target);
    }

    public CombatantRecord executeEnemyTurn(){
        int randomIndex = random.nextInt(playersList.size());
        Combatant target = playersList.get(randomIndex);

        return executeAttack(target.getName());
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

    // UTILS E BUSCAS INTERNAS
    private Combatant findCombatantById(String id) {
        for (Combatant c : playersList) {
            if (c.getName().equals(id)) return c;
        }
        for (Combatant c : enemiesList) {
            if (c.getName().equals(id)) return c;
        }
        return null;
    }

    private CombatantRecord toRecord(Combatant c) {
        if (c == null) return null;
        return new CombatantRecord(
                c.getName(),
                c.getName(),
                c.getCurrentHealth(),
                c.getMaxHealth(),
                c instanceof Player
        );
    }

    // GETTERS
    public CombatantRecord getCurrentAttackerRecord(){
        return toRecord(turnQueue.getCombatantOnIndex(0));
    }

    public List<CombatantRecord> getEnemiesRecords() {
        return enemiesList.stream().map(this::toRecord).collect(Collectors.toList());
    }

    public List<CombatantRecord> getPlayersRecords() {
        return playersList.stream().map(this::toRecord).collect(Collectors.toList());
    }

    public List<CombatantRecord> getTurnOrderRecords() {
        List<CombatantRecord> order = new ArrayList<>();
        int totalCombatants = getPlayersQuantity() + getEnemiesQuantity();
        for (int i = 0; i < totalCombatants; i++) {
            Combatant c = turnQueue.getCombatantOnIndex(i);
            if (c != null) {
                order.add(toRecord(c));
            }
        }
        return order;
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