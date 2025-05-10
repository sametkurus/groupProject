package application;

import java.util.List;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public abstract  class Towers {
	private double towerx ;
	private double towery ;
	private javafx.scene.Node view;
	double range ;
	int damage;
	double  attackSpeed;
	private int price;
	Enemy target;
	double time ;
	Circle rangeIndicator;

	ImageView towerImage = new ImageView();
	
	public Towers(int towerx, int towery, int range, int damage, double attackSpeed, int price, ImageView tower) {
		this.towerx = towerx;
		this.towery = towery;
		this.range = range;
		this.damage = damage;
		this.attackSpeed = attackSpeed;
		this.price = price;
		this.towerImage = tower;

		rangeIndicator = new Circle(range);
		rangeIndicator.setCenterX(towerx + towerImage.getFitWidth() / 2);
		rangeIndicator.setCenterY(towery + towerImage.getFitHeight() / 2);
		rangeIndicator.setStroke(Color.RED);
		rangeIndicator.setFill(Color.TRANSPARENT);
	}


	public void setTarget(Enemy newTarget) {
		this.target = newTarget; 
	}

	public boolean isInRange(Enemy enemy) {
		double distance = (Math.sqrt(Math.pow(enemy.getX() - towerx, 2) + Math.pow(enemy.getY() -towery,2)));
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
				double distance = Math.sqrt(Math.pow(enemy.getX() - towerx, 2) + Math.pow(enemy.getY() - towery, 2));
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
	public void showRangeIndicator(Pane layer) {
		if (!layer.getChildren().contains(rangeIndicator)) {
			layer.getChildren().add(rangeIndicator);
		}
	}

	public abstract  ImageView loadTowerImage(int x , int y);
	public abstract void shoot() ;


	public double getTowerx() {
		return towerx;
	}
	public void setTowerx(double towerx) {
		this.towerx = towerx;
	}
	public double getTowery() {
		return towery;
	}
	public void setTowery(double towery) {
		this.towery = towery;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	
	
	//Updates by Samet
	 public javafx.scene.Node getView() {
	        return view;
	    }
	 
	public void setPosition(double x, double y) {
        this.towerx = x;
        this.towery = y;
        
        // Update the view position
        if (view instanceof javafx.scene.shape.Shape) {
            ((javafx.scene.shape.Shape) view).setTranslateX(x);
            ((javafx.scene.shape.Shape) view).setTranslateY(y);
        } else if (view instanceof javafx.scene.Group) {
            ((javafx.scene.Group) view).setTranslateX(x);
            ((javafx.scene.Group) view).setTranslateY(y);
        }
        
        // Update range circle position
        if (rangeIndicator != null) {
        	rangeIndicator.setCenterX(x);
        	rangeIndicator.setCenterY(y);
        }
    }
}
