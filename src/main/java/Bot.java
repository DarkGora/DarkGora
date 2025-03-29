import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

@Log4j2
public class Bot extends TelegramLongPollingBot {
    public static final String USER_NAME = "Gora321_bot";
    public static final String TOKEN = "7753540504:AAF6PE6BC8WlrrsIQUHOpO30zcLmqAovII8";
    public static final long GROUP_ID = -1002474189401L;
    private static final int MAX_QUESTIONS = 3;

    private final Map<Long, Student> activeUsers = new HashMap<>();
    private final Map<Long, String> currentTestType = new HashMap<>();
    private final List<Question> javaQuestions;
    private final List<Question> pythonQuestions;

    public Bot() {
        // Инициализируем списки перед использованием
        this.javaQuestions = new ArrayList<>();
        this.pythonQuestions = new ArrayList<>();

        // Теперь можно добавлять вопросы
        initializeJavaQuestions();
        initializePythonQuestions();
    }

    private void initializeJavaQuestions() {
        javaQuestions.add(new Question("Как изначально назывался язык java", List.of("Oak", "Tree", "Brich", "Pine"), 0));
        javaQuestions.add(new Question("Кто создал Джаву", List.of("Гоплинг", "Гослинг", "Готлинг", "Годлинг"), 1));
        javaQuestions.add(new Question("Сколько байт памяти занимает тип переменных", List.of("2", "4", "8", "16"), 2));
        javaQuestions.add(new Question("Два важных ключевых слова, используемых в циклах", List.of("Break и Contine", "Break и Add", "Break и loop", "loop и Add"), 0));
        javaQuestions.add(new Question("Какие данные возвращает метод  main()", List.of("String", "Int", "Не может возвращать данные", "Указанные в скобках"), 2));
        javaQuestions.add(new Question("Сколько методов у класса  Object", List.of("8", "9", "11", "12"), 2));
        javaQuestions.add(new Question("Выберите несуществующий метод Object", List.of("String toString()", "Object clone()", "int hashCode()", "void patify()"), 3));
        javaQuestions.add(new Question("Какие элементы может содержать класс", List.of("Поля", "Конструкоры", "Методы", "Интерфейсы", "Все вышеперечислонные"), 4));
        javaQuestions.add(new Question("Что означает этот метасимвол регулярных выражений -$ ", List.of("Начало строки", "Конец строки", "Начало слова", "Конец ввода"), 1));
        javaQuestions.add(new Question("Что озн  ачает этот метасимвол регулярных выражений -\s ", List.of("Цифровой символ", "Не цифровой символ", "символ пробела", "бкувенно-цифровой символ", "Все вышеперечислонные"), 2));
        javaQuestions.add(new Question("Какой из следующих типов данных является примитивным в Java?", List.of("String", "Integer", "int", "ArrayList"), 2));
        javaQuestions.add(new Question("Какой из следующих операторов используется для сравнения двух значений в Java?", List.of("=", "==", "===", "!="), 1));
        javaQuestions.add(new Question("Какой метод используется для запуска программы в Java?", List.of("main()", "start()", "run()", "startJava()"), 0));
        javaQuestions.add(new Question("Как останосить case?", List.of("break", "stop", "stopline", "short"), 3));
        javaQuestions.add(new Question("Какой из следующих интерфейсов используется для работы с коллекциями в Java?", List.of("List", "Map", "Eilast", "Collection"), 1));
        javaQuestions.add(new Question("Какой модификатор доступа делает член класса доступным только внутри этого класса?", List.of("public", "String", "private", "ModerPriv"), 0));
        javaQuestions.add(new Question("Что такое исключение в Java?", List.of("Ошибка компиляции", "Исключение обьекта путем команд", "Doms", "Где?"), 3));
        javaQuestions.add(new Question("Какой из следующих классов является частью Java Collections Framework?", List.of("HashMap", "Scanner", "Framework", "Collection"), 1));
        javaQuestions.add(new Question("Какой оператор используется для создания нового объекта в Java?", List.of("new", "object", "ineselert", "int"), 1));
        javaQuestions.add(new Question("Какой из следующих методов позволяет получить длину массива в Java?", List.of("length()", "size()", "getlength()", "length"), 0));
        javaQuestions.add(new Question("В каком году основали язык java?", List.of("1995", "1990", "1997", "2000"), 0));
        javaQuestions.add(new Question("Назовите фамилию разработчика языка java?", List.of("Паскаль", "Эйх", "Гослинг", "Россум"), 2));
        javaQuestions.add(new Question("Кто был первым программистом?", List.of("Ari", "Ada", "Кэй", "Эйх"), 1));
        javaQuestions.add(new Question("Как называется виртуальная машина, которая позволяет компьютеру запускать программы?", List.of("JVM", "JDK", "JRE", "JIT"), 0));
        javaQuestions.add(new Question("Первоначальное название языка java?", List.of("Oak", "Delphi", "Php", "Perl"), 0));
    }
    private void initializePythonQuestions() {
        pythonQuestions.add(new Question("Какой тип данных в Python является неизменяемым?",
                List.of("Список", "Словарь", "Кортеж", "Множество"), 2));
        pythonQuestions.add(new Question("Какой оператор используется для возведения в степень в Python?",
                List.of("^", "**", "*", "//"), 1));

    }

