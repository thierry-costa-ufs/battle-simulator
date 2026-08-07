package com.game.battlesimulator.model.domain;

public class Player extends Combatant{
    public Player(String id, String name, int maxHealth, int attackDamage) {
        super(id, name, maxHealth, attackDamage);
    }

    public void levelUp(){
        maxHealth += 20;
        attackDamage += 4;

        currentHealth = maxHealth;
    }

}
