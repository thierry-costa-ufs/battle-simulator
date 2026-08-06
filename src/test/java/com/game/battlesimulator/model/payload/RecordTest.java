package com.game.battlesimulator.model.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecordTest {

    @Test
    void healthPercentageDividesCurrentByMax() {
        CombatantRecord r = new CombatantRecord("Hero-1", "Hero", 50, 100, true);
        assertEquals(0.5, r.getHealthPercentage());
    }

    @Test
    void healthPercentageZeroWhenMaxHealthZero() {
        CombatantRecord r = new CombatantRecord("Enemy-1", "Enemy", 10, 0, false);
        assertEquals(0.0, r.getHealthPercentage());
    }

    @Test
    void isDeadOnlyAtOrBelowZero() {
        assertFalse(new CombatantRecord("id", "n", 1, 10, true).isDead());
        assertTrue(new CombatantRecord("id", "n", 0, 10, true).isDead());
    }
}
