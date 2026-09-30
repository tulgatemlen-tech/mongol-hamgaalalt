package com.example;

import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public abstract class Tower {

    protected double x, y;
    protected double range;
    protected double damage;
    protected double fireRate;
    protected int cost;

    private double cooldown = 0;

    protected final List<Projectile> projectiles;

    public Tower(double x, double y, double range, double damage, double fireRate, int cost,
                 List<Projectile> projectiles) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.damage = damage;
        this.fireRate = fireRate;
        this.cost = cost;
        this.projectiles = projectiles;
    }

    public void update(double deltaSeconds, List<Enemy> enemies) {
        cooldown -= deltaSeconds;
        if (cooldown <= 0) {
            Enemy target = findTarget(enemies);
            if (target != null) {
                attack(target);
                cooldown = 1.0 / fireRate;
            }
        }
    }

    protected Enemy findTarget(List<Enemy> enemies) {
        Enemy closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Enemy e : enemies) {
            if (e.isDead() || e.hasReachedGoal()) continue;
            double dist = Math.hypot(e.getX() - x, e.getY() - y);
            if (dist <= range && dist < closestDist) {
                closest = e;
                closestDist = dist;
            }
        }
        return closest;
    }

    protected abstract void attack(Enemy target);

    public abstract void render(GraphicsContext gc);

    public void renderRange(GraphicsContext gc) {
        gc.setStroke(javafx.scene.paint.Color.rgb(255, 255, 255, 0.4));
        gc.strokeOval(x - range, y - range, range * 2, range * 2);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getCost() { return cost; }
}
