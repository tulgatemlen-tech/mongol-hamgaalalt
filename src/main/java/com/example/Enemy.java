package com.example;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.List;

public class Enemy {

    private double x, y;
    private double health;
    private final double maxHealth;
    private final double speed; 
    private final int goldReward;

    private final List<Point2D> path;
    private int targetIndex = 1;
    private boolean reachedGoal = false;
    private boolean dead = false;

    private static Image SPRITE;
    private static final double SPRITE_SIZE = 62;

    // Sprite Sheet анимейшны тохиргоо
    private static final int TOTAL_FRAMES = 3; // Нийт 3 кадр
    private double animTimer = 0;
    private int currentFrame = 0;

   static {
        try {
            // ImageLoader-оор дамжуулан зургаа ачаална
            SPRITE = ImageLoader.get("daisan.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Enemy(List<Point2D> path, double health, double speed, int goldReward) {
        this.path = path;
        this.x = path.get(0).getX();
        this.y = path.get(0).getY();
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.goldReward = goldReward;
    }

    public void update(double deltaSeconds) {
        if (reachedGoal || dead) return;

        // Анимейшны кадр солих логик (0.15 секунд тутамд 1 кадр шилжинэ)
        animTimer += deltaSeconds;
        if (animTimer >= 0.15) {
            currentFrame = (currentFrame + 1) % TOTAL_FRAMES;
            animTimer = 0;
        }

        Point2D target = path.get(targetIndex);
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double dist = Math.hypot(dx, dy);

        double step = speed * deltaSeconds;
        if (dist <= step) {
            x = target.getX();
            y = target.getY();
            targetIndex++;
            if (targetIndex >= path.size()) {
                reachedGoal = true;
            }
        } else {
            x += dx / dist * step;
            y += dy / dist * step;
        }
    }

    public void takeDamage(double amount) {
        health -= amount;
        if (health <= 0) {
            dead = true;
        }
    }

    public void render(GraphicsContext gc) {
        if (SPRITE != null && !SPRITE.isError()) {
            // Эх зургийн 1 кадрийн өргөн ба өндрийг тооцоолох
            double frameWidth = SPRITE.getWidth() / TOTAL_FRAMES;
            double frameHeight = SPRITE.getHeight();

            // Эх зургийн аль хэсгээс (sx, sy) хэдий хэмжээтэй (sw, sh) авахаа заана
            double sx = currentFrame * frameWidth;
            double sy = 0;

            // Canvas дээр хаана (dx, dy), ямар хэмжээтэй (dw, dh) зурахаа заана
            gc.drawImage(
                SPRITE, 
                sx, sy, frameWidth, frameHeight, // Эх зургийн тайрах хэсэг
                x - SPRITE_SIZE / 2, y - SPRITE_SIZE / 2, SPRITE_SIZE, SPRITE_SIZE // Дэлгэц дээр зурах хэмжээ
            );
        } else {
            // Fallback: Зураг олдохгүй үед
            gc.setFill(Color.web("#7A2E2E"));
            gc.fillRect(x - 8, y - 10, 16, 20);
            gc.setFill(Color.web("#C0392B"));
            gc.fillOval(x - 10, y - 4, 20, 14);
        }

        // Амьдралын зурвас
        double barWidth = 24;
        double pct = Math.max(0, health / maxHealth);
        gc.setFill(Color.BLACK);
        gc.fillRect(x - barWidth / 2, y - 20, barWidth, 4);
        gc.setFill(pct > 0.5 ? Color.LIMEGREEN : (pct > 0.25 ? Color.ORANGE : Color.RED));
        gc.fillRect(x - barWidth / 2, y - 20, barWidth * pct, 4);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isDead() { return dead; }
    public boolean hasReachedGoal() { return reachedGoal; }
    public int getGoldReward() { return goldReward; }
}