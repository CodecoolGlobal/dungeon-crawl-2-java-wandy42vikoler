package com.codecool.dungeoncrawl.ai;

public class Node {
    Node parent;
    public int x;
    public int y;
    public int gCost;
    public int hCost;
    public int fCost;
    boolean solid;
    boolean open;
    boolean checked;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }
}