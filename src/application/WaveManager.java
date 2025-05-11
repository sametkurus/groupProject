package application;
import java.util.List;
import java.util.ArrayList;

/**
 * Bu sınıf, oyundaki düşman dalgalarını (wave) yönetir.
 * Her dalga belirli bir süre sonra başlar ve belirli aralıklarla düşman üretir.
 */
public class WaveManager {

	private List<Wave> waves;           // Tüm dalga verilerini tutar
	private int currentWaveIndex = 0;   // Şu anki aktif dalganın indeksi

	private double waveStartTimer = 0;  // Dalga başlamadan önce bekleme süresi
	private double spawnTimer = 0;      // İki düşman arası süre
	private int enemiesSpawned = 0;     // Şu ana kadar üretilen düşman sayısı
	private boolean waveActive = false; // Dalga şu anda aktif mi?

	private List<Enemy> activeEnemies;  // Sahnedeki canlı düşmanlar
	private ArrayList<Cell> path;       // Düşmanların izleyeceği yol
	private Player player;


	/**
	 * Kurucu: Dalgaları, düşman listesini ve path bilgisini alır.
	 */
	public WaveManager(List<Wave> waves, List<Enemy> activeEnemies, ArrayList<Cell> path ,Player player) {
		this.waves = waves;
		this.activeEnemies = activeEnemies;
		this.path = path;
		this.player = player;

		// İlk dalganın başlaması için bekleme süresi
		this.waveStartTimer = waves.get(0).getStartDelay();
	}

	/**
	 * Her frame çağrılır, zamanlayıcıları işler ve düşman üretir.
	 */
	public void update(double deltaTime) {
		if (currentWaveIndex >= waves.size()) return; // Tüm dalgalar tamamlandıysa çık

		Wave currentWave = waves.get(currentWaveIndex);

		if (!waveActive) {
			// Dalga başlamadan önceki bekleme süresi azaltılır
			waveStartTimer -= deltaTime;

			if (waveStartTimer <= 0) {
				waveActive = true;
				spawnTimer = 0; // ilk düşman hemen çıkabilir
			}
		}

		if (waveActive) {
			spawnTimer -= deltaTime;

			// Yeni düşman üretilecek mi kontrol et
			if (spawnTimer <= 0 && enemiesSpawned < currentWave.getEnemyCount()) {
				spawnEnemy(); // düşman oluştur
				enemiesSpawned++;
				spawnTimer = currentWave.getSpawnDelay(); // sıradaki için bekleme süresi
			}

			// Dalganın tüm düşmanları üretildiyse bir sonraki dalgaya hazırlan
			if (enemiesSpawned == currentWave.getEnemyCount()) {
				waveActive = false;
				currentWaveIndex++;
				enemiesSpawned = 0;

				// Eğer hâlâ dalga kaldıysa yeni bekleme süresini ayarla
				if (currentWaveIndex < waves.size()) {
					waveStartTimer = waves.get(currentWaveIndex).getStartDelay();
				}
			}
		}
	}

	/**
	 * Yeni bir düşman nesnesi oluşturur ve aktif düşman listesine ekler.
	 */
	private void spawnEnemy() {
		Enemy enemy = new Enemy(path, player);
		activeEnemies.add(enemy);
	}

	/**
	 * UI için: Bir sonraki dalganın başlamasına kaç saniye kaldığını döndürür.
	 */
	public double getCountdownToNextWave() {
		if (!waveActive && currentWaveIndex < waves.size()) {
			return Math.max(0, waveStartTimer);
		}
		return 0;
	}

	/**
	 * Tüm dalgalar tamamlandığında true döner.
	 */
	public boolean allWavesCompleted() {
		return currentWaveIndex >= waves.size();
	}
}