package com.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class selemcin extends Tower {

    public static final int COST = 65;
    private static final double RANGE = 70;
    private static final double DAMAGE = 30;
    private static final double FIRE_RATE = 1.5;

    private static final SpriteSheet SHEET = new SpriteSheet("selemcin.png", 3);
    private static final double SPRITE_SIZE = 54;
    private static final double ATTACK_ANIM_DURATION = 1.0 / FIRE_RATE * 0.6;

    private double attackAnimTimer = 0;

    public selemcin(double x, double y, List<Projectile> projectiles) {
        super(x, y, RANGE, DAMAGE, FIRE_RATE, COST, projectiles);
    }

    @Override
    public void update(double deltaSeconds, List<Enemy> enemies) {
        super.update(deltaSeconds, enemies);
        if (attackAnimTimer > 0) {
            attackAnimTimer -= deltaSeconds;
        }
    }

    @Override
    protected void attack(Enemy target) {
        target.takeDamage(damage);
        attackAnimTimer = ATTACK_ANIM_DURATION; 
    }

    @Override
    public void render(GraphicsContext gc) {
        if (SHEET.isLoaded()) {
            SHEET.render(gc, currentFrame(), x, y, SPRITE_SIZE);
        } else {
            gc.setFill(Color.web("#3E2723"));
            gc.fillRect(x - 9, y - 18, 18, 24);
            gc.setFill(Color.web("#D9B48F"));
            gc.fillOval(x - 6, y - 26, 12, 12);
        }
    }

    private int currentFrame() {
        if (attackAnimTimer <= 0) return 0; 
        double progress = 1 - (attackAnimTimer / ATTACK_ANIM_DURATION);
        return progress < 0.5 ? 1 : 2; 
    }
}