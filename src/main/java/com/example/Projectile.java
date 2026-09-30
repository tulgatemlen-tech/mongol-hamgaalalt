package com.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Projectile {

    private double x, y;
    private final double targetSpeed = 500;
    private final Enemy target;
    private final double damage;
    private boolean hit = false;

    public Projectile(double startX, double startY, Enemy target, double damage) {
        this.x = startX;
        this.y = startY;
        this.target = target;
        this.damage = damage;
    }

    public void update(double deltaSeconds) {
        if (hit || target == null || target.isDead() || target.hasReachedGoal()) {
            hit = true;
            return;
        }
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double dist = Math.hypot(dx, dy);
        double step = targetSpeed * deltaSeconds;

        if (dist <= step) {
            target.takeDamage(damage);
            hit = true;
        } else {
            x += dx / dist * step;
            y += dy / dist * step;
        }
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.web("#4A3B2A"));
        gc.fillOval(x - 3, y - 3, 6, 6);
    }

    public boolean isFinished() { return hit; }
}
