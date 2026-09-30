package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/towerdefense";
    private static final String USER = "root";
    private static final String PASSWORD = "Temka0407"; // Өөрийн нууц үг

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Тоглоомын үр дүнг хадгалах
    public static void saveScore(String playerName, int waveReached, int goldEarned) {
        String sql = "INSERT INTO high_scores (player_name, wave_reached, gold_earned) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerName);
            stmt.setInt(2, waveReached);
            stmt.setInt(3, goldEarned);
            stmt.executeUpdate();
            System.out.println("Үр дүн амжилттай хадгалагдлаа!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Шилдэг 5 тоглогчийг авчрах (Leaderboard)
    public static List<String> getTopScores() {
        List<String> topScores = new ArrayList<>();
        // wave_reached-ээр эхэлж эрэмбэлээд, ижил бол gold_earned-ээр эрэмбэлнэ
        String sql = "SELECT player_name, wave_reached, gold_earned FROM high_scores ORDER BY wave_reached DESC, gold_earned DESC LIMIT 5";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            int rank = 1;
            while (rs.next()) {
                String name = rs.getString("player_name");
                int wave = rs.getInt("wave_reached");
                int gold = rs.getInt("gold_earned");
                topScores.add(rank + ". " + name + " - " + wave + " тойрог (" + gold + " алт)");
                rank++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return topScores;
    }
}