package com.game.battlesimulator.model.domain;

public class Player extends Combatant{
    public Player(String name, int maxHealth, int attackDamage) {
        super(name, maxHealth, attackDamage, true);
    }
}
