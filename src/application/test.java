package application;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class test extends Application {
	private Stage stage;
	private Scene scene;
	private Pane root;
	private GameMap gameMap;
	private Player player;
	private List<Enemy> enemies;
	private List<Wave> waves;
	private Wave currentWave;
	private AnimationTimer gameLoop;
	private AnimationTimer waveTimer;
	private int currentLevel = 1;
	private boolean gameRunning = false;

	// Tower selection UI
	private Towers selectedTower;
	private boolean isDraggingTower = false;

	@Override
	public void start(Stage primaryStage) {
		Text title = new Text("Tower Defense Game");
		title.setStyle("-fx-font-size: 24px;");

		Button startButton = new Button("Start Game");
		startButton.setOnAction(e -> startGame(primaryStage));

		VBox layout = new VBox(80);
		layout.setStyle("-fx-alignment: center; -fx-padding: 40;");
		layout.getChildren().addAll(title, startButton);

		Scene mainMenuScene = new Scene(layout, 800, 600);
		primaryStage.setScene(mainMenuScene);
		primaryStage.setTitle("Tower Defence Game");
		primaryStage.show();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Application.launch(args);
	}
	
	private void startGame(Stage primaryStage) {
        root = new Pane();
        root.setPrefWidth(800);
        root.setPrefHeight(600);
        scene = new Scene(root, 800, 600);
        
        player = new Player();
        enemies = new ArrayList<>();
        waves = new ArrayList<>();
        
        Pane mapPane = new Pane();
        mapPane.setPrefWidth(root.getPrefWidth());
        mapPane.setPrefHeight(root.getPrefHeight());

        loadLevel(currentLevel);
        setUI();

        root.getChildren().add(mapPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Tower Defense Game");
        primaryStage.show();
        
        
    }
	
	private void initializeGame() {
		root = new Pane();
		scene = new Scene(root, 800, 600);

		// Initialize game objects
		player = new Player();
		enemies = new ArrayList<>();
		waves = new ArrayList<>();

		loadLevel(currentLevel);

		//		setUI();
	}

	private void loadLevel(int level) {
		// Clear previous level
		root.getChildren().clear();
		enemies.clear();
		waves.clear();

		// Load map
		File levelFile = new File("C:\\Users\\Simit\\eclipse-workspace\\TowerDefenceGame\\src\\application\\MapLevel-" + level );
		TextDecoder decoder = new TextDecoder(levelFile);
		gameMap = new GameMap(root, decoder);
		gameMap.initializeFromDecoder();
		// Load waves from decoder
		//	        loadWavesFromDecoder(decoder);

		// Reset player for new level (but keep money)
		player.setLives(5);

		// Start with first wave
		/*	        if (!waves.isEmpty()) {
	            currentWave = waves.get(0);
	            gameRunning = true;
	        }
	    }

	 private void loadWavesFromDecoder(TextDecoder decoder) {
	        int[] enemyCounts = decoder.getEnemyCountPerWave();
	        double[] spawnDelays = decoder.getEnemySpawnDelayPerWave();
	        int[] waveDelays = decoder.getDelayToOtherWave();

	        if (enemyCounts != null && spawnDelays != null && waveDelays != null &&
	            enemyCounts.length == spawnDelays.length && enemyCounts.length == waveDelays.length) {
	            for (int i = 0; i < enemyCounts.length; i++) {
	                waves.add(new Wave(enemyCounts[i], spawnDelays[i], (double) waveDelays[i]));
	            }
	        } else {
	            System.err.println("Error: Wave data arrays from decoder have inconsistent lengths or are null.");
	        }
	    }
		 **/
	}
}
