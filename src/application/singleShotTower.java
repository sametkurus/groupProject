package application;
import java.util.List;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
public class singleShotTower extends Towers {
 private double time =0;
 private List<Enemy> enemies;
 ImageView image;
 long lastUpdate;
 
 public singleShotTower(int towerx, int towery) {
	 super(towerx, towery ,30 , 10 , 1 ,50, null);
	 this.image =  loadTowerImage(towerx, towery);
	 
	 
 }

 
 public singleShotTower(int towerx, int towery,List<Enemy> enemies) {
     super(towerx, towery, 30, 10, 1, 50, new ImageView());
     this.enemies = enemies;
     this.image =  loadTowerImage(towerx, towery);
     startAnimationTimer();
    
 }
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
	 // 1. Zaman sayacını azalt
     // 2. Menzil süresi dolduysa (time<=0) ateş et
     if (time <= 0) {
         Enemy nearest = null;
         double minDist = range;

         // 3. En yakın düşmanı, tower merkezine uzaklığına göre bul
         double centerX = image.getX() + image.getFitWidth() / 2;
         double centerY = image.getY() + image.getFitHeight() / 2;
         for (Enemy e : enemies) {
             double deltax = e.getX() - centerX;
             double deltay = e.getY() - centerY;
             double distance = Math.sqrt(deltax*deltax+ deltay*deltay);
             if (distance <= range && distance < minDist) {
                 minDist = distance;
                 nearest = e;
             }
         }

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
                 bullet Bullet = new bullet(centerX,centerY,nearest.getX(),nearest.getY() ,damage , 5, this ,bulletView);
                 Bullet.createImage();                    // varsa ekstra init için
                    // sahneye ekle
                    // mantığa ekle

                 // zaman sayacını yeniden yükle
                 time = 1.00/attackSpeed; 
             } catch (FileNotFoundException ex) {
                 System.err.println("Bullet image bulunamadı: " + ex.getMessage());
             }
         }
     }
 }
 public void setEnemies(List<Enemy> enemies) {
	    this.enemies = enemies;
	}
 }
 
 


