package application;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Sağlık çubuğu: yeşil bir bar ile can durumunu gösterir.
 * Oranı değiştikçe genişliği değişir.
 */
public class HealthBar extends Group {

    private Rectangle background;  // Arka plan (kırmızı)
    private Rectangle fill;        // Doluluk (yeşil)
    private double barWidth = 20;
    private double barHeight = 4;

    private int maxHealth;
    private int currentHealth;

    /**
     * Sağlık çubuğu oluşturulurken maksimum can değeri verilir.
     */
    public HealthBar(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;

        background = new Rectangle(-barWidth / 2, 0, barWidth, barHeight);
        background.setFill(Color.DARKRED);

        fill = new Rectangle(-barWidth / 2, 0, barWidth, barHeight);
        fill.setFill(Color.LIMEGREEN);

        this.getChildren().addAll(background, fill);
    }

    /**
     * Sağlık değeri güncellenir, yeşil çubuğun genişliği oranlanır.
     */
    public void setHealth(int value) {
        currentHealth = Math.max(0, Math.min(value, maxHealth));
        double ratio = (double) currentHealth / maxHealth;
        fill.setWidth(barWidth * ratio);
    }

    /**
     * Sağlık çubuğunu düşmanın üstünde göstermek için konum ayarlaması yapılır.
     */
    public void setOffset(double offsetY) {
        this.setTranslateY(offsetY); // yukarı kaydır
    }
}