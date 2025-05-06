package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class missileLauncherTower extends Towers {
	  public double lastUpdate;
	  private double time = 0;
	  private List<Enemy> enemies;
	  ImageView image;
	
	public missileLauncherTower (int towerx, int towery) {
		 super(towerx, towery ,30 , 10 , 1 ,50, null );
		 this.image =  loadTowerImage(towerx, towery);
		 this.startAnimationTimer();

	 }
	 public missileLauncherTower(int towerx, int towery,List<Enemy> enemies) {
	     super(towerx, towery, 30, 10, 1, 50, new ImageView());
	     this.enemies = enemies;
	     this.image =  loadTowerImage(towerx, towery);
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
	             double deltaTime = (now - lastUpdate) / 1e9; // Nano -> second
	             lastUpdate = now;

	             // Update time
	             update(deltaTime);
	         }
	     }.start();
	 }
	 private void update(double deltaTime) {
	     if (time > 0) {
	         time -= deltaTime; // redducing time
	     } else {
	         shoot();
	     }
	 }
 public void shoot() {
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
         if (nearest != null && nearest.isAlive()) {
             try {
                 // missile picture
                 Image missileImg = new Image(new FileInputStream("Game/bullet.png"));
                 ImageView missileView = new ImageView(missileImg);
                 missileView.setFitWidth(10);
                 missileView.setFitHeight(10);
                 missileView.setX(centerX);
                 missileView.setY(centerY);

                 // bullet nesnesi
                 Missile Missile = new Missile(centerX,centerY,nearest.getX(),nearest.getY() ,damage ,  this ,enemies,missileView);
                 Missile.createImage();                    // varsa ekstra init için
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

 
 public  ImageView loadTowerImage(int x , int y){
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
