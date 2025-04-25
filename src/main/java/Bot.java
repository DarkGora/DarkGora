import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Log4j2
public class Bot extends TelegramLongPollingBot {
    private final String WEATHER = "1c8f74380ef3426ab077c06aa3342f35"; // Ваш API-ключ OpenWeatherMap
    // Состояния пользователей
    private final Map<Long, Student> activeUsers = new HashMap<>();
    private final Map<Long, String> currentTestType = new HashMap<>();
    private final Map<Long, Boolean> inConversationMode = new HashMap<>();
    private final Map<Long, Boolean> inInternetSearchMode = new HashMap<>();

    // Базы данных
    private final List<Question> javaQuestions = new ArrayList<>();
    private final List<Question> pythonQuestions = new ArrayList<>();
    private final Map<String, String> conversationResponses = initConversationResponses();
    private final Map<String, String> techAnswers = initTechAnswers();
    private final Map<String, String> codeExamples = initCodeExamples();
    private final Map<String, String> emotionalResponses = initEmotionalResponses();
    private final Map<String, String> jokes = initJokes();
    private final Map<String, String> motivation = initMotivation();
    private final Map<String, String> personalQuestions = initPersonalQuestions();
    private final Map<String, String[]> followUpQuestions = initFollowUpQuestions();
    private final Map<Long, List<TestHistory>> testHistory = new HashMap<>();
    private final Map<Long, CalculatorMode> calculatorModes = new HashMap<>();
    private final Map<Long, String> calculatorInputs = new HashMap<>();
    private final Map<Long, Integer> calculatorMessageIds = new HashMap<>();
    //мини игра
    private Map<Long, GameMode> gameModes = new ConcurrentHashMap<>();
    private Map<Long, Integer> targetNumbers = new ConcurrentHashMap<>();
    private Map<Long, Integer> guessAttempts = new ConcurrentHashMap<>();
    private Map<Long, Integer> rpsScores = new ConcurrentHashMap<>(); // Счётчик побед
    private Map<Long, Long> rpsChallenges = new ConcurrentHashMap<>();
    private final Map<Long, Integer> lastMessageIds = new ConcurrentHashMap<>();
    // Добавляем новые поля для системы вызовов
    private Map<Long, Boolean> awaitingChallengeTarget = new ConcurrentHashMap<>();


    public Bot(DefaultBotOptions options) {
        super(options);
        initializeQuestions();
    }
    private enum CalculatorMode {
        OFF,
        ON
    }
    private enum GameMode {
        OFF, GUESS_NUMBER, RPS
    }

    private void initializeQuestions() {
        //  Java вопросов
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

        // Инициализация Python вопросов
        pythonQuestions.addAll(Arrays.asList(
                new Question("Какой тип данных в Python является неизменяемым?",
                        List.of("Список", "Словарь", "Кортеж", "Множество"), 2),
                new Question("Какой оператор используется для возведения в степень в Python?",
                        List.of("^", "**", "*", "//"), 1)
        ));
    }

