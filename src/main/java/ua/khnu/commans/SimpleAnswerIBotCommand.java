package ua.khnu.commans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class SimpleAnswerIBotCommand implements IBotCommand {
    private static final Logger LOG = LogManager.getLogger(SimpleAnswerIBotCommand.class);

    protected void sendMessage(AbsSender absSender, String messageText, long chatId) {
        SendMessage reMessage = new SendMessage();
        reMessage.setChatId(chatId);
        reMessage.setText(messageText);
        try {
            absSender.execute(reMessage);
        } catch (TelegramApiException e) {
            LOG.error("Can not execute reMessage", e);
        }
    }
}

