import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class Question {
    private String name;
    private List<String> answer;
    private int index;

    public Question(String name, List<String> answer, int index) {
        this.name = name;
        this.answer = answer;
        this.index = index;

    }
}

