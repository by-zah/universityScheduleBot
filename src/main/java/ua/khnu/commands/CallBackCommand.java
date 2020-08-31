package ua.khnu.commands;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface CallBackCommand {

    String getCommandIdentifier();

    void processCallBackMessage(AbsSender absSender, CallbackQuery callbackQuery);
}
