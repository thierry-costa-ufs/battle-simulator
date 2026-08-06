package com.game.battlesimulator.model.factory;

import com.game.battlesimulator.model.domain.Enemy;

public class EnemyFactory {

    private int minEnemys = 2;
    private int maxEnemys = 5;

    private int baseMinHealth = 10;
    private int baseMaxHealth = 20;
    private int healthGrowth = 15;
    private int baseMinAttack = 2;
    private int baseMaxAttack = 5;
    private int attackGrowth = 2;

    public Enemy[] generateHorde(int currentRound) {
        int enemiesQnt = (int) (Math.random() * (maxEnemys - minEnemys + 1) + minEnemys);

        int multiplier = currentRound-1;
        int currentMinMaxHealth = baseMinHealth + (multiplier * healthGrowth);
        int currentmaxMaxHealth = baseMaxHealth + (multiplier * healthGrowth);
        int currentminAttackDamage = baseMinAttack + (multiplier * attackGrowth);
        int currentmaxAttackDamage = baseMaxAttack + (multiplier * attackGrowth);

        Enemy[] enemiesList = new Enemy[enemiesQnt];
        for (int i = 0; i < enemiesQnt; i++) {

            int maxHealth = (int) (Math.random() * (currentmaxMaxHealth - currentMinMaxHealth + 1) + currentMinMaxHealth);
            int attackDamage = (int) (Math.random() * (currentmaxAttackDamage - currentminAttackDamage + 1) + currentminAttackDamage);

            Enemy enemy = new Enemy("Inimigo" + (i + 1), maxHealth, attackDamage);
            enemiesList[i] = enemy;
        }
        return enemiesList;
    }
}
