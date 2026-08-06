package com.game.battlesimulator.model.factory;

import com.game.battlesimulator.model.domain.Player;

public class PlayerFactory {

    private int baseHealth = 50;
    private int healthGrowth = 40;
    private int baseAttack = 10;
    private int attackGrowth = 5;

    public Player[] generatePlayer(int currentRound){
        int playersQuantity = 1;

        int multiplier = currentRound-1;
        int currentMaxHealth = baseHealth + (multiplier * healthGrowth);
        int currentAttackDamage = baseAttack + (multiplier * attackGrowth);

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
