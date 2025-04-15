import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class Student {
    private static final int MAX_QUESTIONS = 3;
    private static final String DEFAULT_NAME = "Аноним";

    private final Long id;
    private final String firstName;
    private final String testType;
    private int currentQuestionIndex = 0;
    private int correctAnswersCount = 0;
    private final List<Question> questions = new ArrayList<>();
    private final List<String> userAnswers = new ArrayList<>();
    private List<Question> shuffledQuestions = new ArrayList<>();

    public Student(Long id, String firstName, String testType) {
        this.id = Objects.requireNonNull(id, "ID пользователя не может быть null");
        this.firstName = validateName(firstName);
        this.testType = validateTestType(testType);
    }

    private String validateName(String name) {
        return (name == null || name.trim().isEmpty()) ? DEFAULT_NAME : name.trim();
    }

    private String validateTestType(String testType) {
        Objects.requireNonNull(testType, "Тип теста не может быть null");
        if (!testType.equalsIgnoreCase("java") && !testType.equalsIgnoreCase("python")) {
            throw new IllegalArgumentException("Недопустимый тип теста. Допустимые значения: Java или Python");
        }
        return testType.toLowerCase();
    }

    public void reset() {
        this.currentQuestionIndex = 0;
        this.correctAnswersCount = 0;
        this.userAnswers.clear();
        shuffleQuestions();
    }
    public void incrementCorrectAnswers() {
        this.correctAnswersCount++;
    }

    public void shuffleQuestions() {
        if (questions.isEmpty()) {
            this.shuffledQuestions = Collections.emptyList();
            return;
        }

        List<Question> newShuffled = new ArrayList<>(questions);
        Collections.shuffle(newShuffled);

        if (newShuffled.size() > MAX_QUESTIONS) {
            newShuffled = newShuffled.subList(0, MAX_QUESTIONS);
        }

        this.shuffledQuestions = Collections.unmodifiableList(newShuffled);
    }

    public void addQuestion(Question question) {
        if (question != null && !questions.contains(question)) {
            questions.add(question);
        }
    }

    private boolean isValidQuestionIndex(int index) {
        return index >= 0 && index < shuffledQuestions.size();
    }

    public Question getCurrentQuestion() {
        if (!hasMoreQuestions()) {
            return null;
        }
        return shuffledQuestions.get(currentQuestionIndex);
    }

    public boolean hasMoreQuestions() {
        return currentQuestionIndex < shuffledQuestions.size();
    }

    public double getSuccessPercentage() {
        if (shuffledQuestions.isEmpty()) {
            return 0.0;
        }
        return (double) correctAnswersCount / shuffledQuestions.size() * 100;
    }

    public String getTestResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("Результаты теста по ")
                .append(testType.equals("java") ? "Java" : "Python")
                .append(" для ").append(firstName).append(":\n\n");

        if (shuffledQuestions.isEmpty()) {
            sb.append("Тест не содержал вопросов.\n");
            return sb.toString();
        }

        sb.append("Всего вопросов: ").append(shuffledQuestions.size()).append("\n")
                .append("Правильных ответов: ").append(correctAnswersCount).append("\n")
                .append("Успешность: ").append(String.format("%.1f%%", getSuccessPercentage())).append("\n\n");

        appendQuestionDetails(sb);

        return sb.toString();
    }

    private void appendQuestionDetails(StringBuilder sb) {
        sb.append("Детализация:\n");
        for (int i = 0; i < shuffledQuestions.size(); i++) {
            Question q = shuffledQuestions.get(i);
            String userAnswer = i < userAnswers.size() ? userAnswers.get(i) : "Нет ответа";
            boolean isCorrect = q.isCorrectAnswer(userAnswer);

            sb.append(i + 1).append(". ").append(q.getQuestionText()).append("\n")
                    .append("Ваш ответ: ").append(userAnswer)
                    .append(isCorrect ? " ✓" : " ✗").append("\n");

            if (!isCorrect) {
                sb.append("Правильно: ").append(q.getCorrectAnswer()).append("\n");
            }
            sb.append("\n");
        }
    }

    public void setCurrentQuestionIndex(int index) {
        if (isValidQuestionIndex(index)) {
            this.currentQuestionIndex = index;
        }
    }
}