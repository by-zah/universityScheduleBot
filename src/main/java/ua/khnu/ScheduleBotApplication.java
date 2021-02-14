package ua.khnu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//TODO add localization

@ComponentScan
public class ScheduleBotApplication {
    private static final Logger LOG = LogManager.getLogger(ScheduleBotApplication.class);
    private static final String PORT = System.getenv("PORT");
    private static ApplicationContext context;

    public static void main(String[] args) {
        LOG.info("Running with docker!");
        context = new AnnotationConfigApplicationContext(ScheduleBotApplication.class);
        startBot();
        listenHostingPort();
    }

    private static void listenHostingPort() {
        if (PORT == null){
            return;
        }
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(PORT))) {
            while (true) {
                serverSocket.accept();
            }
        } catch (IOException e) {
            LOG.error(e);
        }
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