    // Метод для поиска в интернете (например, через Wikipedia API)
    private String searchInternet(String query) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://en.wikipedia.org/w/api.php" +
                "?action=query" +
                "&format=json" +
                "&list=search" +
                "&srsearch=" + query +
                "&srlimit=1"; // Ограничение до одного результата

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "Ошибка при запросе к API: " + response.code();
            }

            // Парсим JSON-ответ
            String jsonData = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
            JsonObject queryObject = jsonObject.getAsJsonObject("query");
            JsonArray searchResults = queryObject.getAsJsonArray("search");

            if (searchResults.size() > 0) {
                JsonObject firstResult = searchResults.get(0).getAsJsonObject();
                String title = firstResult.get("title").getAsString();
                String snippet = firstResult.get("snippet").getAsString();
                String pageId = firstResult.get("pageid").getAsString();

                // Формируем ссылку на статью
                String articleUrl = "https://en.wikipedia.org/?curid=" + pageId;

                return "Результат по запросу '" + query + "':\n" +
                        "Название: " + title + "\n" +
                        "Описание: " + cleanSnippet(snippet) + "\n" +
                        "Читать полностью: " + articleUrl;
            } else {
                return "Ничего не найдено по запросу: " + query;
            }
        } catch (Exception e) {
            return "Ошибка при выполнении запроса: " + e.getMessage();
        }
    }

    /**
     * Очищает сниппет от HTML-тегов.
     */
    private String cleanSnippet(String snippet) {
        return snippet.replaceAll("<[^>]*>", ""); // Удаляем HTML-теги
    }

    // Инициализация баз ответов
    private Map<String, String> initConversationResponses() {
        return Map.ofEntries(
                entry("привет", "Привет! Я бот-помощник по Java и Python. Как я могу помочь?"),
                entry("здравствуй", "Здравствуйте! Готов помочь с программированием. Что вас интересует?"),
                entry("hi", "Hello! I'm a programming tutor bot. Would you like to talk in English?"),
                entry("как дела", "Отлично! Готов помочь с вопросами или тестами. Как у тебя настроение?"),
                entry("как жизнь", "Как у бота - отлично! Всегда на связи и готов помочь. А у вас как?"),
                entry("кто ты", "Я учебный бот для программистов. Могу:\n- Провести тест\n- Объяснить концепции\n- Дать примеры кода\nЧто интересует?"),
                entry("меню", "Доступные команды:\n/test - начать тест\n/chat - свободный диалог\n/code - получить пример кода\n/help - справка\n/joke - шутка про код"),
                entry("погода", "Извините, я не могу проверить погоду, но надеюсь, что она хорошая! Какое время года вам нравится?"),
                entry("что ты умеешь", "Я могу:\n- Объяснять концепции программирования\n- Проводить тесты\n- Давать советы по коду\n- Поддерживать беседу"),
                entry("выход", "До свидания! Хорошего дня программирования!"),
                entry("пока", "До скорой встречи! Если будут вопросы - я тут!"),
                entry("спасибо", "Всегда пожалуйста! Обращайся, если что-то понадобится 😊"),
                entry("хорошо", "Отлично! Может продолжим обучение? Или расскажу что-нибудь интересное?"),
                entry("что нового", "В мире программирования всегда что-то происходит! Недавно вышла новая версия Java/Python. Хотите узнать подробности?")
        );
    }

    private Map<String, String> initTechAnswers() {
        return Map.ofEntries(
                entry("ооп", "ООП включает 4 принципа:\n1. Инкапсуляция\n2. Наследование\n3. Полиморфизм\n4. Абстракция"),
                entry("инкапсуляция", "Инкапсуляция - это сокрытие реализации. Пример:\nclass BankAccount {\n  private double balance;\n  public void deposit(double amount) {...}\n}"),
                entry("наследование", "Наследование позволяет создавать иерархии классов:\nclass Animal {}\nclass Dog extends Animal {}"),
                entry("полиморфизм", "Полиморфизм - это возможность объектов вести себя по-разному:\nAnimal a = new Dog();\na.sound(); // Вызовется метод Dog"),
                entry("абстракция", "Абстракция - это выделение главного:\ninterface Vehicle {\n  void move();\n}"),
                entry("коллекции", "Коллекции в Java - это фреймворк для хранения групп объектов:\n- List (ArrayList, LinkedList)\n- Set (HashSet, TreeSet)\n- Map (HashMap, TreeMap)\nНужны примеры кода?"),
                entry("стримы", "Stream API в Java 8+:\nlist.stream()\n  .filter(x -> x > 5)\n  .map(String::valueOf)\n  .collect(Collectors.toList())"),
                entry("дженерики", "Generics позволяют создавать типобезопасные коллекции:\nList<String> list = new ArrayList<>();\nХотите практический пример?"),
                entry("исключения", "Обработка исключений:\ntry {\n  // код\n} catch (Exception e) {\n  // обработка\n} finally {\n  // cleanup\n}"),
                entry("многопоточность", "Основы многопоточности:\nThread t = new Thread(() -> {...});\nt.start();"),
                entry("аннотации", "Аннотации в Java:\n@Override\n@Deprecated\nМожно создавать свои аннотации"),
                entry("рефлексия", "Рефлексия позволяет анализировать классы в runtime:\nClass<?> clazz = obj.getClass();")

        );
    }

    private Map<String, String> initCodeExamples() {
        return Map.ofEntries(
                entry("коллекция",
                        "Пример ArrayList:\n" +
                                "List<String> names = new ArrayList<>();\n" +
                                "names.add(\"Анна\");\n" +
                                "names.get(0); // Вернет \"Анна\""),

                entry("потоки",
                        "Чтение файла в Java:\n" +
                                "Files.lines(Paths.get(\"file.txt\"))\n" +
                                "  .forEach(System.out::println);"),

                entry("декоратор",
                        "Паттерн Декоратор в Python:\n" +
                                "def decorator(func):\n" +
                                "  def wrapper():\n" +
                                "    print(\"Дополнительная логика\")\n" +
                                "    func()\n" +
                                "  return wrapper"),

                entry("лямбда",
                        "Лямбда-выражения в Java:\n" +
                                "List<Integer> nums = Arrays.asList(1,2,3);\n" +
                                "nums.forEach(n -> System.out.println(n));"),

                entry("сервлет",
                        "Простой сервлет:\n" +
                                "@WebServlet(\"/hello\")\n" +
                                "public class HelloServlet extends HttpServlet {\n" +
                                "  protected void doGet(...) {\n" +
                                "    response.getWriter().print(\"Hello\");\n" +
                                "  }\n}"),

                entry("spring",
                        "Контроллер Spring Boot:\n" +
                                "@RestController\n" +
                                "public class MyController {\n" +
                                "  @GetMapping(\"/hello\")\n" +
                                "  public String hello() { return \"Hi\"; }\n}"),

                entry("рекурсия",
                        "Пример рекурсии в Java:\n" +
                                "public int factorial(int n) {\n" +
                                "  if (n == 1) return 1;\n" +
                                "  return n * factorial(n - 1);\n" +
                                "}"),

                entry("многопоточность",
                        "Создание потока в Java:\n" +
                                "new Thread(() -> {\n" +
                                "  System.out.println(\"Поток работает\");\n" +
                                "}).start();"),

                entry("sql",
                        "Работа с JDBC:\n" +
                                "Connection conn = DriverManager.getConnection(url);\n" +
                                "Statement stmt = conn.createStatement();\n" +
                                "ResultSet rs = stmt.executeQuery(\"SELECT...\");")

        );
    }

    private Map<String, String> initEmotionalResponses() {
        return Map.ofEntries(
                entry("спасибо", "Всегда рад помочь! 😊 Если будут еще вопросы - обращайся!"),
                entry("отлично", "Супер! Давай продолжим обучение? Может, пройдешь тест? /test"),
                entry("скучно", "Давай развлечемся! Могу:\n- Задать каверзный вопрос\n- Показать интересный код\n- Устроить мини-викторину\nЧто выберешь?"),
                entry("ура", "🎉 Отличное настроение! Давай что-нибудь по программируем?"),
                entry("грустно", "Не грусти! Программирование - это весело! Хочешь, расскажу смешную историю про баги?"),
                entry("устал", "Отдохни немного! Помни про баланс между работой и отдыхом. Может, расскажу что-нибудь легкое?"),
                entry("рад", "Я тоже рад нашему общению! Давай сделаем что-нибудь интересное?"),
                entry("злюсь", "Ох, похоже что-то не получается... Давай разберемся вместе? Опиши проблему."),
                entry("люблю", "❤️ Программирование - это действительно прекрасно! Какой твой любимый язык?"),
                entry("ненавижу", "Ох, похоже что-то разочаровало... Может, попробуем другой подход к изучению?")
        );
    }

    private Map<String, String> initJokes() {
        return Map.ofEntries(
                entry("шутка", "Как называют программиста, который боится женщин?\nЖеноФоб..."),
                entry("анекдот", "Программист ставит чайник на плиту и ждет, пока вода закипит...\nТаймаут."),
                entry("смешно", "Почему программисты путают Хэллоуин и Рождество?\nПотому что Oct 31 == Dec 25!"),
                entry("баг", "Разговор двух программистов:\n- У меня баг!\n- Перезагрузись.\n- Не помогает.\n- Ну тогда не знаю...")
        );
    }

    private Map<String, String> initMotivation() {
        return Map.ofEntries(
                entry("мотивация", "Каждая ошибка - это шаг к мастерству! Продолжай в том же духе!"),
                entry("успех", "Помни: даже Билл Гейтс когда-то начинал с 'Hello, World!'"),
                entry("вера", "Ты можешь стать отличным разработчиком! Главное - практика и упорство."),
                entry("совет", "Лучший способ научиться - пробовать, ошибаться и исправлять!"),
                entry("цитата", "'Программирование - это не о том, чтобы знать все ответы, а о том, чтобы уметь их находить.'")
        );
    }

    private Map<String, String> initPersonalQuestions() {
        return Map.ofEntries(
                entry("имя", "Меня зовут DarkGora, но ты можешь придумать мне другое имя 😊"),
                entry("возраст", "Я цифровой, поэтому мой возраст измеряется в версиях! Сейчас я v2.0"),
                entry("создатель", "Меня создал разработчик, который тоже когда-то начинал с основ программирования"),
                entry("дом", "Я живу в облаке, но иногда спускаюсь на сервера к людям"),
                entry("друг", "Конечно, мы можем быть друзьями! Друзья-программисты - это здорово!")
        );
    }

    private Map<String, String[]> initFollowUpQuestions() {
        return Map.ofEntries(
                entry("ооп", new String[]{
                        "Хотите подробнее про:\n1. Инкапсуляцию\n2. Наследование\n3. Полиморфизм\n4. Абстракцию",
                        "Могу привести пример из реального проекта",
                        "Интересно ли вам практическое применение ООП?"
                }),
                entry("коллекции", new String[]{
                        "Какую коллекцию рассмотрим подробнее?\n1. List\n2. Set\n3. Map",
                        "Нужны примеры использования в реальных задачах?",
                        "Хотите сравнение производительности разных коллекций?"
                })
        );
    }

    private String getWeather(String city) throws Exception {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + WEATHER + "&units=metric&lang=ru";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Ошибка при запросе к API OpenWeatherMap");
            }

            String responseBody = response.body().string();
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

            if (jsonObject.has("main")) {
                String weatherDescription = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
                double temperature = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
                int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

                return "Погода в городе " + city + ":\n" +
                        "Температура: " + String.format("%.1f", temperature) + "°C\n" +
                        "Влажность: " + humidity + "%\n" +
                        "Описание: " + weatherDescription;
            } else {
                return "Город не найден.";
            }
        }
    }

    private void startCalculatorMode(Long chatId) {
        if (checkActiveModes(chatId)) return;
        calculatorModes.put(chatId, CalculatorMode.ON);
        calculatorInputs.put(chatId, "");

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Первый ряд: 7 8 9 /
        rows.add(Arrays.asList(
                createCalcButton("7"),
                createCalcButton("8"),
                createCalcButton("9"),
                createCalcButton("/", "Деление")
        ));

        // Второй ряд: 4 5 6 *
        rows.add(Arrays.asList(
                createCalcButton("4"),
                createCalcButton("5"),
                createCalcButton("6"),
                createCalcButton("*", "Умножение")
        ));

        // Третий ряд: 1 2 3 -
        rows.add(Arrays.asList(
                createCalcButton("1"),
                createCalcButton("2"),
                createCalcButton("3"),
                createCalcButton("-", "Вычитание")
        ));

        // Четвертый ряд: 0 . = +
        rows.add(Arrays.asList(
                createCalcButton("0"),
                createCalcButton(".", "Точка"),
                createCalcButton("=", "Равно"),
                createCalcButton("+", "Сложение")
        ));

        // Пятый ряд: C ⌫ ( )
        rows.add(Arrays.asList(
                createCalcButton("C", "Очистить"),
                createCalcButton("⌫", "Удалить"),
                createCalcButton("(", "Открыть скобку"),
                createCalcButton(")", "Закрыть скобку")
        ));

        // Шестой ряд: Выход
        rows.add(Collections.singletonList(
                createCalcButton("Exit", "Выход из калькулятора")
        ));

        keyboard.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🧮 Режим калькулятора\nТекущее выражение: ");
        message.setReplyMarkup(keyboard);

        try {
            Message sentMessage = execute(message);
            calculatorMessageIds.put(chatId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            log.error("Ошибка при запуске калькулятора", e);
            sendMessage(chatId, "Не удалось запустить калькулятор. Попробуйте позже.");
        }
    }
    private InlineKeyboardButton createCalcButton(String text, String description) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData("calc_" + text)
                .build();
    }
    private InlineKeyboardButton createCalcButton(String text) {
        return createCalcButton(text, text);
    }

    private double evaluateExpression(String expr) throws IllegalArgumentException {
        // Удаляем все пробелы
        expr = expr.replaceAll("\\s+", "");

        // Проверяем баланс скобок
        if (!checkParenthesesBalance(expr)) {
            throw new IllegalArgumentException("Несбалансированные скобки в выражении");
        }

        // Проверяем корректность выражения
        if (!isValidMathExpression(expr)) {
            throw new IllegalArgumentException("Недопустимое математическое выражение");
        }

        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");

            // Проверяем деление на ноль
            if (expr.contains("/0") && !expr.contains("/0.")) {
                throw new IllegalArgumentException("Деление на ноль недопустимо");
            }

            Object result = engine.eval(expr);
            return ((Number) result).doubleValue();
        } catch (ScriptException e) {
            throw new IllegalArgumentException("Ошибка вычисления: " + e.getMessage());
        }
    }
    private boolean checkParenthesesBalance(String expr) {
        int balance = 0;
        for (char c : expr.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) return false;
        }
        return balance == 0;
    }
    private String formatResult(double result) {
        // Если число целое, показываем без десятичной части
        if (result == (long) result) {
            return String.format("%d", (long) result);
        }

        // Форматируем с 4 знаками после запятой и убираем лишние нули
        String formatted = String.format("%.4f", result);
        formatted = formatted.replaceAll("\\.?0+$", "");

        // Для очень больших/маленьких чисел используем научную нотацию
        if (Math.abs(result) > 1_000_000 || Math.abs(result) < 0.0001) {
            formatted = String.format("%.4e", result).replace(",", ".");
        }

        return formatted;
    }
    private void handleCalculatorInput(Long chatId, String input) {
        // Проверка на команду остановки
        if ("/stop".equalsIgnoreCase(input.trim())) {
            exitCalculatorMode(chatId);
           // sendMessage(chatId, "2.0"); доп текст
            return;
        }

        String currentInput = calculatorInputs.getOrDefault(chatId, "");

        try {
            switch (input) {
                case "=":
                    if (!currentInput.isEmpty()) {
                        double result = evaluateExpression(currentInput);
                        String formattedResult = formatResult(result);
                        calculatorInputs.put(chatId, formattedResult);
                        updateCalculatorDisplay(chatId, "Результат: " + formattedResult +
                                "\n\nДля выхода /stop");
                    }
                    break;
                case "C":
                    calculatorInputs.put(chatId, "");
                    updateCalculatorDisplay(chatId);
                    break;
                case "⌫":
                    if (!currentInput.isEmpty()) {
                        calculatorInputs.put(chatId, currentInput.substring(0, currentInput.length() - 1));
                        updateCalculatorDisplay(chatId);
                    }
                    break;
                case "Exit":
                    exitCalculatorMode(chatId);
                    break;
                default:
                    if (input.matches("[0-9+\\-*/.()]")) {
                        if (isOperator(input) && !currentInput.isEmpty() &&
                                isOperator(currentInput.substring(currentInput.length() - 1))) {
                            calculatorInputs.put(chatId, currentInput.substring(0, currentInput.length() - 1) + input);
                        } else {
                            calculatorInputs.put(chatId, currentInput + input);
                        }
                        updateCalculatorDisplay(chatId);
                    }
            }
        } catch (Exception e) {
            log.error("Ошибка обработки ввода калькулятора", e);
            updateCalculatorDisplay(chatId, "Ошибка: " + e.getMessage() +
                    "\n\nПопробуйте снова или /stop для выхода");
        }
    }
    private boolean isOperator(String s) {
        return s.matches("[+\\-*/]");
    }

    private boolean isValidMathExpression(String expr) {
        // Проверяем на наличие недопустимых символов
        if (!expr.matches("^[0-9+\\-*/.()]+$")) {
            return false;
        }

        // Проверяем на несколько операторов подряд
        if (expr.matches(".*[+\\-*/]{2,}.*")) {
            return false;
        }

        // Проверяем на точку без цифр вокруг
        if (expr.matches(".*\\D\\.\\D.*") || expr.startsWith(".") || expr.endsWith(".")) {
            return false;
        }

        return true;
    }
    private void updateCalculatorDisplay(Long chatId) {
        updateCalculatorDisplay(chatId, calculatorInputs.getOrDefault(chatId, ""));
    }

    private void updateCalculatorDisplay(Long chatId, String text) {
        if (!calculatorMessageIds.containsKey(chatId)) {
            return;
        }

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(calculatorMessageIds.get(chatId));
        editMessage.setText("🧮 Режим калькулятора\nТекущее выражение: " + text);

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка обновления калькулятора", e);
        }
    }
    //мини игра

    private void startNumberGame(Long chatId) {
        // Сначала сбросим предыдущую игру, если была
        resetGame(chatId);

        gameModes.put(chatId, GameMode.GUESS_NUMBER);
        targetNumbers.put(chatId, new Random().nextInt(10) + 1);
        guessAttempts.put(chatId, 0);

        String message = "🎮 Игра 'Угадай число от 1 до 10' началась!\n\n" +
                "Попробуй угадать число, которое я загадал.\n" +
                "Просто напиши число от 1 до 10.\n\n" +
                "Чтобы остановить игру, напиши /stop";
        sendMessage(chatId, message);
    }
    private void handleNumberGame(Long chatId, String text) {
        try {
            // Проверяем, не хочет ли пользователь остановить игру
            if ("/stop".equalsIgnoreCase(text.trim())) {
                endNumberGame(chatId, false, guessAttempts.get(chatId));
                return;
            }

            int guess = Integer.parseInt(text);
            int target = targetNumbers.get(chatId);
            int attempts = guessAttempts.get(chatId) + 1;
            guessAttempts.put(chatId, attempts);

            if (guess < 1 || guess > 10) {
                sendMessage(chatId, "Пожалуйста, введи число от 1 до 10");
                return;
            }

            if (guess == target) {
                endNumberGame(chatId, true, attempts);
            } else {
                String hint = guess < target ? "больше" : "меньше";
                sendMessage(chatId, "Не угадал! Моё число " + hint + " чем " + guess +
                        "\nПопытка #" + attempts + "\n\nМожешь остановить игру командой /stop");
            }
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Пожалуйста, вводи только числа от 1 до 10 или /stop для выхода");
        }
    }

    private void endNumberGame(Long chatId, boolean win, int attempts) {
        String message;
        if (win) {
            message = "🎉 Поздравляю! Ты угадал число за " + attempts + " попыток!";
        } else {
            message = "🛑 Игра остановлена. Загаданное число: " +
                    targetNumbers.getOrDefault(chatId, 0);
        }

        sendMessage(chatId, message + "\n\nСыграем ещё? Напиши /game");

        resetGame(chatId);
    }

    private void resetGame(Long chatId) {
        gameModes.remove(chatId);
        targetNumbers.remove(chatId);
        guessAttempts.remove(chatId);
        rpsScores.remove(chatId);
        rpsChallenges.remove(chatId);
        awaitingChallengeTarget.remove(chatId);
    }

    private void startRPSGame(Long chatId) {
        try {
            gameModes.put(chatId, GameMode.RPS);
            rpsScores.putIfAbsent(chatId, 0);

            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            // Используем KeyboardFactory.createButton()
            rows.add(Arrays.asList(
                    KeyboardFactory.createButton("✊", "rps_rock"),
                    KeyboardFactory.createButton("✌️", "rps_scissors"),
                    KeyboardFactory.createButton("✋", "rps_paper")
            ));
            rows.add(Collections.singletonList(
                    KeyboardFactory.createButton("🏁 Завершить игру", "rps_exit")
            ));

            keyboard.setKeyboard(rows);

            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText("🎮 *Камень-Ножницы-Бумага*\n\nТвои победы: " +
                    rpsScores.getOrDefault(chatId, 0));
            message.setReplyMarkup(keyboard);

            Message sentMessage = execute(message);
            lastMessageIds.put(chatId, sentMessage.getMessageId());
        } catch (Exception e) {
            log.error("Ошибка старта RPS", e);
        }
    }

    private void processRPSMove(Long chatId, String playerChoice) {
        try {
            String[] options = {"✊ Камень", "✌️ Ножницы", "✋ Бумага"};
            int botChoice = new Random().nextInt(3);
            int playerChoiceInt = Integer.parseInt(playerChoice);

            String result;
            if (playerChoiceInt == botChoice) {
                result = "🤝 Ничья!";
            } else if ((playerChoiceInt - botChoice + 3) % 3 == 1) {
                result = "😃 Ты победил!";
                rpsScores.merge(chatId, 1, Integer::sum);
            } else {
                result = "🤖 Я победил!";
            }

            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(chatId.toString());
            editMessage.setMessageId(lastMessageIds.get(chatId));
            editMessage.setText(String.format(
                    "%s\n\nТы: %s\nБот: %s\n\nПобеды: %d",
                    result,
                    options[playerChoiceInt],
                    options[botChoice],
                    rpsScores.getOrDefault(chatId, 0)
            ));
            execute(editMessage);

            startRPSGame(chatId); // Обновляем интерфейс
        } catch (Exception e) {
            log.error("Ошибка обработки хода", e);
        }
    }

    private void startRPSChallenge(Long initiatorId, Long opponentId) {
        try {
            awaitingChallengeTarget.remove(initiatorId); // Сбрасываем флаг ожидания
// Создаем final копии для использования в лямбде
            final Long finalInitiatorId = initiatorId;
            final Long finalOpponentId = opponentId;
            final Student finalOpponent = activeUsers.get(opponentId);
            // Проверка на самовызов
            if (initiatorId.equals(opponentId)) {
                sendMessage(initiatorId, "❌ Нельзя играть против самого себя!");
                return;
            }

            Student opponent = activeUsers.get(opponentId);
            if (opponent == null) {
                sendMessage(initiatorId, "❌ Пользователь не найден или неактивен");
                return;
            }

            // Проверяем, не занят ли оппонент
            if (gameModes.getOrDefault(opponentId, GameMode.OFF) != GameMode.OFF) {
                sendMessage(initiatorId, "❌ Этот пользователь уже занят в другой игре");
                return;
            }

            // Сохраняем вызов
            rpsChallenges.put(initiatorId, opponentId);
            rpsChallenges.put(opponentId, initiatorId);

            // Создаем клавиатуру
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            keyboard.setKeyboard(List.of(
                    List.of(KeyboardFactory.createButton("✅ Принять вызов", "accept_rps_" + initiatorId)),
                    List.of(KeyboardFactory.createButton("❌ Отклонить", "decline_rps"))
            ));

            // Формируем сообщение
            Student initiator = activeUsers.get(initiatorId);
            String initiatorName = initiator != null ? initiator.getFullName() : "Игрок";

            SendMessage challenge = new SendMessage();
            challenge.setChatId(opponentId.toString());
            challenge.setText(String.format(
                    "🎮 *Вызов на игру!*\n\n" +
                            "%s вызывает вас в Камень-Ножницы-Бумага!\n\n" +
                            "У вас есть 2 минуты чтобы ответить.",
                    initiatorName
            ));
            challenge.setReplyMarkup(keyboard);
            challenge.setParseMode("Markdown");

            // Отправляем и сохраняем ID сообщения
            Message sentMessage = execute(challenge);
            lastMessageIds.put(opponentId, sentMessage.getMessageId());

            // Подтверждение инициатору
            sendMessage(initiatorId, String.format(
                    "✅ Вызов отправлен %s! Ожидаем ответа...",
                    opponent.getFullName()
            ));

            // Таймер для автоматического отклонения
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (rpsChallenges.containsKey(finalInitiatorId)) {
                        rpsChallenges.remove(finalInitiatorId);
                        rpsChallenges.remove(finalOpponentId);
                        sendMessage(finalInitiatorId,
                                "⌛ Время вызова истекло. " + finalOpponent.getFullName() + " не ответил.");
                    }
                }
            }, 2 * 60 * 1000);

        } catch (Exception e) {
            log.error("Ошибка отправки вызова", e);
            sendMessage(initiatorId, "⚠️ Ошибка при отправке вызова. Попробуйте позже.");
        }
    }
    //конец игры там 2
    @Override
    public String getBotUsername() {
        return BotConfig.USER_NAME;
    }
    @Override
    public String getBotToken() {
        return BotConfig.TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                Message message = update.getMessage();

                // Обработка только текстовых сообщений и команд
                if (message.hasText()) {
                    handleMessage(update);
                }
                // Можно добавить обработку других типов сообщений (документы, фото и т.д.)
                else if (message.hasDocument() || message.hasPhoto()) {
                    handleNonTextMessage(update);
                }

            } else if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                Long chatId = callbackQuery.getMessage().getChatId();

                // Проверяем режим калькулятора первым
                if (calculatorModes.getOrDefault(chatId, CalculatorMode.OFF) == CalculatorMode.ON) {
                    handleCalculatorCallback(update);
                } else {
                    handleCallbackQuery(update);
                }

            } else if (update.hasEditedMessage()) {
                // Обработка редактированных сообщений
                handleEditedMessage(update);
            }

        } catch (Exception e) {
            log.error("Ошибка при обработке обновления", e);
            sendErrorMessage(update);
        }
    }

    private void handleMessage(Update update) {
        Message message = update.getMessage();
        if (message == null || !message.hasText()) return;

        Long chatId = message.getChatId();
        String text = message.getText().toLowerCase();

        if (Boolean.TRUE.equals(inInternetSearchMode.get(chatId))) {
            String searchResult = searchInternet(text);
            sendMessage(chatId, searchResult);
            inInternetSearchMode.put(chatId, false);
            return;
        }
        if (awaitingChallengeTarget.getOrDefault(chatId, false)) {
            handleChallengeTargetInput(chatId, text);
            return;
        }
        // Проверяем режим игры перед другими обработчиками
        if (gameModes.getOrDefault(chatId, GameMode.OFF) == GameMode.GUESS_NUMBER) {
            handleNumberGame(chatId, text);
            return;
        }
        if (text.startsWith("погода") || text.startsWith("weather")) {
            handleWeatherRequest(chatId, text);
            return;
        }
        // Проверяем, активен ли режим калькулятора
        if (calculatorModes.getOrDefault(chatId, CalculatorMode.OFF) == CalculatorMode.ON) {
            handleCalculatorInput(chatId, text);
            return;
        }
        if (awaitingChallengeTarget.getOrDefault(chatId, false)) {
            try {
                String input = message.getText().trim();
                Long opponentId = resolveUserId(input); // Метод для преобразования ввода в ID
                if (opponentId != null) {
                    startRPSChallenge(chatId, opponentId);
                } else {
                    sendMessage(chatId, "Пользователь не найден. Попробуйте еще раз:");
                }
            } catch (Exception e) {
                sendMessage(chatId, "Ошибка обработки запроса. Попробуйте еще раз:");
            }
            return;
        }

        if (text.startsWith("/")) {
            handleCommand(chatId, text, message.getFrom());
            return;
        }

        if (Boolean.TRUE.equals(inConversationMode.get(chatId))) {
            handleConversation(chatId, text);
            return;
        }

        if (!activeUsers.containsKey(chatId)) {
            sendTestSelection(chatId, message.getFrom());
        }
    }
    //mini game
    private void handleChallengeTargetInput(Long chatId, String input) {
        try {
            Long opponentId = resolveUserId(input);
            if (opponentId != null) {
                startRPSChallenge(chatId, opponentId);
            } else {
                sendMessage(chatId, "❌ Пользователь не найден. Попробуйте еще раз или отправьте /cancel");
            }
        } catch (Exception e) {
            log.error("Ошибка обработки цели вызова", e);
            sendMessage(chatId, "⚠️ Ошибка обработки запроса. Попробуйте еще раз:");
        }
    }
    private Long resolveUserId(String input) {
        // Удаляем возможные пробелы и @
        input = input.trim().replace("@", "");

        try {
            // Если ввод - числовой ID
            if (input.matches("\\d+")) {
                return Long.parseLong(input);
            }

            // Поиск по username в activeUsers
            String finalInput = input;
            return activeUsers.entrySet().stream()
                    .filter(entry -> finalInput.equalsIgnoreCase(entry.getValue().getUserName()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

        } catch (Exception e) {
            log.error("Ошибка разрешения ID пользователя", e);
            return null;
        }
    }
    //finish
    private void handleCalculatorCallback(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callbackData.startsWith("calc_")) {
            String buttonValue = callbackData.substring(5);
            handleCalculatorInput(chatId, buttonValue);
        } else {
            // Если это не калькулятор, передаем обычному обработчику
            handleCallbackQuery(update);
        }
    }
    private void handleNonTextMessage(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();

        if (calculatorModes.getOrDefault(chatId, CalculatorMode.OFF) == CalculatorMode.ON) {
            sendMessage(chatId, "В режиме калькулятора принимаются только текстовые команды");
            return;
        }

        // Обработка других типов сообщений
        sendMessage(chatId, "Извините, я пока не умею обрабатывать этот тип сообщений");
    }
    private void handleEditedMessage(Update update) {
        Message editedMessage = update.getEditedMessage();
        Long chatId = editedMessage.getChatId();

        // Можно добавить логику обработки редактированных сообщений
        log.info("Message edited in chat {}: {}", chatId, editedMessage.getText());
    }

    private void handleCommand(Long chatId, String command, User user) {
        switch (command) {
            case "/start":
                sendWelcomeMessage(chatId, user);
                break;
            case "/test":
                if (Boolean.TRUE.equals(inInternetSearchMode.get(chatId))) {
                    sendMessage(chatId, "Пожалуйста, дождитесь завершения поиска в интернете.");
                    return;
                }
                sendTestSelection(chatId, user);
                break;
            case "/game":
                startNumberGame(chatId);
                break;
            case "/challenge":
                sendMessage(chatId, "Введите @username или ID пользователя для вызова (или /cancel для отмены):");
                awaitingChallengeTarget.put(chatId, true);
                break;
            case "/accept_rps":
                // Обработка принятия вызова
                break;
            case "/rps":
                startRPSGame(chatId);
                break;
            case "/chat":
                startConversationMode(chatId);
                break;
            case "/cancel":
                if (awaitingChallengeTarget.containsKey(chatId)) {
                    awaitingChallengeTarget.remove(chatId);
                    sendMessage(chatId, "❌ Вызов отменен");
                }
                break;
            case "/stop":
                handleStopCommand(chatId);
                break;
            case "/help":
                sendHelpMessage(chatId);
                break;
            case "/stats":
                sendUserStats(chatId);
                break;
            case "/history":
                sendMessage(chatId, getFullHistory(user.getId()));
                break;
            case "/internet":
                if (Boolean.TRUE.equals(inInternetSearchMode.get(chatId))) {
                    inInternetSearchMode.put(chatId, false);
                    sendMessage(chatId, "🔍 Режим поиска отключен");
                } else {
                    inInternetSearchMode.put(chatId, true);
                    sendMessage(chatId, "🔍 Введите запрос для поиска в интернете:");
                }
                break;
            case "/weather":
                sendMessage(chatId,"Введите: погода 'город' ");
                break;
            case "/calculator":
                if (calculatorModes.getOrDefault(chatId, CalculatorMode.OFF) == CalculatorMode.ON) {
                    exitCalculatorMode(chatId);
                } else {
                    startCalculatorMode(chatId);
                }
                break;
            case "/exit":
                exitCalculatorMode(chatId);
                break;
            default:
                sendMessage(chatId, "Неизвестная команда. Попробуйте /help");
        }
    }
    private void handleStopCommand(Long chatId) {
        String activeMode = getActiveMode(chatId);

        switch (activeMode) {
            case "calculator":
                calculatorModes.put(chatId, CalculatorMode.OFF);
                calculatorInputs.remove(chatId);
                calculatorMessageIds.remove(chatId);
                sendMessage(chatId, "🧮 Режим калькулятора выключен");
                break;

            case "game":
                GameMode gameMode = gameModes.get(chatId);
                if (gameMode == GameMode.GUESS_NUMBER) {
                    endNumberGame(chatId, false, guessAttempts.getOrDefault(chatId, 0));
                    sendMessage(chatId, "🛑 Игра 'Угадай число' остановлена");
                } else if (gameMode == GameMode.RPS) {
                    endRPSGame(chatId);
                    sendMessage(chatId, "🛑 Игра 'Камень-ножницы-бумага' остановлена");
                } else {
                    sendMessage(chatId, "ℹ️ Нет активной игры для остановки");
                }
                break;
            case "internet":
                inInternetSearchMode.put(chatId, false);
                sendMessage(chatId, "🔍 Режим поиска в интернете выключен");
                break;

            case "conversation":
                inConversationMode.put(chatId, false);
                sendMessage(chatId, "💬 Режим общения выключен");
                break;

            default:
                sendMessage(chatId, "ℹ️ Нет активных режимов для остановки");
        }
    }
    private void handleCallbackQuery(Update update) {
        if (update == null || update.getCallbackQuery() == null) {
            log.warn("Received null update or callback query");
            return;
        }

        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        User user = callbackQuery.getFrom();

        try {
            //log.info("Processing callback from {}: {}", user.getId(), callbackData); это логирование позволяет видеть выбранный вариант ответа.

            // 1. Обработка RPS игры
            if (callbackData.startsWith("rps_")) {
                handleRpsGameCallback(callbackData, chatId);
                answerCallbackQuery(callbackQuery.getId());
                return;
            }

            // 2. Обработка вызовов
            if (callbackData.startsWith("accept_rps_")) {
                handleRpsChallenge(callbackData, user);
                answerCallbackQuery(callbackQuery.getId());
                return;
            }

            if (callbackData.equals("decline_rps")) {
                declineRPSChallenge(user.getId());
                answerCallbackQuery(callbackQuery.getId());
                return;
            }

            // 3. Основные команды бота
            switch (callbackData) {
                case "restart_test":
                    restartTest(chatId);
                    break;
                case "start_game":
                    startNumberGame(chatId);
                    break;
                case "select_another_test":
                    sendTestSelection(chatId, user);
                    break;
                case "main_menu":
                    sendWelcomeMessage(chatId, user);
                    break;
                case "show_stats":
                    sendUserStats(chatId);
                    break;
                case "next_question":
                    handleNextQuestion(chatId);
                    break;
                default:
                    // Обработка тестов и ответов
                    if (callbackData.startsWith("test_")) {
                        String testType = callbackData.substring(5); // "java" или "python"
                        startTest(chatId, testType, user);
                    } else {
                        checkAnswer(chatId, callbackData);
                    }
            }

            answerCallbackQuery(callbackQuery.getId());

        } catch (Exception e) {
            log.error("Callback processing error", e);
            try {
                AnswerCallbackQuery answer = new AnswerCallbackQuery(callbackQuery.getId());
                answer.setText("⚠️ Ошибка обработки команды");
                answer.setShowAlert(true);
                execute(answer);
            } catch (TelegramApiException ex) {
                log.error("Failed to send error callback", ex);
            }
        }
    }
    private void handleRpsGameCallback(String callbackData, Long chatId) {
        String action = callbackData.substring(4);
        switch (action) {
            case "rock":
                processRPSMove(chatId, "0");
                break;
            case "scissors":
                processRPSMove(chatId, "1");
                break;
            case "paper":
                processRPSMove(chatId, "2");
                break;
            case "exit":
                endRPSGame(chatId);
                break;
            default:
                log.warn("Unknown RPS action: {}", action);
        }
    }
    private void handleRpsChallenge(String callbackData, User user) {
        Long initiatorId = Long.parseLong(callbackData.substring(11));
        Long opponentId = user.getId();
        acceptRPSChallenge(initiatorId, opponentId);
    }

    private void answerCallbackQuery(String callbackId) throws TelegramApiException {
        execute(new AnswerCallbackQuery(callbackId));
    }

    private boolean checkActiveModes(Long chatId) {
        String activeMode = getActiveMode(chatId);
        if (activeMode != null) {
            sendMessage(chatId, "⚠️ Сначала завершите текущий режим (" + activeMode + ") командой /stop");
            return true;
        }
        return false;
    }
    private String getActiveMode(Long chatId) {
        if (calculatorModes.getOrDefault(chatId, CalculatorMode.OFF) == CalculatorMode.ON) {
            return "calculator";
        }
        if (gameModes.getOrDefault(chatId, GameMode.OFF) != GameMode.OFF) {
            return "game";
        }
        if (Boolean.TRUE.equals(inInternetSearchMode.get(chatId))) {
            return "internet";
        }
        if (Boolean.TRUE.equals(inConversationMode.get(chatId))) {
            return "conversation";
        }
        return null;
    }

    private void handleWeatherRequest(Long chatId, String text) {
        try {
            // Извлекаем название города из запроса
            String city = text.replaceAll("погода|weather", "").trim();

            if (city.isEmpty()) {
                sendMessage(chatId, "Пожалуйста, укажите город после команды. Например: \"погода Брест\"");
                return;
            }

            String weatherInfo = getWeather(city);
            sendMessage(chatId, weatherInfo);
        } catch (Exception e) {
            log.error("Ошибка при получении погоды", e);
            sendMessage(chatId, "Не удалось получить данные о погоде. Попробуйте позже или укажите другой город.");
        }
    }
    private void exitCalculatorMode(Long chatId) {
        calculatorModes.remove(chatId);
        calculatorInputs.remove(chatId);
        calculatorMessageIds.remove(chatId);
        sendMessage(chatId, "🧮 Режим калькулятора завершен. " +
                "Для повторного запуска введите /calculator");
    }
    private String getFullHistory(Long userId) {
        List<TestHistory> history = testHistory.getOrDefault(userId, Collections.emptyList());
        if (history.isEmpty()) {
            return "История тестов пуста";
        }

        StringBuilder sb = new StringBuilder("📅 История тестов:\n");
        for (TestHistory item : history) {
            sb.append(String.format("%s: %s - %d/%d\n",
                    item.getTestDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    item.getTestType().toUpperCase(),
                    item.getScore(),
                    getQuestionsCount(item.getTestType())));
        }
        return sb.toString();
    }

    private int getQuestionsCount(String testType) {
        return "java".equals(testType) ? javaQuestions.size() : pythonQuestions.size();
    }
    private void sendUserStats(Long chatId) {
        Student student = activeUsers.get(chatId);
        if (student != null) {
            // Используем getTestResults() для вывода полной статистики
            sendMessage(chatId, student.getTestResults());
        } else {
            sendMessage(chatId, "Сначала пройдите тест для получения статистики.");
        }
    }

    private void handleConversation(Long chatId, String text) {
        text = text.toLowerCase().trim();

        if (emotionalResponses.containsKey(text)) {
            sendMessage(chatId, emotionalResponses.get(text));
            return;
        }

        if (text.contains("пример") || text.contains("код")) {
            handleCodeExampleRequest(chatId, text);
            return;
        }

        if (techAnswers.containsKey(text)) {
            sendTechAnswer(chatId, text);
            return;
        }

        if (jokes.containsKey(text)) {
            sendJoke(chatId, text);
            return;
        }

        sendMessage(chatId, getResponse(text));
    }

    private void sendTechAnswer(Long chatId, String text) {
        sendMessage(chatId, techAnswers.get(text));

        if (followUpQuestions.containsKey(text)) {
            String followUp = String.join("\n", followUpQuestions.get(text));
            sendMessage(chatId, followUp);
        }
    }

    private void sendJoke(Long chatId, String text) {
        String jokeKey = text.replaceAll("шутка|анекдот|расскажи", "").trim();
        String joke = jokes.getOrDefault(jokeKey,
                new ArrayList<>(jokes.values()).get(new Random().nextInt(jokes.size())));

        sendMessage(chatId, joke);
    }

    public String getResponse(String userMessage) {
        String lowerMsg = userMessage.toLowerCase().trim();

        if (conversationResponses.containsKey(lowerMsg)) {
            return conversationResponses.get(lowerMsg);
        }

        for (String key : techAnswers.keySet()) {
            if (lowerMsg.contains(key)) {
                return techAnswers.get(key);
            }
        }

        for (String key : motivation.keySet()) {
            if (lowerMsg.contains(key)) {
                return motivation.get(key);
            }
        }

        for (String key : personalQuestions.keySet()) {
            if (lowerMsg.contains(key)) {
                return personalQuestions.get(key);
            }
        }

        return "Я не совсем понял ваш вопрос. Можете уточнить?\n" +
                "Например, вы можете спросить:\n" +
                "- ООП\n" +
                "- Пример кода\n" +
                "- Шутку про программистов\n" +
                "- Погода\n" +
                "- Мотивацию";
    }

    private void handleCodeExampleRequest(Long chatId, String text) {
        String normalizedRequest = normalizeCodeRequest(text);
        String key = normalizedRequest.replaceAll("пример|код|покажи|как|сделать|реализовать", "").trim();

        if (codeExamples.containsKey(key)) {
            sendCodeExample(chatId, key);
            return;
        }

        Optional<String> foundKey = codeExamples.keySet().stream()
                .filter(k -> key.contains(k))
                .findFirst();

        if (foundKey.isPresent()) {
            sendCodeExample(chatId, foundKey.get());
        } else {
            String availableExamples = "Доступные примеры кода:\n" +
                    String.join("\n", codeExamples.keySet()) +
                    "\n\nПопробуйте уточнить, например: \"пример коллекции\"";
            sendMessage(chatId, availableExamples);
        }
    }

    private void sendCodeExample(Long chatId, String key) {
        String code = codeExamples.get(key);
        String language = detectCodeLanguage(code);
        sendMessage(chatId, "```" + language + "\n" + code + "\n```");
    }

    private String detectCodeLanguage(String code) {
        if (code.contains("@WebServlet") || code.contains("List<")) return "java";
        if (code.contains("def ") || code.contains("lambda ")) return "python";
        if (code.contains("SELECT") || code.contains("CREATE TABLE")) return "sql";
        return "java";
    }

    private String normalizeCodeRequest(String text) {
        Map<String, String> synonyms = Map.of(
                "стрим", "потоки",
                "декор", "декоратор",
                "лямбды", "лямбда",
                "сервлеты", "сервлет",
                "спринг", "spring",
                "sql запрос", "sql"
        );

        String normalized = text.toLowerCase().trim();
        for (Map.Entry<String, String> entry : synonyms.entrySet()) {
            normalized = normalized.replace(entry.getKey(), entry.getValue());
        }
        return normalized;
    }

    private void startConversationMode(Long chatId) {
        inConversationMode.put(chatId, true);

        try {
            String photoPath = "images/eab2e77f92de15a95ebf828c08fe5290.jpg";
            String caption = "🔹 Режим свободного общения активирован!🔹\n\n" +
                    "Теперь вы можете:\n" +
                    "- Задавать вопросы по Java/Python\n" +
                    "- Запрашивать примеры кода\n" +
                    "- Общатся с ботом.\n" +
                    "- Узнать погоду.\n" +
                    "- Обсуждать концепции программирования\n" +
                    "- А так же можете узнать список команд которые доступны /help\n\n" +
                    "🔹 Для возврата к тестам нажмите /test 🔹";

            sendPhoto(chatId, photoPath, caption);
        } catch (Exception e) {
            log.error("Ошибка при активации режима чата: {}", e.getMessage());
            sendMessage(chatId, "🔹 Режим свободного общения активирован!");
        }
    }

    private void stopConversationMode(Long chatId) {
        inConversationMode.remove(chatId);
        sendMessage(chatId, "Режим свободного общения выключен. Для выбора теста напишите /test");
    }

    private void sendWelcomeMessage(Long chatId, User user) {
        String photoPath = "images/1697737128_flomaster-top-p-krutie-risunki-simpsoni-vkontakte-1.jpg";
        String welcomeText = String.format(
                "Привет, %s! Я бот DarkGora для веселья.\n\n" +
                        "Доступные команды:\n" +
                        "/test - начать тест\n" +
                        "/chat - свободное общение\n" +
                        "/help - помощь\n" +
                        "/game - игра угадай число.\n" +
                        "/rps - игра в цуефа.\n" +
                        "/internet - запросы в WIKI\n" +
                        "/stats - статистика \n" +
                        "/weather - погода\n\n" +
                        "Что бы в игру цуефа запросить игру /challenge!",
                        "Выберите что вам интересно!",
                user.getFirstName());
        sendPhoto(chatId, photoPath, welcomeText);

    }

    private void sendHelpMessage(Long chatId) {
        String helpText = "꧁Я могу:꧂\n\n" +
                "1. Проводить тесты по Java и Python (/test)\n" +
                "2. Просто общаться (/chat)\n" +
                "3. Узнать свою статистику по тестам. (/stats)\n" +
                "4. Делать запросы в википедию (/internet)\n" +
                "5. Игра угадай число (/game)\n" +
                "6. Игра цуефа (/rps)\n" +
                "7. Использовать калькулятор (/calculator)\n\n" +
                "Во время теста вы можете прервать его и начать заново.\n" +
                "Для выхода из режима общения напишите /stop";
        sendMessage(chatId, helpText);
    }

    private void sendTestSelection(Long chatId, User user) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Выберите тест:");
        message.setReplyMarkup(KeyboardFactory.createTestSelectionKeyboard());

        try {
            String photoPath = "images/dfefc27b717e11ee8bacaaafe6635749_upscaled.jpg";
            String photoCaption = "Добро пожаловать в тесты, " + user.getFirstName() + "!";
            sendPhoto(chatId, photoPath, photoCaption);
            log.info("Пользователь начинает тест: {} {}", user.getFirstName(), user.getId());

            execute(message);
        } catch (Exception e) {
            log.error("Ошибка при отправке выбора теста", e);
            sendMessage(chatId, "Произошла ошибка при загрузке теста. Попробуйте позже.");
        }
    }

    private void endRPSGame(Long chatId) {
        gameModes.remove(chatId);
        rpsScores.remove(chatId);
        lastMessageIds.remove(chatId);
        rpsChallenges.remove(chatId); // Очищаем вызовы, если они были
        sendMessage(chatId, "Игра завершена! Твой счёт: " + rpsScores.get(chatId) +
                "\nЧтобы сыграть снова - напиши /rps");
    }

    //мини игра камень
    private void acceptRPSChallenge(Long initiatorId, Long opponentId) {
        try {
            Student opponent = activeUsers.get(opponentId);

            // Удаляем вызов
            rpsChallenges.remove(initiatorId);

            // Уведомления
            sendMessage(initiatorId, "🎉 " + opponent.getFullName() + " принял ваш вызов! Начинаем игру...");
            sendMessage(opponentId, "Вы приняли вызов! Начинаем игру...");

            // Запускаем игру для обоих
            startRPSGame(initiatorId);
            startRPSGame(opponentId);

        } catch (Exception e) {
            log.error("Ошибка принятия вызова", e);
        }
    }

    private void declineRPSChallenge(Long opponentId) {
        try {
            Long initiatorId = rpsChallenges.get(opponentId);
            if (initiatorId != null) {
                Student opponent = activeUsers.get(opponentId);
                String opponentName = opponent != null ? opponent.getFullName() : "Игрок";

                sendMessage(initiatorId, "❌ " + opponentName + " отклонил ваш вызов");
                sendMessage(opponentId, "Вы отклонили вызов");

                rpsChallenges.remove(initiatorId);
            }
        } catch (Exception e) {
            log.error("Ошибка отклонения вызова", e);
        }
    }
    //конец
    private void handleNextQuestion(Long chatId) {
        Student student = activeUsers.get(chatId);
        if (student != null && student.getCurrentQuestionIndex() < student.getShuffledQuestions().size() - 1) {
            sendQuestion(chatId, student.getCurrentQuestionIndex() + 1);
        } else {
            finishTest(chatId, student);
        }
    }

    private void startTest(Long chatId, String testType, User user) {
        // Создаем объект Student на основе User
        Student student = new Student(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserName(),
                testType
        );

        currentTestType.put(chatId, testType);

        List<Question> questions = "java".equals(testType) ? javaQuestions : pythonQuestions;
        questions.forEach(student::addQuestion);
        student.shuffleQuestions();

        activeUsers.put(chatId, student);
        sendMessage(chatId, "Выбран тест по " + ("java".equals(testType) ? "Java" : "Python") + ". Начинаем!");
        sendQuestion(chatId, 0);
    }

    private void sendQuestion(Long chatId, int questionIndex) {
        Student student = activeUsers.get(chatId);
        if (student == null) {
            log.warn("Student not found for chatId: {}", chatId);
            return;
        }

        student.setCurrentQuestionIndex(questionIndex);
        Question question = student.getCurrentQuestion();

        if (question != null) {
            String messageText = String.format("""
            🧩 Вопрос %d/%d
            %s
            """,
                    questionIndex + 1,
                    student.getShuffledQuestions().size(),
                    question.getQuestionText());

            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(messageText);
            message.setReplyMarkup(KeyboardFactory.createOptionsKeyboard(question.getAnswers()));

            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Ошибка при отправке вопроса", e);
            }
        } else {
            finishTest(chatId, student);
        }
    }
    private void checkAnswer(Long chatId, String selectedAnswer) {
        Student student = activeUsers.get(chatId);
        if (student == null) return;

        Question currentQuestion = student.getCurrentQuestion();
        if (currentQuestion == null) return;

        // Сохраняем ответ пользователя
        student.saveAnswer(selectedAnswer);

        boolean isCorrect = currentQuestion.isCorrectAnswer(selectedAnswer);
        if (isCorrect) {
            student.incrementCorrectAnswers();
        }

        try {
            String imagePath = isCorrect ? BotConfig.CORRECT_IMAGE_PATH : BotConfig.WRONG_IMAGE_PATH;
            String caption = isCorrect ? "✅ Правильно! Так держать!" :
                    "❌ Неправильно! Правильный ответ: " + currentQuestion.getCorrectAnswer();

            sendImageFromResources(chatId, imagePath, caption);
        } catch (Exception e) {
            log.error("Ошибка при отправке изображения: {}", e.getMessage());
            String textResponse = isCorrect ? "✅ Правильно!" :
                    "❌ Неправильно! Правильный ответ: " + currentQuestion.getCorrectAnswer();
            sendMessage(chatId, textResponse);
        }

        sendNextQuestionButton(chatId);
    }

    private void sendImageFromResources(Long chatId, String imagePath, String caption) {
        InputStream imageStream = null;
        try {
            imageStream = getClass().getClassLoader().getResourceAsStream(imagePath);
            if (imageStream == null) {
                throw new FileNotFoundException("Изображение не найдено в ресурсах: " + imagePath);
            }

            SendPhoto photo = SendPhoto.builder()
                    .chatId(chatId.toString())
                    .photo(new InputFile(imageStream, "result.jpg"))
                    .caption(caption)
                    .build();

            execute(photo);
        } catch (FileNotFoundException e) {
            log.error("Изображение не найдено: {}", e.getMessage());
            sendMessage(chatId, caption);
        } catch (TelegramApiException e) {
            log.error("Ошибка Telegram API при отправке фото", e);
            sendMessage(chatId, caption);
        } finally {
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                    log.error("Ошибка при закрытии потока изображения", e);
                }
            }
        }
        }

    private void finishTest(Long chatId, Student student) {
        // Логируем завершение теста
        log.info("Пользователь завершил тест: {} {}", student.getFirstName(), student.getId());

        // Формируем и отправляем результаты пользователю
        String userResults = formatUserResults(student);
        sendMessage(chatId, userResults);

        // Отправляем меню действий
        SendMessage menu = new SendMessage();
        menu.setChatId(chatId.toString());
        menu.setText("Тест завершен! Выберите действие:");
        menu.setReplyMarkup(KeyboardFactory.createPostTestKeyboard());
        sendMessage(menu);

        // Отправляем результаты в группу
        sendTestResultsToGroup(student);
    }

    private String formatUserResults(Student student) {
        StringBuilder sb = new StringBuilder();

        // Основные результаты
        sb.append("📊 Ваши результаты теста по ").append(student.getTestTypeDisplayName()).append("\n\n")
                .append("✅ Правильных ответов: ").append(student.getCorrectAnswersCount()).append("/")
                .append(student.getShuffledQuestions().size()).append(" (")
                .append(String.format("%.1f", student.getSuccessPercentage())).append("%)\n")
                .append("⏱ Время прохождения: ").append(student.getTestDuration()).append("\n\n");

        // Детализация ответов
        sb.append("🔍 Детализация:\n");
        for (int i = 0; i < student.getShuffledQuestions().size(); i++) {
            Question q = student.getShuffledQuestions().get(i);
            String userAnswer = i < student.getUserAnswers().size() ?
                    student.getUserAnswers().get(i) : "Нет ответа";
            boolean isCorrect = q.isCorrectAnswer(userAnswer);

            sb.append(i+1).append(". ").append(q.getQuestionText()).append("\n")
                    .append("   Ваш ответ: ").append(userAnswer)
                    .append(isCorrect ? " ✅\n" : " ❌\n")
                    .append("   Правильно: ").append(q.getCorrectAnswer()).append("\n\n");
        }

        // Статистика по категориям
        Map<String, String> categoryStats = student.getCategoryStatistics();
        if (!categoryStats.isEmpty()) {
            sb.append("📈 Статистика по категориям:\n");
            categoryStats.forEach((category, stats) ->
                    sb.append("• ").append(category).append(": ").append(stats).append("\n"));
        }

        return sb.toString();
    }

    private void sendTestResultsToGroup(Student student) {
        // Основная информация о результате
        String mainInfo = String.format(
                "📌 Новый результат теста по %s\n" +
                        "👤 Пользователь: %s\n" +
                        "🆔 ID: %d\n" +
                        "⏱ Время: %s\n" +
                        "✅ Результат: %d/%d (%.1f%%)",
                student.getTestTypeDisplayName(),
                student.getFullName(),
                student.getId(),
                student.getTestDuration(),
                student.getCorrectAnswersCount(),
                student.getShuffledQuestions().size(),
                student.getSuccessPercentage()
        );

        // Статистика по категориям
        String categoryStats = "";
        Map<String, String> stats = student.getCategoryStatistics();
        if (!stats.isEmpty()) {
            categoryStats = "\n\n📊 Статистика по категориям:\n" +
                    stats.entrySet().stream()
                            .map(e -> String.format("• %s: %s", e.getKey(), e.getValue()))
                            .collect(Collectors.joining("\n"));
        }

        // Отправляем сообщение в группу
        sendMessage(BotConfig.GROUP_ID, mainInfo + categoryStats);

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

    private void restartTest(Long chatId) {
        Student student = activeUsers.get(chatId);
        if (student != null) {
            student.reset();
            student.shuffleQuestions();
            sendMessage(chatId, "Тест начинается заново!");
            log.info("Пользователь начал тест заново: {}", chatId);
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

    public void sendPhoto(Long chatId, String filePath, String caption) {
        InputStream is = null;
        try {
            // Сначала пробуем загрузить из ресурсов
            is = getClass().getClassLoader().getResourceAsStream(filePath);

            // Если в ресурсах не нашли, пробуем как файл
            if (is == null) {
                File file = new File(filePath);
                if (!file.exists()) {
                    log.error("Файл изображения не найден: {}", filePath);
                    sendMessage(chatId, caption);
                    return;
                }
                is = new FileInputStream(file);
            }

            // Отправляем фото
            execute(SendPhoto.builder()
                    .chatId(chatId.toString())
                    .photo(new InputFile(is, "result.jpg"))
                    .caption(caption)
                    .build());
        } catch (Exception e) {
            log.error("Ошибка отправки фото: {}", e.getMessage());
            sendMessage(chatId, caption);
        } finally {
            // Закрываем поток вручную в блоке finally
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("Ошибка при закрытии потока: {}", e.getMessage());
                }
            }
        }
    }
}