import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@Log4j2
@Getter
public class Student {
    private static final int MAX_QUESTIONS = 3; // Увеличил лимит вопросов
    private static final String DEFAULT_NAME = "Аноним";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String userName;
    private final String testType;
    private final LocalDateTime testStartTime;
    private int currentQuestionIndex = 0;
    private int correctAnswersCount = 0;
    private final List<Question> questions = new ArrayList<>();
    private final List<String> userAnswers = new ArrayList<>();
    private List<Question> shuffledQuestions = new ArrayList<>();
    private final Map<String, Object> additionalData = new HashMap<>();

    public Student(Long id, String firstName, String testType) {
        this(id, firstName, null, null, testType);
    }

    public Student(Long id, String firstName, String lastName,
                   String userName, String testType) {
        this.id = Objects.requireNonNull(id, "ID пользователя не может быть null");
        this.firstName = validateName(firstName);
        this.lastName = lastName;
        this.userName = userName;
        this.testType = validateTestType(testType);
        this.testStartTime = LocalDateTime.now();
    }

    // Валидация данных
    private String validateName(String name) {
        return (name == null || name.trim().isEmpty()) ? DEFAULT_NAME : name.trim();
    }

    private String validateTestType(String testType) {
        Objects.requireNonNull(testType, "Тип теста не может быть null");
        String normalized = testType.toLowerCase();
        if (!List.of("java", "python", "sql", "algorithms").contains(normalized)) {
            throw new IllegalArgumentException("Недопустимый тип теста. Допустимые значения: " +
                    "Java, Python, SQL, Algorithms");
        }
        return normalized;
    }

    public Optional<Question> getQuestion(int index) {
        return Optional.ofNullable(shuffledQuestions)
                .filter(q -> index >= 0 && index < q.size())
                .map(q -> q.get(index));
    }

    public String getProgress() {
        return String.format("%d/%d",
                currentQuestionIndex + 1,
                shuffledQuestions.size());
    }

    public boolean moveToQuestion(int index) {
        if (isValidQuestionIndex(index)) {
            this.currentQuestionIndex = index;
            return true;
        }
        log.warn("Попытка установить недопустимый индекс вопроса: {}", index);
        return false;
    }

    public void setCurrentQuestionIndex(int index) {
        if (index >= 0 && index < shuffledQuestions.size()) {
            this.currentQuestionIndex = index;
        } else {
            throw new IllegalArgumentException("Invalid question index: " + index);
        }
    }
    // В классе Student
    public String getDisplayName() {
        if (userName != null && !userName.isEmpty()) {
            return "@" + userName;
        }
        return firstName != null ? firstName : "Аноним";
    }

