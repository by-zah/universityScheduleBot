package ua.khnu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ua.khnu.commans.NonCommandCommand;
import ua.khnu.commans.StartCommand;

public class BotInitializer {
    private static final Logger LOG = LogManager.getLogger(BotInitializer.class);
    private final TelegramLongPollingCommandBot bot;

    public BotInitializer() {
        this.bot = new Bot(new NonCommandCommand());
        registerCommands();
    }

    public void init() {
        try {
            LOG.info("Initializing API context...");
            ApiContextInitializer.init();
            TelegramBotsApi botsApi = new TelegramBotsApi();
            LOG.info("Registering Bot...");
            botsApi.registerBot(bot);
            LOG.info("Bot bot is ready for work!");
        } catch (TelegramApiRequestException e) {
            LOG.error("Error while initializing bot!", e);
        }
    }

    private void registerCommands() {
        bot.register(new StartCommand());
    }
}
