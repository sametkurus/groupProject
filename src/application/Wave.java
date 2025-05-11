package application;
// Wave sınıfına spawnedEnemies özelliği ekleyin
// (Kodda Wave sınıfının tam tanımını görmediğimden dolayı bunu varsayarak ekliyorum)
public class Wave {
	private int enemyCount;
	private double timeBetweenEnemies;
	private double delayBeforeStart;
	private int spawnedEnemies = 0;  // Yeni eklenen özellik

	public Wave(int enemyCount, double timeBetweenEnemies, double delayBeforeStart) {
		this.enemyCount = enemyCount;
		this.timeBetweenEnemies = timeBetweenEnemies;
		this.delayBeforeStart = delayBeforeStart;
	}

	// Diğer get/set metodları
	public int getEnemyCount() { return enemyCount; }
	public double getTimeBetweenEnemies() { return timeBetweenEnemies; }
	public double getDelayBeforeStart() { return delayBeforeStart; }

	// Yeni eklenen metodlar
	public int getSpawnedEnemies() { return spawnedEnemies; }
	public void incrementSpawnedEnemies() { spawnedEnemies++; }
	public void resetSpawnedEnemies() { spawnedEnemies = 0; }
}