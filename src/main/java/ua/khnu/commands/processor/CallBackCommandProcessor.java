package ua.khnu.commands.processor;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.CallBackCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static ua.khnu.util.MessageSender.sendMessage;

public class CallBackCommandProcessor implements CommandProcessor<CallBackCommand> {
    private final Map<String, CallBackCommand> commands;

    public CallBackCommandProcessor() {
        this.commands = new HashMap<>();
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
        Optional<CallBackCommand> command = Optional.ofNullable(commands.get(commandText));
        if (command.isPresent()) {
            command.get().processCallBackMessage(absSender, callbackQuery);
        } else {
            sendMessage(absSender, "Unsupported operation", chatId);
        }
    }

    @Override
    public void registerCommand(CallBackCommand command) {
        commands.put("/" + command.getCommandIdentifier(), command);
    }
}
