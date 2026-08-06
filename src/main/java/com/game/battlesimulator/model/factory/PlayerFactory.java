package com.game.battlesimulator.model.factory;

import com.game.battlesimulator.model.domain.Player;

public class PlayerFactory {

    private final int baseHealth = 50;
    private final int baseAttack = 10;

    // ponytail: 1 hero; array kept for shape symmetry with EnemyFactory
    public Player[] generatePlayer(){
        return new Player[]{ new Player("Hero1", baseHealth, baseAttack) };
    }
}
