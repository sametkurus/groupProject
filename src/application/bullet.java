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
	        double dx = bulletX - enemy.getX();
	        double dy = bulletY - enemy.getY();
	        double distance = Math.sqrt(dx * dx + dy * dy);

	        if (distance < 15) {  // Çarpışma mesafesi
	            // Düşmana hasar ver
	            enemy.takeDamage(getDamage());

	            // Mermi sahneden kaldırılır
	            pane.getChildren().remove(image);

	            // Eğer düşman öldüyse, onu sahneden kaldır
	            if (!enemy.isAlive()) {
	                pane.getChildren().remove(enemy.getView());
	            }

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
