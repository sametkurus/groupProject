package application;

//Bir düşman dalgasının bilgilerini tutar
public class Wave {

	private int enemyCount;     // Dalga başına kaç düşman üretilecek
	private double spawnDelay;  // Her düşman arasında kaç saniye olacak
	private double startDelay;  // Dalgadan önce kaç saniye beklenmeli

	// Kurucu metot - wave bilgilerini alır ve kaydeder
	public Wave(int enemyCount, double spawnDelay, double startDelay) {
		this.enemyCount = enemyCount;
		this.spawnDelay = spawnDelay;
		this.startDelay = startDelay;
	}

	// Düşman sayısını döndürür
	public int getEnemyCount() {
		return enemyCount;
	}

	// Düşmanlar arası süreyi döndürür
	public double getSpawnDelay() {
		return spawnDelay;
	}

	// Dalgadan önceki bekleme süresi
	public double getStartDelay() {
		return startDelay;
	}
}