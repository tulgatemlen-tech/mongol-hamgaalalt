package com.example;

import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public class morit_harwaac extends Tower {

    public static final int COST = 90;
    private static final double RANGE = 200;
    private static final double DAMAGE = 18;
    private static final double FIRE_RATE = 1.0;

    private static final SpriteSheet SHEET = new SpriteSheet("morit_harwaac.png", 3);
    private static final double SPRITE_SIZE = 64;
    private static final double ATTACK_ANIM_DURATION = 1.0 / FIRE_RATE * 0.6;

    private double attackAnimTimer = 0;

    public morit_harwaac(double x, double y, List<Projectile> projectiles) {
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
        projectiles.add(new Projectile(x, y, target, damage));
        attackAnimTimer = ATTACK_ANIM_DURATION;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (SHEET.isLoaded()) {
            SHEET.render(gc, currentFrame(), x, y, SPRITE_SIZE);
        } else {
            gc.setFill(javafx.scene.paint.Color.web("#4A2E12"));
            gc.fillOval(x - 16, y - 6, 32, 18);
        }
    }

    private int currentFrame() {
        if (attackAnimTimer <= 0) return 0;
        double progress = 1 - (attackAnimTimer / ATTACK_ANIM_DURATION);
        return progress < 0.5 ? 1 : 2;
    }
}