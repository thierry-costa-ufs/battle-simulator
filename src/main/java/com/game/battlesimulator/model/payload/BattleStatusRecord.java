package com.game.battlesimulator.model.payload;

import java.util.List;

public record BattleStatusRecord(
        List<CombatantRecord> players,
        List<CombatantRecord> enemies,
        String actionLog,
        boolean isGameOver,
        boolean isVictory,
        CombatantRecord killedTarget
){
}
