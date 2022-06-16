package com.codecool.dungeoncrawl.ai;

import com.codecool.dungeoncrawl.logic.GameMap;

import java.util.ArrayList;
import java.util.Objects;

public class Pathfinding {
    GameMap map;
    Node[][] node;
    ArrayList<Node> openList = new ArrayList<>();
    public ArrayList<Node> pathList = new ArrayList<>();
    Node startNode, goalNode, currentNode;
    boolean goalReached = false;
    int step = 0;

    public Pathfinding(GameMap map) {
        this.map = map;
        instantiateNodes();
    }

    public void instantiateNodes() {
        node = new Node[map.getHeight()][map.getWidth()];

        int x = 0;
        int y = 0;

        while (x < map.getHeight() && y < map.getWidth()){
            node[x][y] = new Node(x, y);
            x++;
            if (x == map.getHeight()) {
                x = 0;
                y++;
            }
        }
    }
    public void resetNode() {

        int x = 0;
        int y = 0;

        while (x < map.getHeight() && y < map.getWidth()){

            //Reset open, checked and solid state
            node[x][y].open = false;
            node[x][y].checked = false;
            node[x][y].solid = false;

            x++;
            if(x == map.getHeight()){
                x = 0;
                y++;
            }

        }
        //Reset other settings
        openList.clear();
        pathList.clear();
        goalReached = false;
        step = 0;

    }

    public void setNode(int startX, int startY, int goalX, int goalY){
        resetNode();

        //Set start Node
        startNode = node[startX][startY];
        currentNode = startNode;
        goalNode = node[goalX][goalY];
        openList.add(currentNode);

        int x = 0;
        int y = 0;

        while (x < 20 && y < 25){
            //Set solid Node
            //Check tiles
            String tileCheck = map.getCell(x, y).getType().getTileName();
            if(Objects.equals(tileCheck, "wall")){
                node[x][y].solid = true; //not implemented, make work
            }
            //Set cost
            getCost(node[x][y]);

            x++;
            if(x == map.getWidth()){
                x = 0;
                y++;
            }
        }

    }
    public void getCost(Node node){

        //gCost
        int xDistance = Math.abs(node.x - startNode.x);
        int yDistance = Math.abs(node.y - startNode.y);
        node.gCost = xDistance + yDistance;

        //hCost
        xDistance = Math.abs(node.x - goalNode.x);
        yDistance = Math.abs(node.y - goalNode.y);
        node.hCost = xDistance + yDistance;

        //fCost
        node.fCost = node.gCost + node.hCost;
    }
    public boolean search(){
        while(!goalReached && step < 500){
            int x = currentNode.x;
            int y = currentNode.y;

            //Check currentNode
            currentNode.checked = true;
            openList.remove(currentNode);

            //Open up Node
            if ( y - 1 >= 0 ){
                openNode(node[x][y - 1]);
            }
            //Open left node
            if (x - 1 >= 0){
                openNode(node[x - 1][y]);
            }
            //open down node
            if(y + 1 < map.getWidth()){
                openNode(node[x][y+1]);
            }
            //open right node
            if(x + 1 < map.getHeight()){
                openNode(node[x+1][y]);
            }

            //Find best node
            int bestNodeIndex = 0;
            int bestNodefCost = 999;

            for (int i = 0; i < openList.size(); i++){
                //check better node f cost
                if (openList.get(i).fCost < bestNodefCost){
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).fCost;
                }
                else if(openList.get(i).fCost == bestNodefCost){
                    if(openList.get(i).gCost < openList.get(bestNodeIndex).gCost){
                        bestNodeIndex = i;
                    }

                }
            }
            if(openList.size() == 0) {
                break;
            }
            currentNode = openList.get(bestNodeIndex);
            if(currentNode == goalNode){
                goalReached = true;
                trackThePath();
            }
            step++;

        }
        return goalReached;
    }
    public void openNode(Node node){
        if (!node.open && !node.checked && !node.solid){
            node.open = true;
            node.parent = currentNode;
            openList.add(node);

        }
    }
    public void trackThePath(){
        Node current = goalNode;

        while (current != startNode){
            pathList.add(0, current);
            current = current.parent;
        }
    }
}
