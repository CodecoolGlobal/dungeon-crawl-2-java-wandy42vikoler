package com.codecool.dungeoncrawl.model;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

public class GameState extends BaseModel {
    private String playerName;
    private String currentMap;

    public GameState(String currentMap, String playerName) {
        this.currentMap = currentMap;
        this.playerName = playerName;
    }

    public String getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(String currentMap) {
        this.currentMap = currentMap;
    }

    public String getPlayerName() {
        return playerName;
    }
}
