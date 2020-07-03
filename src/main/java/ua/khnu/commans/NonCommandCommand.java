package ua.khnu.commans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class NonCommandCommand implements IBotCommand {
    private static final Logger LOG = LogManager.getLogger(NonCommandCommand.class);

    @Override
    public String getCommandIdentifier() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage reMessage = new SendMessage();
        reMessage.setChatId(message.getChatId());
        reMessage.setText("Unsupported command");
        try {
            absSender.execute(reMessage);
        } catch (TelegramApiException e) {
            LOG.error("Can not execute reMessage", e);
        }
    }
}
