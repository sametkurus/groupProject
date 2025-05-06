package application;

import javafx.scene.shape.Rectangle;

public class Cell {
	private int row;
	private int col;
	private double size; //  Cell size
	private boolean isPath;
	private Towers tower;
	private Rectangle rectangle; // JavaFX Rectangle to represent the cell

	public Cell(int row, int col, double size, boolean isPath) {
		this.row = row;
		this.col = col;
		this.size = size;
		this.isPath = isPath;
		this.tower = null; // Initially, no tower
		this.rectangle = new Rectangle(col * size, row * size, size, size); // create the Rectangle
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public double getSize() {
		return size;
	}

	public boolean isPath() {
		return isPath;
	}

	public void setPath(boolean path) {
		isPath = path;
	}

	public Towers getTower() {
		return tower;
	}

	public void setTower(Towers tower) {
		this.tower = tower;
	}

	public void removeTower() {
		this.tower = null;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public double getCenterX() {
		return rectangle.getX() + size / 2;
	}

	public double getCenterY() {
		return rectangle.getY() + size / 2;
	}

	public boolean hasTower() {
		return tower != null;
	}

/*
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell {
	private int row,col;
	private int x,y,width,height;
	private boolean isPath;
	private Towers tower;
	private Rectangle rectangle;

	public Cell(int row, int col, double size, boolean isPath ) {
		this.row = row;
		this.col = col;
		this.width = size;
		this.height = size;
		this.x = col * size;
		this.y = row * size;
		this.isPath = false;

		// Create visual representation
		this.rectangle = new Rectangle(x, y, width, height);
		this.rectangle.setFill(getRandomTowerPlacementColor());
		this.rectangle.setStroke(Color.WHITE);
	}

	private Color getRandomTowerPlacementColor() {
		// Two different yellow tones as specified in requirements
		return Math.random() < 0.5 ? Color.rgb(255, 255, 150) : Color.rgb(255, 255, 180);
	}

	public boolean contains(double x, double y) {
		return x >= this.x && x <= this.x + width && 
				y >= this.y && y <= this.y + height;
	}

	public void setPath(boolean isPath) {
		this.isPath = isPath;
		if (isPath) {
			rectangle.setFill(Color.GRAY);
		}
	}

	public void setTower(Towers tower) {
		this.tower = tower;
		tower.setTowerx((int)(x + width / 2));
		tower.setTowery((int)(y + height / 2));
	}

	public void removeTower() {
		this.tower = null;
	}

	public boolean hasTower() {
		return tower != null; 
	}
	public Towers getTower() { 
		return tower; 
	}

 */

}
