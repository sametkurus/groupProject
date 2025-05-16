package application;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


import java.util.List;

// Bu sınıf, sahnede hareket eden bir düşmanı temsil eder.
// Enemy bir JavaFX Node'dur (Group) ve daire, üçgen ve sağlık çubuğu içerir.

public class Enemy extends Group {

    // Temel bilgiler
    private double x, y;               // Düşmanın ekrandaki konumu
    private int health;                // Mevcut can değeri
    private double speed;              // Hareket hızı
    private List<Cell> path;           // İzlenecek yol
    private int stepIndex;             // Yol üzerindeki geçilen adım
    private boolean isAlive;           // Düşman canlı mı?

    // Grafik bileşenleri
    private Circle body;               // Ana gövde (kırmızı daire)
    private HealthBar healthBar;       // Ayrı bir sınıfta tanımlı sağlık çubuğu

    private final int maxHealth = 30;  // Sabit maksimum can
    private ImageView enemyImageView;  // Düşman resmi

    // Düşman oluşturulurken yol bilgisi verilir ve şekiller tanımlanır.
    public Enemy(List<Cell> path, Player player) {
        this.path = path;
        
        // PATH DÜZELTME: Başlangıç hücresine erişim öncesi kontrol ekleme
        if (path == null || path.isEmpty()) {
            System.out.println("HATA: Düşman için geçerli bir yol bulunamadı!");
            this.x = 0;
            this.y = 0;
        } else {
            Cell startingCell = path.get(0);
            System.out.println("Düşman başlangıç: " + startingCell.getRow() + "," + startingCell.getCol() + 
                              " @ (" + startingCell.getCenterX() + "," + startingCell.getCenterY() + ")");
            this.x = startingCell.getCenterX();
            this.y = startingCell.getCenterY();
        }

        this.health = maxHealth;
        this.speed = 1.0;
        this.stepIndex = 0;
        this.isAlive = true;

        // Önce grafikleri oluştur
        createGraphics();
        // İlk konum verilir
        updateGraphics(); 
    }

    /**
     * Düşmanı oluşturan görseller: daire, üçgen, sağlık çubuğu
     */
    private void createGraphics() {
        // Yedek daire grafiği oluştur (resim yüklenemezse kullanılacak)
        body = new Circle(15, Color.RED);
        
        try {
            // Resmi göreceli yoldan yüklemeye çalış
            Image enemyImage = new Image(getClass().getResourceAsStream("/resources/Enemy1.png"));
            enemyImageView = new ImageView(enemyImage);
            enemyImageView.setFitWidth(30);
            enemyImageView.setFitHeight(30);
            enemyImageView.setLayoutX(-15);
            enemyImageView.setLayoutY(-15);
            this.getChildren().add(enemyImageView);
        } catch (Exception e) {
            System.out.println("Düşman resmi yüklenemedi: " + e.getMessage());
            // Resim yüklenemezse daire göster
            this.getChildren().add(body);
        }

        healthBar = new HealthBar(maxHealth);
        healthBar.setOffset(-20); // düşmanın üstüne yerleştirilir
        this.getChildren().add(healthBar);
    }

    /**
     * Her frame düşman hareket eder ve sağlık çubuğu güncellenir
     */
    public void step() {
        if (!isAlive) return;

        // PATH DÜZELTME: Yol kontrolü
        if (path == null || path.isEmpty()) {
            System.out.println("HATA: Geçerli bir yol bulunamadı!");
            return;
        }

        if (stepIndex < path.size() - 1) {
            Cell next = path.get(stepIndex + 1);
            moveTowards(next);
            
            // Debug için hareketleri izle
            System.out.println("Düşman hareket: " + x + "," + y + " -> Hedef: " + 
                               next.getCenterX() + "," + next.getCenterY() + 
                               " (Adım: " + stepIndex + "/" + (path.size()-1) + ")");
        } else {
            reachGoal();
        }

        updateGraphics();
    }

    /**
     * Bir sonraki kareye doğru hareket eder.
     */
    private void moveTowards(Cell targetCell) {
        double targetX = targetCell.getCenterX();
        double targetY = targetCell.getCenterY();
        
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < speed) {
            x = targetX;
            y = targetY;
            stepIndex++;
            System.out.println("Düşman bir sonraki hücreye ulaştı. Yeni adım: " + stepIndex);
        } else {
            x += speed * dx / distance;
            y += speed * dy / distance;
        }
    }

    /**
     * Düşman hasar aldığında çağrılır, can azalır.
     */
    public void takeDamage(double damage) {
        if (!isAlive) return;

        health -= damage;
        healthBar.setHealth(health);

        if (health <= 0) {
            die();
        }
    }

    /**
     * Düşman ölür, sahneden gizlenir.
     */
    public void die() {
        isAlive = false;
        this.setVisible(false); // sahnede görünmesin
    }

    /**
     * Hedefe ulaştığında can eksiltmek için kullanılır.
     */
    private void reachGoal() {
        isAlive = false;
        this.setVisible(false);
        System.out.println("Düşman hedefe ulaştı!");
    }

    // Konum ve sağlık çubuğu görünümü güncellenir.
    private void updateGraphics() {
        this.setLayoutX(x);
        this.setLayoutY(y);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public double getSpeed() {
        return this.speed;
    }
    
    public int getStepIndex() {
        return this.stepIndex;
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
}