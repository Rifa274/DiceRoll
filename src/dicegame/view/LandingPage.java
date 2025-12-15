package dicegame.view;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LandingPage {

    public LandingPage(Stage stage) {
        // 1. Title
        Label title = new Label("DICE MASTER");
        title.getStyleClass().add("game-title");
        
        // Pulse Animation
        ScaleTransition st = new ScaleTransition(Duration.seconds(2), title);
        st.setByX(0.1); st.setByY(0.1);
        st.setCycleCount(-1); 
        st.setAutoReverse(true);
        st.play();

        // 2. Play Button
        Button playBtn = new Button("PLAY GAME");
        playBtn.getStyleClass().add("menu-button");
        playBtn.setOnAction(e -> Main.showGameRoom()); 

        // 3. Exit Button
        Button exitBtn = new Button("EXIT");
        exitBtn.getStyleClass().add("menu-button");
        exitBtn.getStyleClass().add("exit-button");
        exitBtn.setOnAction(e -> stage.close());

        // 4. Layout
        VBox root = new VBox(30, title, playBtn, exitBtn);
        root.setAlignment(Pos.CENTER);
        
        // 5. Scene
        Scene scene = new Scene(root, 800, 600);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ex) { System.out.println("CSS Missing"); }
        
        stage.setScene(scene);
    }
}