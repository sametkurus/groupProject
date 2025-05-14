package application;

import java.util.List;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public abstract  class Towers {
	private double towerx ;
	private double towery ;
	double range ;
	int damage;
	double  attackSpeed;
	private int price;
	Enemy target;
	double time ;
	Circle rangeIndicator;
	

	private  ImageView towerImage = new ImageView();
	
	public Towers() {
		rangeIndicator = new Circle(range);
		rangeIndicator.setCenterX(towerx );
		rangeIndicator.setCenterY(towery + towerImage.getFitHeight() / 2);
		rangeIndicator.setStroke(Color.BLACK);
		rangeIndicator.setFill(Color.TRANSPARENT);
	}


	public Towers(double towerx, double towery, int range, int damage, double attackSpeed, int price) {
		this.towerx = towerx;
		this.towery = towery;
		this.range = range;
		this.damage = damage;
		this.attackSpeed = attackSpeed;
		this.price = price;
		


		rangeIndicator = new Circle(range);
		rangeIndicator.setCenterX(towerx  );
		rangeIndicator.setCenterY(towery );
		rangeIndicator.setStroke(Color.RED);
		rangeIndicator.setFill(Color.TRANSPARENT);
	}


	public void setTarget(Enemy newTarget) {
		this.target = newTarget; 
	}

	public boolean isInRange(Enemy enemy) {
		double distance = (Math.sqrt(Math.pow(enemy.getEnemyX() - towerx, 2) + Math.pow(enemy.getEnemyY() -towery,2)));
		if(range >= distance)
			return true;
		else
			return false;
	}
	public Enemy closestEnemy(List<Enemy> enemies) {
		Enemy closestEnemy = null;
		double minDistance = range ;
		for (Enemy enemy : enemies) {
			if (isInRange(enemy)) {
				double distance = Math.sqrt(Math.pow(enemy.getEnemyX() - towerx, 2) + Math.pow(enemy.getEnemyY() - towery, 2));
				if (distance < minDistance) {
					minDistance = distance;
					closestEnemy = enemy;
				}
			}
		}
		return closestEnemy;
	}

	public void Start(double deltaTime, List<Enemy> enemies) {
		//  target selection
		if (target == null || !isInRange(target)) {
			target = closestEnemy(enemies);
		}

		// shooting
		if ((target != null) && ( time >= 1 / attackSpeed)) {
			shoot();
			time = 0;
		}
		time += deltaTime;
	}
	public void showRangeIndicator(BorderPane layer) {
		if (!layer.getChildren().contains(rangeIndicator)) {
			layer.getChildren().add(rangeIndicator);
		}
	}
	public void hideRangeIndicator(BorderPane layer) {
	    if (rangeIndicator != null && layer.getChildren().contains(rangeIndicator)) {
	        layer.getChildren().remove(rangeIndicator);
	    }
	}

	public abstract  void loadTowerImage(double x , double y);
	
	public abstract void shoot() ;
	
	public abstract ImageView getImageView();
	



	public double getTowerx() {
		return towerx;
	}
	public void setTowerx(int towerx) {
		this.towerx = towerx;
	}
	public double getTowery() {
		return towery;
	}
	public void setTowery(int towery) {
		this.towery = towery;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	
	public void setPosition(double x, double y) {
		this.towerx = x;
		this.towery = y;

		// Update the view position
		/*if (towerImage instanceof ImageView) {
			((ImageView) towerImage).setTranslateX(x);
			((ImageView) towerImage).setTranslateY(y);
		} else if (towerImage instanceof javafx.scene.Group) {
			((javafx.scene.Group) view).setTranslateX(x);
			((javafx.scene.Group) view).setTranslateY(y);
		}*/

		// Update range circle position
		if (rangeIndicator != null) {
			rangeIndicator.setCenterX(x);
			rangeIndicator.setCenterY(y);
		}

	}


	protected abstract double getRange();

}
