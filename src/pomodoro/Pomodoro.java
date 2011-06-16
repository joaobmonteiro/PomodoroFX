package pomodoro;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Pomodoro extends Application {

    public static void main(String[] args) {
        Application.launch(Pomodoro.class, args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 370, 90, Color.BLACK);        
        Timer timer = new Timer();
        root.getChildren().add(timer);
        primaryStage.setTitle("PomodoroFX");
        primaryStage.setScene(scene);
        primaryStage.setVisible(true);
        primaryStage.setResizable(false);
    }
}
