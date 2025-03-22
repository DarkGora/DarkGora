import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BotTest {
    @Test
    public void createButtonTest() {
        List<String> rowsInLine = List.of("Buttons 1", "Button 2");
        InlineKeyboardMarkup inlineKeyboardMarkup = Bot.createButtons(rowsInLine);
        assertNotNull(inlineKeyboardMarkup);
        assertEquals(2, inlineKeyboardMarkup.getKeyboard().size());
        assertEquals(1, inlineKeyboardMarkup.getKeyboard().get(0).size());
        assertEquals(1, inlineKeyboardMarkup.getKeyboard().get(1).size());
        assertEquals("Buttons 1", inlineKeyboardMarkup.getKeyboard().get(0).get(0).getText());
        assertEquals("Button 2", inlineKeyboardMarkup.getKeyboard().get(1).get(0).getText());
        assertEquals("Buttons 1", inlineKeyboardMarkup.getKeyboard().get(0).get(0).getCallbackData());
        assertEquals("Button 2", inlineKeyboardMarkup.getKeyboard().get(1).get(0).getCallbackData());
    }

    @Test
    public void ButtonTest() {
        List<String> rowsInLine = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = Bot.createButtons(rowsInLine);
        assertNotNull(inlineKeyboardMarkup);
        assertEquals(0, inlineKeyboardMarkup.getKeyboard().size());
    }
}