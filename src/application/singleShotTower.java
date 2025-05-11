package application;

import java.util.ArrayList;
import java.util.List;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class singleShotTower extends Towers {
 private double time =0;
 private List<Enemy> enemies;
 private ImageView towerView;
 long lastUpdate;
 double deltaTime;
 Pane pane ;


 
 private List<bullet> bullets = new ArrayList<>();
 
 public singleShotTower(int towerx, int towery,Pane pane) {
	 super(towerx, towery ,30 , 10 , 1 ,50, null);
	 this.pane= pane;
	 this.towerView = loadTowerImage(towerx, towery);
	 this.pane.getChildren().add(towerView);
	
	 
 }

 
 public singleShotTower(int towerx, int towery,List<Enemy> enemies,Pane pane) {
     super(towerx, towery, 30, 10, 1, 50, new ImageView());
     this.enemies = enemies;
     this.pane= pane;
     this.towerView = loadTowerImage(towerx, towery);
     this.pane.getChildren().add(towerView);
     startAnimationTimer();
    
 }

 private ImageView loadTowerImage(int x, int y) {
     ImageView view = new ImageView();
     try {
         Image towerImage = new Image(new FileInputStream("Game/singleshot.png"));
         view.setImage(towerImage);
         view.setFitWidth(40);
         view.setFitHeight(40);
         view.setX(x);
         view.setY(y);
     } catch (FileNotFoundException e) {
         System.err.println("Tower image not found: " + e.getMessage());
     }
     return view;
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
             deltaTime = (now - lastUpdate) / 1e9; // Nano -> saniye
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
     updateBullets(deltaTime);
 }
 public void shoot() {
	 // 1. Zaman sayacını azalt
     // 2. Menzil süresi dolduysa (time<=0) ateş et
     if (time <= 0) {
         Enemy nearest = null;
         double minDist = range;

         // 3. En yakın düşmanı, tower merkezine uzaklığına göre bul
         double centerX = getTowerx() + 20;
         double centerY = getTowery() + 20;
         nearest = closestEnemy(enemies);
         

         // 4. Eğer menzilde bir hedef varsa, mermi oluşturup ekle
         if (nearest != null) {
             try {
                 // Mermi resmi
                 Image bulletImg = new Image(new FileInputStream("Game/bullet.png"));
                 ImageView bulletView = new ImageView(bulletImg);
                 bulletView.setFitWidth(10);
                 bulletView.setFitHeight(10);
                 bulletView.setX(centerX);
                 bulletView.setY(centerY);

                 // bullet nesnesi
                 bullet Bullet = new bullet(centerX,centerY,nearest ,damage , 5, this ,bulletView);
                 bullets.add(Bullet);  // Mermiyi listeye ekle
                 Bullet.createImage();
                 pane.getChildren().add(bulletView);
                    
             

                 // zaman sayacını yeniden yükle
                 time = 1.00/attackSpeed; 
             } catch (FileNotFoundException ex) {
                 System.err.println("Bullet image bulunamadı: " + ex.getMessage());
             }
         }
     }
 }
 private void updateBullets(double deltaTime) {
     // Mermileri güncelle
     for (int i = 0; i < bullets.size(); i++) {
         bullet currentBullet = bullets.get(i);
         currentBullet.update(deltaTime, pane);  // Bullet'ın hareketini güncelle

         // Çarpışma kontrolü ve öldüğünde listeyi temizle
         if (!currentBullet.isActive()) {
             bullets.remove(i);
             i--;  // Listeyi yeniden düzenlemek için i'yi azaltıyoruz
         }
     }
 }
 

 public void setEnemies(List<Enemy> enemies) {
	    this.enemies = enemies;
	}
 }
 


