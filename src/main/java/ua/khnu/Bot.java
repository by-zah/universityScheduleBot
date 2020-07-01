package ua.khnu;

import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Bot extends TelegramLongPollingCommandBot {
    public static final String TOKEN_FILE_PATH = "token.txt";
    private final String botToken;

    public Bot() throws IOException {
        botToken = Files.readAllLines(Paths.get(TOKEN_FILE_PATH)).get(0);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {

    }

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public void processNonCommandUpdate(Update update) {

    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
