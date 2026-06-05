package com.game.battlesimulator.model.factory;

import com.game.battlesimulator.model.domain.Enemy;

public class EnemyFactory {

    private int minEnemys = 2;
    private int maxEnemys = 5;

    private int baseMinHealth = 30;
    private int baseMaxHealth = 80;
    private int healthGrowth = 10;
    private int baseMinAttack = 5;
    private int baseMaxAttack = 15;
    private int attackGrowth = 2;

    public EnemyFactory(){
    };

    public Enemy[] generateHorde(int currentRound) {
        int enemiesQnt = (int) (Math.random() * (maxEnemys - minEnemys + 1) + minEnemys);

        int currentMinMaxHealth = baseMinHealth + (currentRound * healthGrowth);
        int currentmaxMaxHealth = baseMaxHealth + (currentRound * healthGrowth);
        int currentminAttackDamage = baseMinAttack + (currentRound * attackGrowth);
        int currentmaxAttackDamage = baseMaxAttack + (currentRound * attackGrowth);

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
