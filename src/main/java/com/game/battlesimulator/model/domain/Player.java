package com.game.battlesimulator.model.domain;

public class Player extends Combatant{
    public static final int HEALTH_GROWTH = 20;
    public static final int ATTACK_GROWTH = 4;

    public Player(String id, String name, int maxHealth, int attackDamage, int spriteIndex) {
        super(id, name, maxHealth, attackDamage, spriteIndex);
    }

    public void levelUp(){
        maxHealth += HEALTH_GROWTH;
        attackDamage += ATTACK_GROWTH;

        currentHealth = maxHealth;
    }

}
