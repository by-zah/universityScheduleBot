package ua.khnu.commands.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.AbstractSender;
import ua.khnu.commands.CallBackCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;


@Component
public class CallBackCommandProcessor extends AbstractSender implements CommandProcessor {
    private final Map<String, CallBackCommand> commands;

    @Autowired
    public CallBackCommandProcessor(final List<CallBackCommand> callBackCommands) {
        this.commands = new HashMap<>();
        callBackCommands.forEach(command -> commands.put("/" + command.getCommandIdentifier(), command));
    }

    @Override
    public Predicate<Update> getCondition() {
        return Update::hasCallbackQuery;
    }

    @Override
    public void process(Update update, AbsSender absSender) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        String commandText = data.split(" ")[0];
        Optional.ofNullable(commands.get(commandText))
                .ifPresentOrElse(command -> command.processCallBackMessage(absSender, callbackQuery),
                        () -> sendMessage(absSender, chatId, "Unsupported operation"));
    }
}
