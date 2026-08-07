package com.game.battlesimulator.model.payload;

public record BattleStatusRecord(
        CombatantRecord[] players,
        CombatantRecord[] enemies,
        String actionLog,
        boolean isGameOver,
        boolean isVictory,
        CombatantRecord killedTarget,
        String hitTargetId,
        int hitDamage
){
}