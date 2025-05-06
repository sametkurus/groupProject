package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class LaserTower extends Towers {
	 
 public laserBeam currentBeam;
 public Enemy currentTarget;
 double lastUpdate = 0;
 double time =0;
 final double Laser_Damage_per_second = 10;
 private Color laserColor = Color.RED;
 ImageView image;
 private List<Enemy> enemies;
 
 public LaserTower(int towerx, int towery) {
	 super(towerx, towery ,30 , 10 , 1 ,50, null);
	 this.image =  loadTowerImage(towerx, towery);
	 this.startAnimationTimer();

	 
 }
 public LaserTower(int x, int y,List<Enemy> enemies) {
     super(x, y, 30, 10, 1, 50, new ImageView());
     this.enemies = enemies;
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
 }
 public void shoot() {
	 double centerX = image.getX() + image.getFitWidth() / 2;
     double centerY = image.getY() + image.getFitHeight() / 2;

     // Geçerli hedefi kontrol et
     if ((currentTarget != null) && (!currentTarget.isAlive())) {
         currentTarget = null;
         if (currentBeam != null) {
             //gamePane.getChildren().remove(currentBeam.image);
            // projectiles.remove(currentBeam);
             currentBeam = null;
         }
     }

     if (currentTarget == null) {
         // Yeni hedef ara
         Enemy nearest = null;
         double minDist = range;

         for (Enemy e : enemies) {
             if (!e.isAlive()) continue;
             double deltax = e.getX() - centerX;
             double deltay = e.getY() - centerY;
             double distance = Math.hypot(deltax, deltay);
             if (distance <= range && distance < minDist) {
                 minDist = distance;
                 nearest = e;
             }
         }
     
         if (nearest != null) {
         currentTarget = nearest;
         ImageView beamView = new ImageView();  // isteğe göre lazer efekti resmi yüklenebilir

         laserBeam Beam = new laserBeam (centerX,centerY,nearest.getX(),nearest.getY(), damage , 0,Laser_Damage_per_second, this ,beamView);
         Beam.createImage();                 
         //gamePane.getChildren().add(beamView);
         //projectiles.add(beam);

         currentBeam = Beam;
     }
         if (currentTarget != null && currentBeam != null) {
             currentBeam.targetX = currentTarget.getX();
             currentBeam.targetY = currentTarget.getY();
         }
     }
     time = 1.00/attackSpeed; 
  
 }
 //currentTarget.takeDamage(beamDamagePerSecond * deltaSeconds);
 public  ImageView loadTowerImage(int x, int y) {

	 ImageView towerView = new ImageView();
	 try {
         Image towerImage = new Image(new FileInputStream("Game/singleshot.png"));
         towerView.setImage(towerImage);
         towerView.setFitWidth(40);
         towerView.setFitHeight(40);
         towerView.setX(x);
         towerView.setY(y);
     } catch (FileNotFoundException e) {
         System.err.println("Image file not found: " + e.getMessage());
     }
	 return towerView;
 }
}
