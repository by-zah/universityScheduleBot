package ua.khnu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.extensions.bots.timedbot.TimedSendLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.khnu.commands.processor.NonCommandProcessor;
import ua.khnu.service.MailingService;
import ua.khnu.util.MessageSender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BotV2 extends TimedSendLongPollingBot {
    private static final Logger LOG = LogManager.getLogger(BotV2.class);
    private final Map<String, IBotCommand> commandsRegistry;
    private final NonCommandProcessor nonCommandProcessor;


    @Autowired
    public BotV2(List<IBotCommand> commands, NonCommandProcessor nonCommandProcessor, MailingService mailingService) {
        super();
        this.nonCommandProcessor = nonCommandProcessor;
        this.commandsRegistry = commands.stream()
                .collect(Collectors.toMap(IBotCommand::getCommandIdentifier, Function.identity()));
        mailingService.setBot(this);
    }


    @Override
    public void sendMessageCallback(Long chatId, Object messageRequest) {
        if (!(messageRequest instanceof BotApiMethod<?>)) {
            throw new IllegalArgumentException();
        }
        MessageSender.execute(this, (BotApiMethod<?>) messageRequest);

    }

    @Override
    public String getBotUsername() {
        return "KSBot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        LOG.info("Update received {}", update);
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.isCommand()) {
                if (!executeCommand(message)) {
                    nonCommandProcessor.process(update, this);
                }
                return;
            }
        }
        nonCommandProcessor.process(update, this);
    }

    private boolean executeCommand(Message message) {
        if (message.hasText()) {
            var text = message.getText();
            if (text.startsWith(BotCommand.COMMAND_INIT_CHARACTER)) {
                var commandMessage = text.substring(1);
                var commandSplit = commandMessage.split(BotCommand.COMMAND_PARAMETER_SEPARATOR_REGEXP);

                var commandIdentifier = commandSplit[0];
                var command = commandsRegistry.get(commandIdentifier);
                if (command != null) {
                    String[] parameters = Arrays.copyOfRange(commandSplit, 1, commandSplit.length);
                    command.processMessage(this, message, parameters);
                    return true;
                }
            }
        }
        return false;
    }


}
