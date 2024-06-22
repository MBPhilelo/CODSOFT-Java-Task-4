/**
 * 
 */

/**
 * @author HP
 *
 */
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class QuizApplication {
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private Label questionLabel;
    private ToggleGroup optionsGroup;
    private Label timerLabel;
    private Timeline timeline;
    private int timeLimit = 15; // Time limit in seconds per question
    private Stage primaryStage;

    public QuizApplication(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Quiz Application");

        questions = loadQuestions();

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        questionLabel = new Label();
        timerLabel = new Label();
        optionsGroup = new ToggleGroup();
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> submitAnswer());

        layout.getChildren().addAll(questionLabel, timerLabel, submitButton);
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        displayNextQuestion();
    }

    private List<Question> loadQuestions() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("What is the capital of France?", new String[]{"Paris", "London", "Berlin", "Madrid"}, 0));
        questions.add(new Question("What is the largest planet in our solar system?", new String[]{"Earth", "Jupiter", "Saturn", "Mars"}, 1));
        questions.add(new Question("What is the boiling point of water?", new String[]{"90°C", "100°C", "110°C", "120°C"}, 1));
        return questions;
    }

    private void displayNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            questionLabel.setText(currentQuestion.getQuestion());

            VBox optionsBox = new VBox(10);
            optionsGroup.getToggles().clear();
            for (int i = 0; i < currentQuestion.getOptions().length; i++) {
                RadioButton optionButton = new RadioButton(currentQuestion.getOptions()[i]);
                optionButton.setToggleGroup(optionsGroup);
                optionsBox.getChildren().add(optionButton);
            }
            ((VBox) questionLabel.getParent()).getChildren().removeIf(node -> node instanceof VBox);
            ((VBox) questionLabel.getParent()).getChildren().add(1, optionsBox);

            startTimer();
        } else {
            endQuiz();
        }
    }

    private void startTimer() {
        if (timeline != null) {
            timeline.stop();
        }
        timerLabel.setText("Time left: " + timeLimit + " seconds");
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLimit--;
            timerLabel.setText("Time left: " + timeLimit + " seconds");
            if (timeLimit <= 0) {
                submitAnswer();
            }
        }));
        timeline.setCycleCount(timeLimit);
        timeline.play();
    }

    private void submitAnswer() {
        if (timeline != null) {
            timeline.stop();
        }

        RadioButton selectedOption = (RadioButton) optionsGroup.getSelectedToggle();
        if (selectedOption != null) {
            String selectedText = selectedOption.getText();
            Question currentQuestion = questions.get(currentQuestionIndex);
            if (selectedText.equals(currentQuestion.getOptions()[currentQuestion.getCorrectOption()])) {
                score++;
            }
        }

        currentQuestionIndex++;
        displayNextQuestion();
    }

    private void endQuiz() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Result");
        alert.setHeaderText("Quiz Completed");
        alert.setContentText("Your score: " + score + "/" + questions.size());

        ButtonType restartButtonType = new ButtonType("Restart");
        ButtonType exitButtonType = new ButtonType("Exit");
        alert.getButtonTypes().setAll(restartButtonType, exitButtonType);

        alert.showAndWait().ifPresent(response -> {
            if (response == restartButtonType) {
                restartQuiz();
            } else if (response == exitButtonType) {
                primaryStage.close();
            }
        });
    }

    private void restartQuiz() {
        currentQuestionIndex = 0;
        score = 0;
        timeLimit = 15; // Reset time limit for each question
        displayNextQuestion();
    }
}

