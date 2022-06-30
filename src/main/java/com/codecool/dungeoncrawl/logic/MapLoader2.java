package com.codecool.dungeoncrawl.logic;

import com.codecool.dungeoncrawl.logic.actors.Monster;
import com.codecool.dungeoncrawl.logic.actors.Player;
import com.codecool.dungeoncrawl.logic.actors.Skeleton;
import com.codecool.dungeoncrawl.logic.items.HealthPotion;
import com.codecool.dungeoncrawl.logic.items.Key;
import com.codecool.dungeoncrawl.logic.items.Sword;

import java.io.InputStream;
import java.util.Scanner;

public class MapLoader2 {
    public static GameMap loadMap() {
        InputStream is = MapLoader2.class.getResourceAsStream("/map2.txt");
        Scanner scanner = new Scanner(is);
        int width = scanner.nextInt();
        int height = scanner.nextInt();

        scanner.nextLine(); // empty line

        GameMap map = new GameMap(width, height, CellType.EMPTY);
        for (int y = 0; y < height; y++) {
            String line = scanner.nextLine();
            for (int x = 0; x < width; x++) {
                if (x < line.length()) {
                    Cell cell = map.getCell(x, y);
                    switch (line.charAt(x)) {
                        case ' ':
                            cell.setType(CellType.EMPTY);
                            break;
                        case '#':
                            cell.setType(CellType.WALL);
                            break;
                        case 'W':
                            cell.setType(CellType.WINDOW);
                            break;
                        case '.':
                            cell.setType(CellType.FLOOR);
                            break;
                        case 'f':
                            cell.setType(CellType.TORCH);
                            break;
                        case 'd':
                            cell.setType(CellType.CLOSED_DOOR);
                            break;
                        case 'o':
                            cell.setType(CellType.OPEN_DOOR);
                            break;
                        case 's':
                            cell.setType(CellType.FLOOR);
                            map.setSkeleton(new Skeleton(cell));
                            break;
                        case '@':
                            cell.setType(CellType.FLOOR);
                            map.setPlayer(new Player(cell));
                            break;
                        case '+':
                            cell.setType(CellType.FLOOR);
                            new Sword(cell);
                            break;
                        case '§':
                            cell.setType(CellType.FLOOR);
                            new Key(cell);
                            break;
                        case 'p':
                            cell.setType(CellType.FLOOR);
                            new HealthPotion(cell);
                            break;
                        case 'm':
                            cell.setType(CellType.FLOOR);
                            map.setMonster(new Monster(cell));
                            break;
                        case 'l':
                            cell.setType(CellType.NEXTLEVELDOOR);
                            break;
                        default:
                            throw new RuntimeException("Unrecognized character: '" + line.charAt(x) + "'");
                    }
                }
            }
        }
        return map;
    }
}
