package com.game.battlesimulator.model.domain;

public class Enemy extends Combatant{
    public Enemy(String id, String name, int maxHealth, int attackDamage, int spriteIndex) {
        super(id, name, maxHealth, attackDamage, spriteIndex);
    }
}
