package ua.khnu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.khnu.commands.processor.NonCommandProcessor;
import ua.khnu.service.MailingService;

import java.util.List;

@Component
public class Bot extends TelegramLongPollingCommandBot {
    private final String botToken;
    private final NonCommandProcessor nonCommandProcessor;

    @Autowired
    public Bot(List<IBotCommand> commands, MailingService mailingService, NonCommandProcessor nonCommandProcessor) {
        super();
        botToken = System.getenv("BOT_TOKEN");
        commands.forEach(this::register);
//        mailingService.setBot(this);
        this.nonCommandProcessor = nonCommandProcessor;
    }

    @Override
    public String getBotUsername() {
        return "KSBot";
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        nonCommandProcessor.process(update, this);
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
