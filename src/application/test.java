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
import javafx.scene.image.ImageView;
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class test extends Application {
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
	private WaveManager waveManager;
	private Label waveTimerLabel;
	private long waveStartTime;
	private boolean isWaveActive = false;

	// Tower selection UI
	private Towers selectedTower;
	private boolean isDraggingTower = false;
	private ImageView towerView;
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
		Application.launch(args);
	}

	private void startGame(Stage primaryStage) {
		root = new BorderPane();
		scene = new Scene(root, 800, 600);

		player = new Player();
		enemies = new ArrayList<>();
		waves = new ArrayList<>();

		loadLevel(currentLevel);
		setUI();

		waveManager = new WaveManager(waves, enemies, gameMap.getPath(), player);
		startGameLoop();

		startWaveTimer();

		primaryStage.setScene(scene);
		primaryStage.setTitle("Tower Defense Game");
		primaryStage.show();

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


	private void setUI() {
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
		Button laserTowerBtn = createTowerButton("Laser Tower", 3, 200);
		Button tripleShotTowerBtn = createTowerButton("Triple Shot Tower", 2, 150);
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
		Label moneyLabel = new Label("Money: $" + player.getMoney());
		moneyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

		Label livesLabel = new Label("Lives: " + player.getLives());
		livesLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

		Label waveLabel = new Label("Wave: " + (currentWave != null ? (waves.indexOf(currentWave) + 1) : "0") + 
				"/" + waves.size());
		waveLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");

		waveTimerLabel = new Label("Next Wave: Waiting...");
		waveTimerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
		statsPane.getChildren().add(waveTimerLabel);

		statsPane.getChildren().addAll(moneyLabel, livesLabel, waveLabel);

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

		setupTowerButtonActions(singleShotTowerBtn, missileTowerBtn, laserTowerBtn, tripleShotTowerBtn, mapPane);
	}


	public Button createTowerButton(String name, int towerType, int cost) throws FileNotFoundException{


		// Set color based on tower type
		// Create a tower to drag
		switch (towerType) {
		case 1: // Single Shot Tower
			selectedTower = new singleShotTower();
			towerView = selectedTower.getImageView();
			break;
		case 2: // Laser Tower
			selectedTower = new LaserTower();
			towerView = selectedTower.getImageView();
			break;
		case 3: // Triple Shot Tower
			selectedTower = new tripleShotTower();
			towerView = selectedTower.getImageView();
			break;
		case 4: // Missile Launcher Tower
			selectedTower = new missileLauncherTower();
			towerView = selectedTower.getImageView();
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
		Button towerButton = new Button(name);
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


	// Sets up the tower button actions for dragging and placing towers
	private void setupTowerButtonActions(Button singleShotTowerBtn, Button tripleShotTowerBtn, 
			Button laserTowerBtn, Button missileTowerBtn, Pane mapPane) {
		// Create event handlers for each tower button
		javafx.event.EventHandler<javafx.scene.input.MouseEvent> towerDragHandler = 
				event -> {
					Button sourceButton = (Button) event.getSource();
					int[] userData = (int[]) sourceButton.getUserData();
					int towerType = userData[0];
					int towerCost = userData[1];

					// Check if player has enough money first
					if (player.getMoney() < towerCost) {
						showMessage("Not enough money! Need $" + towerCost, mapPane);
						return;
					}

					// Create a tower to drag
					switch (towerType) {
					case 1: // Single Shot Tower
						selectedTower = new singleShotTower();
						break;
					case 2: // Laser Tower
						selectedTower = new LaserTower();
						break;
					case 3: // Triple Shot Tower
						selectedTower = new tripleShotTower();
						break;
					case 4: // Missile Launcher Tower
						selectedTower = new missileLauncherTower();
						break;
					}
					selectedTower.setPrice(towerCost);

					// Show tower range
					selectedTower.showRangeIndicator(root);

					// Add tower view to map pane temporarily
					mapPane.getChildren().add(selectedTower.getView());

					// Position tower at mouse location
					selectedTower.setPosition(event.getSceneX() - root.getRight().getLayoutBounds().getWidth(), 
							event.getSceneY() - root.getBottom().getLayoutBounds().getHeight());

					// Start dragging
					isDraggingTower = true;
				};

				// Set mouse pressed handler for tower buttons
				singleShotTowerBtn.setOnMousePressed(towerDragHandler);
				tripleShotTowerBtn.setOnMousePressed(towerDragHandler);
				laserTowerBtn.setOnMousePressed(towerDragHandler);
				missileTowerBtn.setOnMousePressed(towerDragHandler);

				// Set up map pane mouse handlers for tower placement
				mapPane.setOnMouseMoved(e -> {
					if (isDraggingTower && selectedTower != null) {
						// Update tower position as mouse moves
						selectedTower.setPosition(e.getX(), e.getY());

						// Calculate grid position
						int col = (int) (e.getX() / gameMap.getWidth());
						int row = (int) (e.getY() / gameMap.getHeight());

						// Check if placement is valid and highlight accordingly
						boolean validPlacement = gameMap.isValidPlacement(row, col);
						selectedTower.setValidPlacement(validPlacement);
					}
				});

				mapPane.setOnMouseClicked(e -> {
					if (isDraggingTower && selectedTower != null) {
						// Get grid position from mouse coordinates
						int col = (int) (e.getX() / gameMap.getWidth());
						int row = (int) (e.getY() / gameMap.getHeight());

						// Try to place tower
						if (gameMap.isValidPlacement(row, col)) {
							if (player.getMoney() >= selectedTower.getPrice()) {
								gameMap.placeTower(selectedTower, row, col);

								// Tower placed successfully, deduct cost
								player.spendMoney(selectedTower.getPrice());

								// Hide range display but keep it for functional range calculation
								selectedTower.hideRangeIndicator(root);

								// Add tower to game map's tower list for targeting and shooting
								gameMap.addTower(selectedTower);

								// Update money display
								updateUI();
							} else {
								// Not enough money, show message
								showMessage("Not enough money!", mapPane);

								// Remove temporary tower view
								mapPane.getChildren().remove(selectedTower.getView());
							}
						} else {
							// Invalid placement, show message
							showMessage("Invalid placement!", mapPane);

							// Remove temporary tower view
							mapPane.getChildren().remove(selectedTower.getView());
						}

						// Reset selection state
						isDraggingTower = false;
						selectedTower = null;
					}
				});

				// Add handler for canceling tower placement with right click
				mapPane.setOnMouseReleased(e -> {
					if (e.isSecondaryButtonDown() && isDraggingTower && selectedTower != null) {
						// Cancel tower placement
						mapPane.getChildren().remove(selectedTower.getView());
						isDraggingTower = false;
						selectedTower = null;
					}
				});
	}

	// Wave timer'ı başlatan metot
	 private void startWaveTimer() {
	        waveStartTime = System.nanoTime();
	        isWaveActive = false;

	        waveTimer = new AnimationTimer() {
	            @Override
	            public void handle(long now) {
	                if (!gameRunning) return;

	                waveManager.update((now - waveStartTime) / 1_000_000_000.0); // Use WaveManager to update

	                if (waveManager.allWavesCompleted() && enemies.isEmpty()) {
	                    levelComplete();
	                }

	                updateUI();
	            }
	        };
	        waveTimer.start();
	    }


	private void spawnEnemy() {
		Cell startCell = gameMap.getStartCell();
		if (startCell != null) {
			ArrayList<Cell> pathCells = new ArrayList<>(gameMap.getPath());
			Enemy enemy = new Enemy(pathCells, player);
			enemies.add(enemy);

			// Düşmanı haritaya ekle
			Pane mapPane = (Pane) root.getCenter();
			mapPane.getChildren().add(enemy);
		}
	}


	// Oyun döngüsünü başlatan metot - düşmanların hareketi ve kulelerin ateş etmesi için
	private void startGameLoop() {
		gameLoop = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (!gameRunning) return;

				// Düşmanları hareket ettir
				List<Enemy> deadEnemies = new ArrayList<>();
				for (Enemy enemy : enemies) {
					enemy.step();

					// Düşman öldü mü?
					if (!enemy.isAlive()) {
						deadEnemies.add(enemy);
						player.addMoney(10); // Düşman öldürme ödülü
					}

					// Düşman bitiş çizgisine ulaştı mı?
					if (!enemy.isAlive() && enemy.getStepIndex() >= gameMap.getPath().size() - 1) {
						player.loseLife();
						if (player.getLives() <= 0) {
							gameOver();
						}
					}
				}

				// Ölü düşmanları listeden kaldır
				for (Enemy enemy : deadEnemies) {
					Pane mapPane = (Pane) root.getCenter();
					mapPane.getChildren().remove(enemy);
					enemies.remove(enemy);
				}

				// Kuleleri güncelle (düşmanları hedefle ve ateş et)
				gameMap.update(enemies);

				// UI'ı güncelle
				updateUI();
			}
		};
		gameLoop.start();
	}

	//	 Shows a temporary message on the map

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



	private void updateUI() {
		// Update money and lives labels
		Label moneyLabel = (Label) ((Pane) root.getChildren().get(1)).getChildren().get(0);
		moneyLabel.setText("Money: $" + player.getMoney());

		Label livesLabel = (Label) ((Pane) root.getChildren().get(1)).getChildren().get(1);
		livesLabel.setText("Lives: " + player.getLives());

		Label waveLabel = (Label) ((Pane) root.getChildren().get(1)).getChildren().get(2);
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
}
