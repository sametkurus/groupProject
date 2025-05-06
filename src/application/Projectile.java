package application;

import javafx.scene.image.ImageView;

public abstract class Projectile {
	 double  bulletX ;
	 double bulletY;
	 double targetX;
	 double targetY;
	 double distance;
	 
	 int damage;
	 int speed;
	 Towers tower ;
	 ImageView image;
	public Projectile(double bulletx, double bullety, double targetx, double targety, int damage, int speed,
			Towers tower, ImageView image) {
		this.bulletX = bulletx;
		this.bulletY = bullety;
		this.targetX = targetx;
		this.targetY = targety;
		this.damage = damage;
		this.speed = speed;
		this.tower = tower;
		this.image = image;
	}
	
	public void  update(double passedTime) {
		moving(passedTime);
		
	}

    protected void moving(double passedTime) {
        double distance = Math.sqrt(Math.pow(targetX - bulletX, 2) + Math.pow(targetY - bulletY, 2));
        if (distance > 0) {
            double cosx = (targetX - bulletX) / distance;
            double cosy = (targetY - bulletY) / distance;
            bulletX += cosx * speed * passedTime;
            bulletY += cosy * speed * passedTime;
        }
    }
    public boolean hasReachedTarget() {
        return distance < 3;
    }
	public abstract void collision (Enemy enemy) ;
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
