package ua.khnu;

import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Bot extends TelegramLongPollingCommandBot {
    private final String botToken;
    private final IBotCommand nonCommandCommand;

    public Bot(IBotCommand nonCommandCommand) {
        botToken = System.getenv("BOT_TOKEN");
        this.nonCommandCommand = nonCommandCommand;
    }

    @Override
    public String getBotUsername() {
        return "KSBot";
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        nonCommandCommand.processMessage(this, update.getMessage(), null);
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
