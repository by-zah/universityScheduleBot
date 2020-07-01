package ua.khnu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;

public class Bot extends TelegramLongPollingCommandBot {
    private static final Logger LOG = LogManager.getLogger(Bot.class);
    public static final String TOKEN_FILE_PATH = "token.txt";
    private static final String BOT_TOKEN = "1372200103:AAE7d3xaocVhD0RV4z17vahNDm7sBYTwmvk";

    public Bot() throws IOException {

    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        updates.forEach(update ->{
            SendMessage message = new SendMessage();
            message.setText("!!!");
            message.setChatId(update.getMessage().getChatId());
            sendMessageToUser(message,update.getMessage().getFrom(),null);
        });
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
        return BOT_TOKEN;
    }

    private void sendMessageToUser(SendMessage message, User receiver, User sender) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOG.error(e);
        }
    }

    private void replyToUser(SendMessage message, User user, String messageText) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOG.error(e);
        }
    }
}
