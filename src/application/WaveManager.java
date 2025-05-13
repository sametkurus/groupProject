package application;

import java.util.List;
import java.util.ArrayList;

public class WaveManager {
    private List<Wave> waves;
    private List<Enemy> enemies;
    private List<Cell> path;
    private Player player;
    private int currentWaveIndex;
    private double elapsedTime;
    private boolean allWavesCompleted;
    
    public WaveManager(List<Wave> waves, List<Enemy> enemies, List<Cell> path, Player player) {
        this.waves = waves;
        this.enemies = enemies;
        this.path = path;
        this.player = player;
        this.currentWaveIndex = 0;
        this.elapsedTime = 0;
        this.allWavesCompleted = false;
    }
    
    public void update(double deltaTime) {
        if (allWavesCompleted || currentWaveIndex >= waves.size()) {
            return;
        }
        
        elapsedTime += deltaTime;
        Wave currentWave = waves.get(currentWaveIndex);
        
        // Check if it's time to spawn an enemy
        if (currentWave.shouldSpawnEnemy(elapsedTime)) {
            Enemy enemy = currentWave.spawnEnemy(path, player);
            if (enemy != null) {
                enemies.add(enemy);
                System.out.println("Spawned enemy " + currentWave.getSpawnedEnemies() + "/" + currentWave.getEnemyCount());
            }
        }
        
        // Check if current wave is complete
        boolean waveComplete = currentWave.getSpawnedEnemies() >= currentWave.getEnemyCount();
        
        // Check if all enemies from this wave have been defeated or reached the end
        boolean allEnemiesProcessed = true;
        for (Enemy enemy : new ArrayList<>(enemies)) {
            if (!enemy.isAlive()) {
                continue; // Skip dead enemies that haven't been removed yet
            }
            
            // If this enemy was spawned in the current wave, the wave is not fully processed
            allEnemiesProcessed = false;
        }
        
        // If all enemies spawned and processed, move to next wave
        if (waveComplete && allEnemiesProcessed) {
            currentWave.markCompleted();
            currentWaveIndex++;
            System.out.println("Wave " + currentWaveIndex + " completed.");
            
            // Check if all waves are completed
            if (currentWaveIndex >= waves.size()) {
                allWavesCompleted = true;
                System.out.println("All waves completed!");
            }
        }
    }
    
    public Wave getCurrentWave() {
        if (currentWaveIndex < waves.size()) {
            return waves.get(currentWaveIndex);
        }
        return null;
    }
    
    public int getCurrentWaveIndex() {
        return currentWaveIndex;
    }
    
    public double getTimeUntilNextWave() {
        if (currentWaveIndex < waves.size()) {
            Wave wave = waves.get(currentWaveIndex);
            return wave.getRemainingTime(elapsedTime);
        }
        return -1; // No more waves
    }
    
    public boolean allWavesCompleted() {
        return allWavesCompleted;
    }
    
    public void reset() {
        currentWaveIndex = 0;
        elapsedTime = 0;
        allWavesCompleted = false;
    }
    
    public double getElapsedTime() {
        return elapsedTime;
    }
}