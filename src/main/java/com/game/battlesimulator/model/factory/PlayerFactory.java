package com.game.battlesimulator.model.factory;

import com.game.battlesimulator.model.domain.Player;

public class PlayerFactory {

    private int baseHealth = 100;
    private int healthGrowth = 10;
    private int baseAttack = 10;
    private int attackGrowth = 1;

    public PlayerFactory(){
    }

    public Player[] generatePlayer(int currentRound){
        int playersQuantity = 1;

        int currentMaxHealth = baseHealth + (currentRound * healthGrowth);
        int currentAttackDamage = baseAttack + (currentRound * attackGrowth);

        Player[] players = new Player[playersQuantity];
        for(int i = 0; i < playersQuantity; i++){

            int maxHealth = currentMaxHealth;
            int attackDamage = currentAttackDamage;

            Player player = new Player("Hero" + (i+1), maxHealth, attackDamage);
            players[i] = player;
        }
        return players;
    }
}
