import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KeyboardFactory {

    // Защита от создания экземпляров утилитного класса
    private KeyboardFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static InlineKeyboardMarkup createTestSelectionKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        // Кнопка для Java теста с эмодзи
        InlineKeyboardButton javaButton = createButton("☕ Java тест", "test_java");

        // Кнопка для Python теста с эмодзи
        InlineKeyboardButton pythonButton = createButton("🐍 Python тест", "test_python");

        // Добавляем кнопки в две строки
        keyboard.setKeyboard(List.of(
                Collections.singletonList(javaButton),
                Collections.singletonList(pythonButton)
        ));
        return keyboard;
    }

    public static InlineKeyboardMarkup createOptionsKeyboard(List<String> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Options list cannot be null or empty");
        }

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Разбиваем варианты ответов на группы по 2 для лучшего отображения
        for (int i = 0; i < options.size(); i += 2) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(createButton(options.get(i), options.get(i)));

            if (i + 1 < options.size()) {
                row.add(createButton(options.get(i + 1), options.get(i + 1)));
            }

            rows.add(row);
        }

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    public static InlineKeyboardMarkup createSingleButtonKeyboard(String text, String callbackData) {
        if (text == null || callbackData == null) {
            throw new IllegalArgumentException("Text and callback data cannot be null");
        }

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(Collections.singletonList(
                Collections.singletonList(createButton(text, callbackData))
        ));
        return keyboard;
    }
    public static InlineKeyboardButton createButton(String text, String callbackData) {
        if (text == null || callbackData == null) {
            throw new IllegalArgumentException("Text and callback data cannot be null");
        }

        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .switchInlineQueryCurrentChat("") // Это уберет @username бота
                .build();
    }
    //кнопка для игры
    public static InlineKeyboardMarkup createMainMenuKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(Arrays.asList(
                createButton("🎮 Угадай число", "start_game"),
                createButton("✊✌️✋ RPS", "start_rps")
        ));
        rows.add(Collections.singletonList(
                createButton("📝 Пройти тест", "start_test")
        ));
        keyboard.setKeyboard(rows);
        return keyboard;
    }


    // Новый метод для создания клавиатуры с действиями после теста
    public static InlineKeyboardMarkup createPostTestKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Первая строка: Действия с тестом
        rows.add(Arrays.asList(
                createButton("🔄 Повторить тест", "restart_test"),
                createButton("📊 Статистика", "show_stats")
        ));

        // Вторая строка: Навигация
        rows.add(Arrays.asList(
                createButton("📚 Другие тесты", "select_another_test"),
                createButton("🏠 В меню", "main_menu")
        ));

        // Третья строка: Дополнительные опции
        rows.add(Collections.singletonList(
                createButton("💬 Режим общения", "start_chat")
        ));

        keyboard.setKeyboard(rows);
        return keyboard;
    }
}