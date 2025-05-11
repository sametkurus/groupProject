package application;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Bu sınıf, düşman öldüğünde sahneye gelen patlama efektini oluşturuyor
 * Her parçacık bir Circle'dır ve belirli bir yöne doğru 500ms boyunca ilerliyor.
 */
public class Explosion extends Group {

    private static final int PARTICLE_COUNT = 20;
    private static final double PARTICLE_RADIUS = 3;
    private static final long DURATION_NS = 500_000_000;

    private List<Circle> particles = new ArrayList<>();
    private List<Double> dxList = new ArrayList<>();
    private List<Double> dyList = new ArrayList<>();

    private long startTime;
    private boolean finished = false;

    public Explosion(double originX, double originY) {
        createParticles(originX, originY);
        startTime = System.nanoTime();
    }

    /**
     *Bu method parçacıkları rastgele yön ve hızlarda oluşturuyor ve sahneye ekliyor.
     */
    private void createParticles(double originX, double originY) {
        Random rand = new Random();

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double speed = rand.nextDouble() * 2 + 1;

            double dx = Math.cos(angle) * speed;
            double dy = Math.sin(angle) * speed;

            Circle particle = new Circle(PARTICLE_RADIUS);
            particle.setFill(Color.ORANGE);
            particle.setTranslateX(originX);
            particle.setTranslateY(originY);

            particles.add(particle);
            dxList.add(dx);
            dyList.add(dy);
            this.getChildren().add(particle); // sahneye ekle
        }
    }

    /**
     * Her frame çağrılıyor ve parçacıkları hareket ettiriyor.
     * 500ms sonra explosion kendini "bitmiş" olarak işaretler.
     */
    public void update(double deltaTime) {
        for (int i = 0; i < particles.size(); i++) {
            Circle p = particles.get(i);
            p.setTranslateX(p.getTranslateX() + dxList.get(i));
            p.setTranslateY(p.getTranslateY() + dyList.get(i));
        }

        if (System.nanoTime() - startTime >= DURATION_NS) {
            finished = true;
            this.setVisible(false); // sahneden gizle
        }
    }

    /**
     * Patlama süresi bittiyse true dönecek.
     */
    public boolean isFinished() {
        return finished;
    }
}