package ua.khnu.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class MarkDeadlineAsDoneCommand implements SafelyIBotCommand {
    public static final String COMMAND_IDENTIFIER = "markDeadlineAsDone";

    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] strings) {

    }

    @Override
    public String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
