import java.util.ArrayList;
import java.util.List;

public class Question {
    private String name;
    private List<String> answer;
    private int index;


    public String getName() {
        return name;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public int getIndex() {
        return index;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    public Question(String name, List<String> answer, int index) {
        this.name = name;
        this.answer = answer;
        this.index = index;

    }
}

