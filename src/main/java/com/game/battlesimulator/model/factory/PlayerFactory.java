package com.game.battlesimulator.model.factory;

import com.game.battlesimulator.model.domain.Player;

public class PlayerFactory {

    private final int baseHealth = 50;
    private final int baseAttack = 10;

    // ponytail: base stats fixed; growth lives ONLY in Player.levelUp()
    public Player[] generatePlayers(int count){
        Player[] players = new Player[count];
        for (int i = 0; i < count; i++) {
            players[i] = new Player("Hero-" + (i + 1), "Hero" + (i + 1), baseHealth, baseAttack, i + 1);
        }
        return players;
    }

    public Player[] generatePlayer(){
        return generatePlayers(1);
    }
}
