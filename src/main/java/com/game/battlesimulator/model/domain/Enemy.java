package com.game.battlesimulator.model.domain;

public class Enemy extends Combatant{
    public Enemy(String name, int maxHealth, int attackDamage) {
        super(name, maxHealth, attackDamage, false);
    }
}
