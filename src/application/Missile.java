package application;

import java.util.List;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Missile extends Projectile {
	private boolean active = true;
	
	   int explosionRadius = 50; 
	   int explosionDamage = 80; 
	   boolean exploded = false;
	   List<Enemy> enemies;
	    
	    
	public Missile(double bulletx, double bullety,  int damage, Towers tower, List<Enemy> enemies,Enemy enemy,
			ImageView image) {
		super(bulletx, bullety, enemy, damage, 5,  tower, image);
		   this.explosionDamage = damage * 2;
	        this.enemies = enemies;	
	}
    public void update(double passedTime, Pane pane) {
    	 moving(passedTime);
	        
	        // 2. Çarpışma kontrolünü yap
	        checkCollision(pane);
        }
    
    private void explode() {
        for (Enemy enemy : enemies) {
            double dx = enemy.getEnemyX() - bulletX;
            double dy = enemy.getEnemyY() - bulletY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance <= explosionRadius && enemy.isAlive()) {
                enemy.takeDamage(explosionDamage);
            }

        }
        exploded = true;
    }
    private void checkCollision(Pane pane) {
        double dx = bulletX - enemy.getEnemyX();
        double dy = bulletY - enemy.getEnemyY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < explosionRadius) {  // Çarpışma mesafesi
            // Düşmana hasar ver
        	explode();
            

            // Mermi sahneden kaldırılır
            pane.getChildren().remove(image);

            // Mermiyi pasif yap
            deactivate();
        }
      
    }

    @Override
    public void createImage() {
    }


public void deactivate() {
    this.active = false;
}
public boolean isActive() {
    return active;
}

}	
	

