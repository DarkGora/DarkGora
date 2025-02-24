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
        listQuestion.add(new Question ("Какой из следующих типов данных является примитивным в Java?", List.of("String","Integer","int","ArrayList"), 2));
        listQuestion.add(new Question ("Какой из следующих операторов используется для сравнения двух значений в Java?", List.of("=","==","===","!="),1));
        listQuestion.add(new Question ("Какой метод используется для запуска программы в Java?", List.of("main()","start()","run()","startJava()"),0));
        listQuestion.add(new Question ("Как останосить case?",List.of("break","stop","stopline","short"),3));
        listQuestion.add(new Question ("Какой из следующих интерфейсов используется для работы с коллекциями в Java?", List.of("List","Map","Eilast","Collection"),1));
        listQuestion.add(new Question ("Какой модификатор доступа делает член класса доступным только внутри этого класса?", List.of("public","String","private","ModerPriv"),0));
        listQuestion.add(new Question ("Что такое исключение в Java?",List.of("Ошибка компиляции","Исключение обьекта путем команд","Doms","Где?"),3));
        listQuestion.add(new Question ("Какой из следующих классов является частью Java Collections Framework?",List.of("HashMap","Scanner","Framework","Collection"),1));
        listQuestion.add(new Question ("Какой оператор используется для создания нового объекта в Java?",List.of("new","object","ineselert","int"),1));
        listQuestion.add(new Question ("Какой из следующих методов позволяет получить длину массива в Java?",List.of("length()","size()","getlength()","length"),0));
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
            Long chartId = update.getMessage().getChatId();
            Message message = update.getMessage();
            if (!heroes.containsKey(chartId)) {
                sendMassage(chartId, "Привет! Ну что поехали ");
                heroes.put(chartId, new Student(message.getFrom().getId(), message.getFrom().getFirstName()));
                sendQuestion(chartId, 0);
            }

        }else if (update.hasCallbackQuery()) {
                String answers = update.getCallbackQuery().getData();
                Long chatId = update.getCallbackQuery().getMessage().getChatId();

                if (heroes.containsKey(chatId)) {
                    Student student = heroes.get(chatId);

                    if ("restart".equals(answers)) {

                        student.reset();
                        sendMassage(chatId, "Тест начинается заново!");
                        sendQuestion(chatId, 0);
                        return;
                    }

                    // Обработка обычных ответов
                    int questionIndex = student.getNumber();
                    if (questionIndex < listQuestion.size()) {
                        Question question = listQuestion.get(questionIndex);
                        if (answers.equals(question.getAnswer().get(question.getIndex()))) {
                            student.veryGoodQuestion();
                            sendMassage(chatId, "Ответ верный! " + student.getGoodQuestion() + " правильных ответов.");
                            sendPhoto(chatId, "C:\\Users\\andre\\IdeaProjects\\Java\\Good_Question.jpg", "");
                        } else {
                            sendMassage(chatId, "Ответ неверный. У вас все еще " + student.getGoodQuestion() + " правильных ответов.");
                            sendPhoto(chatId, "C:\\Users\\andre\\IdeaProjects\\Java\\bad-or-good-word-on-question-mark-background-E3KKBT.jpg", "");
                        }


                        student.addAnswer(answers);
                        student.vetynumber();


                        if (student.getNumber() == listQuestion.size()) {
                            sendMassage(chatId, "Тест завершен!");
                            String finalResult = student.getFinalResult();
                            sendMassageWithRetryButton(chatId, finalResult);
                            sendMassage(GROUP_ID, student.getFirstName() + " завершил тест с " + student.getGoodQuestion() + " правильными ответами.");
                            heroes.remove(chatId);
                        } else {
                            sendQuestion(chatId, student.getNumber());
                        }
                    }
                }
            }

    }
    private void sendQuestion(Long chatId, int questionIndex) {
        if (questionIndex < listQuestion.size()) {
            Question question = listQuestion.get(questionIndex);
            sendMassage(chatId, question.getName(), question.getAnswer());
        } else {
            sendMassage(chatId, "Все вопросы закончились.");
        }
    }

    public void sendPhoto(Long chartId, String fileName, String caption){
        try {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chartId.toString());
            sendPhoto.setPhoto(new InputFile( new FileInputStream(  fileName), fileName));
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
    private void sendMassageWithRetryButton(Long chatId, String text) {
        SendMessage reply = new SendMessage();
        reply.setChatId(chatId.toString());
        reply.setText(text);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton retryButton = new InlineKeyboardButton();
        retryButton.setText("Снова!!");
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


    private void sendMassage (Long chartId, String text, List < String > button){
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

    public static InlineKeyboardMarkup createButtons(List<String> buttonsName){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        buttonsName .forEach(name->{
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



