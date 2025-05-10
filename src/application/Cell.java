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


	public boolean hasTower() {
		return tower != null;
	}
	
	public void resize(double newSize) {
	    // Hücrenin boyutunu güncelle
	    Rectangle rect = getRectangle();
	    rect.setWidth(newSize);  
	    rect.setHeight(newSize); 
	}

	public void setPosition(double x, double y) {
	    // Hücrenin pozisyonunu güncelle
	    Rectangle rect = getRectangle();
	    rect.setX(x + (rect.getWidth() )); 
	    rect.setY(y + (rect.getHeight() )); 
	}

	public double getCenterX() {
	    Rectangle rect = getRectangle();
	    return rect.getX() + rect.getWidth() / 2;
	}

	public double getCenterY() {
	    Rectangle rect = getRectangle();
	    return rect.getY() + rect.getHeight() / 2;
	}
}
