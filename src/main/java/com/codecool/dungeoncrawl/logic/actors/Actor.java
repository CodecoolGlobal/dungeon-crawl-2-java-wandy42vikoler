package com.codecool.dungeoncrawl.logic.actors;

import com.codecool.dungeoncrawl.App;
import com.codecool.dungeoncrawl.Main;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.CellType;
import com.codecool.dungeoncrawl.logic.Drawable;
import com.codecool.dungeoncrawl.logic.items.Item;
import com.codecool.dungeoncrawl.logic.items.Key;
import com.codecool.dungeoncrawl.logic.items.Sword;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class Actor implements Drawable {
    private Cell cell;
    private int playerHealth = 10;
    private int skeletonHealth = 3;
    private int mentorBot = 10;
    public Set<Item> inventory = new HashSet<>();

    public boolean nextLevelOk = false;

    public Actor(Cell cell) {
        this.cell = cell;
        this.cell.setActor(this);
    }

    public void move(int dx, int dy) {
        Cell nextCell = cell.getNeighbor(dx, dy);
        Actor actor = nextCell.getActor();
        if (actor instanceof Skeleton){
            if (isSwordInInventory()) {
                playerHealth -= 2;
                skeletonHealth -= 5;
            }
            else {
                skeletonHealth -= 3;
                playerHealth -= 2;
            }
            if (skeletonHealth <= 0) {
                nextCell.setActor(null);
                skeletonHealth = 0;
            }
            if (playerHealth <= 0) {
                cell.setActor(null);
                playerHealth = 0;
            }
        }
        if (actor instanceof Monster){
            if (isSwordInInventory()) {
                playerHealth -= 4;
                mentorBot -= 5;
            }
            else {
                playerHealth -= 4;
                mentorBot -= 2;
            }
            if (mentorBot <= 0) {
                nextCell.setActor(null);
                mentorBot = 0;
            }
            if (playerHealth <= 0) {
                cell.setActor(null);
                playerHealth = 0;
            }
        }
        if (nextCell.getType() == CellType.WALL  || nextCell.getType() == CellType.TORCH || nextCell.getType() == CellType.WINDOW || actor instanceof Monster) {
            cell.setActor(this);
        } else if (nextCell.getType() == CellType.CLOSED_DOOR) {
            if (isKeyInInventory()) {
                nextCell.setType(CellType.OPEN_DOOR);
                removeKey();
                nextLevelOk = true;
            }
        }
        else {
            cell.setActor(null);
            nextCell.setActor(this);
            cell = nextCell;
        }
    }

    public void moveMonster(int x, int y){
        Cell nextCell =  cell.nextPosition(x, y);
        if (nextCell.getActor() != null) {
            cell.setActor(this);
        } else {
            cell.setActor(null);
            nextCell.setActor(this);
            cell = nextCell;
        }
    }

    public int getPlayerHealth() {
        return playerHealth;
    }

    public int getMentorBotHealth() {
        return mentorBot;
    }

    public Cell getCell() {
        return cell;
    }

    public int getX() {
        return cell.getX();
    }

    public int getY() {
        return cell.getY();
    }

    public Set<Item> getInventory() {
        return inventory;
    }

    public void addItem() {
        if (cell.getItem() != null) {
            if (Objects.equals(cell.getItem().getTileName(), "potion")) {
                playerHealth += 5;
            } else {
                inventory.add(cell.getItem());
            }
            cell.setItem(null);
        }
    }

    boolean isKeyInInventory() {
        boolean result = false;
        for(Item item : inventory) {
            if (item instanceof Key) {
                result = true;
                break;
            }
        }
        return result;
    }

    boolean isSwordInInventory() {
        boolean result = false;
        for(Item item : inventory) {
            if (item instanceof Sword) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void removeKey() {
        inventory.removeIf(item -> item instanceof Key);
    }
}


