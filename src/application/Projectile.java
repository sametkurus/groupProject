package application;

import javafx.scene.image.ImageView;

public abstract class Projectile {
	 double bulletX ;
	 double bulletY;
	 Enemy enemy;
	 double distance;
	 
	 int damage;
	 int speed;
	 Towers tower ;
	 ImageView image;
	public Projectile(double bulletx, double bullety, Enemy enemy, int damage, int speed,
			Towers tower, ImageView image) {
		this.bulletX = bulletx;
		this.bulletY = bullety;
		this.enemy=enemy;
		this.damage = damage;
		this.speed = speed;
		this.tower = tower;
		this.image = image;
	}
	
	protected void moving(double passedTime) {
	    // Düşmanın gelecekteki pozisyonunu tahmin et
	    double[] futurePosition = predictFuturePosition(enemy);
	    double futureX = futurePosition[0];
	    double futureY = futurePosition[1];

	    // Hedefe olan yön vektörü
	    double dx = futureX - bulletX;
	    double dy = futureY - bulletY;
	    double distance = Math.sqrt(dx * dx + dy * dy);

	    if (distance > 0) {
	        double directionX = dx / distance;
	        double directionY = dy / distance;

	        // Mermiyi ilerlet
	        bulletX += directionX * speed * passedTime;
	        bulletY += directionY * speed * passedTime;

	        // ImageView konumunu güncelle
	        image.setX(bulletX);
	        image.setY(bulletY);
	    }
	}


    
	public  double[] predictFuturePosition(Enemy enemy) {

	    double dx = enemy.getEnemyX() - bulletX;
	    double dy = enemy.getEnemyY() - bulletY;
	    double distance = Math.sqrt(dx * dx + dy * dy);
	    double timeToReach = distance / speed;

	    double futureX = enemy.getEnemyX() + enemy.getSpeed()* timeToReach;
	    double futureY = enemy.getEnemyY() + enemy.getSpeed() * timeToReach;

	    return new double[]{futureX, futureY};
	}

	public abstract void createImage();

	public double getBulletX() {
		return bulletX;
	}

	public double getBulletY() {
		return bulletY;
	}

	public int getDamage() {
		return damage;
	}

	public Towers getTower() {
		return tower;
	}
	
	
	 
}