    @Override
    public String getBotUsername() {
        return USER_NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessage(update);
            } else if (update.hasCallbackQuery()) {
                handleCallbackQuery(update);
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке обновления", e);
            sendErrorMessage(update);
        }
    }

    private void handleMessage(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        if (!activeUsers.containsKey(chatId)) {
            sendTestSelection(chatId, message.getFrom());
        }
    }

    private void sendTestSelection(Long chatId, User user) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        log.info("-Пользователь начинает тест  "+ user.getFirstName()+"  "+user.getId());
        message.setText("Выберите тест:");
        message.setReplyMarkup(KeyboardFactory.createTestSelectionKeyboard());
        sendMessage(message);
    }
    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Message message = update.getCallbackQuery().getMessage();

        try {
            if (callbackData.startsWith("test_")) {
                startTest(chatId, callbackData, message);
            } else if ("restart".equals(callbackData)) {
                restartTest(chatId);
            } else if ("next_question".equals(callbackData)) {
                sendNextQuestion(chatId);
            } else {
                checkAnswer(chatId, callbackData);
            }
        } catch (Exception e) {
            log.error("Ошибка обработки callback", e);
            sendMessage(chatId, "Произошла ошибка. Пожалуйста, попробуйте снова.");
        }
    }

    private void startTest(Long chatId, String callbackData, Message message) {
        String testType = callbackData.substring(5); // "test_java" -> "java"
        currentTestType.put(chatId, testType);

        User user = message.getFrom();
        Student student = new Student(user.getId(), user.getFirstName(), testType);

        List<Question> questions = "java".equals(testType) ? javaQuestions : pythonQuestions;
        questions.forEach(student::addQuestion);
        student.shuffleQuestions();

        activeUsers.put(chatId, student);
        sendMessage(chatId, "Выбран тест по " + ("java".equals(testType) ? "Java" : "Python") + ". Начинаем!");
        sendQuestion(chatId, 0);
    }

    private void sendQuestion(Long chatId, int questionIndex) {
        Student student = activeUsers.get(chatId);
        if (student == null) return;

        List<Question> questions = student.getShuffledQuestions();
        if (questionIndex < questions.size()) {
            student.setCurrentQuestionIndex(questionIndex);
            Question question = questions.get(questionIndex);
            sendMessageWithOptions(chatId, question.getQuestionText(), question.getAnswers());
        } else {
            finishTest(chatId, student);
        }
    }

    private void sendMessageWithOptions(Long chatId, String text, List<String> options) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(KeyboardFactory.createOptionsKeyboard(options));
        sendMessage(message);
    }

    private void checkAnswer(Long chatId, String selectedAnswer) {
        Student student = activeUsers.get(chatId);
        if (student == null) return;

        Question currentQuestion = student.getCurrentQuestion();
        if (currentQuestion == null) return;

        boolean isCorrect = currentQuestion.isCorrectAnswer(selectedAnswer);
        if (isCorrect) {
            student.incrementCorrectAnswers();  // Было incrementScore()
        }

        String feedback = isCorrect ? "✅ Правильно!" :
                "❌ Неправильно! Правильный ответ: " + currentQuestion.getCorrectAnswer();

        sendMessage(chatId, feedback);
        sendNextQuestionButton(chatId);
    }

    private void finishTest(Long chatId, Student student) {
        log.info("Пользователь завершил тест: {} {} {}", chatId, student.getFirstName(), student.getCurrentQuestionIndex());
        String groupMessage = String.format("%s %s завершил тест с %d правильными ответами.",
                student.getId(), student.getFirstName(), student.getCorrectAnswersCount());
        sendMessage(GROUP_ID, groupMessage);
        String result = String.format("Тест завершен! Ваш результат: %d/%d",
                student.getCorrectAnswersCount(),  // Было getScore()
                student.getShuffledQuestions().size());

        sendMessage(chatId, result);
        sendRetryButton(chatId);
    }

    private void sendNextQuestionButton(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Перейти к следующему вопросу:");
        message.setReplyMarkup(KeyboardFactory.createSingleButtonKeyboard(
                "Следующий вопрос", "next_question"));
        sendMessage(message);
    }
    private void sendErrorMessage(Update update) {
        Long chatId = update.hasMessage() ? update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();
        sendMessage(chatId, "Произошла ошибка. Пожалуйста, попробуйте позже.");
    }


    private void sendRetryButton(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Хотите попробовать снова?");
        message.setReplyMarkup(KeyboardFactory.createSingleButtonKeyboard(
                "Заново", "restart"));
        sendMessage(message);
    }

    private void restartTest(Long chatId) {
        Student student = activeUsers.get(chatId);
        if (student != null) {
            student.reset();
            student.shuffleQuestions();
            sendMessage(chatId, "Тест начинается заново!");
            log.info("Пользователь "+chatId+"  "+"  Начал тест заново!" );
            sendQuestion(chatId, 0);
        }
    }

    private void sendNextQuestion(Long chatId) {
        Student student = activeUsers.get(chatId);
        if (student != null) {
            sendQuestion(chatId, student.getCurrentQuestionIndex() + 1);
        }
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        sendMessage(message);
    }

    public void sendPhoto(Long chatId, String fileName, String caption) {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId.toString());
            photo.setPhoto(new InputFile(fis, fileName));
            photo.setCaption(caption);
            execute(photo);
        } catch (Exception e) {
            log.error("Ошибка отправки фото", e);
        }
    }
}