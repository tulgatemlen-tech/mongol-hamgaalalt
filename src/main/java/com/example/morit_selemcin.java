package com.example;

import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public class morit_selemcin extends Tower {

    public static final int COST = 110;
    private static final double RANGE = 90;
    private static final double DAMAGE = 50;
    private static final double FIRE_RATE = 1.2;

    private static final SpriteSheet SHEET = new SpriteSheet("morit_selemcin.png", 3);
    private static final double SPRITE_SIZE = 64;
    private static final double ATTACK_ANIM_DURATION = 1.0 / FIRE_RATE * 0.6;

    private double attackAnimTimer = 0;

    public morit_selemcin(double x, double y, List<Projectile> projectiles) {
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
            gc.setFill(javafx.scene.paint.Color.web("#5B2C0E"));
            gc.fillRect(x - 11, y - 20, 22, 26);
        }
    }

    private int currentFrame() {
        if (attackAnimTimer <= 0) return 0;
        double progress = 1 - (attackAnimTimer / ATTACK_ANIM_DURATION);
        return progress < 0.5 ? 1 : 2;
    }
}