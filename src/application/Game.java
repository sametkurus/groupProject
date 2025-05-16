
//150123034-Samet KURUŞ-Game.java 


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
	private List<Cell> pathCells;
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
	Pane mapPane;
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
	private WaveManager waveManager;

	public void start(Stage primaryStage) {
		gameCanvas = new Canvas(800, 700); // genişlik x yükseklik
		gc = gameCanvas.getGraphicsContext2D();

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
		mapPane = new Pane();

		root = new BorderPane();
		mapPane.getChildren().add(gameCanvas);
		scene = new Scene(root, 800, 700);
		root.setCenter(mapPane);

		// Add window resize listener
		scene.widthProperty().addListener((obs, oldVal, newVal) -> {
			if (gameMap != null) {
				gameMap.handleResize();
			}
		});

		scene.heightProperty().addListener((obs, oldVal, newVal) -> {
			if (gameMap != null) {
				gameMap.handleResize();
			}
		});

		player = new Player();
		enemies = new ArrayList<>();
		waves = new ArrayList<>();

		waveTimerLabel = new Label("Next Wave: Waiting...");
		moneyLabel = new Label("Money: $" + player.getMoney());
		livesLabel = new Label("Lives: " + player.getLives());
		waveLabel = new Label("Wave: 0/0");

		loadLevel(currentLevel);
		waveManager = new WaveManager(waves, enemies, pathCells, player,root,currentLevel);
		System.out.println("path x " + pathCells.get(0).getCenterX());
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

		if(mapPane==null) {
			mapPane = new BorderPane();
		}

		gameMap = new GameMap(root, decoder);

		pathCells = decoder.getPathCells();
		// Load waves from decoder
		loadWavesFromDecoder(decoder);

		// Reset player for new level (but keep money)
		player.setLives(100000);

		// Start with first wave
		if (!waves.isEmpty()) {
			currentWave = waves.get(0);
			gameRunning = true;
		}

		try {
			setUI();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		startGameLoop();
		startWaveTimer();

		// Debug output
		System.out.println("Level " + currentLevel + " loaded with " + waves.size() + " waves");
		for (int i = 0; i < waves.size(); i++) {
			Wave wave = waves.get(i);
			System.out.println("Wave " + (i+1) + ": " + wave.getEnemyCount() + " enemies, " 
					+ wave.getTimeBetweenEnemies() + "s delay, " + wave.getDelayBeforeStart() + "s before start");
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
		mapPane = new Pane();
		towerPane = new VBox(10);
		statsPane = new HBox(10);

		// Configure map pane (center area)
		mapPane.setStyle("-fx-background-color: #222;");

		// Configure tower selection panel (right side)
		towerPane.setPrefWidth(150);
		towerPane.setPadding(new javafx.geometry.Insets(15));
		towerPane.setStyle("-fx-background-color: #333; -fx-padding: 10;");

		// Add tower selection buttons here
		Button laserTowerBtn = createTowerButton("Laser Tower", 2, 120);
		Button tripleShotTowerBtn = createTowerButton("Triple Shot Tower", 3, 150);
		Button singleShotTowerBtn =createTowerButton("Single Shot Tower", 1, 50);
		Button missileTowerBtn = createTowerButton("Missle Launcher Tower", 4, 200);
		towerPane.getChildren().addAll(
				new Label("Towers:"), 
				singleShotTowerBtn ,
				laserTowerBtn,
				tripleShotTowerBtn, 
				missileTowerBtn
				);

		//To sell towers
		Button sellButton = new Button("Sell");
		sellButton.setPrefWidth(120);
		sellButton.setStyle("-fx-background-color: #cc0000; -fx-text-fill: white;");
		towerPane.getChildren().add(sellButton);

		towerPane.setAlignment(javafx.geometry.Pos.TOP_CENTER);

		// Configure stats panel (bottom area)
		statsPane.setPrefHeight(30);
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
		mapPane.widthProperty().addListener((obs, oldVal, newVal) -> {
			if (gameMap != null) {
				gameMap.handleResize();
			}
		});

		mapPane.heightProperty().addListener((obs, oldVal, newVal) -> {
			if (gameMap != null) {
				gameMap.handleResize();
			}
		});

		root.setCenter(mapPane);
		mapPane.setLayoutX(0);
		mapPane.setLayoutY(0);
		root.setRight(towerPane);
		root.setBottom(statsPane);

		// Make UI components resize with window
		root.prefWidthProperty().bind(scene.widthProperty());
		root.prefHeightProperty().bind(scene.heightProperty());

		// Make map pane fill available space
		mapPane.prefWidthProperty();
		mapPane.prefHeightProperty();

		// Make stats pane span full width
		statsPane.prefWidthProperty().bind(root.widthProperty());

		// Add game map to the map pane
		if (gameMap != null) {
			// This assumes your GameMap class adds its visual elements to the provided pane
			gameMap.addToPane(mapPane);
		}
		setupSellEventHandler(sellButton, mapPane);
		setupEventHandler(singleShotTowerBtn, mapPane);
		setupEventHandler(laserTowerBtn, mapPane);
		setupEventHandler(tripleShotTowerBtn, mapPane);	
		setupEventHandler(missileTowerBtn, mapPane);
	}

	private void setupSellEventHandler(Button sellButton, Pane mapPane) {
		sellButton.setOnDragOver(event -> {
			event.acceptTransferModes(javafx.scene.input.TransferMode.MOVE);
			event.consume();
		});

		sellButton.setOnDragDropped(event -> {
			if (selectedTower != null) {
				// Kuleyi bul ve sil
				for (int row = 0; row < gameMap.getHeight(); row++) {
					for (int col = 0; col < gameMap.getWidth(); col++) {
						Cell cell = gameMap.getCell(row, col);
						if (cell.getTower() == selectedTower) {
							cell.removeTower();
							mapPane.getChildren().remove(selectedTower.getImageView());
							gameMap.getTowers().remove(selectedTower);

							// Fiyatı iade et
							int refund = getTowerRefunds(selectedTower);
							player.setMoney(player.getMoney() + refund);
							updateUI();
							showMessage("Tower sold for $" + refund, mapPane);
							break;
						}
					}
				}
			}
			selectedTower = null;
			event.setDropCompleted(true);
			event.consume();
		});

	}

	private int getTowerRefunds(Towers tower) {
		if (tower instanceof singleShotTower) return 40;
		if (tower instanceof tripleShotTower) return 50;
		if (tower instanceof LaserTower) return 100;
		if (tower instanceof missileLauncherTower) return 120;
		return 0;
	}

	// setupEventHandler method
	private void setupEventHandler(Button towerButton, Pane mapPane) throws FileNotFoundException {
		towerButton.setOnMousePressed(event -> {
			int[] towerInfo = (int[]) towerButton.getUserData();
			int towerType = towerInfo[0];
			int cost = towerInfo[1];

			if (player.getMoney() < cost) {
				showMessage("Not enough money!", mapPane);
				return;
			}

			// Create tower object based on type

			switch (towerType) {
			case 1: 
				selectedTower = new singleShotTower(1,2,mapPane);
				break;
			case 2: selectedTower = new LaserTower(1,2,root,gc); 
			break;
			case 3: 
				selectedTower = new tripleShotTower(1,2,root);

				break;
			case 4: 
				selectedTower = new missileLauncherTower(1,2,gc,root);

				break;
			}




			towerView = selectedTower.getImageView();
			towerView.setFitWidth(40);
			towerView.setFitHeight(40);
			towerView.setOpacity(0.7); // Drag sırasında yarı saydam

			//Get the range indicator for this tower
			rangeIndicator = selectedTower.rangeIndicator;
			;

			Group dragGroup = new Group(rangeIndicator, towerView);
			mapPane.getChildren().add(dragGroup);

			// Harita üzerinde mouse hareketi ile kulesi sürükle
			mapPane.setOnMouseMoved(e -> {
				double mouseX = e.getX();
				double mouseY = e.getY();

				dragGroup.setLayoutX(mouseX- 25 );
				dragGroup.setLayoutY(mouseY - 25);
				rangeIndicator.setCenterX(20); // Ortalanmış şekilde, çünkü grup 25 sola kaydırıldı
				rangeIndicator.setCenterY(20);


			});

			mapPane.setOnMouseClicked(e -> {
				double mouseX = e.getX();
				double mouseY = e.getY();

				// Calculate the map offsets
				double cellSize = gameMap.getCellSize();
				double paneWidth = mapPane.getWidth();
				double paneHeight = mapPane.getHeight();
				double offsetX = (paneWidth - (gameMap.getWidth() * cellSize)) / 2;
				double offsetY = (paneHeight - (gameMap.getHeight() * cellSize)) / 2;

				if (offsetX < 0) offsetX = 0;
				if (offsetY < 0) offsetY = 0;

				// Adjust mouse coordinates based on offsets
				double adjustedX = mouseX - offsetX;
				double adjustedY = mouseY - offsetY;

				// Calculate grid cell from adjusted coordinates
				int col = (int) (adjustedX / cellSize);
				int row = (int) (adjustedY / cellSize);

				System.out.println("Mouse at: " + mouseX + "," + mouseY);
				System.out.println("Adjusted to: " + adjustedX + "," + adjustedY);
				System.out.println("Trying to place at cell: " + row + "," + col);

				// Make sure we're getting the cell with the correct row/col
				Cell targetCell = gameMap.getCell(row, col);
				if (targetCell == null) {
					showMessage("Invalid cell position!", mapPane);
					mapPane.getChildren().remove(dragGroup);
					mapPane.setOnMouseMoved(null);
					mapPane.setOnMouseClicked(null);
					return;
				}

				if (targetCell.isPath()) {
					showMessage("Cannot place tower on the path!", mapPane);
				}
				else if (gameMap.isValidPlacement(row, col)) {

					// Kule nesnesi oluştur
					switch (towerType) {
					case 1: 
						selectedTower = new singleShotTower(col*gameMap.getCellSize(),row*gameMap.getCellSize(),enemies,mapPane);
						break;
					case 2: 
						selectedTower = new LaserTower(col* gameMap.getCellSize(),row* gameMap.getCellSize(),enemies, gc,root);

						break;
					case 3: 
						selectedTower = new tripleShotTower(col* gameMap.getCellSize(),row* gameMap.getCellSize(),enemies,root);

						break;
					case 4: 
						selectedTower = new missileLauncherTower(col* gameMap.getCellSize(),row* gameMap.getCellSize(),enemies,gc,root);

						break;
					}

					// After tower creation, get the proper cell coordinates
					double cellCenterX = targetCell.getCenterX();
					double cellCenterY = targetCell.getCenterY();

					selectedTower.getImageView().setLayoutX(cellCenterX );
					selectedTower.getImageView().setLayoutY(cellCenterY );

					boolean placed = true;
					if (placed) {
						player.setMoney(player.getMoney() - cost);
						updateUI();
						showMessage("Tower placed!", mapPane);
						selectedTower.update(1.0);  // Initialize tower
					} else {
						showMessage("Placement failed!", mapPane);
					}
				} else {
					showMessage("Invalid placement location!", mapPane);

				}
				// Make sure the tower position is calculated correctly

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

	private void startWaveTimer() {
		waveStartTime = System.nanoTime();
		isWaveActive = false;

		waveTimer = new AnimationTimer() {
			private long lastUpdate = System.nanoTime();

			@Override
			public void handle(long now) {
				if (!gameRunning) return;

				// Doğru bir delta hesaplayalım
				double deltaTime = (now - lastUpdate) / 1_000_000_000.0; // nanosecondsdan saniyeye çevir
				lastUpdate = now;

				try {
					// Delta time'ı doğrudan kullan - 60'a bölmeyi kaldırdık
					waveManager.update(deltaTime);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Wave timer UI güncellemesi
				if (waveManager.isWaveActive()) {
					int currentWaveIdx = waveManager.getCurrentWaveIndex();
					if (currentWaveIdx < waves.size()) {
						Wave activeWave = waves.get(currentWaveIdx);
						int totalEnemies = activeWave.getEnemyCount();
						int spawnedEnemies = activeWave.getSpawnedEnemies();
						waveTimerLabel.setText(String.format("Wave Progress: %d/%d enemies", spawnedEnemies, totalEnemies));
					}
				} else {
					// Dalga aktif değilse bekleme süresini göster
					double timeUntilNextWave = waveManager.getCountdownToNextWave();
					if (timeUntilNextWave > 0) {
						waveTimerLabel.setText(String.format("Next Wave: %.1fs", timeUntilNextWave));
					}
				}

				// Check for level completion
				if (waveManager.allWavesCompleted() && enemies.isEmpty()) {
					levelComplete();
				}

				updateUI();
			}
		};
		waveTimer.start();
	}

	private void startGameLoop() {
		gameLoop = new AnimationTimer() {
			private long lastUpdate = 0;

			@Override
			public void handle(long now) {
				if (!gameRunning) return;

				// Calculate time delta for smooth movement regardless of frame rate
				double deltaTime = (lastUpdate == 0) ? 0 : (now - lastUpdate) / 1_000_000_000.0;
				lastUpdate = now;

				// Process each enemy
				List<Enemy> deadEnemies = new ArrayList<>();
				for (Enemy enemy : new ArrayList<>(enemies)) {
					// Move enemy along path
					enemy.step();

					// Check if enemy is dead
					if (!enemy.isAlive()) {
						deadEnemies.add(enemy);

						// Check if the enemy died because it reached the end
						if (enemy.getStepIndex() >= gameMap.getPath().size() - 1) {
							// Enemy reached the end
							// Note: Life is already deducted in Enemy.reachEnd()
							if (player.getLives() <= 0) {
								gameOver();
							}
						} else {
							// Enemy was killed by tower
							player.addMoney(10); // Reward for killing enemy
						}
					}
				}

				// Remove dead enemies from game
				for (Enemy enemy : deadEnemies) {
					mapPane.getChildren().remove(enemy);
					enemies.remove(enemy);
				}

				// Update towers to target and attack enemies
				gameMap.update(enemies);

				// Update UI
				updateUI();
			}
		};
		gameLoop.start();
	}

	// gameOver
	private void gameOver() {
		gameRunning = false;

		// Stop all timers
		if (gameLoop != null) gameLoop.stop();
		if (waveTimer != null) waveTimer.stop();

		// Create game over overlay
		VBox gameOverBox = new VBox(20);
		gameOverBox.setAlignment(Pos.CENTER);
		gameOverBox.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 30px;");

		Label gameOverLabel = new Label("GAME OVER");
		gameOverLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: red; -fx-font-weight: bold;");

		Button restartButton = new Button("Restart Level");
		restartButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px 20px;");
		restartButton.setOnAction(e -> {
			mapPane.getChildren().remove(gameOverBox);
			loadLevel(currentLevel);
		});

		Button mainMenuButton = new Button("Main Menu");
		mainMenuButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px 20px;");
		mainMenuButton.setOnAction(e -> {
			try {
				start(stage); // Go back to start screen
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		gameOverBox.getChildren().addAll(gameOverLabel, restartButton, mainMenuButton);

		// Position in center of map pane
		gameOverBox.layoutXProperty().bind(mapPane.widthProperty().divide(2).subtract(gameOverBox.widthProperty().divide(2)));
		gameOverBox.layoutYProperty().bind(mapPane.heightProperty().divide(2).subtract(gameOverBox.heightProperty().divide(2)));

		mapPane.getChildren().add(gameOverBox);
	}

	// levelComplete
	private void levelComplete() {
		gameRunning = false;

		// Stop all timers
		if (gameLoop != null) gameLoop.stop();
		if (waveTimer != null) waveTimer.stop();

		// Calculate how many enemies reached the end
		int startingLives = 5; // Initial lives value
		int livesLost = startingLives - player.getLives();

		// Create level complete overlay
		VBox levelCompleteBox = new VBox(20);
		levelCompleteBox.setAlignment(Pos.CENTER);
		levelCompleteBox.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 30px;");

		Label levelCompleteLabel = new Label("LEVEL " + currentLevel + " COMPLETE!");
		levelCompleteLabel.setStyle("-fx-font-size: 36px; -fx-text-fill: green; -fx-font-weight: bold;");

		Label statisticsLabel = new Label("Enemies that reached the end: " + livesLost);
		statisticsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

		Button nextButton = null;

		if (currentLevel < 5) {
			// There are more levels available
			if (livesLost < 5) {
				// Player performed well enough to advance
				nextButton = new Button("Next Level");
				nextButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px 20px;");
				nextButton.setOnAction(e -> {
					mapPane.getChildren().remove(levelCompleteBox);
					currentLevel++;
					loadLevel(currentLevel);
				});
			} else {
				// Too many enemies got through, must retry
				statisticsLabel.setText("Too many enemies reached the end! Try again.");
				statisticsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #ffcc00;");

				nextButton = new Button("Retry Level");
				nextButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px 20px;");
				nextButton.setOnAction(e -> {
					mapPane.getChildren().remove(levelCompleteBox);
					loadLevel(currentLevel);
				});
			}
		} else {
			// This was the final level
			levelCompleteLabel.setText("CONGRATULATIONS! GAME COMPLETE!");
			levelCompleteLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: gold; -fx-font-weight: bold;");

			nextButton = new Button("Play Again");
			nextButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px 20px;");
			nextButton.setOnAction(e -> {
				mapPane.getChildren().remove(levelCompleteBox);
				currentLevel = 1;
				loadLevel(currentLevel);
			});
		}

		Button mainMenuButton = new Button("Main Menu");
		mainMenuButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px 20px;");
		mainMenuButton.setOnAction(e -> {
			try {
				start(stage); // Go back to start screen
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		levelCompleteBox.getChildren().addAll(levelCompleteLabel, statisticsLabel, nextButton, mainMenuButton);

		// Position in center of map pane
		levelCompleteBox.layoutXProperty().bind(mapPane.widthProperty().divide(2).subtract(levelCompleteBox.widthProperty().divide(2)));
		levelCompleteBox.layoutYProperty().bind(mapPane.heightProperty().divide(2).subtract(levelCompleteBox.heightProperty().divide(2)));

		mapPane.getChildren().add(levelCompleteBox);
	}

	private void updateUI() {
		// Update money and lives labels
		moneyLabel.setText("Money: $" + player.getMoney());
		livesLabel.setText("Lives: " + player.getLives());
		waveLabel.setText("Wave: " + (currentWave != null ? (waves.indexOf(currentWave) + 1) : "0") + 
				"/" + waves.size());
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
