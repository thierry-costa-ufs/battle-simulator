package com.game.battlesimulator.model.engine;

import com.game.battlesimulator.model.payload.BattleStatusRecord;
import com.game.battlesimulator.model.payload.CombatantRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BattleEngineTest {

    private BattleEngine engine;

    @BeforeEach
    void setUp() {
        engine = new BattleEngine();
        engine.startBattle();
    }

    private boolean hasId(CombatantRecord[] records, String id) {
        for (CombatantRecord r : records) {
            if (r.id().equals(id)) return true;
        }
        return false;
    }

    private String firstEnemyId() {
        return engine.getEnemiesRecords()[0].id();
    }

    @Test
    void attackKillsEnemyAndRemovesFromAllQueues() {
        String id = firstEnemyId();

        while (hasId(engine.getEnemiesRecords(), id)) {
            BattleStatusRecord r = engine.executeAttack(id);
            CombatantRecord target = null;
            for (CombatantRecord rec : r.players()) {
                if (rec.id().equals(id)) target = rec;
            }
            for (CombatantRecord rec : r.enemies()) {
                if (rec.id().equals(id)) target = rec;
            }
            if (target != null) {
                assertTrue(target.currentHealth() >= 0, "health never below 0");
            }
        }

        assertFalse(hasId(engine.getEnemiesRecords(), id));
        assertFalse(hasId(engine.getTurnOrderRecords(), id));
    }

    @Test
    void killingLastEnemyIsVictory() {
        BattleStatusRecord last = null;
        while (engine.getEnemiesQuantity() > 0) {
            last = engine.executeAttack(firstEnemyId());
        }
        assertNotNull(last);
        assertTrue(last.isGameOver());
        assertTrue(last.isVictory());
    }

    @Test
    void killingLastPlayerIsDefeat() {
        BattleStatusRecord last = null;
        BattleStatusRecord r;
        while (engine.getPlayersQuantity() > 0 && (r = engine.executeEnemyTurn()) != null) {
            last = r;
        }
        assertNotNull(last);
        assertTrue(last.isGameOver());
        assertFalse(last.isVictory());

        BattleStatusRecord afterDefeat = engine.executeEnemyTurn();
        assertNotNull(afterDefeat);
        assertTrue(afterDefeat.isGameOver());
        assertFalse(afterDefeat.isVictory());
    }

    @Test
    void prepareNextRoundLevelsUpAndOrdersPlayerFirst() {
        engine.executeEnemyTurn();
        engine.prepareNextRound();

        assertEquals(2, engine.getCurrentRound());
        CombatantRecord hero = engine.getPlayersRecords()[0];
        assertEquals(70, hero.maxHealth());
        assertEquals(70, hero.currentHealth());
        assertTrue(engine.getCurrentAttackerRecord().isPlayer());
    }

    @Test
    void executeAttackTargetsById() {
        String id = survivingEnemyId();
        int before = healthOf(id);
        int othersBefore = engine.getEnemiesQuantity() - 1;

        engine.executeAttack(id);

        int after = healthOf(id);
        assertTrue(after < before);
        assertEquals(othersBefore, engine.getEnemiesQuantity() - 1);
    }

    private String survivingEnemyId() {
        for (CombatantRecord r : engine.getEnemiesRecords()) {
            if (r.maxHealth() > 10) return r.id();
        }
        return firstEnemyId();
    }

    @Test
    void startBattleWithPartyOfThree() {
        BattleEngine multiEngine = new BattleEngine();
        multiEngine.startBattle(3);

        assertEquals(3, multiEngine.getPlayersQuantity());
        assertEquals(3, multiEngine.getPlayersRecords().length);
    }

    private int healthOf(String id) {
        for (CombatantRecord r : engine.getEnemiesRecords()) {
            if (r.id().equals(id)) return r.currentHealth();
        }
        for (CombatantRecord r : engine.getPlayersRecords()) {
            if (r.id().equals(id)) return r.currentHealth();
        }
        fail("no combatant with id " + id);
        return -1;
    }
}
