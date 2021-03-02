package ua.khnu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.ServerSocket;

//TODO
// 1. Add localization
// 2. Investigate and implement /stop command - done
// 3. Switch to TimedSendLongPollingBot
// 4. Deadline feature - done
// 5. Collect statistic
// 6. User settings - done
// 7. Manage deadline that user created


@ComponentScan
public class ScheduleBotApplication {
    private static final Logger LOG = LogManager.getLogger(ScheduleBotApplication.class);
    private static final int PORT = 8443;
    private static ApplicationContext context;

    public static void main(String[] args) {
        context = new AnnotationConfigApplicationContext(ScheduleBotApplication.class);
        startBot();
        listenHostingPort();
    }

    private static void listenHostingPort() {
//        if (PORT == null){
//            return;
//        }
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOG.info(serverSocket.accept());
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    private static void startBot() {
        try {
            LOG.info("Registering Bot...");
            var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(context.getBean(Bot.class));
            LOG.info("Bot bot is ready for work!");
        } catch (TelegramApiException e) {
            LOG.error("Error while initializing bot!", e);
        }
    }
}
