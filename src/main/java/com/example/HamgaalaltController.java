package com.example;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class HamgaalaltController {

    @FXML private Canvas mapCanvas;
    @FXML private Label goldValueLabel;
    @FXML private Label lifeValueLabel;
    @FXML private Label roundValueLabel;

    @FXML private Button btnHarwaac;
    @FXML private Button btnSelemcin;
    @FXML private Button btnMoritHarwaac;
    @FXML private Button btnMoritSelemcin;

    @FXML private Label unitNameLabel;
    @FXML private Label unitCostLabel;
    @FXML private Label unitDamageLabel;
    @FXML private Label unitRangeLabel;

    private static final int CANVAS_WIDTH = 900;
    private static final int CANVAS_HEIGHT = 600;

    private final List<Point2D> path = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Tower> towers = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();

    private WaveManager waveManager;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;
    private long lastNanoTime = -1;

    private int gold = 150;
    private int lives = 20;
    private int totalKills = 0; 
    private int totalGoldEarned = 0; 
    private boolean gameOver = false;
    private boolean dialogShown = false; 

    private String selectedTowerType = "HARWAAC";

    @FXML
    public void initialize() {
        buildPath();
        waveManager = new WaveManager(path);
        gc = mapCanvas.getGraphicsContext2D();
        mapCanvas.setOnMouseClicked(this::placeTower);
        updateHud();

        // Анхдах цэргийн тохиргоо
        selectTower("HARWAAC", btnHarwaac);

        startGameLoop();
    }

    private void buildPath() {
        path.add(new Point2D(-20, 308));
        path.add(new Point2D(240, 308));
        path.add(new Point2D(240, 158));
        path.add(new Point2D(680, 158));
        path.add(new Point2D(680, 305));
        path.add(new Point2D(580, 305));
        path.add(new Point2D(580, 349));
        path.add(new Point2D(637, 395));
    }

    @FXML
    void Start(ActionEvent event) {
        if (gameOver) {
            resetGame();
            return;
        }
        if (!waveManager.isWaveInProgress()) {
            waveManager.startNextWave();
        }
        updateHud();
    }

    @FXML
    void end(ActionEvent event) {
        if (!gameOver) {
            gameOver = true;
            if (!dialogShown) {
                dialogShown = true;
                Platform.runLater(this::handleGameOver);
            }
        }
    }

    @FXML 
    void harwaac_select(ActionEvent event) { 
        selectTower("HARWAAC", btnHarwaac); 
    }

    @FXML 
    void selemcin_select(ActionEvent event) { 
        selectTower("SELEMCIN", btnSelemcin); 
    }

    @FXML 
    void moritHarwaac_select(ActionEvent event) { 
        selectTower("MORIT_HARWAAC", btnMoritHarwaac); 
    }

    @FXML 
    void moritSelemcin_select(ActionEvent event) { 
        selectTower("MORIT_SELEMCIN", btnMoritSelemcin); 
    }

    private void selectTower(String type, Button selectedBtn) {
        selectedTowerType = type;

        btnHarwaac.setStyle("-fx-background-color: black; -fx-border-color: transparent;");
        btnSelemcin.setStyle("-fx-background-color: black; -fx-border-color: transparent;");
        btnMoritHarwaac.setStyle("-fx-background-color: black; -fx-border-color: transparent;");
        btnMoritSelemcin.setStyle("-fx-background-color: black; -fx-border-color: transparent;");

        selectedBtn.setStyle("-fx-background-color: black; -fx-border-color: #ffd700; -fx-border-width: 3px; -fx-border-radius: 5px;");

        switch (type) {
            case "HARWAAC":
                unitNameLabel.setText("Харваач");
                unitCostLabel.setText("Үнэ: " + harwaac.COST + " алт");
                unitDamageLabel.setText("Хохирол: 12");
                unitRangeLabel.setText("Зай: 160");
                break;
            case "SELEMCIN":
                unitNameLabel.setText("Сэлэмчин");
                unitCostLabel.setText("Үнэ: " + selemcin.COST + " алт");
                unitDamageLabel.setText("Хохирол: 30");
                unitRangeLabel.setText("Зай: 70");
                break;
            case "MORIT_HARWAAC":
                unitNameLabel.setText("Морит Харваач");
                unitCostLabel.setText("Үнэ: " + morit_harwaac.COST + " алт");
                unitDamageLabel.setText("Хохирол: 18");
                unitRangeLabel.setText("Зай: 200");
                break;
            case "MORIT_SELEMCIN":
                unitNameLabel.setText("Морит Сэлэмчин");
                unitCostLabel.setText("Үнэ: " + morit_selemcin.COST + " алт");
                unitDamageLabel.setText("Хохирол: 50");
                unitRangeLabel.setText("Зай: 90");
                break;
        }
    }

    private void placeTower(MouseEvent e) {
        if (gameOver || e.getButton() != MouseButton.PRIMARY) return;

        int cost = towerCost(selectedTowerType);
        if (gold < cost) return;

        towers.add(createTower(selectedTowerType, e.getX(), e.getY()));
        gold -= cost;
        updateHud();
    }

    private int towerCost(String type) {
        switch (type) {
            case "SELEMCIN": return selemcin.COST;
            case "MORIT_HARWAAC": return morit_harwaac.COST;
            case "MORIT_SELEMCIN": return morit_selemcin.COST;
            default: return harwaac.COST;
        }
    }

    private Tower createTower(String type, double x, double y) {
        switch (type) {
            case "SELEMCIN": return new selemcin(x, y, projectiles);
            case "MORIT_HARWAAC": return new morit_harwaac(x, y, projectiles);
            case "MORIT_SELEMCIN": return new morit_selemcin(x, y, projectiles);
            default: return new harwaac(x, y, projectiles);
        }
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastNanoTime < 0) lastNanoTime = now;
                double delta = (now - lastNanoTime) / 1_000_000_000.0;
                lastNanoTime = now;

                if (!gameOver) {
                    update(delta);
                }
                render();
            }
        };
        gameLoop.start();
    }

    private void update(double delta) {
        Enemy spawned = waveManager.update(delta);
        if (spawned != null) enemies.add(spawned);

        for (Enemy en : enemies) en.update(delta);
        for (Tower t : towers) t.update(delta, enemies);
        for (Projectile p : projectiles) p.update(delta);

        List<Enemy> toRemove = new ArrayList<>();
        for (Enemy en : enemies) {
            if (en.hasReachedGoal()) {
                lives--;
                toRemove.add(en);
            } else if (en.isDead()) {
                int reward = en.getGoldReward();
                gold += reward;
                totalGoldEarned += reward; 
                totalKills++;
                toRemove.add(en);
            }
        }
        enemies.removeAll(toRemove);
        projectiles.removeIf(Projectile::isFinished);

        if (lives <= 0) {
            lives = 0;
            gameOver = true;

            if (!dialogShown) {
                dialogShown = true;
                Platform.runLater(this::handleGameOver);
            }
        }

        updateHud();
    }

    private void render() {
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        gc.setGlobalAlpha(1.0);

        for (Tower t : towers) t.render(gc);
        for (Projectile p : projectiles) p.render(gc);
        for (Enemy en : enemies) en.render(gc);

        if (gameOver) {
            gc.setFill(Color.rgb(0, 0, 0, 0.6));
            gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(32));
            gc.fillText("ТОГЛООМ ДУУСЛАА", CANVAS_WIDTH / 2.0 - 150, CANVAS_HEIGHT / 2.0);
        }
    }

    private void updateHud() {
        if (goldValueLabel != null) goldValueLabel.setText(String.valueOf(gold));
        if (lifeValueLabel != null) lifeValueLabel.setText(String.valueOf(lives));
        if (roundValueLabel != null) roundValueLabel.setText(String.valueOf(waveManager.getWaveNumber()));
    }

    private void resetGame() {
        towers.clear();
        enemies.clear();
        projectiles.clear();
        gold = 150;
        lives = 20;
        totalKills = 0;
        totalGoldEarned = 0; 
        gameOver = false;
        dialogShown = false;
        waveManager = new WaveManager(path);
        updateHud();
    }

    private void handleGameOver() {
        int finalEarnedGold = this.totalGoldEarned; 
        int finalWave = this.waveManager.getWaveNumber();
        int finalKills = this.totalKills;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Game Over");

        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #ff3333; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label titleLabel = new Label("ТОГЛООМ ДУУСЛАА");
        titleLabel.setTextFill(Color.RED);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 22));

        VBox statsBox = new VBox(5);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        statsBox.setStyle("-fx-background-color: #2a2a2a; -fx-padding: 10px; -fx-background-radius: 5px;");

        Label waveLabel = new Label("🛡️ Хүрсэн тойрог: " + finalWave);
        waveLabel.setTextFill(Color.LIGHTBLUE);
        waveLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label killLabel = new Label("⚔️ Устгасан дайсан: " + finalKills);
        killLabel.setTextFill(Color.LIGHTGREEN);
        killLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label goldLabel = new Label("💰 Цуглуулсан алт: " + finalEarnedGold); 
        goldLabel.setTextFill(Color.YELLOW);
        goldLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        statsBox.getChildren().addAll(waveLabel, killLabel, goldLabel);

        javafx.scene.layout.HBox saveBox = new javafx.scene.layout.HBox(8);
        saveBox.setAlignment(Pos.CENTER);

        TextField nameInput = new TextField();
        nameInput.setPromptText("Нэрээ оруулна уу");
        nameInput.setStyle("-fx-background-color: #2b2b2b; -fx-text-fill: white; -fx-prompt-text-fill: gray; -fx-font-size: 13px;");

        Button saveBtn = new Button("Хадгалах");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        saveBox.getChildren().addAll(nameInput, saveBtn);

        VBox leaderboardBox = new VBox(5);
        leaderboardBox.setAlignment(Pos.CENTER_LEFT);
        leaderboardBox.setStyle("-fx-background-color: #222222; -fx-padding: 10px; -fx-background-radius: 5px;");

        Label leaderTitle = new Label("🏆 ШИЛДЭГ ТОГЛОГЧИД");
        leaderTitle.setTextFill(Color.GOLD);
        leaderTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        leaderboardBox.getChildren().add(leaderTitle);

        Runnable updateLeaderboardUI = () -> {
            leaderboardBox.getChildren().retainAll(leaderTitle);
            List<String> topScores = DatabaseManager.getTopScores();
            if (topScores.isEmpty()) {
                Label emptyLabel = new Label("Одоогоор рекорд бичигдээгүй байна.");
                emptyLabel.setTextFill(Color.GRAY);
                leaderboardBox.getChildren().add(emptyLabel);
            } else {
                for (String score : topScores) {
                    Label scoreLabel = new Label(score);
                    scoreLabel.setTextFill(Color.WHITE);
                    scoreLabel.setFont(Font.font("System", 12));
                    leaderboardBox.getChildren().add(scoreLabel);
                }
            }
        };

        updateLeaderboardUI.run();

        saveBtn.setOnAction(e -> {
            String name = nameInput.getText().trim();
            if (name.isEmpty()) name = "Player";

            DatabaseManager.saveScore(name, finalWave, finalEarnedGold);
            saveBtn.setDisable(true);
            nameInput.setDisable(true);
            saveBtn.setText("Хадгалагдлаа!");

            updateLeaderboardUI.run();
        });

        javafx.scene.layout.HBox actionBox = new javafx.scene.layout.HBox(15);
        actionBox.setAlignment(Pos.CENTER);

        Button restartBtn = new Button("🔄 Дахин эхлэх");
        restartBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 8 15;");
        restartBtn.setOnAction(e -> {
            dialog.getDialogPane().getScene().getWindow().hide();
            resetGame();
        });

        Button exitBtn = new Button("🚪 Гарах");
        exitBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 8 15;");
        exitBtn.setOnAction(e -> {
            dialog.getDialogPane().getScene().getWindow().hide();
            Platform.exit();
        });

        actionBox.getChildren().addAll(restartBtn, exitBtn);

        content.getChildren().addAll(titleLabel, statsBox, saveBox, leaderboardBox, actionBox);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setStyle("-fx-background-color: transparent;");

        dialog.showAndWait();
    }
}