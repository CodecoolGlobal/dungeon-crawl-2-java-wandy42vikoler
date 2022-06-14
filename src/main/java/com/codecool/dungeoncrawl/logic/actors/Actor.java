package com.codecool.dungeoncrawl.logic.actors;

import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.Drawable;
import com.codecool.dungeoncrawl.logic.items.Item;

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
        if (Objects.equals(nextCell.getTileName(), "wall") || nextCell.getActor() != null){
            cell.setActor(this);
        }
        else {
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
            inventory.add(cell.getItem());
            cell.setItem(null);
        }
    }
}
