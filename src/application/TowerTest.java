package application;

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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.File;
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

public class test extends Application {
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
	private int currentLevel = 1;
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

	@Override
	public void start(Stage primaryStage) {
		this.stage = primaryStage;

		// Create main menu
		displayMainMenu();

		// Set up the stage
		primaryStage.setTitle("Tower Defense Game");
		primaryStage.setResizable(true);
		primaryStage.show();
	}

	private void displayMainMenu() {
		// Creating title with enhanced visual effect
		Text title = new Text("Tower Defense Game");
		title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-fill: linear-gradient(from 0% 0% to 100% 200%, repeat, #ff8a00 0%, #ff2b6d 50%);");

		// Apply glow effect to title
		Glow glow = new Glow();
		glow.setLevel(0.8);
		title.setEffect(glow);

		// Create start button with hover effect
		Button startButton = new Button("Start Game");
		startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 5;");
		startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-background-color: #3e8e41; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 5;"));
		startButton.setOnMouseExited(e -> startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 5;"));
		startButton.setOnAction(e -> startGame());

		// Create how to play button
		Button howToPlayButton = new Button("How to Play");
		howToPlayButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 5;");
		howToPlayButton.setOnMouseEntered(e -> howToPlayButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 5;"));
		howToPlayButton.setOnMouseExited(e -> howToPlayButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 5;"));
		howToPlayButton.setOnAction(e -> showHowToPlay());

		// Exit button
		Button exitButton = new Button("Exit");
		exitButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 5;");
		exitButton.setOnMouseEntered(e -> exitButton.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 5;"));
		exitButton.setOnMouseExited(e -> exitButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 15 30; -fx-background-radius: 5;"));
		exitButton.setOnAction(e -> System.exit(0));

		// Layout
		VBox layout = new VBox(50);
		layout.setStyle("-fx-alignment: center; -fx-padding: 40; -fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #121212, #2c3e50);");
		layout.getChildren().addAll(title, startButton, howToPlayButton, exitButton);

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
		stage.setScene(mainMenuScene);
	}

	private void showHowToPlay() {
		VBox instructions = new VBox(20);
		instructions.setStyle("-fx-padding: 40; -fx-background-color: #333; -fx-alignment: center;");

		Text title = new Text("How to Play");
		title.setStyle("-fx-font-size: 36px; -fx-fill: white; -fx-font-weight: bold;");

		VBox content = new VBox(15);
		content.setStyle("-fx-alignment: center-left;");

		Text line1 = new Text("• Place towers on the yellow cells to defend against enemy waves");
		Text line2 = new Text("• Each tower has different abilities and costs");
		Text line3 = new Text("• Prevent enemies from reaching the end of the path");
		Text line4 = new Text("• Destroy enemies to earn money for more towers");
		Text line5 = new Text("• You lose a life when an enemy reaches the end");
		Text line6 = new Text("• Game over when you lose 5 lives");

		line1.setStyle("-fx-font-size: 18px; -fx-fill: white;");
		line2.setStyle("-fx-font-size: 18px; -fx-fill: white;");
		line3.setStyle("-fx-font-size: 18px; -fx-fill: white;");
		line4.setStyle("-fx-font-size: 18px; -fx-fill: white;");
		line5.setStyle("-fx-font-size: 18px; -fx-fill: white;");
		line6.setStyle("-fx-font-size: 18px; -fx-fill: white;");

		content.getChildren().addAll(line1, line2, line3, line4, line5, line6);

		Text towerTitle = new Text("\nTowers:");
		towerTitle.setStyle("-fx-font-size: 24px; -fx-fill: white; -fx-font-weight: bold;");

		Text tower1 = new Text("Single Shot Tower ($50): Shoots a single bullet at enemies");
		Text tower2 = new Text("Laser Tower ($120): Continuous beam that damages enemies");
		Text tower3 = new Text("Triple Shot Tower ($150): Targets up to three enemies at once");
		Text tower4 = new Text("Missile Launcher Tower ($200): Area damage to multiple enemies");

		tower1.setStyle("-fx-font-size: 18px; -fx-fill: white;");
		tower2.setStyle("-fx-font-size: 18px; -fx-fill: white;");
		tower3.setStyle("-fx-font-size: 18px; -fx-fill: white;");
		tower4.setStyle("-fx-font-size: 18px; -fx-fill: white;");

		VBox towerInfo = new VBox(15);
		towerInfo.setStyle("-fx-alignment: center-left;");
		towerInfo.getChildren().addAll(towerTitle, tower1, tower2, tower3, tower4);

		Button backButton = new Button("Back to Main Menu");
		backButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10 20;");
		backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-background-color: #3e8e41; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10 20;"));
		backButton.setOnMouseExited(e -> backButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10 20;"));
		backButton.setOnAction(e -> displayMainMenu());

		instructions.getChildren().addAll(title, content, towerInfo, backButton);

		Scene howToPlayScene = new Scene(instructions, 1000, 700);
		stage.setScene(howToPlayScene);
	}

	private void startGame() {
		// Initialize the game
		root = new BorderPane();
		scene = new Scene(root, 1000, 700);

		// Create game canvas
		gameCanvas = new Canvas(800, 500);
		gc = gameCanvas.getGraphicsContext2D();

		// Initialize game objects
		player = new Player();
		enemies = new ArrayList<>();
		waves = new ArrayList<>();

		// Load level and UI
		loadLevel(currentLevel);
		setupUI();

		// Start game loop
		startGameLoop();
		startWaveTimer();   
		// Set the scene
		stage.setScene(scene);
	}


	private void loadLevel(int level) {
		// Clear previous level
		if (mapPane != null) {
			mapPane.getChildren().clear();
		}

		enemies.clear();
		waves.clear();

		// Load map
		File levelFile = new File("Levels/MapLevel-" + level + ".txt");
		if (!levelFile.exists()) {
			System.err.println("Level file not found: " + levelFile.getAbsolutePath());
			levelFile = new File("src/application/MapLevel-" + level);
		}

		TextDecoder decoder = new TextDecoder(levelFile);
		gameMap = new GameMap(mapPane, decoder);

		// Load waves from decoder
		loadWavesFromDecoder(decoder);

		// Reset player lives for new level (but keep money)
		int currentMoney = player.getMoney();
		player = new Player();
		player.setMoney(currentMoney);

		// Reset UI
		if (moneyLabel != null) {
			updateUI();
		}

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

		// Create wave objects
		for (int i = 0; i < enemyCounts.length; i++) {
			waves.add(new Wave(enemyCounts[i], spawnDelays[i], (double) waveDelays[i]));
		}
	}

	private void setupUI() {
		// Create UI panels
		mapPane = new Pane();
		towerPane = new VBox(15);
		statsPane = new HBox(25);

		// Configure map pane (center area)
		mapPane.setStyle("-fx-background-color: #1a1a1a;");

		// Add canvas to map pane
		mapPane.getChildren().add(gameCanvas);

		// Configure tower selection panel
		towerPane.setPrefWidth(200);
		towerPane.setPadding(new javafx.geometry.Insets(20));
		towerPane.setStyle("-fx-background-color: #333333; -fx-padding: 15;");

		// Title for tower selection
		Label towerSelectionTitle = new Label("Tower Selection");
		towerSelectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

		// Create tower buttons
		Button singleShotTowerBtn = createTowerButton("Single Shot", 1, 50, Color.DODGERBLUE);
		Button laserTowerBtn = createTowerButton("Laser Tower", 2, 120, Color.RED);
		Button tripleShotTowerBtn = createTowerButton("Triple Shot", 3, 150, Color.LIMEGREEN);
		Button missileTowerBtn = createTowerButton("Missile Launcher", 4, 200, Color.ORANGE);

		// Add to tower panel
		towerPane.getChildren().addAll(
				towerSelectionTitle,
				singleShotTowerBtn,
				laserTowerBtn,
				tripleShotTowerBtn,
				missileTowerBtn
				);

		towerPane.setAlignment(Pos.TOP_CENTER);

		// Configure stats panel
		statsPane.setPrefHeight(80);
		statsPane.setStyle("-fx-background-color: #222222; -fx-padding: 15;");
		statsPane.setAlignment(Pos.CENTER_LEFT);

		// Create stats labels
		moneyLabel = new Label("Money: $" + player.getMoney());
		moneyLabel.setStyle("-fx-text-fill: gold; -fx-font-size: 18; -fx-font-weight: bold;");

		livesLabel = new Label("Lives: " + player.getLives());
		livesLabel.setStyle("-fx-text-fill: lightgreen; -fx-font-size: 18; -fx-font-weight: bold;");

		waveLabel = new Label("Wave: " + (currentWave != null ? (waves.indexOf(currentWave) + 1) : "0") + 
				"/" + waves.size());
		waveLabel.setStyle("-fx-text-fill: lightblue; -fx-font-size: 18; -fx-font-weight: bold;");

		waveTimerLabel = new Label("Next Wave: Waiting...");
		waveTimerLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 18; -fx-font-weight: bold;");

		// Add stats to panel
		statsPane.getChildren().addAll(moneyLabel, livesLabel, waveLabel, waveTimerLabel);

		// Set up layout structure
		root.setCenter(mapPane);
		root.setRight(towerPane);
		root.setBottom(statsPane);

		// Make UI responsive
		root.prefWidthProperty().bind(scene.widthProperty());
		root.prefHeightProperty().bind(scene.heightProperty());

		mapPane.prefWidthProperty().bind(root.widthProperty().subtract(towerPane.getPrefWidth()));
		mapPane.prefHeightProperty().bind(root.heightProperty().subtract(statsPane.getPrefHeight()));

		statsPane.prefWidthProperty().bind(root.widthProperty());

		// Add game map to map pane
		if (gameMap != null) {
			gameMap.addToPane(mapPane);
		}

		// Setup tower interaction
		setupTowerButtonActions(singleShotTowerBtn, laserTowerBtn, tripleShotTowerBtn, missileTowerBtn);
	}

	private Button createTowerButton(String name, int towerType, int cost, Color color) {
		// Create tower visual representation
		VBox towerGraphic = new VBox(5);
		towerGraphic.setAlignment(Pos.CENTER);

		// Tower shape
		Rectangle towerShape = new Rectangle(40, 40);
		towerShape.setFill(color);
		towerShape.setStroke(Color.BLACK);
		towerShape.setArcWidth(5);
		towerShape.setArcHeight(5);

		// Tower range indicator
		Circle rangeCircle = new Circle(20);
		rangeCircle.setFill(Color.TRANSPARENT);
		rangeCircle.setStroke(color.deriveColor(0, 1, 1, 0.3));
		rangeCircle.setStrokeWidth(1);

		// Shadow effect for 3D look
		DropShadow shadow = new DropShadow();
		shadow.setRadius(5.0);
		shadow.setOffsetX(3.0);
		shadow.setOffsetY(3.0);
		shadow.setColor(Color.color(0, 0, 0, 0.5));
		towerShape.setEffect(shadow);

		// Tower group
		Group towerGroup = new Group(towerShape);

		// Cost label
		Label costLabel = new Label("$" + cost);
		costLabel.setStyle("-fx-text-fill: gold; -fx-font-weight: bold;");

		// Add components to tower graphic
		towerGraphic.getChildren().addAll(towerGroup, costLabel);

		// Create button
		Button towerButton = new Button(name);
		towerButton.setGraphic(towerGraphic);
		towerButton.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
		towerButton.setPrefWidth(160);
		towerButton.setPrefHeight(90);
		towerButton.setStyle("-fx-background-color: #444444; -fx-text-fill: white; -fx-font-weight: bold;");

		// Hover effects
		towerButton.setOnMouseEntered(e -> {
			towerButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-font-weight: bold;");
			towerShape.setStroke(Color.WHITE);
		});

		towerButton.setOnMouseExited(e -> {
			towerButton.setStyle("-fx-background-color: #444444; -fx-text-fill: white; -fx-font-weight: bold;");
			towerShape.setStroke(Color.BLACK);
		});

		// Store tower data
		towerButton.setUserData(new int[]{towerType, cost});

		return towerButton;
	}

	private void setupTowerButtonActions(Button singleShotTowerBtn, Button laserTowerBtn, 
			Button tripleShotTowerBtn, Button missileTowerBtn) {
		// Create tower drag handler
		javafx.event.EventHandler<MouseEvent> towerDragHandler = event -> {
			Button sourceButton = (Button) event.getSource();
			int[] userData = (int[]) sourceButton.getUserData();
			int towerType = userData[0];
			int towerCost = userData[1];

			// Check if player has enough money
			if (player.getMoney() < towerCost) {
				showMessage("Not enough money! Need $" + towerCost, mapPane);
				return;
			}

			// Create appropriate tower type
			switch (towerType) {
			case 1: // Single Shot
				selectedTower = new singleShotTower((int)event.getSceneX(), (int)event.getSceneY(), mapPane);
				break;
			case 2: // Laser
				selectedTower = new LaserTower((int)event.getSceneX(), (int)event.getSceneY(), mapPane, gc);
				break;
			case 3: // Triple Shot
				selectedTower = new tripleShotTower((int)event.getSceneX(), (int)event.getSceneY(), mapPane);
				break;
			case 4: // Missile
				selectedTower = new missileLauncherTower((int)event.getSceneX(), (int)event.getSceneY(), gc, mapPane);
				break;
			}

			if (selectedTower != null) {
				selectedTower.setPrice(towerCost);

				// Create range indicator
				if (rangeIndicator != null) {
					mapPane.getChildren().remove(rangeIndicator);
				}

				rangeIndicator = new Circle(selectedTower.range);
				rangeIndicator.setCenterX(event.getSceneX() - root.getRight().getLayoutBounds().getWidth());
				rangeIndicator.setCenterY(event.getSceneY() - root.getBottom().getLayoutBounds().getHeight());
				rangeIndicator.setFill(Color.TRANSPARENT);
				rangeIndicator.setStroke(Color.RED);
				rangeIndicator.setStrokeWidth(2);
				rangeIndicator.setOpacity(0.5);
				mapPane.getChildren().add(rangeIndicator);

				// Start dragging
				isDraggingTower = true;
			}
		};

		// Set mouse handlers for tower buttons
		singleShotTowerBtn.setOnMousePressed(towerDragHandler);
		laserTowerBtn.setOnMousePressed(towerDragHandler);
		tripleShotTowerBtn.setOnMousePressed(towerDragHandler);
		missileTowerBtn.setOnMousePressed(towerDragHandler);

		// Set up map pane mouse handlers
		mapPane.setOnMouseMoved(e -> {
			if (isDraggingTower && selectedTower != null && rangeIndicator != null) {
				// Update range indicator position
				double adjustedX = e.getX();
				double adjustedY = e.getY();

				rangeIndicator.setCenterX(adjustedX);
				rangeIndicator.setCenterY(adjustedY);

				// Calculate grid position
				int col = (int) (adjustedX / gameMap.getCellWidth());
				int row = (int) (adjustedY / gameMap.getCellHeight());

				// Update indicator color based on validity
				boolean validPlacement = gameMap.isValidPlacement(row, col);
				rangeIndicator.setStroke(validPlacement ? Color.GREEN : Color.RED);
			}
		});

		mapPane.setOnMouseClicked(e -> {
			if (isDraggingTower && selectedTower != null) {
				// Get grid position
				int col = (int) (e.getX() / gameMap.getCellWidth());
				int row = (int) (e.getY() / gameMap.getCellHeight());

				// Try to place tower
				if (gameMap.isValidPlacement(row, col)) {
					if (player.getMoney() >= selectedTower.getPrice()) {
						// Position tower properly on grid
						double cellX = col * gameMap.getCellWidth();
						double cellY = row * gameMap.getCellHeight();

						// Create appropriate tower with correct position
						Towers placedTower = null;

						if (selectedTower instanceof singleShotTower) {
							placedTower = new singleShotTower((int)cellX, (int)cellY, enemies, mapPane);
						} else if (selectedTower instanceof LaserTower) {
							placedTower = new LaserTower((int)cellX, (int)cellY, enemies, mapPane);
						} else if (selectedTower instanceof tripleShotTower) {
							placedTower = new tripleShotTower((int)cellX, (int)cellY, enemies, mapPane);
						} else if (selectedTower instanceof missileLauncherTower) {
							placedTower = new missileLauncherTower((int)cellX, (int)cellY, enemies, gc, mapPane);
						}

						if (placedTower != null) {
							// Set price and add to game map
							placedTower.setPrice(selectedTower.getPrice());
							gameMap.placeTower(placedTower, row, col);

							// Deduct cost
							player.spendMoney(placedTower.getPrice());


							// Update UI
							updateUI();
						}
					} else {
						showMessage("Not enough money!", mapPane);
					}
				} else {
					showMessage("Invalid placement!", mapPane);
				}

				// Remove range indicator
				if (rangeIndicator != null) {
					mapPane.getChildren().remove(rangeIndicator);
					rangeIndicator = null;
				}

				// Reset selection
				isDraggingTower = false;
				selectedTower = null;
			}
		});

		// Cancel placement with right click
		mapPane.setOnMouseReleased(e -> {
			if (e.isSecondaryButtonDown() && isDraggingTower && selectedTower != null) {
				// Remove range indicator
				if (rangeIndicator != null) {
					mapPane.getChildren().remove(rangeIndicator);
					rangeIndicator = null;
				}

				// Reset selection
				isDraggingTower = false;
				selectedTower = null;
			}
		});
	}

	private void startGameLoop() {
		// Initialize and start the game loop
		gameLoop = new AnimationTimer() {
			@Override
			public void handle(long now) {
				// Update game state
				updateGame();

				// Render game elements
				renderGame();

				// Check game over conditions
				checkGameOver();
			}
		};
		gameLoop.start();
	}

	private void updateGame() {
		// Update enemies
		Iterator<Enemy> enemyIterator = enemies.iterator();
		while (enemyIterator.hasNext()) {
			Enemy enemy = enemyIterator.next();

			// Move enemies along path
			enemy.move();

			// Check if enemy reached end of path
			if (enemy.hasReachedEnd()) {
				player.loseLive();
				enemyIterator.remove();
				updateUI();

				// Check if player is out of lives
				if (player.getLives() <= 0) {
					gameOver(false);
					return;
				}
			}

			// Remove dead enemies
			if (!enemy.isAlive()) {
				// Award money for kills
				player.addMoney(10);
				updateUI();

				// Create explosion effect
				createExplosionEffect(enemy.getX(), enemy.getY());

				enemyIterator.remove();
			}
		}

		// Check wave status
		if (isWaveActive && currentWave != null) {
			// Check if wave is complete
			if (enemies.isEmpty() && currentWave.isComplete()) {
				waveCompleted();
			}
		}
	}

	private void renderGame() {
		// Clear canvas
		gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

		// Draw towers range and projectiles handled by tower classes

		// Draw enemies and their health bars
		for (Enemy enemy : enemies) {
			enemy.render(gc);
		}
	}

	private void checkGameOver() {
		// Check if player has lost all lives
		if (player.getLives() <= 0) {
			gameOver(false);
			return;
		}

		// Check win condition - completed all waves
		if (!isWaveActive && waves.isEmpty() && enemies.isEmpty() && currentLevel >= 5) {
			gameOver(true);
		} else if (!isWaveActive && waves.isEmpty() && enemies.isEmpty()) {
			// Level completed
			levelCompleted();
		}
	}

	private void gameOver(boolean victory) {
		gameRunning = false;
		if (gameLoop != null) {
			gameLoop.stop();
		}

		// Clear the screen
		root.getChildren().clear();

		// Create game over message
		VBox gameOverBox = new VBox(30);
		gameOverBox.setAlignment(Pos.CENTER);
		gameOverBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");

		Text message = new Text(victory ? "Victory!" : "Game Over");
		message.setStyle("-fx-font-size: 48px; -fx-fill: " + (victory ? "gold" : "red") + "; -fx-font-weight: bold;");
		message.setEffect(new Glow(0.8));

		Text statsText = new Text("Level reached: " + currentLevel + "\nRemaining lives: " + player.getLives());
		statsText.setStyle("-fx-font-size: 24px; -fx-fill: white;");

		Button mainMenuBtn = new Button("Main Menu");
		mainMenuBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10 20;");
		mainMenuBtn.setOnAction(e -> displayMainMenu());

		Button retryBtn = new Button("Retry");
		retryBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10 20;");
		retryBtn.setOnAction(e -> startGame());

		gameOverBox.getChildren().addAll(message, statsText, mainMenuBtn, retryBtn);

		Scene gameOverScene = new Scene(gameOverBox, 1000, 700);
		stage.setScene(gameOverScene);
	}

	// 7. Add method to start wave timer
	private void startWaveTimer() {
		// Initialize wave timers
		if (!waves.isEmpty()) {
			currentWave = waves.get(0);
			waveStartTime = System.currentTimeMillis() + (long)(currentWave.getDelayBeforeStart() * 1000);

			// Start wave timer
			waveTimer = new AnimationTimer() {
				@Override
				public void handle(long now) {
					long currentTime = System.currentTimeMillis();

					if (!isWaveActive && currentTime >= waveStartTime) {
						// Start spawning enemies
						startWave();
					} else if (!isWaveActive) {
						// Update timer display
						double timeRemaining = (waveStartTime - currentTime) / 1000.0;
						updateWaveTimer(timeRemaining);
					}

					// Spawn enemies if wave is active
					if (isWaveActive && currentWave != null) {
						currentWave.update(currentTime);

						// Spawn enemies if it's time
						if (currentWave.canSpawnEnemy(currentTime)) {
							spawnEnemy();
							currentWave.enemySpawned();
						}
					}
				}
			};
			waveTimer.start();
		}
	}

	// 8. Add method to start a wave
	private void startWave() {
		isWaveActive = true;
		waveLabel.setText("Wave: " + (waves.indexOf(currentWave) + 1) + "/" + waves.size());
		waveTimerLabel.setText("Wave in progress");
	}

	// 9. Add method to handle wave completion
	private void waveCompleted() {
		isWaveActive = false;
		waves.remove(currentWave);

		if (waves.isEmpty()) {
			// All waves completed
			waveTimerLabel.setText("Level Complete!");
			if (currentLevel < 5) {
				// Prepare for next level
				Button nextLevelBtn = new Button("Next Level");
				nextLevelBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
				nextLevelBtn.setOnAction(e -> {
					currentLevel++;
					loadLevel(currentLevel);
				});
				statsPane.getChildren().add(nextLevelBtn);
			} else {
				// Game completed
				gameOver(true);
			}
		} else {
			// Prepare for next wave
			currentWave = waves.get(0);
			waveStartTime = System.currentTimeMillis() + (long)(currentWave.getDelayBeforeStart() * 1000);
		}

		updateUI();
	}

	// 10. Add method to handle level completion
	private void levelCompleted() {
		// Show level complete message
		showMessage("Level " + currentLevel + " Complete!", mapPane);

		// Progress to next level if not at max
		if (currentLevel < 5) {
			Button nextLevelBtn = new Button("Next Level");
			nextLevelBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
			nextLevelBtn.setLayoutX(mapPane.getWidth() / 2 - 50);
			nextLevelBtn.setLayoutY(mapPane.getHeight() / 2 + 30);
			nextLevelBtn.setOnAction(e -> {
				mapPane.getChildren().remove(nextLevelBtn);
				currentLevel++;
				loadLevel(currentLevel);
			});
			mapPane.getChildren().add(nextLevelBtn);
		} else {
			// Game completed
			gameOver(true);
		}
	}

	// 11. Add method to spawn enemies
	private void spawnEnemy() {
		if (gameMap != null) {
			Enemy enemy = new Enemy(gameMap.getPath(), 30); // 30 health points
			enemies.add(enemy);
		}
	}

	// 12. Add method to update the wave timer display
	private void updateWaveTimer(double timeRemaining) {
		if (timeRemaining <= 0) {
			waveTimerLabel.setText("Wave Starting!");
		} else {
			waveTimerLabel.setText("Next Wave: " + String.format("%.1f", timeRemaining) + "s");
		}
	}

	// 13. Add method to create explosion effect
	private void createExplosionEffect(double x, double y) {
		// Create explosion particles
		for (int i = 0; i < 20; i++) {
			double angle = Math.random() * 2 * Math.PI;
			double distance = Math.random() * 30;

			Circle particle = new Circle(3);
			particle.setFill(Color.ORANGE);
			particle.setCenterX(x);
			particle.setCenterY(y);

			mapPane.getChildren().add(particle);

			// Animate particle
			TranslateTransition animation = new TranslateTransition(Duration.millis(500), particle);
			animation.setByX(Math.cos(angle) * distance);
			animation.setByY(Math.sin(angle) * distance);
			animation.setOnFinished(e -> mapPane.getChildren().remove(particle));
			animation.play();
		}
	}

	// 14. Add method to update UI elements
	private void updateUI() {
		moneyLabel.setText("Money: $" + player.getMoney());
		livesLabel.setText("Lives: " + player.getLives());

		if (currentWave != null) {
			waveLabel.setText("Wave: " + (waves.indexOf(currentWave) + 1) + "/" + (waves.size() + waves.indexOf(currentWave)));
		}
	}

	// 15. Add method to show message to player
	private void showMessage(String text, Pane container) {
		Label message = new Label(text);
		message.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5;");
		message.setLayoutX(container.getWidth() / 2 - 100);
		message.setLayoutY(container.getHeight() / 2 - 20);

		container.getChildren().add(message);

		// Create fade out animation
		Timeline timeline = new Timeline(
				new KeyFrame(Duration.seconds(2), e -> container.getChildren().remove(message))
				);
		timeline.play();
	}

}