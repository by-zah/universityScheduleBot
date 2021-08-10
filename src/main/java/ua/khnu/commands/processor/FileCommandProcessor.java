package ua.khnu.commands.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.AbstractSender;
import ua.khnu.commands.FileCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;


@Component
public class FileCommandProcessor extends AbstractSender implements CommandProcessor {

    private final Map<String, FileCommand> commands;

    @Autowired
    public FileCommandProcessor(List<FileCommand> fileCommands) {
        this.commands = new HashMap<>();
        fileCommands.forEach(command -> commands.put(command.getCommandIdentifier(), command));
    }

    @Override
    public Predicate<Update> getCondition() {
        //TODO write unit test
        return update -> update.hasMessage() && update.getMessage().hasDocument();
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
            sendMessage(absSender, message.getChatId(), "Unsupported operation");
        }
    }

}
