import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
@Setter
@Getter
@Log4j2
public class Question {
    private final String questionText;
    private final List<String> answers;
    private final int correctAnswerIndex;
    private final String category;

    public Question(String questionText, List<String> answers, int correctAnswerIndex) {
        this(questionText, answers, correctAnswerIndex, "Общие"); // Категория по умолчанию
    }

    public Question(String questionText, List<String> answers,
                    int correctAnswerIndex, String category) {
        this.questionText = validateQuestionText(questionText);
        this.answers = validateAndPrepareAnswers(answers);
        this.category = category;
        this.correctAnswerIndex = validateCorrectIndex(correctAnswerIndex, answers.size());
        if (correctAnswerIndex < 0 || correctAnswerIndex >= answers.size()) {
            throw new IllegalArgumentException("Недопустимый индекс правильного ответа");
        }
    }



    private String validateQuestionText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be null or empty");
        }
        return text.trim();
    }

    private List<String> validateAndPrepareAnswers(List<String> answers) {
        Objects.requireNonNull(answers, "Answers list cannot be null");

        if (answers.size() < 2) {
            throw new IllegalArgumentException("At least 2 answers are required");
        }

        // Удаляем пустые ответы и обрезаем пробелы
        List<String> processedAnswers = new ArrayList<>();
        for (String answer : answers) {
            if (answer != null && !answer.trim().isEmpty()) {
                processedAnswers.add(answer.trim());
            }
        }

        if (processedAnswers.size() < 2) {
            throw new IllegalArgumentException("After cleanup, less than 2 valid answers remain");
        }

        return Collections.unmodifiableList(processedAnswers);
    }

    private int validateCorrectIndex(int index, int answersSize) {
        if (index < 0 || index >= answersSize) {
            throw new IllegalArgumentException(
                    String.format("Correct answer index %d is out of bounds (0-%d)",
                            index, answersSize - 1));
        }
        return index;
    }
    public boolean isCorrectAnswer(String answer) {
        if (answer == null) return false;
        return answer.trim().equalsIgnoreCase(answers.get(correctAnswerIndex).trim());
    }

    public String getCorrectAnswer() {
        return answers.get(correctAnswerIndex);
    }

    @Override
    public String toString() {
        return "Question: " + questionText + "\nAnswers: " + answers +
                "\nCorrect: " + answers.get(correctAnswerIndex);
    }
}