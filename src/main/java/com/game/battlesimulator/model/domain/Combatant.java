package com.game.battlesimulator.model.domain;

public abstract class Combatant {
    protected String name;
    protected int currentHealth;
    protected int maxHealth;
    protected int attackDamage;

    public Combatant(String n) {
        name = n;
        currentHealth = 5;
        maxHealth = 5;
        attackDamage = 1;
    }

    // METHODS
    public boolean isAlive() {
        return (currentHealth > 0);
    }

    public void takeDamage(){
        currentHealth--;
    }

    // GETTERS
    public String getName() {
        return name;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    // SETTERS
    public void setName(String name) {
        this.name = name;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }
}
