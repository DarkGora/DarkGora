import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;

public class KeyboardFactory {

    public static InlineKeyboardMarkup createTestSelectionKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        // Кнопка для Java теста
        InlineKeyboardButton javaButton = createButton("Java тест", "test_java");

        // Кнопка для Python теста
        InlineKeyboardButton pythonButton = createButton("Python тест", "test_python");

        keyboard.setKeyboard(List.of(
                Collections.singletonList(javaButton),
                Collections.singletonList(pythonButton)
        ));
        return keyboard;
    }

    public static InlineKeyboardMarkup createOptionsKeyboard(List<String> options) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = options.stream()
                .map(option -> Collections.singletonList(createButton(option, option)))
                .toList();

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    public static InlineKeyboardMarkup createSingleButtonKeyboard(String text, String callbackData) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(Collections.singletonList(
                Collections.singletonList(createButton(text, callbackData))
        ));
        return keyboard;
    }

    public static InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }
}