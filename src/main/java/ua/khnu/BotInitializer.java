package ua.khnu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ua.khnu.commans.NonCommandCommand;
import ua.khnu.commans.StartCommand;
import ua.khnu.commans.SubscribeCommand;
import ua.khnu.commans.UnSubscribeCommand;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BotInitializer {
    private static final Logger LOG = LogManager.getLogger(BotInitializer.class);
    private final TelegramLongPollingCommandBot bot;
    private final List<Long> subscribers;
    public BotInitializer() {
        this.bot = new Bot(new NonCommandCommand());
        subscribers = new CopyOnWriteArrayList<>();
        registerCommands();
    }

    public void init() {
        try {
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
        bot.register(new SubscribeCommand(subscribers));
        bot.register(new UnSubscribeCommand(subscribers));
    }
}
