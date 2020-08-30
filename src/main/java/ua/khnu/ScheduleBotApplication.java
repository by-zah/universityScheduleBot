package ua.khnu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

//TODO add localization
public class ScheduleBotApplication {
    private static final Logger LOG = LogManager.getLogger(ScheduleBotApplication.class);

    public static void main(String[] args) {
        LOG.info("Initializing API context...");
        ApiContextInitializer.init();
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("ua.khnu");
        ctx.refresh();
        Bot bot = ctx.getBean(Bot.class);
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            LOG.info("Registering Bot...");
            botsApi.registerBot(bot);
            LOG.info("Bot bot is ready for work!");
        } catch (TelegramApiRequestException e) {
            LOG.error("Error while initializing bot!", e);
        }
    }
}
