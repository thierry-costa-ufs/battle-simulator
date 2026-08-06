package com.game.battlesimulator.model.factory;

import com.game.battlesimulator.model.domain.Enemy;
import com.game.battlesimulator.model.domain.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FactoryTest {

    @Test
    void hordeSizeIsBetweenTwoAndFive() {
        EnemyFactory factory = new EnemyFactory();

        for (int i = 0; i < 100; i++) {
            int size = factory.generateHorde(1).length;
            assertTrue(size >= 2 && size <= 5, "horde size out of range: " + size);
        }
    }

    @Test
    void enemyStatsScaleWithRound() {
        EnemyFactory factory = new EnemyFactory();

        for (int i = 0; i < 100; i++) {
            for (Enemy e : factory.generateHorde(1)) {
                assertTrue(e.getMaxHealth() >= 10 && e.getMaxHealth() <= 20);
                assertTrue(e.getAttackDamage() >= 2 && e.getAttackDamage() <= 5);
            }
            for (Enemy e : factory.generateHorde(2)) {
                assertTrue(e.getMaxHealth() >= 25 && e.getMaxHealth() <= 35);
                assertTrue(e.getAttackDamage() >= 4 && e.getAttackDamage() <= 7);
            }
        }
    }

    @Test
    void levelUpAddsDeltasAndFullyHeals() {
        Player hero = new Player("Hero1", 50, 10);
        hero.takeDamage(30);

        hero.levelUp();

        assertEquals(70, hero.getMaxHealth());
        assertEquals(14, hero.getAttackDamage());
        assertEquals(70, hero.getCurrentHealth());
    }

    @Test
    void playerFactoryReturnsFixedBase() {
        Player hero = new PlayerFactory().generatePlayer()[0];

        assertEquals(50, hero.getMaxHealth());
        assertEquals(10, hero.getAttackDamage());
        assertEquals(50, hero.getCurrentHealth());
        assertEquals("Hero1", hero.getName());
    }
}
