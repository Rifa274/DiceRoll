package dicegame.view;

import dicegame.model.GameEngine;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;

public class GameWindow {

    private GameEngine engine;

    // Containers
    private StackPane rootLayout;
    private VBox gameContent;
    private VBox gameOverOverlay;

    // UI Elements
    private Label roundLabel, statusLabel, resultValueLabel;
    private Button rollButton;
    private ImageView centralDiceImg;

    // Player Sections
    private VBox p1Box, p2Box;
    private Label p1ScoreLabel, p2ScoreLabel;

    public GameWindow(Stage stage) {
        engine = GameEngine.getInstance();
        initUI(stage);
        updateUIState();
    }

    private void initUI(Stage stage) {
        // --- 1. GAME CONTENT ---

        // Header: Round Info
        roundLabel = new Label();
        roundLabel.getStyleClass().add("header-label");

        // --- THE ARENA (Left Player - Center Dice - Right Player) ---

        // Player 1 (Left)
        p1Box = createPlayerBox("Player 1");
        p1ScoreLabel = (Label) p1Box.getChildren().get(1);

        // Player 2 (Right)
        p2Box = createPlayerBox("Player 2");
        p2ScoreLabel = (Label) p2Box.getChildren().get(1);

        // Center Dice
        String defaultDicePath = getClass().getResource("/dice1.png").toExternalForm();
        centralDiceImg = new ImageView(new Image(defaultDicePath));
        centralDiceImg.setFitWidth(100);
        centralDiceImg.setFitHeight(100);

        resultValueLabel = new Label("Rolled: -");
        resultValueLabel.getStyleClass().add("history-label"); // Using your CSS class

        VBox centerDiceBox = new VBox(15, centralDiceImg, resultValueLabel);
        centerDiceBox.getStyleClass().add("dice-container"); // Using your circular background

        // Combine them
        HBox arena = new HBox(40, p1Box, centerDiceBox, p2Box);
        arena.setAlignment(Pos.CENTER);

        // Bottom: Status & Roll Button
        statusLabel = new Label("Welcome!");
        statusLabel.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 16px;");

        rollButton = new Button("ROLL DICE");
        rollButton.getStyleClass().add("roll-button"); // Your gradient button
        rollButton.setOnAction(e -> handleRollClick());

        gameContent = new VBox(30, roundLabel, arena, statusLabel, rollButton);
        gameContent.setAlignment(Pos.CENTER);
        gameContent.setPadding(new javafx.geometry.Insets(40));

        // --- 2. GAME OVER OVERLAY ---
        Label winnerLabel = new Label("WINNER!");
        winnerLabel.getStyleClass().add("winner-text");

        Button restartBtn = new Button("Play Again");
        restartBtn.getStyleClass().add("menu-button"); // Reuse your menu button style
        restartBtn.setOnAction(e -> handleRestart());

        Button exitBtn = new Button("Exit");
        exitBtn.getStyleClass().add("exit-button"); // Reuse your exit button style
        exitBtn.setOnAction(e -> stage.close());

        HBox buttonBox = new HBox(20, restartBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);

        gameOverOverlay = new VBox(30, winnerLabel, buttonBox);
        gameOverOverlay.getStyleClass().add("game-over-overlay");
        gameOverOverlay.setVisible(false);

        // --- 3. ROOT ---
        rootLayout = new StackPane(gameContent, gameOverOverlay);

        Scene scene = new Scene(rootLayout, 900, 600);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) { System.out.println("CSS not found"); }

        stage.setTitle("Dice Duel");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createPlayerBox(String name) {
        Label nameLbl = new Label(name);
        nameLbl.getStyleClass().add("player-label-title");

        Label scoreLbl = new Label("0");
        scoreLbl.getStyleClass().add("score-big");

        VBox box = new VBox(15, nameLbl, scoreLbl);
        box.getStyleClass().add("player-card");
        return box;
    }

    private void handleRollClick() {
        rollButton.setDisable(true);
        statusLabel.setText("Rolling...");

        animateDice(centralDiceImg, () -> {
            int roll = engine.rollForCurrentPlayer();

            // Update Dice Image
            String path = getClass().getResource("/dice" + roll + ".png").toExternalForm();
            centralDiceImg.setImage(new Image(path));

            // Update Text
            resultValueLabel.setText("Rolled: " + roll);

            updateUIState();
            rollButton.setDisable(false);
        });
    }

    private void handleRestart() {
        engine.startNewGame();
        gameOverOverlay.setVisible(false);
        gameContent.setOpacity(1.0);
        resultValueLabel.setText("Rolled: -");
        updateUIState();
    }

    private void updateUIState() {
        // Update Scores
        p1ScoreLabel.setText(String.valueOf(engine.getPlayer1().getTotalScore()));
        p2ScoreLabel.setText(String.valueOf(engine.getPlayer2().getTotalScore()));

        // Check Game Over
        if (engine.isGameOver()) {
            showGameOver();
            return;
        }

        // Update Round Text
        roundLabel.setText("Round " + engine.getCurrentRound() + " / " + engine.getTotalRounds());

        // --- HIGHLIGHT LOGIC (PINK VS CYAN) ---
        // This toggles the classes .p1-active and .p2-active defined in your CSS
        if (engine.getCurrentTurn() == 1) {
            p1Box.getStyleClass().add("p1-active");   // Add Pink Glow
            p2Box.getStyleClass().remove("p2-active"); // Remove Cyan Glow

            statusLabel.setText("Player 1's Turn");
            rollButton.setText("ROLL (Player 1)");
        } else {
            p2Box.getStyleClass().add("p2-active");   // Add Cyan Glow
            p1Box.getStyleClass().remove("p1-active"); // Remove Pink Glow

            statusLabel.setText("Player 2's Turn");
            rollButton.setText("ROLL (Player 2)");
        }
    }

    private void showGameOver() {
        Label winnerLbl = (Label) gameOverOverlay.getChildren().get(0);
        int s1 = engine.getPlayer1().getTotalScore();
        int s2 = engine.getPlayer2().getTotalScore();

        if (s1 > s2) winnerLbl.setText("PLAYER 1 WINS!");
        else if (s2 > s1) winnerLbl.setText("PLAYER 2 WINS!");
        else winnerLbl.setText("IT'S A DRAW!");

        gameContent.setOpacity(0.2);
        gameOverOverlay.setVisible(true);
    }

    private void animateDice(ImageView dice, Runnable onFinished) {
        RotateTransition rt = new RotateTransition(Duration.seconds(0.8), dice);
        rt.setByAngle(360);
        rt.setOnFinished(e -> onFinished.run());
        rt.play();

        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> {
            int randomNum = new Random().nextInt(6) + 1;
            try {
                String path = getClass().getResource("/dice" + randomNum + ".png").toExternalForm();
                dice.setImage(new Image(path));
            } catch (Exception ex) {}
        }));
        tl.setCycleCount(7);
        tl.play();
    }
}