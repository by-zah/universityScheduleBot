package ua.khnu.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public final class MessageSender {
    private static final Logger LOG = LogManager.getLogger(MessageSender.class);

    private MessageSender() {
    }

    public static void sendMessage(AbsSender absSender, String messageText, long chatId) {
        sendMessage(absSender, messageText, chatId, new SendMessage());
    }

    public static void sendMessage(AbsSender absSender, String messageText, long chatId, SendMessage reMessage) {
        reMessage.setChatId(chatId);
        reMessage.setText(messageText);
        try {
            absSender.execute(reMessage);
        } catch (TelegramApiException e) {
            LOG.error("Can not execute reMessage", e);
        }
    }

}
