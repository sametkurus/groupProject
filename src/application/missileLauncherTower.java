package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class missileLauncherTower extends Towers {
	  public double lastUpdate;
	  private double time = 0;
	  private List<Enemy> enemies;
	  ImageView image;
	  Enemy nearest = null;
	  GraphicsContext gc;
	  Pane pane;
	  double deltaTime;
	  private List<ExplosionEffect> effects = new ArrayList<>();
	  public List<Missile> missiles = new ArrayList<>();

	
	public missileLauncherTower (int towerx, int towery,GraphicsContext gc,Pane pane) {
		 super(towerx, towery ,30 , 10 , 1 ,50, null );
		 this.gc= gc;
		 this.pane = pane;
		 this.image =  loadTowerImage(towerx, towery);
		 this.startAnimationTimer();

	 }
	 public missileLauncherTower(int towerx, int towery,List<Enemy> enemies,GraphicsContext gc,Pane pane) {
	     super(towerx, towery, 30, 10, 1, 50, new ImageView());
	     this.enemies = enemies;
	     this.gc = gc;
	     this.pane=pane;
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
	              deltaTime = (now - lastUpdate) / 1e9; // Nano -> second
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
	     updateMissiles(deltaTime);
	     updateEffects(deltaTime);
	 }
	 private void updateEffects(double deltaTime) {
	        Iterator<ExplosionEffect> iterator = effects.iterator();
	        while (iterator.hasNext()) {
	            ExplosionEffect effect = iterator.next();
	            effect.updateAndDraw(gc, deltaTime);
	            if (effect.isFinished()) {
	                iterator.remove();
	            }
	        }
	    }
	 private void updateMissiles(double deltaTime) {
	        // Mermileri güncelleme işlemi
	        for (int i = 0; i < missiles.size(); i++) {
	            Missile missile = missiles.get(i);
	            missile.update(deltaTime,pane); // Mermi hareketini güncelle
	            if (!missile.isActive()) {
	                missiles.remove(i);  // Patladıysa, mermiyi listeden kaldır
	                i--; // Listeyi düzenle
	            }
	        }
	    }
 public void shoot() {
	 if (time <= 0) {
         
         double minDist = range;

         // 3. En yakın düşmanı, tower merkezine uzaklığına göre bul
         double centerX = image.getX() + image.getFitWidth() / 2;
         double centerY = image.getY() + image.getFitHeight() / 2;
         nearest = closestEnemy(enemies);
            
         // 4. Eğer menzilde bir hedef varsa, mermi oluşturup ekle
         if (nearest != null && nearest.isAlive()) {
             try {
                 // missile picture
                 Image missileImg = new Image(new FileInputStream("Game/bullet.png"));
                 ImageView missileView = new ImageView(missileImg);
                 missileView.setFitWidth(10);
                 missileView.setFitHeight(10);
                 missileView.setX(centerX -5);// 20 rastgele verillmiştir
                 missileView.setY(centerY- 5);

                 // bullet nesnesi
                 Missile Missile = new Missile(missileView.getX(),missileView.getY() , 30 ,  this ,enemies,nearest,missileView);
                 Missile.createImage();                    // varsa ekstra init için
                 missiles.add(Missile); 

                 ExplosionEffect explosion = new ExplosionEffect(centerX, centerY);
                 effects.add(explosion);
           
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

