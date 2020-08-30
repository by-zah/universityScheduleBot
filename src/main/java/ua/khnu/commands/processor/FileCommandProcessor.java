package ua.khnu.commands.processor;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.FileCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static ua.khnu.util.MessageSender.sendMessage;


public class FileCommandProcessor implements CommandProcessor<FileCommand> {

    private final Map<String, FileCommand> commands;

    public FileCommandProcessor() {
        this.commands = new HashMap<>();
    }

    @Override
    public Predicate<Update> getCondition() {
        return update -> {
            if (update.hasMessage()) {
                return update.getMessage().hasDocument();
            }
            return false;
        };
    }

    @Override
    public void process(Update update, AbsSender absSender) {
        Message message = update.getMessage();
        String caption = message.getCaption();
        String commandText = caption.split(" ")[0];
        Optional<FileCommand> command = Optional.ofNullable(commands.get(commandText));
        if (command.isPresent()) {
            command.get().processFileMessage(absSender, message);
        } else {
            sendMessage(absSender, "Unsupported operation", message.getChatId());
        }
    }

    @Override
    public void registerCommand(FileCommand command) {
        commands.put(command.getCommandIdentifier(), command);
    }

}
