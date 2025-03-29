import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class Student {
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
        this.testType = Objects.requireNonNull(testType, "Тип теста не может быть null");

    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            // Если имя не указано, используем "Аноним"
            return "Аноним";
        }
        return name.trim();
    }



    public void reset() {
        this.currentQuestionIndex = 0;
        this.correctAnswersCount = 0;
        this.userAnswers.clear();
        shuffleQuestions();
    }


    public void moveToNextQuestion() {
        if (hasMoreQuestions()) {
            this.currentQuestionIndex++;
        }
    }

    public void incrementCorrectAnswers() {
        this.correctAnswersCount++;
    }

    public void shuffleQuestions() {
        if (questions.isEmpty()) {
            this.shuffledQuestions = new ArrayList<>();
            return;
        }

        List<Question> newShuffled = new ArrayList<>(questions);
        Collections.shuffle(newShuffled);

        // Ограничиваем количество вопросов
        final int MAX_QUESTIONS = 3;
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

    public void addUserAnswer(String answer) {
        if (answer != null && !answer.isBlank()) {
            userAnswers.add(answer);
        }
    }

    public boolean checkAnswer(int questionIndex, String userAnswer) {
        if (questionIndex < 0 || questionIndex >= shuffledQuestions.size()) {
            return false;
        }

        Question question = shuffledQuestions.get(questionIndex);
        return question.isCorrectAnswer(userAnswer);
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
                .append(testType.equalsIgnoreCase("java") ? "Java" : "Python")
                .append(" для ").append(firstName).append(":\n\n");

        if (shuffledQuestions.isEmpty()) {
            sb.append("Тест не содержал вопросов.\n");
            return sb.toString();
        }

        sb.append("Всего вопросов: ").append(shuffledQuestions.size()).append("\n")
                .append("Правильных ответов: ").append(correctAnswersCount).append("\n")
                .append("Успешность: ").append(String.format("%.1f%%", getSuccessPercentage())).append("\n\n");

        sb.append("Детализация:\n");
        for (int i = 0; i < shuffledQuestions.size(); i++) {
            Question q = shuffledQuestions.get(i);
            String userAnswer = i < userAnswers.size() ? userAnswers.get(i) : "Нет ответа";
            String correctAnswer = q.getCorrectAnswer();

            sb.append(i+1).append(". ").append(q.getQuestionText()).append("\n")
                    .append("Ваш ответ: ").append(userAnswer)
                    .append(userAnswer.equals(correctAnswer) ? " ✓" : " ✗").append("\n")
                    .append("Правильно: ").append(correctAnswer).append("\n\n");
        }

        return sb.toString();
    }

    // Геттеры и сеттеры, сгенерированные Lombok
    public void setCurrentQuestionIndex(int index) {
        if (index >= 0 && index < shuffledQuestions.size()) {
            this.currentQuestionIndex = index;
        }
    }
}