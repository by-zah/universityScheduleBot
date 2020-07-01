package ua.khnu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;

public class Main {
    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            LOG.info("Initializing API context...");
            ApiContextInitializer.init();
            TelegramBotsApi botsApi = new TelegramBotsApi();
            LOG.info("Registering Bot...");
            botsApi.registerBot(new Bot());
            LOG.info("Bot bot is ready for work!");
        } catch (TelegramApiRequestException e) {
            LOG.error("Error while initializing bot!", e);
        } catch (IOException e) {
            LOG.error("Can not read token from file", e);
        }
    }
}
