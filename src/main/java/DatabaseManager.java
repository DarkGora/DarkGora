import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private final List<Question> javaQuestions = new ArrayList<>();
    private final List<Question> pythonQuestions = new ArrayList<>();
    private final Map<String, String> conversationResponses;
    // Другие коллекции с данными

    public DatabaseManager() {
        initializeQuestions();
        this.conversationResponses = initConversationResponses();
        // Инициализация других коллекций
    }

    private void initializeQuestions() {
        // Инициализация вопросов
    }

    public List<Question> getJavaQuestions() {
        return javaQuestions;
    }

    // Другие методы доступа к данным
}