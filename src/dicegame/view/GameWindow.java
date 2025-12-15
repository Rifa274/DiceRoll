package dicegame.view;

import dicegame.model.GameEngine;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;

public class GameWindow {

    private GameEngine engine;
    
    // UI Elements
    private Label roundLabel;
    private Label historyLabel;
    private Label turnStatusLabel;
    
    private Label p1Score, p2Score;
    private VBox p1Card, p2Card;
    
    private ImageView centerDiceImg;
    private Button rollButton;
    private Button backButton;

    public GameWindow(Stage stage) {
        engine = GameEngine.getInstance();
        engine.startNewGame(); // Reset engine on entry
        
        initUI(stage);
        updateUI(); 
    }

    private void initUI(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new javafx.geometry.Insets(20));

        // --- TOP ---
        backButton = new Button("← Menu");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand;");
        backButton.setOnAction(e -> Main.showLandingPage());

        roundLabel = new Label("Round 1");
        roundLabel.getStyleClass().add("header-label");

        historyLabel = new Label("Game Started");
        historyLabel.getStyleClass().add("history-label");

        VBox topBox = new VBox(10, backButton, roundLabel, historyLabel);
        topBox.setAlignment(Pos.CENTER);
        root.setTop(topBox);

        // --- CENTER ---
        
        // Left: Player 1
        Label p1NameLbl = new Label("PLAYER 1");
        p1NameLbl.getStyleClass().add("player-label-title");

        p1Score = new Label("0");
        p1Score.getStyleClass().add("score-big");
        p1Card = new VBox(10, p1NameLbl, p1Score);
        p1Card.getStyleClass().add("player-card");

        // Middle: The Giant Dice
        centerDiceImg = new ImageView(loadImage("dice1.png"));
        double size = 150;
        centerDiceImg.setFitWidth(size);
        centerDiceImg.setFitHeight(size);
        
        // FIX: Clip to make corners rounded (hides white background corners)
        Rectangle clip = new Rectangle(size, size);
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        centerDiceImg.setClip(clip);
        
        StackPane diceContainer = new StackPane(centerDiceImg);
        diceContainer.getStyleClass().add("dice-container");

        turnStatusLabel = new Label("Player 1's Turn");
        turnStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        VBox centerBox = new VBox(20, diceContainer, turnStatusLabel);
        centerBox.setAlignment(Pos.CENTER);

        // Right: Player 2
        Label p2NameLbl = new Label("PLAYER 2");
        p2NameLbl.getStyleClass().add("player-label-title");

        p2Score = new Label("0");
        p2Score.getStyleClass().add("score-big");
        p2Card = new VBox(10, p2NameLbl, p2Score);
        p2Card.getStyleClass().add("player-card");

        HBox gameArena = new HBox(50, p1Card, centerBox, p2Card);
        gameArena.setAlignment(Pos.CENTER);
        root.setCenter(gameArena);

        // --- BOTTOM ---
        rollButton = new Button("ROLL DICE");
        rollButton.getStyleClass().add("roll-button");
        rollButton.setOnAction(e -> handleRoll());
        
        VBox bottomBox = new VBox(rollButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new javafx.geometry.Insets(30, 0, 0, 0));
        root.setBottom(bottomBox);

        // Scene
        Scene scene = new Scene(root, 800, 600);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) { System.out.println("CSS Missing"); }

        stage.setScene(scene);
    }

    private void handleRoll() {
        if (engine.isGameOver()) {
            engine.startNewGame();
            historyLabel.setText("New Game Started");
            updateUI();
            return;
        }

        rollButton.setDisable(true);
        turnStatusLabel.setText("Rolling...");

        animateDice(centerDiceImg, () -> {
            int rollValue = engine.rollForCurrentPlayer();
            centerDiceImg.setImage(loadImage("dice" + rollValue + ".png"));
            updateUI();
            rollButton.setDisable(false);
        });
    }

    private void updateUI() {
        p1Score.setText(String.valueOf(engine.getPlayer1().getTotalScore()));
        p2Score.setText(String.valueOf(engine.getPlayer2().getTotalScore()));
        
        if (engine.isGameOver()) {
            roundLabel.setText("GAME OVER");
            turnStatusLabel.setText(engine.determineWinnerMessage());
            rollButton.setText("PLAY AGAIN");
            p1Card.getStyleClass().removeAll("p1-active", "p2-active");
            p2Card.getStyleClass().removeAll("p1-active", "p2-active");
        } else {
            roundLabel.setText("Round " + engine.getCurrentRound() + " / " + engine.getTotalRounds());
            
            // Highlight current player
            if (engine.getCurrentTurn() == 1) {
                turnStatusLabel.setText("Player 1's Turn");
                p1Card.getStyleClass().add("p1-active");
                p2Card.getStyleClass().remove("p2-active");
            } else {
                turnStatusLabel.setText("Player 2's Turn");
                p2Card.getStyleClass().add("p2-active");
                p1Card.getStyleClass().remove("p1-active");
            }
        }
    }

// REPLACE YOUR OLD animateDice METHOD WITH THIS ONE

    private void animateDice(ImageView dice, Runnable onFinished) {
        // 1. Physical Spin (Takes 0.8 seconds)
        RotateTransition rt = new RotateTransition(Duration.seconds(0.8), dice);
        rt.setByAngle(360);
        rt.setOnFinished(e -> onFinished.run()); // Runs logic AFTER 0.8s
        rt.play();

        // 2. Image Flickering (Fake Random Numbers)
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> {
            int randomNum = new Random().nextInt(6) + 1;
            try {
                // Load random image safely
                String path = getClass().getResource("/dice" + randomNum + ".png").toExternalForm();
                dice.setImage(new Image(path));
            } catch (Exception ex) {
                // Ignore errors during animation
            }
        }));

        // CRITICAL FIX: Cycle 7 times (0.7 seconds)
        // This ensures the flickering stops BEFORE the real result (0.8s) is set.
        tl.setCycleCount(7);
        tl.play();
    }
    
    private Image loadImage(String name) {
        try {
            return new Image(getClass().getResource("/" + name).toExternalForm());
        } catch (Exception e) { return null; }
    }
}