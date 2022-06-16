package com.codecool.dungeoncrawl.logic.actors;

import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.CellType;
import com.codecool.dungeoncrawl.logic.Drawable;
import com.codecool.dungeoncrawl.logic.items.Item;
import com.codecool.dungeoncrawl.logic.items.Key;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class Actor implements Drawable {
    private Cell cell;
    private int health = 10;
    public Set<Item> inventory = new HashSet<>();

    public Actor(Cell cell) {
        this.cell = cell;
        this.cell.setActor(this);
    }

    public void move(int dx, int dy) {
        Cell nextCell = cell.getNeighbor(dx, dy);
        if (nextCell.getType() == CellType.WALL  || nextCell.getType() == CellType.TORCH || nextCell.getType() == CellType.WINDOW ||
                nextCell.getActor() != null) {
            cell.setActor(this);
        } else if (nextCell.getType() == CellType.CLOSED_DOOR) {
            if (isKeyInInventory()) {
                nextCell.setType(CellType.OPEN_DOOR);
                removeKey();
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

    public int getHealth() {
        return health;
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
                health += 5;
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

    public void removeKey() {
        inventory.removeIf(item -> item instanceof Key);
    }
}
