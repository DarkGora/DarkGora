import java.util.ArrayList;
import java.util.List;

public class Student {
    private Long id;
    private String firstName;
    private int number; // Текущий номер вопроса
    private int goodQuestion; // Количество правильных ответов
    private List<String> questions; // Список вопросов
    private List<String> answers; // Список ответов

    public void reset() {
        this.number = 0; // Сброс номера вопроса
        this.goodQuestion = 0; // Сброс количества правильных ответов
        this.questions.clear(); // Очистка списка вопросов
        this.answers.clear(); // Очистка списка ответов
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getGoodQuestion() {
        return goodQuestion;
    }

    public int getNumber() {
        return number;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setGoodQuestion(int goodQuestion) {
        this.goodQuestion = goodQuestion;
    }

    // Увеличиваем количество правильных ответов
    public void veryGoodQuestion() {
        this.goodQuestion++;
    }

    // Увеличиваем номер текущего вопроса
    public void vetynumber() {
        this.number++;
    }

    // Конструктор
    public Student(Long id, String firstName) {
        this.id = id;
        this.firstName = firstName;
        this.number = 0; // Начинаем с первого вопроса
        this.goodQuestion = 0; // Начальное количество правильных ответов
        this.questions = new ArrayList<>(); // Инициализируем список вопросов
        this.answers = new ArrayList<>(); // Инициализируем список ответов
    }

    // Добавляем вопрос в список
    public void addQuestion(String question) {
        questions.add(question);
    }

    // Добавляем ответ в список
    public void addAnswer(String answer) {
        answers.add(answer);
    }

    // Получаем список вопросов
    public List<String> getQuestions() {
        return questions;
    }

    // Получаем список ответов
    public List<String> getAnswers() {
        return answers;
    }

    // Формируем финальный результат
    public String getFinalResult() {
        StringBuilder result = new StringBuilder();
        result.append("Финальный результат для ").append(firstName).append(":\n");

        if (questions.isEmpty()) {
            result.append("Вы не ответили ни на один вопрос.");
        } else {
            int size = Math.min(questions.size(), answers.size());
            for (int i = 0; i < size; i++) {
                result.append("Вопрос: ").append(questions.get(i)).append("\n");
                result.append("Ваш ответ: ").append(answers.get(i)).append("\n");
            }
            result.append("Количество правильных ответов: ").append(goodQuestion).append("\n");
        }

        return result.toString();
    }
    }
