package application;

import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class laserBeam extends Projectile {
	private Color lasercolor = Color.RED;
	private double damagePerSecond;

	public laserBeam(double bulletx, double bullety, double targetx, double targety, int damage, int speed, double damagePerSecond,
			Towers tower, ImageView image) {
		super(bulletx, bullety, targetx, targety, damage, speed, tower, image);
		this.damagePerSecond = damagePerSecond;
		this.speed=0 ;
	}
	
	public void update() {
		
	}
	public  void collision (Enemy enemy) {
		
	}
	public void createImage() {
		
	}
	public boolean hasReachedTarget() {
        return false; 
    }
	

 
}
