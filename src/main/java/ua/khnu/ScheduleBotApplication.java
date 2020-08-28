package ua.khnu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;

//TODO add localization
public class ScheduleBotApplication {
    private static final Logger LOG = LogManager.getLogger(ScheduleBotApplication.class);

    public static void main(String[] args) {
        LOG.info("Initializing API context...");
        ApiContextInitializer.init();
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("ua.khnu");
        ctx.refresh();
        BotInitializer botInitializer = ctx.getBean(BotInitializer.class);
        botInitializer.init();
    }
}
