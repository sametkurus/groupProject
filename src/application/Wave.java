package application;

import java.util.List;
import java.util.ArrayList;

public class Wave {
    private int enemyCount;
    private double spawnDelay; // Time between enemy spawns in seconds
    private double delayBeforeStart; // Time to wait before starting this wave
    private int spawnedEnemies;
    private boolean completed;
    private double lastSpawnTime;
    
    public Wave(int enemyCount, double spawnDelay, double delayBeforeStart) {
        this.enemyCount = enemyCount;
        this.spawnDelay = spawnDelay;
        this.delayBeforeStart = delayBeforeStart;
        this.spawnedEnemies = 0;
        this.completed = false;
        this.lastSpawnTime = 0;
    }
    
    public boolean shouldSpawnEnemy(double currentTime) {
        if (spawnedEnemies >= enemyCount) {
            return false;
        }
        
        if (currentTime < delayBeforeStart) {
            return false;
        }
        
        // Adjust time to count from when the wave actually starts
        double adjustedTime = currentTime - delayBeforeStart;
        
        // First enemy spawns immediately when wave starts
        if (spawnedEnemies == 0) {
            lastSpawnTime = adjustedTime;
            return true;
        }
        
        // Check if enough time has passed since last spawn
        if (adjustedTime - lastSpawnTime >= spawnDelay) {
            lastSpawnTime = adjustedTime;
            return true;
        }
        
        return false;
    }
    
    public Enemy spawnEnemy(List<Cell> path, Player player) {
        if (spawnedEnemies < enemyCount) {
            spawnedEnemies++;
            Enemy enemy = new Enemy(path, player);
            
            // Scale enemy attributes based on spawn order
            double speedFactor = 0.01 + (0.002 * spawnedEnemies / enemyCount); // Makes later enemies slightly faster
            enemy.setSpeed(speedFactor);
            
            // Set health based on spawn order (later enemies are tougher)
            int health = 100 + (spawnedEnemies * 10 / enemyCount);
            enemy.setMaxHealth(health);
            
            return enemy;
        }
        return null;
    }
    
    public boolean isCompleted() {
        return spawnedEnemies >= enemyCount && completed;
    }
    
    public void markCompleted() {
        this.completed = true;
    }
    
    public int getEnemyCount() {
        return enemyCount;
    }
    
    public double getSpawnDelay() {
        return spawnDelay;
    }
    
    public double getDelayBeforeStart() {
        return delayBeforeStart;
    }
    
    public int getSpawnedEnemies() {
        return spawnedEnemies;
    }
    
    public double getRemainingTime(double currentTime) {
        if (currentTime < delayBeforeStart) {
            return delayBeforeStart - currentTime;
        }
        return 0;
    }
}