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
	Missile Missile;
	private final List<ExplosionEffect> explosionEffects = new ArrayList<>();
	public List<Missile> missiles = new ArrayList<>();




	missileLauncherTower() throws FileNotFoundException {
		super();
		this.image = new ImageView();
		this.enemies = new ArrayList<>();
		Image towerImage = new Image(new FileInputStream("C:\\Users\\Simit\\eclipse-workspace\\TowerDefenceGame\\src\\resources\\missileLauncherTowerImage.png"));
		image.setImage(towerImage);
	}
	public missileLauncherTower (double towerx, double towery,GraphicsContext gc,Pane pane) {
		super(towerx, towery ,100 , 10 , 1 ,50);
		this.gc= gc;
		this.pane = pane;
		
		loadTowerImage(towerx, towery);
		this.pane.getChildren().add(image);
		this.startAnimationTimer();

	}
	public missileLauncherTower(double towerx, double towery,List<Enemy> enemies,GraphicsContext gc,Pane pane) {
		super(towerx, towery, 100, 10, 1, 50);
		this.enemies=enemies;
		this.gc = gc;
		this.pane=pane;
		loadTowerImage(towerx, towery);
		 this.pane.getChildren().add(image);
		this.startAnimationTimer();

	}
	public void startAnimationTimer() {
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
	public List<Enemy> getEnemies() {
		return enemies;
	}
	public void setEnemies(List<Enemy> enemies) {
		this.enemies = enemies;
	}
	public void update(double deltaTime) {
		if (time > 0) {
			time -= deltaTime; // redducing time
		} else {
			shoot();
		}

		updateMissiles(0.1);
		updateEffects(0.02);
	
	}
	private void updateEffects(double deltaTime) {
	    for (Missile missile : missiles) {
	        for (Enemy enemy : enemies) {
	            if (enemy.isAlive() && missileHitsEnemy(missile, enemy)) {
	                // Patlama efekti oluştur
	                explosionEffects.add(new ExplosionEffect(enemy.getX(), enemy.getY()));
	                
	                enemy.takeDamage(missile.getDamage());
	                enemies.get(1).takeDamage(missile.getDamage()/2);
	                // Eğer düşman hasar almalıysa
	                missile.deactivate(); // Mermiyi pasif hale getir

	                break; // Aynı mermi sadece bir düşmana çarpsın
	            }
	        }
	    }

	    // Efektleri güncelle
	    Iterator<ExplosionEffect> iterator = explosionEffects.iterator();
	    while (iterator.hasNext()) {
	        ExplosionEffect effect = iterator.next();
	        if (effect.isFinished()) {
	            iterator.remove();
	        } else {
	            effect.updateAndDraw(pane, deltaTime);
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



			// 3. En yakın düşmanı, tower merkezine uzaklığına göre bul
			double centerX = image.getX() ;
			double centerY = image.getY() ;
			 if(nearest == null) 
		        	nearest= closestEnemy(enemies);
			
			

			// 4. Eğer menzilde bir hedef varsa, mermi oluşturup ekle
			if (nearest != null && nearest.isAlive()) {
				try {
					// missile picture
					Image missileImg = new Image(new FileInputStream("C:\\Users\\Simit\\eclipse-workspace\\TowerDefenceGame\\src\\resources\\bullet4.png"));
					ImageView missileView = new ImageView(missileImg);
					missileView.setFitWidth(10);
					missileView.setFitHeight(10);
					missileView.setX(centerX );// 20 rastgele verillmiştir
					missileView.setY(centerY);

					// bullet nesnesi
					 Missile = new Missile(missileView.getX(),missileView.getY() , 30 ,  this ,enemies,nearest,missileView);
					                  // varsa ekstra init için
					missiles.add(Missile); 
					
					
					pane.getChildren().add(missileView);
				    
					
					

					// zaman sayacını yeniden yükle
					time = 1.00/attackSpeed; 
				} catch (FileNotFoundException ex) {
					System.err.println("Bullet image bulunamadı: " + ex.getMessage());
				}
			}
			 nearest= closestEnemy(enemies);
		}


	



	public   void loadTowerImage(double x , double y){
		ImageView towerView = new ImageView();
		try {
			Image towerImage = new Image(new FileInputStream("C:\\Users\\Simit\\eclipse-workspace\\TowerDefenceGame\\src\\resources\\missileLauncherTowerImage.png"));
			towerView.setImage(towerImage);
			towerView.setFitWidth(40);
			towerView.setFitHeight(40);
			towerView.setX(x);
			towerView.setY(y);
		} catch (FileNotFoundException e) {
			System.err.println("Image file not found: " + e.getMessage());
		}
		this.image= towerView;
	}
	public boolean missileHitsEnemy(Missile missile, Enemy enemy) {
	    double dx = missile.bulletX - enemy.getX();
	    double dy = missile.bulletY - enemy.getY();
	    double distance = Math.sqrt(dx * dx + dy * dy);

	    double hitRadius = 20; // Çarpışma hassasiyetine göre ayarlanabilir

	    return distance < hitRadius;
	}

	@Override
	public ImageView getImageView() {
		return image;
	}
	@Override
	protected double getRange() {

		return range;
	}
	
}


