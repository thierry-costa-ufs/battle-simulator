package com.game.battlesimulator.model.domain;

public abstract class Combatant {
    private static int nextId = 0;

    private final String id;
    protected String name;
    protected int currentHealth;
    protected int maxHealth;
    protected int attackDamage;

    public Combatant(String name, int maxHealth, int attackDamage, boolean isPlayer) {
        this.id = (isPlayer ? "Hero-" : "Enemy-") + nextId++;
        this.name = name;
        this.maxHealth = maxHealth;
        currentHealth = maxHealth;
        this.attackDamage = attackDamage;
    }

    // METHODS
    public boolean isAlive() {
        return (currentHealth > 0);
    }

    public void takeDamage(int damage){
        this.currentHealth = Math.max(0, this.currentHealth - damage);
    }

    // GETTERS
    public String getId() {return id;}
    public String getName() {return name;}
    public int getCurrentHealth() {return currentHealth;}
    public int getMaxHealth() {return maxHealth;}
    public int getAttackDamage() {return attackDamage;}
}
