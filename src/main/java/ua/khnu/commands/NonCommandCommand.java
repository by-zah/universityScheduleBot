package ua.khnu.commands;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.function.Predicate;

public interface NonCommandCommand extends IBotCommand {
    Predicate<Message> getCondition();

    default String getCommandIdentifier() {
        return null;
    }
}
