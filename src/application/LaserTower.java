package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class LaserTower extends Towers {
 public GraphicsContext gc;
 
 double lastUpdate = 0;
 double deltaTime = 0.5;
 double time =0;
 final double Laser_Damage_per_second = 0.001;
// private Color laserColor = Color.RED;
 Line laser;
 Image image;
 private List<Enemy> enemies;
 private Pane gamePane;
 Enemy nearest;
 ImageView towerImage;
 
 public LaserTower() throws FileNotFoundException {
	super();
	towerImage = new ImageView(new Image(new FileInputStream("C:/Users/Simit/eclipse-workspace/TowerDefenceGame/src/resources/laserTowerImage.png")));
    towerImage.setFitHeight(30);
    towerImage.setFitWidth(30);
}
public LaserTower(double towerx, double towery,Pane pane,GraphicsContext gc) {
	 super(towerx, towery ,100, 10 , 1 ,50);
	 this.gamePane= pane;
	 this.gc= gc;
	 loadTowerImage(towerx,towery);
	 gamePane.getChildren().add(towerImage);
	 
	

	 
 }
 public LaserTower(double x, double y,List<Enemy> enemies,GraphicsContext gc,Pane gamePane) {
     super(x, y, 100, 10, 1, 50);
     this.enemies = enemies;
     this.gamePane= gamePane;
     this.gc= gc;
     loadTowerImage(x,y);
	 gamePane.getChildren().add(towerImage);
     
     this.startAnimationTimer();
     

}

 private void startAnimationTimer(
) {
     new AnimationTimer() {
         @Override
         public void handle(long now) {
        	 // gc.clearRect(0, 0, 800, 700);
        	  
             // Nano saniyeyi saniyeye çevir
             if (lastUpdate == 0) {
                 lastUpdate = now;
                 return;
             }
              deltaTime = (now - lastUpdate) / 1e9; // Nano -> saniye
             lastUpdate = now;

             // Zamanı güncelle
            
             shoot();
             update(deltaTime);
         }
     }.start();
 }

 public void update(double deltaTime) {

   
 }
 private void drawLaserTo(Enemy target) {
     double centerX = getTowerx() ;
     double centerY = getTowery();
     
     if (laser != null) {
    	    gamePane.getChildren().remove(laser);
    	}

     laser = new Line(centerX, centerY, target.getX(), target.getY());
     laser.setStroke(Color.RED);
     laser.setStrokeWidth(2);
     gamePane.getChildren().add(laser);

 }
 public void shoot() {
	    // Eğer hedef ölü ise sıfırla
	    if (nearest != null && !nearest.isAlive()) {
	        nearest = null;
	    }

	    // Yeni hedef seç
	    if (nearest == null) {
	        nearest = closestEnemy(enemies);
	    }

	    // Eğer hedef varsa ve menzildeyse
	    if (nearest != null && nearest.isAlive() && isInRange(nearest)) {
	        nearest.takeDamage((Laser_Damage_per_second));
	        drawLaserTo(nearest);
	       
	    }
	}
 
@Override
 public void loadTowerImage(double x, double y) {
     ImageView view = new ImageView();
     try {
         Image towerImage = new Image(new FileInputStream("C:/Users/Simit/eclipse-workspace/TowerDefenceGame/src/resources/laserTowerImage.png"));
         view.setImage(towerImage);
         view.setFitWidth(40);
         view.setFitHeight(40);
         view.setX(getTowerx());
         view.setY(getTowery());
     } catch (FileNotFoundException e) {
         System.err.println("Tower image not found: " + e.getMessage());
     }
     this.towerImage=  view;
 }
 public void setEnemies(List<Enemy> enemies) {
	    this.enemies = enemies;
	}

 @Override
 public ImageView getImageView() {
     return towerImage;
 }

@Override
protected double getRange() {
	return range;
}
 

}

