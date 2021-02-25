package ua.khnu.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public final class MessageSender {
    private static final Logger LOG = LogManager.getLogger(MessageSender.class);

    private MessageSender() {
    }

    public static void sendMessage(AbsSender absSender, long chatId, String messageText) {
        sendMessage(absSender, chatId, messageText, new SendMessage());
    }

    public static void sendMessage(AbsSender absSender, long chatId, String messageText, SendMessage reMessage) {
        reMessage.setChatId(String.valueOf(chatId));
        reMessage.setText(messageText);
        LOG.info("Start sending message {} to chatId {}", reMessage, chatId);
        execute(absSender, reMessage);
    }

    public static void sendFile(AbsSender absSender, InputFile inputFile, long chatId, String messageText) {
        var sendDocument = new SendDocument();
        sendDocument.setDocument(inputFile);
        sendDocument.setCaption(messageText);
        sendDocument.setChatId(String.valueOf(chatId));
        try {
            absSender.execute(sendDocument);
        } catch (TelegramApiException e) {
            LOG.error("Can't send document {}", sendDocument, e);
        }

    }

    public static void sendCallBackAnswer(AbsSender absSender, String callBackQueryId) {
        AnswerCallbackQuery message = new AnswerCallbackQuery();
        message.setCallbackQueryId(callBackQueryId);
        execute(absSender, message);
    }

    public static void execute(AbsSender absSender, BotApiMethod<?> apiMethod) {
        try {
            absSender.execute(apiMethod);
        } catch (TelegramApiException e) {
            LOG.error("Can not execute reMessage {}", apiMethod, e);
        }
    }
}
