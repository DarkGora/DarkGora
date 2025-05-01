import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

@Log4j2
public class Main {
    private static final String PROXY_HOST = "84.247.168.26";
    private static final int PROXY_PORT = 40245;
    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = TimeUnit.SECONDS.toMillis(10);
    private static final int CONNECTION_TIMEOUT_MS = 15000;

    public static void main(String[] args) {
        log.info("Starting bot...");

        boolean proxyAvailable = isProxyAvailable();
        if (!proxyAvailable) {
            log.warn("Proxy not available, trying direct connection");
        }

        try {
            DefaultBotOptions botOptions = configureBotOptions(proxyAvailable);
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            Bot bot = new Bot(botOptions);

            registerBotWithRetry(botsApi, bot, MAX_RETRIES);
            log.info("Bot started successfully!");

            // Запуск HTTP сервера для /ping (чтобы не спал на Replit)
            startHttpServer();

        } catch (Exception e) {
            log.error("Failed to start bot", e);
            System.exit(1);
        }
    }

    private static DefaultBotOptions configureBotOptions(boolean useProxy) {
        DefaultBotOptions botOptions = new DefaultBotOptions();
        if (useProxy) {
            botOptions.setProxyHost(PROXY_HOST);
            botOptions.setProxyPort(PROXY_PORT);
            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS4);
        }
        botOptions.setGetUpdatesTimeout(CONNECTION_TIMEOUT_MS);
        return botOptions;
    }

    private static void registerBotWithRetry(TelegramBotsApi botsApi, Bot bot, int maxRetries)
            throws InterruptedException {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("Registering bot (attempt {}/{})", attempt, maxRetries);
                botsApi.registerBot(bot);
                return;
            } catch (TelegramApiException e) {
                log.warn("Registration failed: {}", e.getMessage());

                if (attempt < maxRetries) {
                    log.info("Retrying in {} ms...", RETRY_DELAY_MS);
                    Thread.sleep(RETRY_DELAY_MS);
                } else {
                    throw new RuntimeException("Failed to register bot after " + maxRetries + " attempts", e);
                }
            }
        }
    }

    private static boolean isProxyAvailable() {
        try (Socket socket = new Socket()) {
            log.info("Testing connection to proxy {}:{}...", PROXY_HOST, PROXY_PORT);
            socket.connect(new InetSocketAddress(PROXY_HOST, PROXY_PORT), CONNECTION_TIMEOUT_MS);
            log.info("Proxy connection successful!");
            return true;
        } catch (Exception e) {
            log.error("Failed to connect to proxy {}:{} - {}",
                    PROXY_HOST, PROXY_PORT, e.getMessage());
            log.info("Trying without proxy...");
            return false;
        }
    }

    // === Добавлено: запуск HTTP сервера для эндпоинта /ping ===
    private static void startHttpServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/ping", new PingHandler());
            server.setExecutor(null); // default executor
            server.start();
            log.info("HTTP Server started on port 8080");
        } catch (IOException e) {
            log.error("Failed to start HTTP server", e);
        }
    }

    // === Обработчик для /ping ===
    static class PingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "OK";
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        }
    }
}