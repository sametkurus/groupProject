
//150123034-Samet KURUŞ

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
		this.rectangle = new Rectangle(size, size); // create the Rectangle
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


	public boolean hasTower() {
		return tower != null;
	}

	public void resize(double newSize) {
		this.size = newSize;
		rectangle.setWidth(newSize);  
		rectangle.setHeight(newSize); 
	}

	public void setPosition(double x, double y) {
		// Correctly position the cell at the exact coordinates
		Rectangle rect = getRectangle();
		rect.setX(x);
		rect.setY(y);
	}

	public double getCenterX() {
		return rectangle.getX() + rectangle.getWidth() / 2;
	}

	public double getCenterY() {

		return rectangle.getY() + rectangle.getHeight() / 2;
	}
}
