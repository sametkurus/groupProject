package application;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;


 // Bu sınıf, sahnede hareket eden bir düşmanı temsil eder.
  //Enemy bir JavaFX Node'dur (Group) ve daire, üçgen ve sağlık çubuğu içerir.
 
public class Enemy extends Group {

    // Temel bilgiler
    private double x, y;               // Düşmanın ekrandaki konumu
    private int health;               // Mevcut can değeri
    private double speed;             // Hareket hızı
    private List<Cell> path;         // İzlenecek yol
    private int stepIndex;            // Yol üzerindeki geçilen adım
    private boolean isAlive;          // Düşman canlı mı?

    // Grafik bileşenleri
    private Circle body;              // Ana gövde (kırmızı daire)
    private Polygon head;             // Yön belirten üçgen
    private HealthBar healthBar;      // Ayrı bir sınıfta tanımlı sağlık çubuğu

    private final int maxHealth = 30; // Sabit maksimum can

    
    //  Düşman oluşturulurken yol bilgisi verilir ve şekiller tanımlanır.
     
    public Enemy(List<Cell> path, Player player) {
        this.path = path;
        Cell startingCell = path.getFirst();
        this.x = startingCell.getCenterX();
        this.y = startingCell.getCenterY();

        this.health = maxHealth;
        this.speed = 1.0;
        this.stepIndex = 0;
        this.isAlive = true;

        createGraphics();     // Şekil ve sağlık çubuğu oluşturulur
        updateGraphics();     // İlk konum verilir
    }

    /**
     * Düşmanı oluşturan görseller: daire, üçgen, sağlık çubuğu
     */
    private void createGraphics() {
        body = new Circle(0, 0, 10, Color.DARKRED); // merkezde daire
        head = new Polygon(                       // yukarı bakan üçgen
            0.0, -14.0,
            -8.0, -2.0,
            8.0, -2.0
        );
        head.setFill(Color.GOLD);

        healthBar = new HealthBar(maxHealth);
        healthBar.setOffset(-20); // düşmanın üstüne yerleştirilir

        this.getChildren().addAll(body, head, healthBar);
    }

    /**
     * Her frame düşman hareket eder ve sağlık çubuğu güncellenir
     */
    public void step() {
        if (!isAlive) return;

        if (stepIndex < path.size() - 1) {
            Cell next = path.get(stepIndex + 1);
            moveTowards(next);
        } else {
            reachGoal();
        }

        updateGraphics();
    }

    /**
     * Bir sonraki kareye doğru hareket eder.
     */
    private void moveTowards(Cell targetCell) {
    	
        double dx = targetCell.getCenterX() - x;
        double dy = targetCell.getCenterY() - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= speed) {
            x = targetCell.getCenterX();
            y = targetCell.getCenterY();
            stepIndex++;
        } else {
            x += speed * dx / distance;
            y += speed * dy / distance;
        }
    }

    /**
     * Düşman hasar aldığında çağrılır, can azalır.
     */
    public void takeDamage(int damage) {
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
    private void die() {
        isAlive = false;
        this.setVisible(false); // sahnede görünmesin
    }

    /**
     * Hedefe ulaştığında can eksiltmek için kullanılır.
     */
    private void reachGoal() {
        isAlive = false;
        this.setVisible(false);
    }

    
    //  Konum ve sağlık çubuğu görünümü güncellenir.
     
    private void updateGraphics() {
        this.setTranslateX(x);
        this.setTranslateY(y);
    }

    public boolean isAlive() {
        return isAlive;
    }
    
    public double getSpeed () {
    	return this.speed;
    }
    public int getStepIndex () {
    	return this.stepIndex;
    }
    public double getX() { return x; }
    public double getY() { return y; }
}