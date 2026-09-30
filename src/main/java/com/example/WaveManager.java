package com.example;

import javafx.geometry.Point2D;

import java.util.List;


public class WaveManager {

    private final List<Point2D> path;
    private int waveNumber = 0;
    private int enemiesToSpawn = 0;
    private double spawnTimer = 0;
    private static final double SPAWN_INTERVAL = 0.8;
    private boolean waveInProgress = false;

    public WaveManager(List<Point2D> path) {
        this.path = path;
    }

    public boolean isWaveInProgress() {
        return waveInProgress;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public void startNextWave() {
        waveNumber++;
        enemiesToSpawn = 5 + waveNumber * 2;
        spawnTimer = 0;
        waveInProgress = true;
    }

    public Enemy update(double deltaSeconds) {
        if (!waveInProgress || enemiesToSpawn <= 0) {
            if (enemiesToSpawn <= 0) waveInProgress = false;
            return null;
        }

        spawnTimer -= deltaSeconds;
        if (spawnTimer <= 0) {
            spawnTimer = SPAWN_INTERVAL;
            enemiesToSpawn--;

            double health = 40 + waveNumber * 15;
            double speed = 60 + Math.min(waveNumber * 2, 40);
            int gold = 8 + waveNumber;

            return new Enemy(path, health, speed, gold);
        }
        return null;
    }
}
