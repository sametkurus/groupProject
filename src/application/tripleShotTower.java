package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class tripleShotTower extends Towers {
	public double lastUpdate;
	private int towerx ;
	private int towery ;
    private double time = 0;
    private final int Bullet_Count = 3;
    private List<Enemy> enemies;
    ImageView image;
    Pane gamePane;
    
    private List<bullet> bullets = new ArrayList<>();
    
    
    tripleShotTower() throws FileNotFoundException {
   	 super();
   	 this.image = new ImageView();
   	 Image towerImage = new Image(new FileInputStream("C:\\Users\\Simit\\eclipse-workspace\\TowerDefenceGame\\src\\resources\\tripleShotTowerImage.png"));
     image.setImage(towerImage);
   }
    public tripleShotTower(double towerx, double towery,Pane gamepane) {
   	 super(towerx, towery ,30 , 10 , 1 ,50);
   	 //this.gamePane= gamePane;
   	loadTowerImage(towerx, towery);
	 gamepane.getChildren().add(image);
   	 
    }
    public tripleShotTower(int x, int y,List<Enemy> enemies,Pane gamePane) {
        super(x, y, 30, 10, 1, 50);
        this.enemies = enemies;
        this.gamePane= gamePane;
        loadTowerImage(towerx, towery);
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
	     updateBullets(deltaTime);
	 }
    private void updateBullets(double deltaTime) {
        // Mermileri güncelle
        for (int i = 0; i < bullets.size(); i++) {
            bullet currentBullet = bullets.get(i);
            currentBullet.update(deltaTime, gamePane);  // Bullet'ın hareketini güncelle

            // Çarpışma kontrolü ve öldüğünde listeyi temizle
            if (!currentBullet.isActive()) {
                bullets.remove(i);
                i--;  // Listeyi yeniden düzenlemek için i'yi azaltıyoruz
            }
        }
    }
    
   public void shoot()
   {if (time <= 0) {
       Enemy nearest = null;
       double minDist = range;

       // 3. En yakın düşmanı, tower merkezine uzaklığına göre bul
       double centerX = image.getX() + image.getFitWidth() / 2;
       double centerY = image.getY() + image.getFitHeight() / 2;
       List<Enemy> targets = findClosestEnemies(3);
       for (Enemy enemy : targets) {
           try {
               // Görsel
               Image bulletImage = new Image(new FileInputStream("C:\\Users\\Simit\\eclipse-workspace\\TowerDefenceGame\\src\\resources\\bullet4.png"));
               ImageView bulletView = new ImageView(bulletImage);
               bulletView.setFitWidth(10);
               bulletView.setFitHeight(10);
               bulletView.setX(centerX);
               bulletView.setY(centerY);

               // Mermi nesnesi
               bullet Bullet = new bullet(centerX,centerY,enemy ,damage , 5, this ,bulletView);
               Bullet.createImage();
               bullets.add(Bullet); 

               
               gamePane.getChildren().add(bulletView);
               // projectiles.add(newBullet);
               
           } catch (FileNotFoundException e) {
               System.err.println("Bullet image not found: " + e.getMessage());
           }
       }

       // Zamanı sıfırla (ateş hızıyla ilişkili)
       time = 1.0 /attackSpeed;
   }
   }
   public  void loadTowerImage(double x, double y) {
		 ImageView towerView = new ImageView();
		 try {
	         Image towerImage = new Image(new FileInputStream("C:\\Users\\Simit\\eclipse-workspace\\TowerDefenceGame\\src\\resources\\tripleShotTowerImage.png"));
	         towerView.setImage(towerImage);
	         towerView.setFitWidth(40);
	         towerView.setFitHeight(40);
	         towerView.setX(x);
	         towerView.setY(y);
	     } catch (FileNotFoundException e) {
	         System.err.println("Image file not found: " + e.getMessage());
	     }
		 this.image = towerView;
	 }

  List<Enemy> findClosestEnemies(int count) {
    List<Enemy> closestEnemies = new ArrayList<>();
    List<Enemy> remainingEnemies = new ArrayList<>(enemies); 

    for (int i = 0; i < count && remainingEnemies.size() > 0; i++) {
        Enemy closest = null;
        double minDistance = range;
        for (Enemy enemy : remainingEnemies) {
            if (isInRange(enemy)) { 
                double distance = Math.sqrt(Math.pow(enemy.getX() - towerx, 2) + Math.pow(enemy.getY() - towery, 2));
                if (distance < minDistance) {
                    minDistance = distance;
                    closest = enemy;
                }
            }
        }
        if (closest != null) {
            closestEnemies.add(closest);
            remainingEnemies.remove(closest); 
        } else {
            break;
        }
    }
    return closestEnemies;
}
  @Override
  public ImageView getImageView() {
      return image;
  }
@Override
protected double getRange() {
	return range;
}

public void setEnemies(List<Enemy> enemies) {
	    this.enemies = enemies;
	}

}
