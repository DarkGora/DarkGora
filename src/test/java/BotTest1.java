import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class BotTest1 {

    @Mock
    private Bot bot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendMessageWithRetryButton() throws TelegramApiException {
        Long chatId = 12345L;
        String text = "Тест завершен!";
        bot.sendMessageWithRetryButton(chatId, text);
        verify(bot, times(1)).execute(any(SendMessage.class));
    }

    @Test
    void testSendMessageWithRetryButtonThrowsException() throws TelegramApiException {
        Long chatId = 12345L;
        String text = "Тест завершен!";
        doThrow(new TelegramApiException("Ошибка API Telegram")).when(bot).execute(any(SendMessage.class));
        bot.sendMessageWithRetryButton(chatId, text);
        verify(bot, times(1)).execute(any(SendMessage.class));
    }
}