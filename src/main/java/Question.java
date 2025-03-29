import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Question {
    private final String questionText;  // Переименовано для ясности
    private final List<String> answers; // Переименовано и сделано неизменяемым
    private final int correctAnswerIndex; // Переименовано для ясности

    public Question(String questionText, List<String> answers, int correctAnswerIndex) {
        if (questionText == null || questionText.isBlank()) {
            throw new IllegalArgumentException("Текст вопроса не может быть пустым");
        }

        if (answers == null || answers.size() < 2) {
            throw new IllegalArgumentException("Должно быть как минимум 2 варианта ответа");
        }

        if (correctAnswerIndex < 0 || correctAnswerIndex >= answers.size()) {
            throw new IllegalArgumentException("Индекс правильного ответа выходит за границы");
        }

        this.questionText = questionText;
        this.answers = Collections.unmodifiableList(new ArrayList<>(answers)); // Защита от изменений
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public boolean isCorrectAnswer(int answerIndex) {
        return answerIndex == correctAnswerIndex;
    }

    public boolean isCorrectAnswer(String answer) {
        return answers.get(correctAnswerIndex).equals(answer);
    }

    public String getCorrectAnswer() {
        return answers.get(correctAnswerIndex);
    }
}