    public String getChallengeName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return userName != null ? "@" + userName : "Игрок";
    }
    private boolean isValidQuestionIndex(int index) {
        return index >= 0 && index < shuffledQuestions.size();
    }

    // Основные методы
    public void reset() {
        this.currentQuestionIndex = 0;
        this.correctAnswersCount = 0;
        this.userAnswers.clear();
        this.additionalData.clear();
        shuffleQuestions();
    }

    // Добавим метод для сохранения ответа
    public void saveAnswer(String answer) {
        userAnswers.add(answer);
    }

    public void incrementCorrectAnswers() {
        this.correctAnswersCount++;
    }


    // Методы работы с вопросами
    public void shuffleQuestions() {
        if (questions.isEmpty()) {
            this.shuffledQuestions = Collections.emptyList();
            return;
        }

        this.shuffledQuestions = new ArrayList<>(questions);
        Collections.shuffle(shuffledQuestions);

        if (shuffledQuestions.size() > MAX_QUESTIONS) {
            this.shuffledQuestions = shuffledQuestions.subList(0, MAX_QUESTIONS);
        }
    }

    public void addQuestion(Question question) {
        if (question != null && !questions.contains(question)) {
            questions.add(question);
        }
    }
    // В классе Student
    public String getUserName() {
        if (this.userName != null && !this.userName.isEmpty()) {
            return "@" + this.userName;
        }
        return this.firstName != null ? this.firstName : "Аноним";
    }

    // Методы получения информации
    public Question getCurrentQuestion() {
        return hasMoreQuestions() ? shuffledQuestions.get(currentQuestionIndex) : null;
    }

    public boolean hasMoreQuestions() {
        return currentQuestionIndex < shuffledQuestions.size();
    }

    public double getSuccessPercentage() {
        if (shuffledQuestions.isEmpty()) return 0.0;
        return (double) correctAnswersCount / shuffledQuestions.size() * 100;
    }

    public String getTestDuration() {
        return formatDuration(testStartTime, LocalDateTime.now());
    }

    private String formatDuration(LocalDateTime start, LocalDateTime end) {
        long seconds = java.time.Duration.between(start, end).getSeconds();
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

    // Форматированные отчеты
    public String getUserInfo() {
        return String.format(
                "👤 Пользователь: %s\n" +
                        "🔖 ID: %d\n" +
                        "📧 Юзернейм: %s\n" +
                        "📚 Тест: %s\n" +
                        "📅 Дата: %s",
                getFullName(),
                id,
                userName != null ? "@" + userName : "не указан",
                getTestTypeDisplayName(),
                testStartTime.format(DATE_FORMATTER)
        );
    }

    // Обновим метод getTestResults()
    public String getTestResults() {
        if (shuffledQuestions.isEmpty()) {
            return "Тест не содержал вопросов.";
        }

        String summary = String.format(
                "📊 Результаты теста по %s\n" +
                        "👤 Испытуемый: %s\n" +
                        "✅ Правильных ответов: %d/%d (%.1f%%)\n" +
                        "⏱ Время прохождения: %s\n\n",
                getTestTypeDisplayName(),
                getFullName(),
                correctAnswersCount,
                shuffledQuestions.size(),
                getSuccessPercentage(),
                getTestDuration()
        );

        String details = "🔍 Детализация ответов:\n" +
                IntStream.range(0, shuffledQuestions.size())
                        .mapToObj(i -> {
                            Question q = shuffledQuestions.get(i);
                            String userAnswer = i < userAnswers.size() ? userAnswers.get(i) : "Нет ответа";
                            boolean isCorrect = q.isCorrectAnswer(userAnswer);

                            return String.format(
                                    "%d. %s\n" +
                                            "   Ваш ответ: %s %s\n" +
                                            "   Правильный ответ: %s\n",
                                    i + 1,
                                    q.getQuestionText(),
                                    userAnswer,
                                    isCorrect ? "✅" : "❌",
                                    q.getCorrectAnswer()
                            );
                        })
                        .collect(Collectors.joining("\n"));

        return summary + details;
    }

    // Вспомогательные методы
    public String getFullName() {
        return lastName != null ? firstName + " " + lastName : firstName;
    }

    public String getTestTypeDisplayName() {
        return switch (testType.toLowerCase()) {
            case "java" -> "Java";
            case "python" -> "Python";
            case "sql" -> "SQL";
            case "algorithms" -> "Алгоритмам";
            default -> "Неизвестный тест";
        };
    }

    // Статистика
    public Map<String, String> getCategoryStatistics() {
        if (shuffledQuestions.isEmpty() || userAnswers.isEmpty()) {
            return Map.of("Общие", "Нет данных");
        }

        return shuffledQuestions.stream()
                .collect(Collectors.groupingBy(
                        Question::getCategory,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                questions -> {
                                    long correct = IntStream.range(0, questions.size())
                                            .filter(i -> {
                                                int globalIndex = shuffledQuestions.indexOf(questions.get(i));
                                                return globalIndex < userAnswers.size() &&
                                                        questions.get(i).isCorrectAnswer(userAnswers.get(globalIndex));
                                            })
                                            .count();
                                    return String.format("%d/%d (%.1f%%)",
                                            correct, questions.size(), (correct * 100.0 / questions.size()));
                                }
                        )
                ));
    }
}