package application;

import java.util.ArrayList;
import java.util.List;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.animation.AnimationTimer;



public class singleShotTower extends Towers {
 private double time =0;
 private List<Enemy> enemies;
   ImageView towerView;
 long lastUpdate;
 double deltaTime = 0.5;
 Pane pane ;
 Enemy nearest = null;





 public singleShotTower() throws FileNotFoundException {
	 super();
     towerView = new ImageView(new Image(new FileInputStream("C:/Users/Simit/eclipse-workspace/TowerDefenceGame/src/resources/singleShotTowerImage.png")));
     towerView.setFitHeight(30);
     towerView.setFitWidth(30);
 }




private List<bullet> bullets = new ArrayList<>();
 
 public singleShotTower(double towerx, double towery,Pane pane) {
	 super(towerx, towery ,100 , 10 , 1 ,50);
	 this.pane= pane;
	 loadTowerImage(towerx, towery);
	 this.pane.getChildren().add(towerView);
	 
	
	 
 }

 
 public singleShotTower(double towerx, double towery,List<Enemy> enemies,Pane pane) {
     super(towerx, towery, 100, 10, 1, 50);
     this.enemies = enemies;
     this.pane= pane;
     loadTowerImage(towerx, towery);
     this.pane.getChildren().add(towerView);
     startAnimationTimer();
    
 }
 
 
 @Override
 public void loadTowerImage(double x, double y) {
     try {
         Image image = new Image(new FileInputStream("C:/Users/Simit/eclipse-workspace/TowerDefenceGame/src/resources/singleShotTowerImage.png")); // Replace with actual image path
         towerView = new ImageView(image);
         towerView.setFitWidth(40);
         towerView.setFitHeight(40);
         towerView.setPreserveRatio(true);
         towerView.setSmooth(true);
         
         // Set the position properly - this is crucial
         setPosition(x, y);
         
         // Store the image view in the parent class for reference
         setTowerImage(towerView);
         
     } catch (FileNotFoundException e) {
         System.err.println("Tower image not found: " + e.getMessage());
         
         // Create a fallback image for testing
         towerView = new ImageView();
         towerView.setFitWidth(40);
         towerView.setFitHeight(40);
     }
 }
 
 public  void startAnimationTimer() {
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

 public void update(double deltaTime) {
	
     if (time > 0 ) {
         time -= deltaTime; // Gerçek zamanlı azaltma
     } else {
         shoot();
     }
     updateBullets(0.1);
 }
 
 public void shoot() {
	 // 1. Zaman sayacını azalt
     // 2. Menzil süresi dolduysa (time<=0) ateş et
     if (time <= 0) {
         ;

         // 3. En yakın düşmanı, tower merkezine uzaklığına göre bul
         double centerX = getTowerx() ;
         double centerY = getTowery();
         ;
        if(nearest == null) 
        	nearest= closestEnemy(enemies);
       // System.out.println(isInRange(nearest));
        
        if(nearest!=null) {
         // 4. Eğer menzilde bir hedef varsa, mermi oluşturup ekle
         if(isInRange(nearest)) {
             try {
                 // Mermi resmi
                 Image bulletImg = new Image(new FileInputStream("C:/Users/Simit/eclipse-workspace/TowerDefenceGame/src/resources/bullet4.png"));
                 ImageView bulletView = new ImageView(bulletImg);
                 bulletView.setFitWidth(10);
                 bulletView.setFitHeight(10);
                 bulletView.setX(centerX);
                 bulletView.setY(centerY);

                 // bullet nesnesi
                 bullet Bullet = new bullet(centerX,centerY,nearest ,damage , 30, this ,bulletView);
                 bullets.add(Bullet);  // Mermiyi listeye ekle
                 Bullet.createImage();
               
                 pane.getChildren().add(bulletView);
                    
             System.out.println("shot alışıyor");
             updateBullets(deltaTime);

                 // zaman sayacını yeniden yükle
                 time = 1.00/attackSpeed; 
             } catch (FileNotFoundException ex) {
                 System.err.println("Bullet image bulunamadı: " + ex.getMessage());
                 
             }
             
         }
         
         
          
      
     }
        nearest= closestEnemy(enemies);}
    
    	 
     
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
 @Override
 public ImageView getImageView() {
     return towerView;
 }
 

 public void setEnemies(List<Enemy> enemies) {
	    this.enemies = enemies;
	}





public void setTowerView(ImageView towerView) {
	this.towerView = towerView;
}


@Override
protected double getRange() {
	
	return range;
}
 }

 


