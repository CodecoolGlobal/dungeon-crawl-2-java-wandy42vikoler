package com.codecool.dungeoncrawl.logic.actors;

import com.codecool.dungeoncrawl.logic.Cell;

public class Monster extends Actor{

    public Monster(Cell cell){
        super(cell);
    }


    public String getTileName() {
        return "monster";
    }
}
