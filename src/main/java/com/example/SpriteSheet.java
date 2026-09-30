package com.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class SpriteSheet {
    private final Image image;
    private final int totalFrames;

    public SpriteSheet(String imageName, int totalFrames) {
        this.image = ImageLoader.get(imageName);
        this.totalFrames = totalFrames;
    }

    public boolean isLoaded() {
        return image != null && !image.isError();
    }

    public void render(GraphicsContext gc, int frameIndex, double x, double y, double size) {
        if (!isLoaded()) return;

        double frameWidth = image.getWidth() / totalFrames;
        double frameHeight = image.getHeight();

        double sx = frameIndex * frameWidth;
        double sy = 0;

        gc.drawImage(
            image,
            sx, sy, frameWidth, frameHeight,
            x - size / 2, y - size / 2, size, size
        );
    }
}