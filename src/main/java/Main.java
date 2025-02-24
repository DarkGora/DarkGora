import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static final String PROXY_HOST = "84.247.168.26";
    public static final int PROXY_PORT = 40245;

    public static void main(String[] args) throws TelegramApiException {
        DefaultBotOptions botOptions = new DefaultBotOptions();
        botOptions.setProxyHost(PROXY_HOST);
        botOptions.setProxyPort(PROXY_PORT);
        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS4);


        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();
        try {
            telegramBotsApi.registerBot(bot);
        }catch (TelegramApiRequestException e){
            e.printStackTrace();
        }
    }
}