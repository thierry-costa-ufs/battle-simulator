package com.game.battlesimulator.model.payload;

public record CombatantRecord(
        String id,            // Identificador único (útil para referenciar o alvo no ataque)
        String name,          // Nome do personagem ou monstro
        int currentHealth,    // Vida atual
        int maxHealth,        // Vida máxima
        boolean isPlayer,     // Flag para a interface saber de qual lado renderizar
        int spriteIndex       // Índice do sprite (1-based, para o CSS buscar a imagem)
) {

    public double getHealthPercentage() {
        if (maxHealth <= 0) return 0.0;
        return (double) currentHealth / maxHealth;
    }


    public boolean isDead() {
        return currentHealth <= 0;
    }
}