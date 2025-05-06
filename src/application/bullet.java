package application;

import javafx.scene.image.ImageView;

public class bullet extends Projectile{

	public bullet(double bulletx, double bullety, double targetx, double targety, int damage, int speed, Towers tower,
			ImageView image) {
		super(bulletx, bullety, targetx, targety, damage, speed, tower, image);
	}
	public  void collision (Enemy enemy) {
		
	}
	public void createImage() {
		
	}

}
