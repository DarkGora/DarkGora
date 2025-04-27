import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionRepository {
    private final List<Question> javaQuestions = new ArrayList<>();
    private final List<Question> pythonQuestions = new ArrayList<>();

    public QuestionRepository() {
        initializeJavaQuestions();
        initializePythonQuestions();
    }

    private void initializeJavaQuestions() {
        javaQuestions.addAll(Arrays.asList(
                new Question("Как изначально назывался язык Java?",
                        List.of("Oak", "Tree", "Brich", "Pine"), 0),
                new Question("Кто создал Джаву",
                        List.of("Гоплинг", "Гослинг", "Готлинг", "Годлинг"), 1),
                new Question("Как изначально назывался язык java", List.of("Oak", "Tree", "Brich", "Pine"), 0),
                new Question("Кто создал Джаву", List.of("Гоплинг", "Гослинг", "Готлинг", "Годлинг"), 1),
                new Question("Сколько байт памяти занимает тип переменных", List.of("2", "4", "8", "16"), 2),
                new Question("Два важных ключевых слова, используемых в циклах", List.of("Break и Contine", "Break и Add", "Break и loop", "loop и Add"), 0),
                new Question("Какие данные возвращает метод  main()", List.of("String", "Int", "Не может возвращать данные", "Указанные в скобках"), 2),
                new Question("Сколько методов у класса  Object", List.of("8", "9", "11", "12"), 2),
                new Question("Выберите несуществующий метод Object", List.of("String toString()", "Object clone()", "int hashCode()", "void patify()"), 3),
                new Question("Какие элементы может содержать класс", List.of("Поля", "Конструкоры", "Методы", "Интерфейсы", "Все вышеперечислонные"), 4),
                new Question("Что означает этот метасимвол регулярных выражений -$ ", List.of("Начало строки", "Конец строки", "Начало слова", "Конец ввода"), 1),
                new Question("Что озн  ачает этот метасимвол регулярных выражений -\s ", List.of("Цифровой символ", "Не цифровой символ", "символ пробела", "бкувенно-цифровой символ", "Все вышеперечислонные"), 2),
                new Question("Какой из следующих типов данных является примитивным в Java?", List.of("String", "Integer", "int", "ArrayList"), 2),
                new Question("Какой из следующих операторов используется для сравнения двух значений в Java?", List.of("=", "==", "===", "!="), 1),
                new Question("Какой метод используется для запуска программы в Java?", List.of("main()", "start()", "run()", "startJava()"), 0),
                new Question("Как останосить case?", List.of("break", "stop", "stopline", "short"), 3),
                new Question("Какой из следующих интерфейсов используется для работы с коллекциями в Java?", List.of("List", "Map", "Eilast", "Collection"), 1),
                new Question("Какой модификатор доступа делает член класса доступным только внутри этого класса?", List.of("public", "String", "private", "ModerPriv"), 0),
                new Question("Что такое исключение в Java?", List.of("Ошибка компиляции", "Исключение обьекта путем команд", "Doms", "Где?"), 3),
                new Question("Какой из следующих классов является частью Java Collections Framework?", List.of("HashMap", "Scanner", "Framework", "Collection"), 1),
                new Question("Какой оператор используется для создания нового объекта в Java?", List.of("new", "object", "ineselert", "int"), 1),
                new Question("Какой из следующих методов позволяет получить длину массива в Java?", List.of("length()", "size()", "getlength()", "length"), 0),
                new Question("В каком году основали язык java?", List.of("1995", "1990", "1997", "2000"), 0),
                new Question("Назовите фамилию разработчика языка java?", List.of("Паскаль", "Эйх", "Гослинг", "Россум"), 2),
                new Question("Кто был первым программистом?", List.of("Ari", "Ada", "Кэй", "Эйх"), 1),
                new Question("Как называется виртуальная машина, которая позволяет компьютеру запускать программы?", List.of("JVM", "JDK", "JRE", "JIT"), 0),
                new Question("Первоначальное название языка java?", List.of("Oak", "Delphi", "Php", "Perl"), 0)
        ));
    }

    private void initializePythonQuestions() {
        pythonQuestions.addAll(Arrays.asList(
                new Question("Какой тип данных в Python является неизменяемым?",
                        List.of("Список", "Словарь", "Кортеж", "Множество"), 2),
                new Question("Какой оператор используется для возведения в степень в Python?",
                        List.of("^", "**", "*", "//"), 1)
        ));
    }
    public int getQuestionsCount(String testType) {
        if ("java".equalsIgnoreCase(testType)) {
            return javaQuestions.size();
        } else if ("python".equalsIgnoreCase(testType)) {
            return pythonQuestions.size();
        }
        return 0; // или throw new IllegalArgumentException для неизвестных типов
    }
    public List<Question> getJavaQuestions() {
        return new ArrayList<>(javaQuestions);
    }

    public List<Question> getPythonQuestions() {
        return new ArrayList<>(pythonQuestions);
    }
}