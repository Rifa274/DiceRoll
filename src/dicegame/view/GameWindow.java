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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;

public class GameWindow {

    private GameEngine engine;
    private Label roundLabel, statusLabel, p1ScoreLabel, p2ScoreLabel;
    private Button actionButton;
    private VBox p1Card, p2Card;
    private ImageView p1DiceImg, p2DiceImg;

    public GameWindow(Stage stage) {
        engine = GameEngine.getInstance();
        initUI(stage);
        updateUIState();
    }

    private void initUI(Stage stage) {
        roundLabel = new Label();
        roundLabel.getStyleClass().add("header-label");

        // Player Cards
        p1Card = createPlayerCard("Player 1", true);
        p2Card = createPlayerCard("Player 2", false);
        p1DiceImg = (ImageView) p1Card.getChildren().get(1);
        p1ScoreLabel = (Label) p1Card.getChildren().get(2);
        p2DiceImg = (ImageView) p2Card.getChildren().get(1);
        p2ScoreLabel = (Label) p2Card.getChildren().get(2);

        HBox centerPanel = new HBox(40, p1Card, p2Card);
        centerPanel.setAlignment(Pos.CENTER);

        statusLabel = new Label("Welcome!");
        statusLabel.getStyleClass().add("status-label");

        actionButton = new Button("ROLL DICE");
        actionButton.getStyleClass().add("action-button");
        actionButton.setOnAction(e -> handleRollClick());

        VBox root = new VBox(30, roundLabel, centerPanel, statusLabel, actionButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new javafx.geometry.Insets(40));

        Scene scene = new Scene(root, 800, 600);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) { System.out.println("CSS not found"); }

        stage.setTitle("Dice Game");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createPlayerCard(String name, boolean isLeft) {
        Label nameLbl = new Label(name);
        nameLbl.getStyleClass().add("player-name");
        ImageView diceImg = new ImageView(new Image("dice1.png"));
        diceImg.setFitWidth(100); diceImg.setFitHeight(100);
        Label scoreLbl = new Label("0");
        scoreLbl.getStyleClass().add("score-text");
        
        VBox card = new VBox(15, nameLbl, diceImg, scoreLbl);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("player-card");
        return card;
    }

    private void handleRollClick() {
        if (engine.isGameOver()) {
            engine.startNewGame();
            updateUIState();
            return;
        }
        actionButton.setDisable(true);
        statusLabel.setText("Rolling...");
        
        // Animate the dice of whoever is rolling
        ImageView target = (engine.getCurrentTurn() == 1) ? p1DiceImg : p2DiceImg;
        animateDice(target, () -> {
            int roll = engine.rollForCurrentPlayer();
            target.setImage(new Image("dice" + roll + ".png"));
            updateUIState();
            actionButton.setDisable(false);
        });
    }

    private void updateUIState() {
        p1ScoreLabel.setText(String.valueOf(engine.getPlayer1().getTotalScore()));
        p2ScoreLabel.setText(String.valueOf(engine.getPlayer2().getTotalScore()));

        if (engine.isGameOver()) {
            roundLabel.setText("GAME OVER");
            statusLabel.setText(engine.determineWinnerMessage());
            actionButton.setText("NEW GAME");
            p1Card.getStyleClass().remove("active-player");
            p2Card.getStyleClass().remove("active-player");
        } else {
            roundLabel.setText("Round " + engine.getCurrentRound() + " / " + engine.getTotalRounds());
            if (engine.getCurrentTurn() == 1) {
                p1Card.getStyleClass().add("active-player");
                p2Card.getStyleClass().remove("active-player");
                statusLabel.setText("Player 1's Turn");
            } else {
                p2Card.getStyleClass().add("active-player");
                p1Card.getStyleClass().remove("active-player");
                statusLabel.setText("Player 2's Turn");
            }
        }
    }

    private void animateDice(ImageView dice, Runnable onFinished) {
        RotateTransition rt = new RotateTransition(Duration.seconds(0.8), dice);
        rt.setByAngle(360);
        rt.setOnFinished(e -> onFinished.run());
        rt.play();
        
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> 
            dice.setImage(new Image("dice" + (new Random().nextInt(6)+1) + ".png"))
        ));
        tl.setCycleCount(8);
        tl.play();
    }
}