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
 private List<Enemy> enemies;
 private Pane gamePane;
 Enemy nearest;
 
 public LaserTower(int towerx, int towery,Pane pane,GraphicsContext gc) {
	 super(towerx, towery ,30 , 10 , 1 ,50, null);
	 this.gamePane= pane;
	 this.gc= gc;
	 loadTowerImage();
	 this.startAnimationTimer();

	 
 }
 public LaserTower(int x, int y,List<Enemy> enemies,Pane gamePane) {
     super(x, y, 30, 10, 1, 50, new ImageView());
     this.enemies = enemies;
     this.gamePane= gamePane;
     loadTowerImage();
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
         currentTarget.damage(Laser_Damage_per_second * deltaTime);

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
     gc.strokeLine(centerX, centerY, target.getX(), target.getY());
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
        /* if (currentBeam != null) {
             //gamePane.getChildren().remove(currentBeam.image);
            // projectiles.remove(currentBeam);
             currentBeam = null;*/
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
 private void loadTowerImage() {
     try {
         image = new Image(new FileInputStream("Game/singleshot.png"));
     } catch (FileNotFoundException e) {
         System.err.println("Tower image not found: " + e.getMessage());
     }
 
 }
 public void setEnemies(List<Enemy> enemies) {
	    this.enemies = enemies;
	}

}
