package ua.khnu.commands;

import org.telegram.telegrambots.meta.bots.AbsSender;

public interface MultiCommand {
    String getCommandIdentifier();

    void processMultiCommand(AbsSender absSender, long chatId, String message);
}
