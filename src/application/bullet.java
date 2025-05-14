package application;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class bullet extends Projectile{
	private boolean active = true;
	
	
	public bullet(double bulletx, double bullety, Enemy enemy, int damage, int speed, Towers tower,
			ImageView image) {
		super(bulletx, bullety, enemy, damage, speed, tower, image);
		
	}
	
	 public void update(double deltaTime, Pane pane) {
	        // 1. Mermiyi hareket ettir
	        moving(deltaTime);
	        
	        // 2. Çarpışma kontrolünü yap
	        checkCollision(pane);
	    }
	 private void checkCollision(Pane pane) {
	        double dx = bulletX - enemy.getEnemyX();
	        double dy = bulletY - enemy.getEnemyY();
	        double distance = Math.sqrt(dx * dx + dy * dy);

	        if (distance < 15) {  // Çarpışma mesafesi
	            // Düşmana hasar ver
	            enemy.takeDamage(getDamage());

	            // Mermi sahneden kaldırılır
	            pane.getChildren().remove(image);

	            // Mermiyi pasif yap
	            deactivate();
	        }
	    }
	
	public void createImage() {
		
	}
	

	public boolean isActive() {
	    return active;
	}

	public void deactivate() {
	    this.active = false;
	}


}
