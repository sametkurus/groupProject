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
    private Pane mapPane;
    private TextDecoder decoder;

    public GameMap(Pane mapPane, TextDecoder decoder) {
        this.mapPane = mapPane;
        this.decoder = decoder;
        this.towers = new ArrayList<>();
        initializeFromDecoder();
    }

    public void initializeFromDecoder() {
        // Get dimensions from decoder
        this.width = decoder.getLevelWidth();
        this.height = decoder.getLevelHeight();
        
        // Initialize grid
        this.grid = new Cell[height][width];
        this.path = decoder.getPathCells();
        
        // Calculate cell size based on pane dimensions
        double cellSize = calculateCellSize();
        
        // Initialize all cells
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                boolean isPath = isPathCell(row, col);
                grid[row][col] = new Cell(row, col, cellSize, isPath);
                
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
        return Math.min(mapPane.getPrefWidth() / width, 
                      mapPane.getPrefHeight() / height);
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
        st.setToX(0.90);
        st.setToY(0.90);
        st.play();
    }
/*
    public boolean placeTower(Towers tower, int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            return false;
        }
        
        Cell cell = grid[row][col];
        if (cell.isPath() || cell.hasTower()) {
            return false;
        }
        
        cell.setTower(tower);
        towers.add(tower);
        tower.setPosition(cell.getCenterX(), cell.getCenterY());
        mapPane.getChildren().add(tower.getView());
        return true;
    }

    public void removeTower(Towers tower) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (grid[row][col].getTower() == tower) {
                    grid[row][col].removeTower();
                    towers.remove(tower);
                    mapPane.getChildren().remove(tower.getView());
                    return;
                }
            }
        }
    }
**/
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
}