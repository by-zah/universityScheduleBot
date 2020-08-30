package ua.khnu.commands;

import org.telegram.telegrambots.meta.bots.AbsSender;

public interface CallBackCommand {

    String getCommandIdentifier();

    void processCallBackMessage(AbsSender absSender, String callBackData, long chatId);
}
