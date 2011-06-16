package pomodoro;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.Parent;

import java.util.Calendar;
import javafx.animation.Animation.Status;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Timer extends Parent {

    private static final int _25_MINUTES = 1500;
    private static final int _5_MINUTES = 300;
    private Calendar timer;
    private Text clockText;
    private Timeline pomodoroTimer;
    private Timeline breakTimer;
    private ProgressBar pomodoroPB;
    private TextBox successBox;
    private TextBox failBox;
    private Integer successCounter = 0;
    private Integer failCounter = 0;
    private Button startBtn;
    private ToggleButton muteBtn;
    private AudioClip successAudio;
    private AudioClip failAudio;

    public Timer() {
        timer = Calendar.getInstance();
        loadAudioClips();
        buildLayout();
        buildTimelines();
    }

    private void buildLayout() {
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(10d);
        innerShadow.setOffsetX(2);
        innerShadow.setOffsetY(2);

        Rectangle background = new Rectangle(380, 100);
        background.setFill(Color.gray(0.8, 0.5));
        background.setEffect(innerShadow);

        pomodoroPB = new ProgressBar(0);
        pomodoroPB.setPrefSize(300, 80);
        pomodoroPB.setStyle("-fx-color:green;-fx-accent:red;-fx-background-color:green;");
        pomodoroPB.setEffect(innerShadow);

        clockText = new Text();
        clockText.setCache(true);
        clockText.setFill(Color.WHITE);
        clockText.setFont(Font.font("null", FontWeight.BOLD, 32));
        clockText.setContent("25:00");

        successBox = new TextBox("0");
        successBox.setEditable(false);
        successBox.setPrefWidth(30);
        successBox.setStyle("-fx-background-color:gray;-fx-text-fill:lime;");
        successBox.setEffect(innerShadow);

        failBox = new TextBox("0");
        failBox.setEditable(false);
        failBox.setPrefWidth(30);
        failBox.setStyle("-fx-background-color:gray;-fx-text-fill:red;");
        failBox.setEffect(innerShadow);

        startBtn = new Button("start");
        startBtn.setEffect(innerShadow);
        startBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                play();
            }
        });

        muteBtn = new ToggleButton("mute");
        muteBtn.setEffect(innerShadow);

        HBox inputHbox = new HBox();
        inputHbox.setSpacing(5);
        inputHbox.getChildren().addAll(successBox, failBox);

        VBox buttonVbox = new VBox();
        buttonVbox.setSpacing(3);
        buttonVbox.setAlignment(Pos.CENTER);
        buttonVbox.getChildren().addAll(inputHbox, startBtn, muteBtn);

        StackPane timerStackPane = new StackPane();
        timerStackPane.setAlignment(Pos.CENTER);
        timerStackPane.getChildren().addAll(pomodoroPB, clockText);

        HBox backgroundHBox = new HBox();
        backgroundHBox.setAlignment(Pos.CENTER);
        backgroundHBox.setSpacing(3);
        backgroundHBox.getChildren().addAll(timerStackPane, buttonVbox);

        StackPane stackBackground = new StackPane();
        stackBackground.getChildren().addAll(background, backgroundHBox);

        getChildren().addAll(stackBackground);
    }

    private void loadAudioClips() {
        try {
            successAudio = new AudioClip(getClass().getResource("success.wav").toURI().toString());
            failAudio = new AudioClip(getClass().getResource("fail.wav").toURI().toString());
            successAudio.setCycleCount(1);
            failAudio.setCycleCount(1);
        } catch (URISyntaxException ex) {
            Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void buildTimelines() {
        pomodoroTimer = new Timeline();
        pomodoroTimer.setCycleCount(_25_MINUTES);
        pomodoroTimer.getKeyFrames().add(
                new KeyFrame(Duration.valueOf(1000), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                refreshTimer(_25_MINUTES);
            }
        }));

        pomodoroTimer.setOnFinished(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                successCounter++;
                successBox.setText(successCounter.toString());
                if (!muteBtn.isSelected()) {
                    successAudio.play();
                }
                startBtn.setDisable(true);
                pomodoroPB.setProgress(0);
                timer.set(Calendar.MINUTE, 5);
                timer.set(Calendar.SECOND, 00);
                clockText.setContent("05:00");
                breakTimer.playFromStart();
            }
        });

        breakTimer = new Timeline();
        breakTimer.setCycleCount(_5_MINUTES);
        breakTimer.getKeyFrames().add(
                new KeyFrame(Duration.valueOf(1000), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                refreshTimer(_5_MINUTES);
            }
        }));


        breakTimer.setOnFinished(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                startBtn.setDisable(false);
                pomodoroPB.setProgress(0);
                resetTimer();
            }
        });

    }

    private void refreshTimer(double total) {
        timer.set(Calendar.SECOND, timer.get(Calendar.SECOND) - 1);
        double s = timer.get(Calendar.SECOND) + (timer.get(Calendar.MINUTE) * 60);
        pomodoroPB.setProgress((total - s) / total);
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
        clockText.setContent(dateFormat.format(timer.getTime()));
    }

    private void play() {
        if (pomodoroTimer.getStatus().equals(Status.RUNNING)) {
            pomodoroTimer.stop();
            failCounter++;
            failBox.setText(failCounter.toString());
            if(!muteBtn.isSelected()){
                failAudio.play();                
            }
        }
        resetTimer();
        pomodoroTimer.playFromStart();
    }

    private void resetTimer() {
        timer.set(Calendar.MINUTE, 25);
        timer.set(Calendar.SECOND, 00);
        clockText.setContent("25:00");
    }
}
