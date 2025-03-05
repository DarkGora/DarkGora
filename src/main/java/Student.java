import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student {
    private Long id;
    private String firstName;
    private int number;
    private int goodQuestion;
    private List<Question> questions;
    private List<String> answers;
    private List<Question> shuffledQuestions;

    public void reset() {
        this.number = 0;
        this.goodQuestion = 0;
        this.answers.clear();
        shuffleQuestions(); // Перемешиваем вопросы заново
    }

    public void setShuffledQuestions(List<Question> shuffledQuestions) {
        this.shuffledQuestions = shuffledQuestions;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Question> getShuffledQuestions() {
        return shuffledQuestions;
    }
    public void nextQuestion() {
        this.number++;
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


    public void veryGoodQuestion() {
        this.goodQuestion++;
    }


    public void vetynumber() {
        this.number++;
    }

    public void shuffleQuestions() {
        shuffledQuestions = new ArrayList<>(questions);
        Collections.shuffle(shuffledQuestions);
        if (shuffledQuestions.size() > 5) {
            shuffledQuestions = shuffledQuestions.subList(0, 5);
        }
    }

    public Student(Long id, String firstName) {
        this.id = id;
        this.firstName = firstName;
        this.number = 0;
        this.goodQuestion = 0;
        this.questions = new ArrayList<>();
        this.answers = new ArrayList<>();
        this.shuffledQuestions = new ArrayList<>();
    }


    public void addQuestion(Question question) {
        if (!questions.contains(question)) {
            questions.add(question);
        }
    }


    public void addAnswer(String answer) {
        answers.add(answer);
    }


    public List<Question> getQuestions() {
        return questions;
    }


    public List<String> getAnswers() {
        return answers;
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
