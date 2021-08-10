package ua.khnu.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ua.khnu.exception.BotException;

public abstract class AbstractSender {
    private static final Logger LOG = LogManager.getLogger(AbstractSender.class);


    public void sendMessage(AbsSender absSender, long chatId, String messageText) {
        sendMessage(absSender, chatId, new SendMessage(), messageText);
    }

    protected void sendMessage(AbsSender absSender, long chatId, SendMessage reMessage, String messageText) {
        reMessage.setChatId(String.valueOf(chatId));
        reMessage.setText(messageText);
        execute(absSender, reMessage);
    }

    protected void sendFile(AbsSender absSender, InputFile inputFile, long chatId, String messageText) {
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

    protected void sendCallBackAnswer(AbsSender absSender, String callBackQueryId) {
        AnswerCallbackQuery message = new AnswerCallbackQuery();
        message.setCallbackQueryId(callBackQueryId);
        execute(absSender, message);
    }

    protected void execute(AbsSender absSender, BotApiMethod<?> apiMethod) {
        try {
            LOG.info("Execute {}", apiMethod);
            absSender.execute(apiMethod);
        } catch (TelegramApiRequestException e) {
            LOG.error(e);
        } catch (TelegramApiException e) {
            LOG.error("Can not execute reMessage {}", apiMethod, e);
        }
    }

    protected String getArgumentByPositionAndSeparator(int position, String separator, String message) {
        String[] args = message.split(separator);
        if (args.length < position + 1) {
            throw new BotException("This command should have at least " + position + " argument(s)");
        }
        return args[position];
    }
}
