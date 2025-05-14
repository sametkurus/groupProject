package application;

import java.util.List;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Enemy extends Group {
    private List<Cell> path;
    private int stepIndex;
    private double speed;
    private int health;
    private int maxHealth;
    private boolean alive;
    private Circle body;
    private Rectangle healthBar;
    private Rectangle healthBarBackground;
    private Player player;
    
    public Enemy(List<Cell> path, Player player) {
        this.path = path;
        this.stepIndex = 0;
        this.speed = 0.01; // Speed factor (0-1)
        this.maxHealth = 100;
        this.health = maxHealth;
        this.alive = true;
        this.player = player;
        
        System.out.println("Enemy created at" + path.getFirst().getRow() + "," + path.getFirst().getCol());
        // Create enemy visual representation
        body = new Circle(15);
        body.setFill(Color.RED);
        body.setStroke(Color.BLACK);
        
        // Create health bar background
        healthBarBackground = new Rectangle(30, 5);
        healthBarBackground.setFill(Color.GRAY);
        healthBarBackground.setX(-15);
        healthBarBackground.setY(-25);
        
        // Create health bar
        healthBar = new Rectangle(30, 5);
        healthBar.setFill(Color.GREEN);
        healthBar.setX(-15);
        healthBar.setY(-25);
        
        // Add to group
        getChildren().addAll(body, healthBarBackground, healthBar);
        
        // Initial position
        if (!path.isEmpty()) {
            Cell startCell = path.get(0);
            setTranslateX(startCell.getCenterX());
            setTranslateY(startCell.getCenterY());
        }
    }
    
    public void step() {
        if (!alive) return;
        
        if (stepIndex < path.size() - 1) {
            // Current position
            double currentX = getTranslateX();
            double currentY = getTranslateY();
            
            // Target position (next cell)
            Cell nextCell = path.get(stepIndex + 1);
            double targetX = nextCell.getCenterX();
            double targetY = nextCell.getCenterY();
            
            // Calculate direction
            double dx = targetX - currentX;
            double dy = targetY - currentY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            System.out.println("Now the enemy is at " +nextCell.getRow() + "," + nextCell.getCol());
            // Move towards target
            if (distance > 1) {
                // Move a percentage of the way towards the target
                double moveX = dx * speed;
                double moveY = dy * speed;
                setTranslateX(currentX + moveX);
                setTranslateY(currentY + moveY);
            } else {
                // Reached next cell
                stepIndex++;
                if (stepIndex >= path.size() - 1) {
                    // Reached end of path
                    reachEnd();
                }
            }
        }
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        updateHealthBar();
        
        if (health <= 0) {
            die();
        }
    }
    
    private void updateHealthBar() {
        double healthPercentage = (double) health / maxHealth;
        healthBar.setWidth(30 * healthPercentage);
    }
    
    private void die() {
        alive = false;
        // Don't remove from parent yet - let the game loop handle clean-up
    }
    
    private void reachEnd() {
        player.loseLife();
        alive = false;
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public int getStepIndex() {
        return stepIndex;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public double getSpeed() {
    	return this.speed;
    }
    
    public double getEnemyX() {
    	return body.getCenterX();
    }
    
    public double getEnemyY() {
    	return body.getCenterY();
    }
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        updateHealthBar();
    }
}