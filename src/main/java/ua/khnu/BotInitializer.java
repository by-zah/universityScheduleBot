package ua.khnu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@Component
public class BotInitializer {
    public static final String TIME_ZONE_ID = "Europe/Kiev";
    private static final Logger LOG = LogManager.getLogger(BotInitializer.class);
    private final Bot bot;
    private final TelegramBotsApi botsApi;
    private final Thread scheduleSendMessageDemon;

    @Autowired
    public BotInitializer(Bot bot, TelegramBotsApi botsApi, Thread scheduleSendMessageDemon) {
        this.bot = bot;
        this.botsApi = botsApi;
        this.scheduleSendMessageDemon = scheduleSendMessageDemon;
    }

    public void init() {
        try {
            LOG.info("Registering Bot...");
            botsApi.registerBot(bot);
            scheduleSendMessageDemon.start();
            LOG.info("Bot bot is ready for work!");
        } catch (TelegramApiRequestException e) {
            LOG.error("Error while initializing bot!", e);
        }
    }
}
