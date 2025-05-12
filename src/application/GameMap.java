package application;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GameMap {
	private int width;
	private int height;
	private Cell[][] grid;
	private List<Cell> path;
	private List<Towers> towers;
	Pane mapPane;
	private TextDecoder decoder;

	public GameMap(Pane mapPane, TextDecoder decoder) {
		this.mapPane = mapPane;
		this.decoder = decoder;
		this.towers = new ArrayList<>();

		initializeFromDecoder();
	}

	// Fix in GameMap class - initializeFromDecoder method
	public void initializeFromDecoder() {
		// Get dimensions from decoder
		this.width = decoder.getLevelWidth();
		this.height = decoder.getLevelHeight();

		// Initialize grid
		this.grid = new Cell[height][width];
		this.path = decoder.getPathCells();

		// Calculate cell size to fit map within the pane
		double cellSize = calculateCellSize();

		// Calculate offset to center the map in the pane
		double paneWidth = mapPane.getWidth() > 0 ? mapPane.getWidth() : 600;
		double paneHeight = mapPane.getHeight() > 0 ? mapPane.getHeight() : 400;
		double offsetX = (paneWidth - (width * cellSize)) / 2;
		double offsetY = (paneHeight - (height * cellSize)) / 2;

		if (offsetX < 0) offsetX = 0;
		if (offsetY < 0) offsetY = 0;

		// Debug output
		System.out.println("Map dimensions: " + width + "x" + height);
		System.out.println("Cell size: " + cellSize);
		System.out.println("Offsets: X=" + offsetX + ", Y=" + offsetY);
		System.out.println("Path cells count: " + path.size());

		// Initialize all cells
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				boolean isPath = isPathCell(row, col);
				if(isPath) {
					System.out.println("Path cell at: " + row + "," + col);
				}
				grid[row][col] = new Cell(row, col, cellSize, isPath);

				// Position the cell with the offset to center the map
				grid[row][col].setPosition(offsetX + (col * cellSize), offsetY + (row * cellSize));

				// Style the cell based on type
				styleCell(grid[row][col]);

				// Add to pane
				mapPane.getChildren().add(grid[row][col].getRectangle());
			}
		}

		// Animate grid loading
		animateGridLoading();
	}

	private double calculateCellSize() {
		// Get the available width and height
		double availableWidth = mapPane.getWidth();
		double availableHeight = mapPane.getHeight();

		// If the pane dimensions are not yet set, use default values
		if (availableWidth <= 0) availableWidth = 600;
		if (availableHeight <= 0) availableHeight = 400;

		// Calculate the maximum cell size that will fit the entire map
		double maxCellWidth = availableWidth / width;
		double maxCellHeight = availableHeight / height;

		// Use the smaller dimension to ensure all cells are square and fit within the pane
		return Math.min(maxCellWidth, maxCellHeight);
	}


	private boolean isPathCell(int row, int col) {
		for (Cell pathCell : path) {
			if (pathCell.getRow() == row && pathCell.getCol() == col) {
				return true;
			}
		}
		return false;
	}

	private void styleCell(Cell cell) {
		if (cell.isPath()) {
			cell.getRectangle().setFill(Color.GRAY);
		} else {
			// Random yellow tones for tower placement
			cell.getRectangle().setFill(Math.random() < 0.5 ? 
					Color.rgb(255, 255, 150) : Color.rgb(255, 255, 180));
		}
	}

	private void animateGridLoading() {
		// Implementation of cell loading animation
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				animateCell(grid[row][col], row * width + col);
			}
		}
	}

	private void animateCell(Cell cell, int delayFactor) {
		// Scale animation for each cell with sequential delay
		cell.getRectangle().setScaleX(0);
		cell.getRectangle().setScaleY(0);

		javafx.animation.ScaleTransition st = 
				new javafx.animation.ScaleTransition(
						javafx.util.Duration.millis(300), 
						cell.getRectangle());
		st.setDelay(javafx.util.Duration.millis(delayFactor * 30));
		st.setToX(1.00);
		st.setToY(1.00);
		st.play();
	}
	// Fix in GameMap class - placeTower method
	public boolean placeTower(Towers tower, int row, int col) {
	    if (row < 0 || row >= height || col < 0 || col >= width) {
	        System.out.println("placeTower: Out of bounds - " + row + "," + col);
	        return false;
	    }

	    Cell cell = grid[row][col];
	    if (cell.isPath() || cell.hasTower()) {
	        System.out.println("placeTower: Invalid cell - isPath: " + cell.isPath() + ", hasTower: " + cell.hasTower());
	        return false;
	    }

	    cell.setTower(tower);
	    
	    // FIX: Position the tower correctly at the center of the cell
	    double centerX = cell.getCenterX();
	    double centerY = cell.getCenterY();
	    System.out.println("Positioning tower at: " + centerX + "," + centerY + " (cell: " + row + "," + col + ")");
	    
	    tower.setPosition(centerX, centerY);
	    towers.add(tower);
	    
	    // Make sure the tower's image view is added to the pane
	    if (!mapPane.getChildren().contains(tower.getImageView())) {
	        mapPane.getChildren().add(tower.getImageView());
	    }

	    return true;
	}


	public void removeTower(Towers tower) {
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (grid[row][col].getTower() == tower) {
					grid[row][col].removeTower();
					towers.remove(tower);
					mapPane.getChildren().remove(tower.getImageView());
					return;
				}
			}
		}
	}

	public Cell getCell(int row, int col) {
		if (row >= 0 && row < height && col >= 0 && col < width) {
			return grid[row][col];
		}
		return null;
	}

	public List<Cell> getPath() {
		return path;
	}

	public List<Towers> getTowers() {
		return towers;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void addToPane(Pane newMapPane) {
		// Update mapPane reference to the new pane
		this.mapPane = newMapPane;

		// Clear any existing content on the map pane
		mapPane.getChildren().clear();

		// Re-calculate cell size based on the current pane dimensions
		double cellSize = calculateCellSize();

		// Calculate offset to center the map in the pane
		double offsetX = (mapPane.getWidth() - (width * cellSize)) / 2;
		double offsetY = (mapPane.getHeight() - (height * cellSize)) / 2;

		if (offsetX < 0) offsetX = 0;
		if (offsetY < 0) offsetY = 0;

		// If grid hasn't been initialized yet, initialize it first
		if (grid == null) {
			initializeFromDecoder();
			return;
		}

		// Add all existing cells to the new pane and resize them
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				Cell cell = grid[row][col];

				// Resize and reposition the cell based on new cell size
				cell.resize(cellSize);
				cell.setPosition(offsetX + (col * cellSize), offsetY + (row * cellSize));

				// Add the cell to the pane
				mapPane.getChildren().add(cell.getRectangle());
			}
		}

		// Add all existing towers back to the pane and update their positions
		for (Towers tower : towers) {
			mapPane.getChildren().add(tower.getImageView());

			// Find the cell containing this tower and update tower position
			for (int row = 0; row < height; row++) {
				for (int col = 0; col < width; col++) {
					Cell cell = grid[row][col];
					if (cell.getTower() == tower) {
						tower.setPosition(cell.getCenterX(), cell.getCenterY());
						break;
					}
				}
			}
		}
	}

	public void addTower(Towers tower) {
		if (!towers.contains(tower)) {
			towers.add(tower);
		}
	}

	public void handleResize() {
		if (mapPane != null && grid != null) {
			addToPane(mapPane); // Recalculate and reposition all cells
		}
	}

	public boolean isValidPlacement(int row, int col) {
		if (row < 0 || row >= width || col < 0 || col >= height) {
			System.out.println("Invalid placement - out of bounds: " + row + "," + col);
			return false;
		}

		Cell cell = grid[row][col];
		if (cell == null) {
			System.out.println("Invalid placement - cell is null at: " + row + "," + col);
			return false;
		}

		if (cell.isPath()) {
			System.out.println("Invalid placement - cell is path at: " + row + "," + col);
			return false;
		}

		if (cell.hasTower()) {
			System.out.println("Invalid placement - cell already has tower at: " + row + "," + col);
			return false;
		}

		System.out.println("Valid placement at: " + row + "," + col);
		return true;
	}


	public Cell getStartCell() {
		if (path != null && !path.isEmpty()) {
			return path.get(0);
		}
		return null;
	}

	public Cell getEndCell() {
		if (path != null && !path.isEmpty()) {
			return path.get(path.size() - 1);
		}
		return null;
	}

	public void update(List<Enemy> enemies) {
		for (Towers tower : towers) {
			tower.closestEnemy(enemies);
		}
	}

	public double getCellSize() {
		return calculateCellSize();
	}


}