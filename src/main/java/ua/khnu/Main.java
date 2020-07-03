package ua.khnu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;

public class Main {
    private static final Logger LOG = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        LOG.info("Initializing API context...");
        ApiContextInitializer.init();
        BotInitializer botInitializer = new BotInitializer();
        botInitializer.init();
    }
}
