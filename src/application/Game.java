package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
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
import javafx.util.Duration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.animation.ScaleTransition;
import javafx.scene.text.FontWeight;

public class Game extends Application{
	// Game state variables
	private Stage stage;
	private Scene scene;
	private BorderPane root;
	private GameMap gameMap;
	private Player player;
	private List<Enemy> enemies;
	private List<Wave> waves;
	private Wave currentWave;
	private AnimationTimer gameLoop;
	private AnimationTimer waveTimer;
	private int currentLevel = 5;
	private boolean gameRunning = false;
	private Canvas gameCanvas;
	private GraphicsContext gc;

	// UI elements
	private Label waveTimerLabel;
	private Label moneyLabel;
	private Label livesLabel;
	private Label waveLabel;
	private Pane mapPane;
	private VBox towerPane;
	private HBox statsPane;

	// Tower selection
	private Towers selectedTower;
	private boolean isDraggingTower = false;
	private Circle rangeIndicator;

	// Game timers
	private long waveStartTime;
	private boolean isWaveActive = false;

	private ImageView towerView;

	public void start(Stage primaryStage) {
		Text title = new Text("Tower Defense Game");
		title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-fill: linear-gradient(from 0% 0% to 100% 200%,"
				+ " repeat, #ff8a00 0%, #ff2b6d 50%);");

		// Apply glow effect to title
		Glow glow = new Glow();
		glow.setLevel(0.8);
		title.setEffect(glow);

		// Create start button with hover effect
		Button startButton = new Button("Start Game");
		startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding:"
				+ " 15 30; -fx-background-radius: 5;");
		startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-background-color: #3e8e41; -fx-text-fill: white;"
				+ " -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 5;"));
		startButton.setOnMouseExited(e -> startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"
				+ " -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 5;"));
		startButton.setOnAction(e -> {
			try {
				startGame(primaryStage);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		});

		// Exit button
		Button exitButton = new Button("Exit");
		exitButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding:"
				+ " 15 30; -fx-background-radius: 5;");
		exitButton.setOnMouseEntered(e -> exitButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white;"
				+ " -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 5;"));
		exitButton.setOnMouseExited(e -> exitButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;"
				+ " -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 5;"));
		exitButton.setOnAction(e -> System.exit(0));

		// Layout
		VBox layout = new VBox(50);
		layout.setStyle("-fx-alignment: center; -fx-padding: 40; -fx-background-color: linear-gradient(from 0% 0% to "
				+ "100% 100%, #121212, #2c3e50);");
		layout.getChildren().addAll(title, startButton,  exitButton);

		// Add a simple animation to the title
		ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), title);
		scaleTransition.setFromX(1.0);
		scaleTransition.setFromY(1.0);
		scaleTransition.setToX(1.1);
		scaleTransition.setToY(1.1);
		scaleTransition.setCycleCount(Timeline.INDEFINITE);
		scaleTransition.setAutoReverse(true);
		scaleTransition.play();

		// Set the scene
		Scene mainMenuScene = new Scene(layout, 1000, 700);

		primaryStage.setScene(mainMenuScene);
		primaryStage.setTitle("Tower Defense Game");
		primaryStage.setResizable(true);
		primaryStage.show();
	}
	private void startGame(Stage primaryStage) throws FileNotFoundException {

		root = new BorderPane();
		scene = new Scene(root, 800, 600);

		player = new Player();
		enemies = new ArrayList<>();
		waves = new ArrayList<>();

		waveTimerLabel = new Label("Next Wave: Waiting...");
		moneyLabel = new Label("Money: $" + player.getMoney());
		livesLabel = new Label("Lives: " + player.getLives());
		waveLabel = new Label("Wave: 0/0");

		loadLevel(currentLevel);
		setUI();

		primaryStage.setScene(scene);
		primaryStage.setTitle("Tower Defense Game");
		primaryStage.show();
	}

	private void loadLevel(int currentLevel) {
		if(root==null) {
			root = new BorderPane();
		}
		if(mapPane!= null) {
			mapPane.getChildren().clear();
		}

		enemies.clear();
		waves.clear();

		// Load map
		File levelFile = new File("C:\\Users\\Simit\\eclipse-workspace\\TowerDefenceGame\\src\\application\\MapLevel-" 
		+ currentLevel );
		TextDecoder decoder = new TextDecoder(levelFile);
		gameMap = new GameMap(root, decoder);

		// Load waves from decoder
		loadWavesFromDecoder(decoder);

		// Reset player for new level (but keep money)
		player.setLives(5);

		// Start with first wave
		if (!waves.isEmpty()) {
			currentWave = waves.get(0);
			gameRunning = true;
		}
	}

	private void loadWavesFromDecoder(TextDecoder decoder) {
		int[] enemyCounts = decoder.getEnemyCountPerWave();
		double[] spawnDelays = decoder.getEnemySpawnDelayPerWave();
		int[] waveDelays = decoder.getDelayToOtherWave();
		for (int i = 0; i < enemyCounts.length; i++) {
			waves.add(new Wave(enemyCounts[i], spawnDelays[i], (double) waveDelays[i]));
		}

	}

	private void setUI() throws FileNotFoundException{
		Pane mapPane = new Pane();
		VBox towerPane = new VBox(10);
		HBox statsPane = new HBox(20);

		// Configure map pane (center area)
		mapPane.setStyle("-fx-background-color: #222;");

		// Configure tower selection panel (right side)
		towerPane.setPrefWidth(150);
		towerPane.setPadding(new javafx.geometry.Insets(15));
		towerPane.setStyle("-fx-background-color: #333; -fx-padding: 10;");

		// Add tower selection buttons here
		Button laserTowerBtn = createTowerButton("Laser Tower", 2, 200);
		Button tripleShotTowerBtn = createTowerButton("Triple Shot Tower", 3, 150);
		Button singleShotTowerBtn =createTowerButton("Single Shot Tower", 1, 100);
		Button missileTowerBtn = createTowerButton("Missle Launcher Tower", 4, 300);
		towerPane.getChildren().addAll(
				new Label("Towers:"), 
				singleShotTowerBtn ,
				tripleShotTowerBtn, 
				missileTowerBtn, 
				laserTowerBtn);


		towerPane.setAlignment(javafx.geometry.Pos.TOP_CENTER);

		// Configure stats panel (bottom area)
		statsPane.setPrefHeight(60);
		statsPane.setStyle("-fx-background-color: #444; -fx-padding: 10;");
		statsPane.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

		// Add player stats display
		moneyLabel.setText("Money: $" + player.getMoney());
		moneyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

		livesLabel.setText("Lives: " + player.getLives());
		livesLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

		waveLabel.setText("Wave: " + (currentWave != null ? (waves.indexOf(currentWave) + 1) : "0") + 
				"/" + waves.size());
		waveLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

		waveTimerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

		statsPane.getChildren().addAll(moneyLabel, livesLabel, waveLabel, waveTimerLabel);

		// Set up the layout structure with BorderPane
		root.setCenter(mapPane);
		root.setRight(towerPane);
		root.setBottom(statsPane);

		// Make UI components resize with window
		root.prefWidthProperty().bind(scene.widthProperty());
		root.prefHeightProperty().bind(scene.heightProperty());

		// Make map pane fill available space
		mapPane.prefWidthProperty().bind(root.widthProperty().subtract(towerPane.getPrefWidth()));
		mapPane.prefHeightProperty().bind(root.heightProperty().subtract(statsPane.getPrefHeight()));

		// Make stats pane span full width
		statsPane.prefWidthProperty().bind(root.widthProperty());

		// Add game map to the map pane
		if (gameMap != null) {
			// This assumes your GameMap class adds its visual elements to the provided pane
			gameMap.addToPane(mapPane);
		}

		setupEventHandler(singleShotTowerBtn, mapPane);
		setupEventHandler(laserTowerBtn, mapPane);
		setupEventHandler(tripleShotTowerBtn, mapPane);
		setupEventHandler(missileTowerBtn, mapPane);
	}

	private void setupEventHandler(Button towerButton, Pane mapPane) throws FileNotFoundException{
	    towerButton.setOnMousePressed(event -> {
	        int[] towerInfo = (int[]) towerButton.getUserData();
	        int towerType = towerInfo[0];
	        int cost = towerInfo[1];

	        if (player.getMoney() < cost) {
	            showMessage("Not enough money!", mapPane);
	            return;
	        }

	        // Kule nesnesi oluştur
	        switch (towerType) {
	            case 1: 
					selectedTower = new singleShotTower(,,root);
				
	            break;
	            case 2: selectedTower = new LaserTower(); 
	            break;
	            case 3: try {
					selectedTower = new tripleShotTower();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	            break;
	            case 4: try {
					selectedTower = new missileLauncherTower();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	            break;
	        }

	        towerView = selectedTower.getImageView();
	        towerView.setFitWidth(50);
	        towerView.setFitHeight(50);
	        towerView.setOpacity(0.7); // Drag sırasında yarı saydam

	        // Menzil gösterimi (range indicator)
	        rangeIndicator = new Circle(selectedTower.getRange());
	        rangeIndicator.setStroke(Color.RED);
	        rangeIndicator.setFill(Color.rgb(255, 0, 0, 0.2));
	        rangeIndicator.setVisible(true);

	        Group dragGroup = new Group(rangeIndicator, towerView);
	        mapPane.getChildren().add(dragGroup);

	        // Harita üzerinde mouse hareketi ile kulesi sürükle
	        mapPane.setOnMouseMoved(e -> {
	            double mouseX = e.getX();
	            double mouseY = e.getY();

	            dragGroup.setLayoutX(mouseX - 25);
	            dragGroup.setLayoutY(mouseY - 25);

	            rangeIndicator.setLayoutX(25);
	            rangeIndicator.setLayoutY(25);
	        });

	        mapPane.setOnMouseClicked(e -> {
	            double mouseX = e.getX();
	            double mouseY = e.getY();

	            int row = (int) (mouseY / gameMap.getCellSize());
	            int col = (int) (mouseX / gameMap.getCellSize());

	            if (gameMap.isValidPlacement(row, col)) {
	                if (player.getMoney() >= cost) {
	                    boolean placed = gameMap.placeTower(selectedTower, row, col);
	                    if (placed) {
	                        player.setMoney(player.getMoney() - cost);
	                        updateUI();
	                        showMessage("Tower placed!", mapPane);
	                    } else {
	                        showMessage("Placement failed!", mapPane);
	                    }
	                } else {
	                    showMessage("Not enough money!", mapPane);
	                }
	            } else {
	                showMessage("Invalid placement!", mapPane);
	            }

	            mapPane.getChildren().remove(dragGroup);
	            mapPane.setOnMouseMoved(null);
	            mapPane.setOnMouseClicked(null);
	        });

	        event.consume();
	    });
	}



	private Button createTowerButton(String towerName, int towerType, int cost) throws FileNotFoundException{

		// Create a tower to drag
		switch (towerType) {
		case 1: // Single Shot Tower
			selectedTower = new singleShotTower();
			towerView = selectedTower.getImageView();
			towerView.setFitWidth(50);
			towerView.setFitHeight(50);
			break;
		case 2: // Laser Tower
			selectedTower = new LaserTower();
			towerView = selectedTower.getImageView();
			towerView.setFitWidth(50);
			towerView.setFitHeight(50);
			break;
		case 3: // Triple Shot Tower
			selectedTower = new tripleShotTower();
			towerView = selectedTower.getImageView();
			towerView.setFitWidth(50);
			towerView.setFitHeight(50);
			break;
		case 4: // Missile Launcher Tower
			selectedTower = new missileLauncherTower();
			towerView = selectedTower.getImageView();
			towerView.setFitWidth(50);
			towerView.setFitHeight(50);
			break;
		}

		// Create a snapshot group for the tower view
		Group snapshotGroup = new Group(towerView);

		// Create a VBox to hold tower image and cost
		VBox buttonContent = new VBox(5);
		buttonContent.setAlignment(Pos.CENTER);
		buttonContent.getChildren().addAll(snapshotGroup);

		// Create cost label
		Label costLabel = new Label("$" + cost);
		costLabel.setStyle("-fx-text-fill: gold;");
		buttonContent.getChildren().add(costLabel);

		// Create the button with the content
		Button towerButton = new Button(towerName);
		towerButton.setGraphic(buttonContent);
		towerButton.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
		towerButton.setPrefWidth(120);
		towerButton.setPrefHeight(80);
		towerButton.setStyle("-fx-background-color: #555; -fx-text-fill: white;");

		// Add hover effect
		towerButton.setOnMouseEntered(e -> towerButton.setStyle("-fx-background-color: #666; -fx-text-fill: white;"));
		towerButton.setOnMouseExited(e -> towerButton.setStyle("-fx-background-color: #555; -fx-text-fill: white;"));

		// Store tower type and cost in button's user data for later reference
		towerButton.setUserData(new int[]{towerType, cost});

		return towerButton;

	}
	private void updateUI() {
		// Update money and lives labels
		moneyLabel.setText("Money: $" + player.getMoney());
		livesLabel.setText("Lives: " + player.getLives());
		waveLabel.setText("Wave: " + (currentWave != null ? (waves.indexOf(currentWave) + 1) : "0") + 
				"/" + waves.size());

		if (currentWave != null) {
			if (!isWaveActive && waves.indexOf(currentWave) == 0) {
				waveTimerLabel.setText("First Wave Starting...");
			} else if (!isWaveActive) {
				// Wave başlamadan önceki süreyi göster
				long currentTime = System.nanoTime();
				double remainingTime = currentWave.getDelayBeforeStart() - 
						((currentTime - waveStartTime) / 1_000_000_000.0);

				if (remainingTime > 0) {
					waveTimerLabel.setText(String.format("Next Wave: %.1fs", remainingTime));
				} else {
					waveTimerLabel.setText("Wave In Progress");
					isWaveActive = true;
				}
			} else {
				// Wave aktifse progress göster
				int totalEnemies = currentWave.getEnemyCount();
				int killedEnemies = currentWave.getSpawnedEnemies() - enemies.size();
				waveTimerLabel.setText(String.format("Enemies: %d/%d", killedEnemies, totalEnemies));
			}
		}
	}

	private void showMessage(String message, Pane pane) {
		Label messageLabel = new Label(message);
		messageLabel.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-text-fill: white; -fx-padding: 10px;");

		pane.getChildren().add(messageLabel);
		messageLabel.setLayoutX(pane.getWidth() / 2 - 50);
		messageLabel.setLayoutY(pane.getHeight() / 2 - 20);

		// Fade out animation
		javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
				javafx.util.Duration.millis(1500), messageLabel);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.setOnFinished(e -> pane.getChildren().remove(messageLabel));
		fadeOut.play();
	}

	public static void main (String args[]) {
		Application.launch(args);
	}

}
