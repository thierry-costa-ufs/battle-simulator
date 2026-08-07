package com.game.battlesimulator.model.domain;

public class Player extends Combatant{
    public Player(String id, String name, int maxHealth, int attackDamage, int spriteIndex) {
        super(id, name, maxHealth, attackDamage, spriteIndex);
    }

    public void levelUp(){
        maxHealth += 20;
        attackDamage += 4;

        currentHealth = maxHealth;
    }

}
