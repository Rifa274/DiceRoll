package dicegame;

import dicegame.model.GameEngine;
import dicegame.model.Player;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private GameEngine engine = GameEngine.getInstance();

    private Label lblRound;
    private Label lblCurrentPlayer;
    private Label lblP1Score;
    private Label lblP2Score;
    private TextArea logArea;
    private Button rollButton;
    private Button restartButton;

    @Override
    public void start(Stage stage) {
        engine.startNewGame();

        lblRound = new Label("Round: " + engine.getCurrentRound() + " / " + engine.getTotalRounds());
        lblCurrentPlayer = new Label("Current: " + engine.getCurrentPlayer().getName());
        lblP1Score = new Label(engine.getPlayer1().getName() + " Score: " + engine.getPlayer1().getTotalScore());
        lblP2Score = new Label(engine.getPlayer2().getName() + " Score: " + engine.getPlayer2().getTotalScore());

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(8);

        rollButton = new Button("Roll");
        rollButton.setOnAction(e -> onRoll());

        restartButton = new Button("Restart");
        restartButton.setOnAction(e -> onRestart());

        HBox scores = new HBox(20, lblP1Score, lblP2Score);
        scores.setAlignment(Pos.CENTER);

        HBox controls = new HBox(10, rollButton, restartButton);
        controls.setAlignment(Pos.CENTER);

        VBox root = new VBox(12, lblRound, lblCurrentPlayer, scores, controls, logArea);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 420, 320);
        stage.setScene(scene);
        stage.setTitle("Dice Game (Design Patterns)");
        stage.show();

        updateUI();
    }

    private void onRoll() {
        if (engine.isGameOver()) return;

        String currentName = engine.getCurrentPlayer().getName();
        int roll = engine.rollForCurrentPlayer();

        logArea.appendText(currentName + " rolled: " + roll + "\n");
        updateUI();

        if (engine.isGameOver()) {
            String result = engine.determineWinnerMessage();
            logArea.appendText("Game Over! " + result + "\n");
            rollButton.setDisable(true);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Result");
            alert.setHeaderText("Game Over");
            alert.setContentText(result);
            alert.showAndWait();
        }
    }

    private void onRestart() {
        engine.startNewGame();
        logArea.clear();
        rollButton.setDisable(false);
        updateUI();
    }

    private void updateUI() {
        lblRound.setText("Round: " + Math.min(engine.getCurrentRound(), engine.getTotalRounds()) + " / " + engine.getTotalRounds());
        lblCurrentPlayer.setText("Current: " + (engine.isGameOver() ? "-" : engine.getCurrentPlayer().getName()));
        Player p1 = engine.getPlayer1();
        Player p2 = engine.getPlayer2();
        lblP1Score.setText(p1.getName() + " Score: " + p1.getTotalScore());
        lblP2Score.setText(p2.getName() + " Score: " + p2.getTotalScore());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
