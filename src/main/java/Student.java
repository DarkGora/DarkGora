import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.management.ConstructorParameters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Student {
    private final Long id;
    private final String firstName;
    private int number = 0;
    private int goodQuestion = 0;
    private List<Question> questions = new ArrayList<>();
    private List<String> answers = new ArrayList<>();
    private List<Question> shuffledQuestions = new ArrayList<>();

    public void reset() {
        this.number = 0;
        this.goodQuestion = 0;
        this.answers.clear();
        shuffleQuestions();
    }
    public void nextQuestion() {
        this.number++;
    }

    public void veryGoodQuestion() {
        this.goodQuestion++;
    }

    public void shuffleQuestions() {
        shuffledQuestions = new ArrayList<>(questions);
        Collections.shuffle(shuffledQuestions);
        if (shuffledQuestions.size() > 5) {
            shuffledQuestions = shuffledQuestions.subList(0, 5);
        }
    }

    public void addQuestion(Question question) {
        if (!questions.contains(question)) {
            questions.add(question);
        }
    }

    public void addAnswer(String answer) {
        answers.add(answer);
    }

    public boolean isAnswerCorrect(String correctAnswer) {
        if (number < answers.size()) {
            return answers.get(number).equals(correctAnswer);
        }
        return false;
    }

    public Question getCurrentQuestion() {
        if (number < shuffledQuestions.size()) {
            return shuffledQuestions.get(number);
        }
        return null;
    }
    public double getSuccessPercentage() {
        if (questions.isEmpty()) {
            return 0.0;
        }
        return (double) goodQuestion / questions.size() * 100;
    }

    public String getFinalResult() {
        StringBuilder result = new StringBuilder();
        result.append("Финальный результат для ").append(firstName).append(":\n");

        if (questions.isEmpty()) {
            result.append("Вопросов не было.\n");
        } else {
            result.append("Всего вопросов: ").append(questions.size()).append("\n");
            result.append("Правильных ответов: ").append(goodQuestion).append("\n");
            result.append("Процент правильных ответов: ").append(String.format("%.2f", getSuccessPercentage())).append("%\n\n");

            result.append("Детализация:\n");
            for (int i = 0; i < shuffledQuestions.size(); i++) {
                result.append("Вопрос ").append(i + 1).append(": ").append(shuffledQuestions.get(i)).append("\n");
                result.append("Ваш ответ: ").append(i < answers.size() ? answers.get(i) : "Нет ответа").append("\n");
                result.append("----------------------------\n");
            }
        }
        return result.toString();
    }
}