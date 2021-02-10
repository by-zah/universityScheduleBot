package ua.khnu.commands;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface FileCommand {
    String getCommandIdentifier();

    String getDescription();

    void processFileMessage(AbsSender absSender, Message message);
}
