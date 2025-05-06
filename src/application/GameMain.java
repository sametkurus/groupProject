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
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class GameMain extends Application {
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
		startButton.setOnAction(e -> {
			initializeGame();
			setupEventHandlers();

			primaryStage.setTitle("Tower Defense Game");
			primaryStage.setScene(scene);
			primaryStage.show();

			startGameLoop();
		}
				);

		VBox layout = new VBox(20);
		layout.setStyle("-fx-alignment: center; -fx-padding: 40;");
		layout.getChildren().addAll(title, startButton);

		Scene mainMenuScene = new Scene(layout, 800, 600);
		primaryStage.setScene(mainMenuScene);
		primaryStage.setTitle("Main Menu");
		primaryStage.show();
	}

	/**
	 * Initializes the game components
	 */
	private void initializeGame() {
		root = new Pane();
		scene = new Scene(root, 800, 600);

		// Initialize game objects
		player = new Player();
		enemies = new ArrayList<>();
		waves = new ArrayList<>();

		// Load first level
		loadLevel(currentLevel);

		// Setup UI
		setupUI();
	}

	/**
	 * Loads a game level
	 * @param level The level number to load
	 */
	private void loadLevel(int level) {

		// Clear previous level
		root.getChildren().clear();
		enemies.clear();
		waves.clear();

		// Load map
		gameMap = new GameMap(root);
		gameMap.loadFromFile("MapLevel-" + level + ".txt");

		// Load waves from file
		loadWavesFromFile("MapLevel-" + level + ".txt");

		// Reset player for new level (but keep money)
		player.setLives(5);

		// Start with first wave
		if (!waves.isEmpty()) {
			
		}

		/**
		 * Loads wave data from level file
		 * @param filename The level file to parse
		 */
		private void loadWavesFromFile(String filename) {
			try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
				String line;
				boolean waveDataSection = false;

				while ((line = reader.readLine()) != null) {
					line = line.trim();

					if (line.equals("WAVE_DATA:")) {
						waveDataSection = true;
						continue;
					}

					if (waveDataSection && !line.isEmpty()) {
						// Parse wave data (format: "enemyCount, timeBetweenEnemies, delayBeforeStart")
						String[] parts = line.split(",");
						int enemyCount = Integer.parseInt(parts[0].trim());
						double timeBetween = ((int)(Double.parseDouble(parts[1].trim())*100))/100.0;
						double delayBefore = ((int)(Double.parseDouble(parts[2].trim())*100))/100.0;

						waves.add(new Wave(enemyCount, timeBetween, delayBefore));
					}
				}
			} catch (IOException e) {
				System.err.println("Error reading wave data: " + e.getMessage());
			}
		}

		/**
		 * Sets up the game UI elements
		 */
		private void setupUI() {
			// Create side panel for tower selection
			Pane towerPanel = new Pane();
			towerPanel.setStyle("-fx-background-color: #333; -fx-padding: 10;");
			towerPanel.setPrefWidth(150);
			towerPanel.setLayoutX(scene.getWidth() - 150);

			// Create tower selection buttons
			createTowerButton(towerPanel, "Single Shot", 50, 0, TowerType.SINGLE_SHOT);
			createTowerButton(towerPanel, "Laser", 120, 40, TowerType.LASER);
			createTowerButton(towerPanel, "Triple Shot", 150, 80, TowerType.TRIPLE_SHOT);
			createTowerButton(towerPanel, "Missile", 200, 120, TowerType.MISSILE_LAUNCHER);

			// Player stats display
			Pane statsPanel = new Pane();
			statsPanel.setStyle("-fx-background-color: #444; -fx-padding: 10;");
			statsPanel.setPrefWidth(scene.getWidth() - 150);
			statsPanel.setPrefHeight(60);

			// Add money and lives labels
			Label moneyLabel = new Label("Money: $" + player.getMoney());
			moneyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
			moneyLabel.setLayoutX(10);
			moneyLabel.setLayoutY(10);

			Label livesLabel = new Label("Lives: " + player.getLives());
			livesLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
			livesLabel.setLayoutX(10);
			livesLabel.setLayoutY(30);

			Label waveLabel = new Label("Wave: " + (currentWave != null ? (waves.indexOf(currentWave) + 1) : "0") + 
					"/" + waves.size());
			waveLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
			waveLabel.setLayoutX(200);
			waveLabel.setLayoutY(10);

			statsPanel.getChildren().addAll(moneyLabel, livesLabel, waveLabel);

			root.getChildren().addAll(towerPanel, statsPanel);
		}

		/**
		 * Creates a tower selection button
		 */
		private void createTowerButton(Pane panel, String text, int cost, double y, TowerType type) {
			Rectangle bg = new Rectangle(130, 30);
			bg.setFill(Color.DARKGRAY);
			bg.setLayoutY(y);
			bg.setStroke(Color.WHITE);

			Label label = new Label(text + " ($" + cost + ")");
			label.setStyle("-fx-text-fill: white;");
			label.setLayoutX(10);
			label.setLayoutY(y + 5);

			panel.getChildren().addAll(bg, label);

			// Make it draggable
			bg.setOnMousePressed(e -> {
				if (player.hasEnoughMoney(cost)) {
					selectedTower = TowerFactory.createTower(type);
					isDraggingTower = true;

					// Show range indicator
					selectedTower.showRangeIndicator(root);

					// Position at mouse
					selectedTower.setTowerx(e.getSceneX());
					selectedTower.setTowery(e.getSceneY());
					root.getChildren().add(selectedTower.loadTowerImage());
				}
			});
		}

		/**
		 * Sets up mouse event handlers
		 */
		private void setupEventHandlers() {
			scene.setOnMouseDragged(e -> {
				if (isDraggingTower && selectedTower != null) {
					selectedTower.setTowerx(e.getSceneX());
					selectedTower.setTowery(e.getSceneY());
				}
			});

			scene.setOnMouseReleased(e -> {
				if (isDraggingTower && selectedTower != null) {
					// Try to place the tower
					if (gameMap.placeTower(selectedTower)) {
						player.spendMoney(selectedTower.getPrice());
						updateUI();
					} else {
						root.getChildren().remove(selectedTower.getView());
					}

					// Clean up
					selectedTower.clearRangeIndicator(root);
					selectedTower = null;
					isDraggingTower = false;
				}
			});
		}

		/**
		 * Starts the main game loop
		 */
		private void startGameLoop() {
			gameLoop = new AnimationTimer() {
				private long lastUpdate = 0;

				@Override
				public void handle(long now) {
					if (lastUpdate == 0) {
						lastUpdate = now;
						return;
					}

					double deltaTime = (now - lastUpdate) / 1_000_000_000.0; // Convert to seconds
					lastUpdate = now;

					if (!gameRunning) return;

					// Update current wave
					if (currentWave != null && !currentWave.isComplete()) {
						currentWave.update(deltaTime);

						// Spawn new enemies if needed
						if (currentWave.shouldSpawnEnemy()) {
							Enemy enemy = currentWave.spawnEnemy();
							enemy.setPath(gameMap.getPath());
							enemies.add(enemy);
							root.getChildren().add(enemy.getView());
							root.getChildren().add(enemy.getHealthBar());
						}
					} else if (waves.indexOf(currentWave) < waves.size() - 1) {
						// Move to next wave
						currentWave = waves.get(waves.indexOf(currentWave) + 1);
					} else if (enemies.isEmpty()) {
						// All waves completed
						levelComplete();
					}

					// Update all enemies
					List<Enemy> enemiesToRemove = new ArrayList<>();
					for (Enemy enemy : enemies) {
						enemy.move(deltaTime);

						// Check if enemy reached the end
						if (enemy.isAtEnd()) {
							player.loseLife();
							updateUI();
							enemiesToRemove.add(enemy);
							root.getChildren().removeAll(enemy.getView(), enemy.getHealthBar());

							if (player.getLives() <= 0) {
								gameOver();
							}
						}

						// Check if enemy died
						if (enemy.isDead()) {
							player.addMoney(10); // $10 per kill
							updateUI();
							enemiesToRemove.add(enemy);
							root.getChildren().removeAll(enemy.getView(), enemy.getHealthBar());
							createExplosion(enemy.getX(), enemy.getY());
						}
					}
					enemies.removeAll(enemiesToRemove);

					// Update all towers
					for (Tower tower : gameMap.getTowers()) {
						tower.findTarget(enemies);
						tower.update(deltaTime);

						// Handle tower shooting
						if (tower.canShoot()) {
							tower.shoot();

							// Visual effect for shooting
							if (tower.getType() == TowerType.SINGLE_SHOT || tower.getType() == TowerType.TRIPLE_SHOT) {
								createBulletEffect(tower);
							}
						}
					}
				}
			};
			gameLoop.start();
		}

		/**
		 * Creates a bullet visual effect
		 */
		private void createBulletEffect(Tower tower) {
			if (tower.getTarget() == null) return;

			Line bullet = new Line(
					tower.getX(), tower.getY(),
					tower.getTarget().getX(), tower.getTarget().getY()
					);
			bullet.setStroke(Color.RED);
			bullet.setStrokeWidth(2);
			root.getChildren().add(bullet);

			// Remove after short time
			new Timeline(new KeyFrame(Duration.millis(100), e -> {
				root.getChildren().remove(bullet);
			})).play();
		}

		/**
		 * Creates an explosion effect at specified coordinates
		 */
		private void createExplosion(double x, double y) {
			Group explosion = new Group();
			root.getChildren().add(explosion);

			// Create particles
			for (int i = 0; i < 20; i++) {
				Circle particle = new Circle(3, Color.ORANGE);
				explosion.getChildren().add(particle);

				double angle = Math.random() * 2 * Math.PI;
				double distance = 5 + Math.random() * 15;

				TranslateTransition tt = new TranslateTransition(Duration.millis(500), particle);
				tt.setByX(Math.cos(angle) * distance);
				tt.setByY(Math.sin(angle) * distance);
				tt.setCycleCount(1);
				tt.play();
			}

			// Remove after animation
			new Timeline(new KeyFrame(Duration.millis(500), e -> {
				root.getChildren().remove(explosion);
			})).play();
		}

		/**
		 * Updates the UI elements
		 */
		private void updateUI() {
			// Update money and lives labels
			Label moneyLabel = (Label) ((Pane) root.getChildren().get(1)).getChildren().get(0);
			moneyLabel.setText("Money: $" + player.getMoney());

			Label livesLabel = (Label) ((Pane) root.getChildren().get(1)).getChildren().get(1);
			livesLabel.setText("Lives: " + player.getLives());

			Label waveLabel = (Label) ((Pane) root.getChildren().get(1)).getChildren().get(2);
			waveLabel.setText("Wave: " + (currentWave != null ? (waves.indexOf(currentWave) + 1) : "0") + 
					"/" + waves.size());
		}

		/**
		 * Handles game over state
		 */
		private void gameOver() {
			gameRunning = false;

			Label gameOverLabel = new Label("GAME OVER");
			gameOverLabel.setStyle("-fx-font-size: 48; -fx-text-fill: red;");
			gameOverLabel.setLayoutX(scene.getWidth() / 2 - 100);
			gameOverLabel.setLayoutY(scene.getHeight() / 2 - 30);

			Button restartButton = new Button("Restart");
			restartButton.setLayoutX(scene.getWidth() / 2 - 40);
			restartButton.setLayoutY(scene.getHeight() / 2 + 30);
			restartButton.setOnAction(e -> {
				root.getChildren().removeAll(gameOverLabel, restartButton);
				loadLevel(currentLevel);
			});

			root.getChildren().addAll(gameOverLabel, restartButton);
		}

		/**
		 * Handles level completion
		 */
		private void levelComplete() {
			gameRunning = false;

			if (currentLevel < 5) {
				Label levelCompleteLabel = new Label("LEVEL " + currentLevel + " COMPLETE!");
				levelCompleteLabel.setStyle("-fx-font-size: 36; -fx-text-fill: green;");
				levelCompleteLabel.setLayoutX(scene.getWidth() / 2 - 150);
				levelCompleteLabel.setLayoutY(scene.getHeight() / 2 - 30);

				Button nextLevelButton = new Button("Next Level");
				nextLevelButton.setLayoutX(scene.getWidth() / 2 - 50);
				nextLevelButton.setLayoutY(scene.getHeight() / 2 + 30);
				nextLevelButton.setOnAction(e -> {
					root.getChildren().removeAll(levelCompleteLabel, nextLevelButton);
					currentLevel++;
					loadLevel(currentLevel);
				});

				root.getChildren().addAll(levelCompleteLabel, nextLevelButton);
			} else {
				Label gameCompleteLabel = new Label("YOU WIN! GAME COMPLETE!");
				gameCompleteLabel.setStyle("-fx-font-size: 36; -fx-text-fill: gold;");
				gameCompleteLabel.setLayoutX(scene.getWidth() / 2 - 180);
				gameCompleteLabel.setLayoutY(scene.getHeight() / 2 - 30);

				Button restartButton = new Button("Play Again");
				restartButton.setLayoutX(scene.getWidth() / 2 - 50);
				restartButton.setLayoutY(scene.getHeight() / 2 + 30);
				restartButton.setOnAction(e -> {
					root.getChildren().removeAll(gameCompleteLabel, restartButton);
					currentLevel = 1;
					loadLevel(currentLevel);
				});

				root.getChildren().addAll(gameCompleteLabel, restartButton);
			}
		}


		public static void main(String[] args) {
			Application.launch(args);
		}

	}