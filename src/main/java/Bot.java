import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.naming.Name;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;


public class Bot extends TelegramLongPollingBot {
    public static final String USER_NAME = "Gora321_bot";
    public static final String TOKEN = "7753540504:AAF6PE6BC8WlrrsIQUHOpO30zcLmqAovII8";
    public Map<Long, Student> heroes = new HashMap<>();
    public List<Question> listQuestion = new ArrayList<>();

    public static final long GROUP_ID = -1002474189401l;


    public Bot() {
        listQuestion.add(new Question("Как изначально назывался язык java", List.of("Oak", "Tree", "Brich", "Pine"), 0));
        listQuestion.add(new Question("Кто создал Джаву", List.of("Гоплинг", "Гослинг", "Готлинг", "Годлинг"), 1));
        listQuestion.add(new Question("Сколько байт памяти занимает тип переменных", List.of("2", "4", "8", "16"), 2));
        listQuestion.add(new Question("Два важных ключевых слова, используемых в циклах", List.of("Break и Contine", "Break и Add", "Break и loop", "loop и Add"), 0));
        listQuestion.add(new Question("Какие данные возвращает метод  main()", List.of("String", "Int", "Не может возвращать данные", "Указанные в скобках"), 2));
        listQuestion.add(new Question("Сколько методов у класса  Object", List.of("8", "9", "11", "12"), 2));
        listQuestion.add(new Question("Выберите несуществующий метод Object", List.of("String toString()", "Object clone()", "int hashCode()", "void patify()"), 3));
        listQuestion.add(new Question("Какие элементы может содержать класс", List.of("Поля", "Конструкоры", "Методы", "Интерфейсы", "Все вышеперечислонные"), 4));
        listQuestion.add(new Question("Что означает этот метасимвол регулярных выражений -$ ", List.of("Начало строки", "Конец строки", "Начало слова", "Конец ввода"), 1));
        listQuestion.add(new Question("Что озн  ачает этот метасимвол регулярных выражений -\s ", List.of("Цифровой символ", "Не цифровой символ", "символ пробела", "бкувенно-цифровой символ", "Все вышеперечислонные"), 2));
        listQuestion.add(new Question("Какой из следующих типов данных является примитивным в Java?", List.of("String", "Integer", "int", "ArrayList"), 2));
        listQuestion.add(new Question("Какой из следующих операторов используется для сравнения двух значений в Java?", List.of("=", "==", "===", "!="), 1));
        listQuestion.add(new Question("Какой метод используется для запуска программы в Java?", List.of("main()", "start()", "run()", "startJava()"), 0));
        listQuestion.add(new Question("Как останосить case?", List.of("break", "stop", "stopline", "short"), 3));
        listQuestion.add(new Question("Какой из следующих интерфейсов используется для работы с коллекциями в Java?", List.of("List", "Map", "Eilast", "Collection"), 1));
        listQuestion.add(new Question("Какой модификатор доступа делает член класса доступным только внутри этого класса?", List.of("public", "String", "private", "ModerPriv"), 0));
        listQuestion.add(new Question("Что такое исключение в Java?", List.of("Ошибка компиляции", "Исключение обьекта путем команд", "Doms", "Где?"), 3));
        listQuestion.add(new Question("Какой из следующих классов является частью Java Collections Framework?", List.of("HashMap", "Scanner", "Framework", "Collection"), 1));
        listQuestion.add(new Question("Какой оператор используется для создания нового объекта в Java?", List.of("new", "object", "ineselert", "int"), 1));
        listQuestion.add(new Question("Какой из следующих методов позволяет получить длину массива в Java?", List.of("length()", "size()", "getlength()", "length"), 0));
        listQuestion.add(new Question("В каком году основали язык java?", List.of("1995", "1990", "1997", "2000"), 0));
        listQuestion.add(new Question("Назовите фамилию разработчика языка java?", List.of("Паскаль", "Эйх", "Гослинг", "Россум"), 2));
        listQuestion.add(new Question("Кто был первым программистом?", List.of("Ari", "Ada", "Кэй", "Эйх"), 1));
        listQuestion.add(new Question("Как называется виртуальная машина, которая позволяет компьютеру запускать программы?", List.of("JVM", "JDK", "JRE", "JIT"), 0));
        listQuestion.add(new Question("Первоначальное название языка java?", List.of("Oak", "Delphi", "Php", "Perl"), 0));
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
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            Message message = update.getMessage();
            if (!heroes.containsKey(chatId)) {
                sendMassage(chatId, "Привет! Ну что поехали ");
                Student student = new Student(message.getFrom().getId(), message.getFrom().getFirstName());
                for (Question question : listQuestion) {
                    student.addQuestion(question);
                }
                student.shuffleQuestions();
                heroes.put(chatId, student);
                sendQuestion(chatId, 0);
            }

        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (heroes.containsKey(chatId)) {
                Student student = heroes.get(chatId);

                if ("restart".equals(callbackData)) {
                    student.reset();
                    student.shuffleQuestions();
                    sendMassage(chatId, "Тест начинается заново!");
                    sendQuestion(chatId, 0);
                    return;
                }
                if ("next_question".equals(callbackData)) {
                    int nextQuestionIndex = student.getNumber();
                    if (nextQuestionIndex < student.getShuffledQuestions().size()) {
                        sendQuestion(chatId, nextQuestionIndex);
                    } else {
                        sendMassage(chatId, "Все вопросы закончились!");
                    }
                    return;
                }

                int questionIndex = student.getNumber();
                List<Question> shuffledQuestions = student.getShuffledQuestions();
                if (questionIndex < shuffledQuestions.size()) {
                    Question question = shuffledQuestions.get(questionIndex);
                    if (callbackData.equals(question.getAnswer().get(question.getIndex()))) {
                        student.veryGoodQuestion();
                        sendMassage(chatId, "Ответ верный! " + student.getGoodQuestion() + " правильных ответов.");
                    } else {
                        sendMassage(chatId, "Ответ неверный. У вас все еще " + student.getGoodQuestion() + " правильных ответов.");
                    }


                    student.addAnswer(callbackData);
                    student.nextQuestion();


                    if (student.getNumber() == shuffledQuestions.size()) {
                        sendMassage(chatId, "Тест завершен!");
                        String finalResult = student.getFinalResult();
                        sendMessageWithRetryButton(chatId, finalResult);
                        sendMassage(GROUP_ID, student.getId() + student.getFirstName() + " завершил тест с " + student.getGoodQuestion() + " правильными ответами.");
                    } else {
                        sendNextQuestionButton(chatId);
                    }
                }
            }
        }
    }


    private void sendQuestion(Long chatId, int questionIndex) {
        Student student = heroes.get(chatId);
        if (student != null) {
            List<Question> shuffledQuestions = student.getShuffledQuestions();
            if (questionIndex < shuffledQuestions.size()) {
                Question question = shuffledQuestions.get(questionIndex);
                sendMassage(chatId, question.getName(), question.getAnswer());
            } else {
                sendMassage(chatId, "Все вопросы закончились.");
            }
        }
    }

    private void sendNextQuestionButton(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Нажмите кнопку, чтобы перейти к следующему вопросу:");

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton nextButton = new InlineKeyboardButton();
        nextButton.setText("Следующий вопрос");
        nextButton.setCallbackData("next_question");
        row.add(nextButton);

        rowsInLine.add(row);
        keyboardMarkup.setKeyboard(rowsInLine);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    void sendMessageWithRetryButton(Long chatId, String text) {
        SendMessage reply = new SendMessage();
        reply.setChatId(chatId.toString());
        reply.setText(text);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton retryButton = new InlineKeyboardButton();
        retryButton.setText("Заново!!");
        retryButton.setCallbackData("restart");
        row.add(retryButton);

        rowsInLine.add(row);
        keyboardMarkup.setKeyboard(rowsInLine);
        reply.setReplyMarkup(keyboardMarkup);

        try {
            execute(reply);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendPhoto(Long chartId, String fileName, String caption) {
        try {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chartId.toString());
            sendPhoto.setPhoto(new InputFile(new FileInputStream(fileName), fileName));
            sendPhoto.setCaption(caption);
            execute(sendPhoto);

        } catch (FileNotFoundException | TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMassage(Long chartId, String text) {
        SendMessage reply = new SendMessage();
        reply.setChatId(chartId.toString());
        reply.setText(text);

        try {
            execute(reply);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMassage(Long chartId, String text, List<String> button) {
        SendMessage reply = new SendMessage();
        reply.setChatId(chartId.toString());
        reply.setText(text);
        if (button != null) {
            reply.setReplyMarkup(createButtons(button));
        }
        try {
            execute(reply);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public static InlineKeyboardMarkup createButtons(List<String> buttonsName) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        buttonsName.forEach(name -> {
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(name);
            button.setCallbackData(name);
            rowInLine.add(button);
            rowsInLine.add(rowInLine);
        });
        inlineKeyboardMarkup.setKeyboard(rowsInLine);

        return inlineKeyboardMarkup;
    }
}


