package com.codecool.dungeoncrawl;

import com.codecool.dungeoncrawl.dao.GameDatabaseManager;
import com.codecool.dungeoncrawl.ai.Pathfinding;
import com.codecool.dungeoncrawl.dao.PlayerDao;
import com.codecool.dungeoncrawl.dao.PlayerDaoJdbc;
import com.codecool.dungeoncrawl.logic.*;
import com.codecool.dungeoncrawl.logic.actors.Player;
import com.codecool.dungeoncrawl.model.PlayerModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

import java.sql.SQLException;
import javax.sql.DataSource;

import static javafx.application.Platform.exit;

public class Main extends Application {


    GameMap map = MapLoader.loadMap();

    String level = "1";
    Pathfinding pathfinder = new Pathfinding(map);
    Canvas canvas = new Canvas(
            map.getWidth() * Tiles.TILE_WIDTH,
            map.getHeight() * Tiles.TILE_WIDTH);
    GraphicsContext context = canvas.getGraphicsContext2D();
    Label healthLabel = new Label();
    Label inventoryLabel = new Label();

    Label mentorBot = new Label();
    Label xY = new Label();
    GameDatabaseManager dbManager;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setupDbManager();
        GridPane ui = new GridPane();
        ui.setPrefWidth(200);
        ui.setPadding(new Insets(10));

        ui.add(new Label("Health: "), 0, 0);
        ui.add(healthLabel, 1, 0);

        ui.add(new Label("Inventory: "), 0, 1);
        ui.add(inventoryLabel, 1, 1);

        ui.add(new Label("MentorBot: "), 0, 10);
        ui.add(mentorBot, 1, 10);

        ui.add(new Label( "Level:  "), 0, 11);
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

        TextInputDialog saveWithName = new TextInputDialog();
        Label playerName = new Label("enter your Name");
        saveWithName.setHeaderText("Enter your Name:");

        Button saveGameButton = new Button("Save");
        ui.add(saveGameButton, 0, 4);
        saveGameButton.setFocusTraversable(false);
        saveGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveWithName.showAndWait();
                playerName.setText(saveWithName.getEditor().getText());
                String name = playerName.getText();
                GameDatabaseManager savePlayer = new GameDatabaseManager();
                savePlayer.savePlayer(name, level);
            }
        });

        Button loadGameButton = new Button("Load");
        ui.add(loadGameButton, 1, 4);
        loadGameButton.setFocusTraversable(false);
        loadGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            }
        });


        BorderPane borderPane = new BorderPane();

        borderPane.setCenter(canvas);
        borderPane.setRight(ui);

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        refresh();
        scene.setOnKeyPressed(this::onKeyPressed);
        scene.setOnKeyReleased(this::onKeyReleased);

        primaryStage.setTitle("Dungeon Crawl");
        primaryStage.show();
    }

    private void onKeyReleased(KeyEvent keyEvent) {
        KeyCombination exitCombinationMac = new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN);
        KeyCombination exitCombinationWin = new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN);
        if (exitCombinationMac.match(keyEvent)
                || exitCombinationWin.match(keyEvent)
                || keyEvent.getCode() == KeyCode.ESCAPE) {
            exit();
        }
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
        if (map.getPlayer().getPlayerHealth() == 0){
            map = MapLoaderGameOver.loadMap();
            refresh();
        }

        if (map.getPlayer().getX() == 2 && map.getPlayer().getY() == 17){
            map = MapLoader2.loadMap();
            level = "2";
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
        xY.setText("" + level);
    }

    private void setupDbManager() {
        dbManager = new GameDatabaseManager();
        try {
            dbManager.setup();
        } catch (SQLException ex) {
            System.out.println("Cannot connect to database.");
        }
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

