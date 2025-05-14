package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class LaserTower extends Towers {
 public GraphicsContext gc;
 public Enemy currentTarget;
 double lastUpdate = 0;
 double time =0;
 final double Laser_Damage_per_second = 10;
 private Color laserColor = Color.RED;
 Image image;
 ImageView towerImage;
 private List<Enemy> enemies;
 private Pane gamePane;
 Enemy nearest;
 
 public LaserTower() throws FileNotFoundException {
	super();
    towerImage = new ImageView(new Image(new FileInputStream("C:\\Users\\Simit\\eclipse-workspace\\TowerDefenceGame\\src\\resources\\laserTowerImage.png")));
    towerImage.setFitHeight(30);
    towerImage.setFitWidth(30);
}
public LaserTower(double towerx, double towery,Pane pane,GraphicsContext gc) {
	 super(towerx, towery ,30 , 10 , 1 ,50);
	 this.gamePane= pane;
	 this.gc= gc;
	 loadTowerImage(towerx,towery);
	

	 
 }
 public LaserTower(int x, int y,List<Enemy> enemies,Pane gamePane) {
     super(x, y, 30, 10, 1, 50);
     this.enemies = enemies;
     this.gamePane= gamePane;
     loadTowerImage(x,y);
     this.startAnimationTimer();
     

}

 private void startAnimationTimer() {
     new AnimationTimer() {
         @Override
         public void handle(long now) {
             // Nano saniyeyi saniyeye çevir
             if (lastUpdate == 0) {
                 lastUpdate = now;
                 return;
             }
             double deltaTime = (now - lastUpdate) / 1e9; // Nano -> saniye
             lastUpdate = now;

             // Zamanı güncelle
             update(deltaTime);
         }
     }.start();
 }

 private void update(double deltaTime) {
     if (time > 0) {
         time -= deltaTime; // Gerçek zamanlı azaltma
     } else {
         shoot();

     }
     if (currentTarget != null && currentTarget.isAlive()) {
         // Sürekli hasar uygula
         currentTarget.takeDamage((int)(Laser_Damage_per_second * deltaTime));

         drawTower();
         drawLaserTo(currentTarget);
     } else {
         currentTarget = null;
     }
 }
 private void drawLaserTo(Enemy target) {
     double centerX = getTowerx() + 20;
     double centerY = getTowery() + 20;

     gc.setStroke(laserColor);
     gc.setLineWidth(2);
     gc.strokeLine(centerX, centerY, target.getEnemyX(), target.getEnemyY());
 }
 private void drawTower() {
         gc.drawImage(image, getTowerx(), getTowery(), 40, 40);
     
 }
 public void shoot() {
	 double centerX = getTowerx() + 20;
     double centerY = getTowery() + 20;

     // Geçerli hedefi kontrol et
     if ((currentTarget != null) && (!currentTarget.isAlive())) {
         currentTarget = null;
         }
     

     if (currentTarget == null) {
         // Yeni hedef ara
    	 if (currentTarget != null && !currentTarget.isAlive()) {
             currentTarget = null;
         }

         if (currentTarget == null) {
             Enemy nearest = closestEnemy(enemies);
             if (nearest != null) {
                 currentTarget = nearest;
             }
         }

         if (currentTarget != null) {
             time = 1.0 / attackSpeed;
         }
     }
  
 }
 //currentTarget.takeDamage(beamDamagePerSecond * deltaSeconds);
     
 
 public void setEnemies(List<Enemy> enemies) {
	    this.enemies = enemies;
	}
 
public ImageView getImageView() {
   return towerImage;
 }
@Override
public void loadTowerImage(double x, double y) {
	ImageView view = new ImageView();
    try {
        Image towerImage = new Image(new FileInputStream("C:\\Users\\Simit\\eclipse-workspace\\TowerDefenceGame\\src\\resources\\laserTowerImage.png"));
        view.setImage(towerImage);
        view.setFitWidth(40);
        view.setFitHeight(40);
        view.setX(x);
        view.setY(y);
    } catch (FileNotFoundException e) {
        System.err.println("Tower image not found: " + e.getMessage());
    }
this.towerImage = view;
}
@Override
protected double getRange() {
	return range;
}
 

}

