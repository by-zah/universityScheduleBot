package ua.khnu;

import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.khnu.commands.processor.NonCommandProcessor;

public class Bot extends TelegramLongPollingCommandBot {
    public static final String TIME_ZONE_ID = "Europe/Kiev";
    private final String botToken;
    private NonCommandProcessor nonCommandProcessor;

    public Bot() {
        super();
        botToken = System.getenv("BOT_TOKEN");
    }

    public void setNonCommandProcessor(NonCommandProcessor nonCommandProcessor) {
        this.nonCommandProcessor = nonCommandProcessor;
    }

    @Override
    public String getBotUsername() {
        return "KSBot";
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        nonCommandProcessor.process(update);
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void sendMessage(String text, long chatId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setChatId(chatId);
        execute(message);
    }
}
