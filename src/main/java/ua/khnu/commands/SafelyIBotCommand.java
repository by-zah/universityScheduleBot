package ua.khnu.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.exception.BotException;


public interface SafelyIBotCommand extends IBotCommand {
    Logger LOG = LogManager.getLogger(SafelyIBotCommand.class);

    @Override
    default void processMessage(AbsSender absSender, Message message, String[] strings) {
        try {
            safelyProcessMessage(absSender, message, strings);
        } catch (BotException e) {
            LOG.error("Exception while message processing, exception = {}, message = {}, args = {}", e, message, strings);
            handleBotException(e, absSender, message.getChatId());
        } catch (Exception e) {
            LOG.error("Exception while message processing, message = {}, args = {}", message, strings, e);
            sendMessage(absSender, message.getChatId(), "Problem happens while command processing");
        }
    }

    void safelyProcessMessage(AbsSender absSender, Message message, String[] strings);

    default void handleBotException(BotException e, AbsSender absSender, long chatId) {
        sendMessage(absSender, chatId, e.getMessage());
    }

    void sendMessage(AbsSender absSender, long chatId, String messageText);
}
