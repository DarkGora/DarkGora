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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
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

    public Bot(DefaultBotOptions options) {
        super(options);
        initializeQuestions();
    }

    private void initializeQuestions() {
        // Инициализация Java вопросов
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
        if (message == null || !message.hasText()) return;

        Long chatId = message.getChatId();
        String text = message.getText().toLowerCase();

        if (Boolean.TRUE.equals(inInternetSearchMode.get(chatId))) {
            String searchResult = searchInternet(text);
            sendMessage(chatId, searchResult);
            inInternetSearchMode.put(chatId, false);
            return;
        }
        if (text.startsWith("погода") || text.startsWith("weather")) {
            handleWeatherRequest(chatId, text);
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
            case "/chat":
                startConversationMode(chatId);
                break;
            case "/stop":
                stopConversationMode(chatId);
                stopInternetSearchMode(chatId);
                break;
            case "/help":
                sendHelpMessage(chatId);
                break;
            case "/stats":
                sendUserStats(chatId);
                break;
            case "/internet":
                inInternetSearchMode.put(chatId, true);
                sendMessage(chatId, "Введите запрос для поиска в интернете:");
                break;
            case "/weather":
                sendMessage(chatId,"Введите: погода 'город' ");
                break;
            default:
                sendMessage(chatId, "Неизвестная команда. Попробуйте /help");
        }
    }

    private void stopInternetSearchMode(Long chatId) {
        inInternetSearchMode.put(chatId, false);
        sendMessage(chatId, "Режим поиска в интернете завершен.");
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
                        "/weather - погода\n\n" +
                        "Выберите что вам интересно!",
                user.getFirstName());
        sendPhoto(chatId, photoPath, welcomeText);

    }

    private void sendHelpMessage(Long chatId) {
        String helpText = "꧁Я могу:꧂\n\n" +
                "1. Проводить тесты по Java и Python (/test)\n" +
                "2. Просто общаться (/chat)\n" +
                "3. Узнать свою статистику по тестам. (/stats)\n" +
                "4. Делать запросы в википедию (/internet)\n\n" +
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
            } else if ("start_chat".equals(callbackData)) {
                startConversationMode(chatId);
            } else {
                checkAnswer(chatId, callbackData);
            }
        } catch (Exception e) {
            log.error("Ошибка обработки callback", e);
            sendMessage(chatId, "Произошла ошибка. Пожалуйста, попробуйте снова.");
        }
    }

    private void startTest(Long chatId, String callbackData, Message message) {
        String testType = callbackData.substring(5);
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
        try (InputStream imageStream = getClass().getClassLoader().getResourceAsStream(imagePath)) {
            if (imageStream == null) {
                log.error("Изображение не найдено в ресурсах: {}", imagePath);
                sendMessage(chatId, caption);
                return;
            }

            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId.toString());
            photo.setPhoto(new InputFile(imageStream, "result.jpg"));
            photo.setCaption(caption);

            execute(photo);
        } catch (Exception e) {
            log.error("Ошибка при отправке изображения", e);
            sendMessage(chatId, caption);
        }
    }

    private void finishTest(Long chatId, Student student) {
        log.info("Пользователь завершил тест: {} {}", student.getFirstName(), student.getId());

        // Отправляем подробные результаты теста
        sendMessage(chatId, student.getTestResults());

        // Отправка в группу (если нужно)
        String groupMessage = String.format("%s %s завершил тест с %d правильными ответами.",
                student.getId(), student.getFirstName(), student.getCorrectAnswersCount());
        sendMessage(BotConfig.GROUP_ID, groupMessage);

        // Клавиатура с опциями
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Что вы хотите сделать дальше?");

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text("Пройти тест заново")
                        .callbackData("restart")
                        .build()));

        rows.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text("Свободное общение")
                        .callbackData("start_chat")
                        .build()));

        keyboard.setKeyboard(rows);
        message.setReplyMarkup(keyboard);

        sendMessage(message);
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