package com.game.battlesimulator.model.domain;

public class Player extends Combatant{
    public Player(String name, int maxHealth, int attackDamage) {
        super(name, maxHealth, attackDamage, true);
    }

    public void levelUp(){
        maxHealth += 20;
        attackDamage += 4;

        currentHealth = maxHealth;
    }

}
