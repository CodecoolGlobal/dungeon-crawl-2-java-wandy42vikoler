package com.codecool.dungeoncrawl;

import com.codecool.dungeoncrawl.ai.Pathfinding;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.GameMap;
import com.codecool.dungeoncrawl.logic.MapLoader;
import com.codecool.dungeoncrawl.logic.MapLoader2;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class Main extends Application {

    GameMap map = MapLoader.loadMap();
    Pathfinding pathfinder = new Pathfinding(map);
    Canvas canvas = new Canvas(
            map.getWidth() * Tiles.TILE_WIDTH,
            map.getHeight() * Tiles.TILE_WIDTH);
    GraphicsContext context = canvas.getGraphicsContext2D();
    Label healthLabel = new Label();
    Label inventoryLabel = new Label();

    Label mentorBot = new Label();
    Label xY = new Label();
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane ui = new GridPane();
        ui.setPrefWidth(200);
        ui.setPadding(new Insets(10));

        ui.add(new Label("Health: "), 0, 0);
        ui.add(healthLabel, 1, 0);

        ui.add(new Label("Inventory: "), 0, 1);
        ui.add(inventoryLabel, 1, 1);

        ui.add(new Label("MentorBot: "), 0, 10);
        ui.add(mentorBot, 1, 10);

        ui.add(new Label( "X - Y"), 0, 11);
        ui.add(xY, 1, 11);

        Button button = new Button("Pick up item");
        ui.add(button, 0, 2);
        button.setFocusTraversable(false);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                map.getPlayer().addItem();
                refresh();
            }
        });

        Button buttonRestart = new Button("Restart");
        ui.add(buttonRestart, 0, 3);
        buttonRestart.setFocusTraversable(false);
        buttonRestart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
                Platform.runLater(() -> {
                    try {
                        new Main().start(new Stage());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });

        Button buttonExit = new Button("Exit");
        ui.add(buttonExit, 1, 3);
        buttonExit.setFocusTraversable(false);
        buttonExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });


        BorderPane borderPane = new BorderPane();

        borderPane.setCenter(canvas);
        borderPane.setRight(ui);

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        refresh();
        scene.setOnKeyPressed(this::onKeyPressed);

        primaryStage.setTitle("Dungeon Crawl");
        primaryStage.show();
    }

    private void onKeyPressed(KeyEvent keyEvent) {

        switch (keyEvent.getCode()) {
            case UP:
                map.getPlayer().move(0, -1);
                refresh();
                break;
            case DOWN:
                map.getPlayer().move(0, 1);
                refresh();
                break;
            case LEFT:
                map.getPlayer().move(-1, 0);
                refresh();
                break;
            case RIGHT:
                map.getPlayer().move(1,0);
                refresh();
                break;
        }
        if (map.getPlayer().getMentorBotHealth() > 0){
            searchPath(map.getPlayer().getX(), map.getPlayer().getY());

        } else {
            map.setMonster(null);
        }

        if (map.getPlayer().getX() == 2 && map.getPlayer().getY() == 17){
            map = MapLoader2.loadMap();
            refresh();
        }
    }

    public void searchPath(int goalX, int goalY){

        int startX = map.getMonster().getX();
        int startY = map.getMonster().getY();
        pathfinder.setNode(startX, startY, goalX, goalY);

        if(pathfinder.search()){
            int nextX = pathfinder.pathList.get(0).x;
            int nextY = pathfinder.pathList.get(0).y;

            map.getMonster().moveMonster(nextX, nextY);
        }
    }

    private void refresh() {
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Cell cell = map.getCell(x, y);
                if (cell.getActor() != null) {
                    Tiles.drawTile(context, cell.getActor(), x, y);
                } else if (cell.getItem() != null){
                    Tiles.drawTile(context, cell.getItem(), x, y);
                } else {
                    Tiles.drawTile(context, cell, x, y);
                }
            }
        }
        healthLabel.setText("" + map.getPlayer().getPlayerHealth());
        inventoryLabel.setText("" + map.getPlayer().getInventory());
        mentorBot.setText("" + map.getPlayer().getMentorBotHealth());
        xY.setText("" + map.getPlayer().getX() + " - " + map.getPlayer().getY());
    }

    public static void playSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("dungeonmusic.wav").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch(Exception e) {
            System.out.println("Error with playing sound.");
            e.printStackTrace();
        }
    }
}

