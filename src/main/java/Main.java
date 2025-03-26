
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.logging.LogManager;
import java.util.logging.Logger;

@Log4j2
public class Main {
    public static final String PROXY_HOST = "84.247.168.26";
    public static final int PROXY_PORT = 40245;

    public static void main(String[] args) throws TelegramApiException {
        log.info("-----Cool----Start----");
        DefaultBotOptions botOptions = new DefaultBotOptions();
        botOptions.setProxyHost(PROXY_HOST);
        botOptions.setProxyPort(PROXY_PORT);
        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS4);
        log.info("---Параметры---прокси---работают---");


        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();
        log.info("Bot register!!");
        try {
            telegramBotsApi.registerBot(bot);
        }catch (TelegramApiRequestException e){
            e.printStackTrace();
        }
    }
}