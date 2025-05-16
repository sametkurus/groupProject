package application;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;



public class ExplosionEffect {

    private static final int PARTICLE_COUNT = 75;
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    

    public ExplosionEffect(double x, double y) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            Particle p = new Particle(x, y);
            particles.add(p); // EKLİYORUZ!
        }
    }


    public boolean isFinished() {
        return particles.isEmpty();
    }

    public void updateAndDraw(Pane pane, double deltaTime) {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            if (!p.update(deltaTime)) {
                pane.getChildren().remove(p.circle);
                iterator.remove();
            } else {
                if (!pane.getChildren().contains(p.circle)) {
                    pane.getChildren().add(p.circle);
                }
            }
        }
    }


    private class Particle {
        double x, y;
        double vx, vy;
        double size;
        Color color;
        double lifetime;
        boolean isSmoke;
        Circle circle;

        public Particle(double startX, double startY) {
            this.x = startX;
            this.y = startY;

            isSmoke = random.nextDouble() > 0.7;

            if (isSmoke) {
                this.vx = random.nextDouble() * 0.4 - 0.2;
                this.vy = -(random.nextDouble() * 0.5);
                this.size = 4 + random.nextDouble() * 6;
                this.color = Color.gray(random.nextDouble() * 0.5, 0.5);
                this.lifetime = 1.5 + random.nextDouble();
            } else {
                this.vx = random.nextDouble() * 2 - 1;
                this.vy = random.nextDouble() * 2 - 1;
                this.size = random.nextDouble() * 4;
                this.color = Color.rgb(
                    255,
                    (int) (150 + random.nextDouble() * 105),
                    0,
                    0.8
                );
                this.lifetime = random.nextDouble() / 2;
            }

            circle = new Circle(x, y, size);
            circle.setFill(color);
        }


        public boolean update(double deltaTime) {
            vy += 0.5 * deltaTime;
            vx *= 0.99;
            vy *= 0.99;
            x += vx * deltaTime * 60;
            y += vy * deltaTime * 60;

            if (isSmoke) {
                size += deltaTime * 3;
                circle.setRadius(size);
            }

            lifetime -= deltaTime;
            if (lifetime < 0) lifetime = 0;

            // Pozisyonu güncelle
            circle.setCenterX(x);
            circle.setCenterY(y);

            return lifetime > 0;
        }

    }
}
