package application;
import java.util.List;

import javafx.scene.layout.Pane;

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
	private boolean waveActive = false; // Dalga başlamadan önce aktif değil

	private List<Enemy> activeEnemies;  // Sahnedeki canlı düşmanlar
	private List<Cell> path;            // Düşmanların izleyeceği yol
	private Player player;
	Pane pane;
	GameMap gameMap;
	int level;
	/**
	 * Kurucu: Dalgaları, düşman listesini ve path bilgisini alır.
	 */
	public WaveManager(List<Wave> waves, List<Enemy> activeEnemies, List<Cell> path, Player player,Pane pane,int level) {
		this.level= level;
		this.waves = waves;
		this.activeEnemies = activeEnemies;
		this.path = path;
		this.player = player;
		this.pane = pane;
		this.gameMap = gameMap;
		// İlk dalganın başlaması için bekleme süresi (ilk dalga hemen başlasın)
		if (!waves.isEmpty()) {
			this.waveStartTimer = 1.0; // Sadece 1 saniye bekle
			this.waveActive = false;   // Aktif değil, zamanlayıcı sonrası aktif olacak
		}
	}

	/**
	 * Her frame çağrılır, zamanlayıcıları işler ve düşman üretir.
	 * @throws Exception 
	 */
	public void update(double deltaTime) throws Exception {
		if (currentWaveIndex >= waves.size())
			return; // Tüm dalgalar tamamlandıysa çık
		Wave currentWave = waves.get(currentWaveIndex);
		if (!waveActive) { // Dalga başlamadan önceki bekleme süresi azaltılır
			waveStartTimer -= deltaTime;
			if (waveStartTimer <= 0) {
				waveActive = true;
				spawnTimer = 0; // ilk düşman hemen üretilir
				waveStartTimer = System.nanoTime(); // Dalga başlama zamanını kaydet

				enemiesSpawned = 0; //reset enemies spawned
				System.out.println("Wave " + (currentWaveIndex + 1) + " started!");
			}
		} else {
			if (enemiesSpawned < currentWave.getEnemyCount()) {
				spawnTimer += deltaTime;
				if (spawnTimer >= currentWave.getDelayBeforeStart()) {
					spawnEnemy();
					enemiesSpawned++;

					spawnTimer = 0; // Zamanlayıcıyı sıfırla
				}
			}
			if (enemiesSpawned >= currentWave.getEnemyCount() && activeEnemies.isEmpty()) {
				waveActive = false;
				enemiesSpawned = 0;
				currentWaveIndex++;
				System.out.println("Wave " + currentWaveIndex + " completed!");
				// Bir sonraki dalga için hazırlık yap
				if (currentWaveIndex < waves.size()) {
					waveStartTimer = waves.get(currentWaveIndex).getDelayBeforeStart();
					System.out.println("Preparing next wave. Delay: " + waveStartTimer);
				}
			}
		}
		// Update enemies' positions
		for (int i = 0; i < activeEnemies.size(); i++) {
			Enemy enemy = activeEnemies.get(i);
			enemy.step();
			if (!enemy.isAlive()) {
				activeEnemies.remove(i);
				i--;
			}
		}
	}

	public int getEnemiesSpawned() {
		return enemiesSpawned;
	}

	public void setEnemiesSpawned(int enemiesSpawned) {
		this.enemiesSpawned = enemiesSpawned;
	}

	public List<Enemy> getActiveEnemies() {
		return activeEnemies;
	}

	public void setActiveEnemies(List<Enemy> activeEnemies) {
		this.activeEnemies = activeEnemies;
	}

	/**
	 * Yeni bir düşman nesnesi oluşturur ve aktif düşman listesine ekler.
	 */
	private void spawnEnemy() {
		Enemy enemy = new Enemy(path,player);
		activeEnemies.add(enemy);

		pane.getChildren().add(enemy);
		//System.out.println("Enemy spawned at: " + enemy.getTranslateX() + ", " + enemy.getTranslateY());System.out.println("Enemy spawned at: " + enemy.getPositionX() + ", " + enemy.getPositionY());

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

	/**
	 * Şu anki dalga indeksini döndürür.
	 */
	public int getCurrentWaveIndex() {
		return currentWaveIndex;
	}

	/**
	 * Dalganın aktif olup olmadığını döndürür.
	 */
	public boolean isWaveActive() {
		return waveActive;
	}
}