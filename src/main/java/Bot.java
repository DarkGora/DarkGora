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

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

import static java.util.Map.entry;

@Log4j2
public class Bot extends TelegramLongPollingBot {
    public static final String USER_NAME = "Gora321_bot";
    public static final String TOKEN = "7753540504:AAF6PE6BC8WlrrsIQUHOpO30zcLmqAovII8";
    public static final long GROUP_ID = -1002474189401L;
    private static final int MAX_QUESTIONS = 3;

    private final Map<Long, Student> activeUsers = new HashMap<>();
    private final Map<Long, String> currentTestType = new HashMap<>();
    private final Map<Long, Boolean> inConversationMode = new HashMap<>();

    private static final String CORRECT_IMAGE_PATH = "images\\EZpNyk3XYAA7kv0.jpg";
    private static final String WRONG_IMAGE_PATH = "images\\scale_1200.jpg";

    private final List<Question> javaQuestions;
    private final List<Question> pythonQuestions;

    public Bot() {
        this.javaQuestions = new ArrayList<>();
        this.pythonQuestions = new ArrayList<>();
        initializeJavaQuestions();
        initializePythonQuestions();
    }

    public static InlineKeyboardMarkup createButtons(List<String> rowsInLine) {
        return null;
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
    public String getResponse(String userMessage) {
        String lowerMsg = userMessage.toLowerCase().trim();

        // 1. Проверяем приветствия и общие фразы
        if (conversationResponses.containsKey(lowerMsg)) {
            return conversationResponses.get(lowerMsg);
        }

        // 2. Проверяем технические вопросы
        for (String key : techAnswers.keySet()) {
            if (lowerMsg.contains(key)) {
                return techAnswers.get(key);
            }
        }

        // 3. Проверяем мотивационные фразы
        for (String key : motivation.keySet()) {
            if (lowerMsg.contains(key)) {
                return motivation.get(key);
            }
        }

        // 4. Проверяем личные вопросы
        for (String key : personalQuestions.keySet()) {
            if (lowerMsg.contains(key)) {
                return personalQuestions.get(key);
            }
        }

        // 5. Если ничего не найдено
        return "Я не совсем понял ваш вопрос. Можете уточнить?\n" +
                "Например, вы можете спросить:\n" +
                "- ООП\n" +
                "- Пример кода\n" +
                "- Шутку про программистов\n" +
                "- Мотивацию";
    }


    Map<String, String> conversationResponses = Map.ofEntries(
            entry("привет", "Привет! Я бот-помощник по Java и Python. Как я могу помочь?"),
            entry("здравствуй", "Здравствуйте! Готов помочь с программированием. Что вас интересует?"),
            entry("hi", "Hello! I'm a programming tutor bot. Would you like to talk in English?"),
            entry("как дела", "Отлично! Готов помочь с вопросами или тестами. Как у тебя настроение?"),
            entry("как жизнь", "Как у бота - отлично! Всегда на связи и готов помочь. А у вас как?"),
            entry("кто ты", "Я учебный бот для программистов. Могу:\n- Провести тест\n- Объяснить концепции\n- Дать примеры кода\nЧто интересует?"),
            entry("меню", "Доступные команды:\n/test - начать тест\n/chat - свободный диалог\n/code - получить пример кода\n/help - справка\n/joke - шутка про код"),
            entry("погода","Извините, я не могу проверить погоду, но надеюсь, что она хорошая! Какое время года вам нравится?"),
            entry("что ты умеешь","Я могу:\n- Объяснять концепции программирования\n- Проводить тесты\n- Давать советы по коду\n- Поддерживать беседу"),
            entry("выход","До свидания! Хорошего дня программирования!"),
            entry("пока","До скорой встречи! Если будут вопросы - я тут!"),
            entry("спасибо","Всегда пожалуйста! Обращайся, если что-то понадобится 😊"),
            entry("хорошо","Отлично! Может продолжим обучение? Или расскажу что-нибудь интересное?"),
            entry("что нового","В мире программирования всегда что-то происходит! Недавно вышла новая версия Java/Python. Хотите узнать подробности?")
    );

    Map<String, String> techAnswers = Map.ofEntries(
            entry("ооп", "ООП включает 4 принципа:\n1. Инкапсуляция\n2. Наследование\n3. Полиморфизм\n4. Абстракция\nРассказать подробнее о каком-то из них?"),
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

    private final Map<String, String> codeExamples = Map.ofEntries(
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

    Map<String, String> emotionalResponses = Map.ofEntries(
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

    // Новые категории
    Map<String, String> jokes = Map.ofEntries(
            entry("шутка", "Как называют программиста, который боится женщин?\nЖеноФоб... или wait, это же женоФор!"),
            entry("анекдот", "Программист ставит чайник на плиту и ждет, пока вода закипит...\nТаймаут."),
            entry("смешно", "Почему программисты путают Хэллоуин и Рождество?\nПотому что Oct 31 == Dec 25!"),
            entry("баг", "Разговор двух программистов:\n- У меня баг!\n- Перезагрузись.\n- Не помогает.\n- Ну тогда не знаю...")
    );

    Map<String, String> motivation = Map.ofEntries(
            entry("мотивация", "Каждая ошибка - это шаг к мастерству! Продолжай в том же духе!"),
            entry("успех", "Помни: даже Билл Гейтс когда-то начинал с 'Hello, World!'"),
            entry("вера", "Ты можешь стать отличным разработчиком! Главное - практика и упорство."),
            entry("совет", "Лучший способ научиться - пробовать, ошибаться и исправлять!"),
            entry("цитата", "'Программирование - это не о том, чтобы знать все ответы, а о том, чтобы уметь их находить.'")
    );

    Map<String, String> personalQuestions = Map.ofEntries(
            entry("имя", "Меня зовут CodeTutor, но ты можешь придумать мне другое имя 😊"),
            entry("возраст", "Я цифровой, поэтому мой возраст измеряется в версиях! Сейчас я v2.0"),
            entry("создатель", "Меня создал разработчик, который тоже когда-то начинал с основ программирования"),
            entry("дом", "Я живу в облаке, но иногда спускаюсь на сервера к людям"),
            entry("друг", "Конечно, мы можем быть друзьями! Друзья-программисты - это здорово!")
    );

    // Для обработки последовательных вопросов
    Map<String, String[]> followUpQuestions = Map.ofEntries(
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
    private void handleMessage(Update update) {
        Message message = update.getMessage();
        if (message == null || !message.hasText()) return;

        Long chatId = message.getChatId();
        String text = message.getText().toLowerCase();

        // Обработка команд
        if (text.startsWith("/")) {
            handleCommand(chatId, text, message.getFrom());
            return;
        }

        // Проверка режима свободного общения
        if (Boolean.TRUE.equals(inConversationMode.get(chatId))) {
            handleConversation(chatId, text);
            return;
        }

        // Стандартная обработка (тесты)
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
                sendTestSelection(chatId, user);
                break;
            case "/chat":
                startConversationMode(chatId);
                break;
            case "/stop":
                stopConversationMode(chatId);
                break;
            case "/help":
                sendHelpMessage(chatId);
                break;
            default:
                sendMessage(chatId, "Неизвестная команда. Попробуйте /help");
        }
    }
    private void handleCodeExampleRequest(Long chatId, String text) {
        // Нормализуем запрос (удаляем лишние слова и заменяем синонимы)
        String normalizedRequest = normalizeCodeRequest(text);
        String key = normalizedRequest.replaceAll("пример|код|покажи|как|сделать|реализовать", "").trim();

        // Ищем точное совпадение
        if (codeExamples.containsKey(key)) {
            String code = codeExamples.get(key);
            String language = detectCodeLanguage(code);
            sendMessage(chatId, "```" + language + "\n" + code + "\n```");
            return;
        }

        // Ищем частичное совпадение
        Optional<String> foundKey = codeExamples.keySet().stream()
                .filter(k -> key.contains(k))
                .findFirst();

        if (foundKey.isPresent()) {
            String code = codeExamples.get(foundKey.get());
            String language = detectCodeLanguage(code);
            sendMessage(chatId, "```" + language + "\n" + code + "\n```");
        } else {
            // Если ничего не найдено, показываем список доступных примеров
            String availableExamples = "Доступные примеры кода:\n" +
                    String.join("\n", codeExamples.keySet()) +
                    "\n\nПопробуйте уточнить, например: \"пример коллекции\"";
            sendMessage(chatId, availableExamples);
        }
    }
    private String detectCodeLanguage(String code) {
        if (code.contains("@WebServlet") || code.contains("List<")) return "java";
        if (code.contains("def ") || code.contains("lambda ")) return "python";
        if (code.contains("SELECT") || code.contains("CREATE TABLE")) return "sql";
        return "java"; // по умолчанию
    }

    private String normalizeCodeRequest(String text) {
        Map<String, String> synonyms = Map.of(
                "лист", "коллекция",
                "массив", "коллекция",
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

    private void handleConversation(Long chatId, String text) {
        text = text.toLowerCase().trim();

        // 1. Проверяем эмоциональные реакции
        if (emotionalResponses.containsKey(text)) {
            sendMessage(chatId, emotionalResponses.get(text));
            return;
        }

        // 2. Проверяем запросы примеров кода
        if (text.contains("пример") || text.contains("код") ||
                text.contains("покажи") || text.contains("как сделать")) {
            handleCodeExampleRequest(chatId, text);
            return;
        }

        // 3. Проверяем технические вопросы
        if (techAnswers.containsKey(text)) {
            sendMessage(chatId, techAnswers.get(text));

            // Добавляем follow-up вопросы если есть
            if (followUpQuestions.containsKey(text)) {
                String[] followUps = followUpQuestions.get(text);
                String followUpMessage = String.join("\n", followUps);
                sendMessage(chatId, followUpMessage);
            }
            return;
        }

        // 4. Проверяем шутки
        if (jokes.containsKey(text) || text.contains("шутка") || text.contains("анекдот")) {
            String jokeKey = text.replaceAll("шутка|анекдот|расскажи", "").trim();
            if (jokes.containsKey(jokeKey)) {
                sendMessage(chatId, jokes.get(jokeKey));
            } else {
                // Отправляем случайную шутку
                List<String> jokeList = new ArrayList<>(jokes.values());
                Random rand = new Random();
                sendMessage(chatId, jokeList.get(rand.nextInt(jokeList.size())));
            }
            return;
        }

        // 5. Используем общий метод getResponse для остальных случаев
        String response = getResponse(text);
        sendMessage(chatId, response);
    }

    private void startConversationMode(Long chatId) {
        inConversationMode.put(chatId, true);

        try {
            // Путь относительно папки resources
            String photoPath = "C:\\Users\\andre\\IdeaProjects\\DarkGora\\src\\main\\resources\\2821755862.jpg";
            File photoFile = new File(photoPath);
            if (!photoFile.exists()) {
                log.error("Файл изображения не найден по пути: {}", photoPath);
                sendMessage(chatId, "🔹 Режим чата включен! (изображение недоступно)");
                return;
            }
            String caption = "🔹 Режим свободного общения активирован!\n\n" +
                    "Теперь вы можете:\n" +
                    "- Задавать вопросы по Java/Python\n" +
                    "- Запрашивать примеры кода\n" +
                    "- Обсуждать концепции программирования\n\n" +
                    "Для возврата к тестам нажмите /test";

            sendPhoto(chatId, photoPath, caption);

        } catch (Exception e) {
            log.error("Ошибка при активации режима чата: {}", e.getMessage());
            sendMessage(chatId, "🔹 Режим свободного общения активирован! (изображение недоступно)");
        }
    }

    private void sendCodeExample(Long chatId, String topic) {
        String code = codeExamples.getOrDefault(topic,
                "Пример по теме \"" + topic + "\" не найден. Попробуй запросить:\n- коллекция\n- потоки\n- декоратор");

        sendMessage(chatId, "```java\n" + code + "\n```");
    }

    private void stopConversationMode(Long chatId) {
        inConversationMode.remove(chatId);
        sendMessage(chatId, "Режим свободного общения выключен. Для выбора теста напишите /test");
    }

    private void sendWelcomeMessage(Long chatId, User user) {
        String welcomeText = String.format(
                "Привет, %s! Я бот для тестирования знаний по Java и Python.\n\n" +
                        "Доступные команды:\n" +
                        "/test - начать тест\n" +
                        "/chat - свободное общение\n" +
                        "/help - помощь\n\n" +
                        "Выберите что вам интересно!",
                user.getFirstName());

        sendMessage(chatId, welcomeText);
    }

    private void sendHelpMessage(Long chatId) {
        String helpText = "Я могу:\n" +
                "1. Проводить тесты по Java и Python (/test)\n" +
                "2. Просто общаться (/chat)\n\n" +
                "Во время теста вы можете прервать его и начать заново.\n" +
                "Для выхода из режима общения напишите /stop";

        sendMessage(chatId, helpText);
    }

    private void sendTestSelection(Long chatId, User user) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        String photoPath = "C:\\Users\\andre\\IdeaProjects\\DarkGora\\src\\main\\resources\\eab2e77f92de15a95ebf828c08fe5290.jpg";
        String photoCaption = "Добро пожаловать в тесты, " + user.getFirstName() + "!";
        sendPhoto(chatId, photoPath, photoCaption);
        log.info("-Пользователь начинает тест  " + user.getFirstName() + "  " + user.getId());
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
            student.incrementCorrectAnswers();
        }

        try {
            if (isCorrect) {
                sendImageFromResources(chatId, CORRECT_IMAGE_PATH,
                        "✅ Правильно! Так держать!");
            } else {
                sendImageFromResources(chatId, WRONG_IMAGE_PATH,
                        "❌ Неправильно! Правильный ответ: " + currentQuestion.getCorrectAnswer());
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке изображения: {}", e.getMessage());
            // Фолбэк на текстовое сообщение
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
                sendMessage(chatId, caption); // Отправляем просто текст если изображение не найдено
                return;
            }

            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId.toString());
            photo.setPhoto(new InputFile(imageStream, "result.jpg")); // Имя файла может быть любым
            photo.setCaption(caption);

            execute(photo);
        } catch (Exception e) {
            log.error("Ошибка при отправке изображения", e);
            sendMessage(chatId, caption);
        }
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
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Что вы хотите сделать дальше?");

        // Создаем клавиатуру с вариантами
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Кнопка "Пройти тест заново"
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("Пройти тест заново")
                .callbackData("restart")
                .build());

        // Кнопка "Свободное общение"
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder()
                .text("Свободное общение")
                .callbackData("start_chat")
                .build());

        rows.add(row1);
        rows.add(row2);
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
            log.info("Пользователь " + chatId + "  " + "  Начал тест заново!");
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
        try {
            // Сначала пробуем загрузить из ресурсов
            InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);

            if (is == null) {
                // Если в ресурсах нет, пробуем как абсолютный путь
                File file = new File(filePath);
                if (!file.exists()) {
                    log.error("Файл изображения не найден: {}", filePath);
                    sendMessage(chatId, caption); // Отправляем текст если нет изображения
                    return;
                }
                is = new FileInputStream(file);
            }

            execute(SendPhoto.builder()
                    .chatId(chatId.toString())
                    .photo(new InputFile(is, "result.jpg"))
                    .caption(caption)
                    .build());
        } catch (Exception e) {
            log.error("Ошибка отправки фото: {}", e.getMessage());
            sendMessage(chatId, caption); // Фолбэк на текст при ошибке
        }
    }
}