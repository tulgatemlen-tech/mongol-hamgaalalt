package com.example;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class ImageLoader {

    private static final Map<String, Image> cache = new HashMap<>();

    public static Image get(String fileName) {
        return cache.computeIfAbsent(fileName, name -> {
            try {
                InputStream stream = ImageLoader.class.getResourceAsStream("/com/example/" + name);
                if (stream == null) stream = ImageLoader.class.getResourceAsStream("/com/" + name);
                if (stream == null) stream = ImageLoader.class.getResourceAsStream("/" + name);

                if (stream == null) {
                    System.err.println("Зураг олдсонгүй: " + name);
                    return null;
                }

                return new Image(stream);
            } catch (Exception e) {
                System.err.println("Зураг ачаалахад алдаа гарлаа: " + name + " — " + e.getMessage());
                return null;
            }
        });
    }
}