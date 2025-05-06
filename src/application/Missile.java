package application;

import java.util.List;

import javafx.scene.image.ImageView;

public class Missile extends Projectile {
	   int explosionRadius = 50; 
	   int explosionDamage = 80; 
	   boolean exploded = false;
	   List<Enemy> enemies;
	    
	    
	public Missile(double bulletx, double bullety, double targetx, double targety, int damage, Towers tower, List<Enemy> enemies,
			ImageView image) {
		super(bulletx, bullety, targetx, targety, damage, 5,  tower, image);
		   this.explosionDamage = damage * 2;
	        this.enemies = enemies;	
	}
    @Override
    public void update(double passedTime) {
        super.update(passedTime);
        if (!exploded && hasReachedTarget()) {
            explode();
        }
    }
    private void explode() {
        for (Enemy enemy : enemies) {
            double dx = enemy.getX() - bulletX;
            double dy = enemy.getY() - bulletY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance <= explosionRadius) {
                //enemy canı düşecek ;
            }
        }
        exploded = true;
    }
    public void collision(Enemy enemy) {
        
        if (!exploded) explode();
    }

    @Override
    public void createImage() {
    }
}

	
	

