package ua.khnu.commans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartCommand implements IBotCommand {
    private static final Logger LOG = LogManager.getLogger(StartCommand.class);

    @Override
    public String getCommandIdentifier() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Start bot command";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage reMessage = new SendMessage();
        reMessage.setChatId(message.getChatId());
        reMessage.setText("Bot is started, you can /subscribe or" + System.lineSeparator() + " /unSubscribe to receive or not university schedule updates");
        try {
            absSender.execute(reMessage);
        } catch (TelegramApiException e) {
            LOG.error("Can not execute reMessage", e);
        }
    }
}
