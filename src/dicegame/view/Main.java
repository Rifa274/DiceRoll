package dicegame.view;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Neon Dice Master");
        
        // Start with the Landing Page
        showLandingPage();
        
        primaryStage.show();
    }

    // Switch to Menu
    public static void showLandingPage() {
        new LandingPage(primaryStage);
    }

    // Switch to Game
    public static void showGameRoom() {
        new GameWindow(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}