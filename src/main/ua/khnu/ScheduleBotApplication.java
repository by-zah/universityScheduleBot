package ua.khnu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

//TODO add localization

@ComponentScan
public class ScheduleBotApplication {
    private static final Logger LOG = LogManager.getLogger(ScheduleBotApplication.class);
    private static ApplicationContext context;

    public static void main(String[] args) {
        context = new AnnotationConfigApplicationContext(ScheduleBotApplication.class);
        startBot();
    }

    private static void startBot() {
        try {
            LOG.info("Registering Bot...");
            new TelegramBotsApi(DefaultBotSession.class).registerBot(context.getBean(Bot.class));
            LOG.info("Bot bot is ready for work!");
        } catch (TelegramApiException e) {
            LOG.error("Error while initializing bot!", e);
        }
    }
}
