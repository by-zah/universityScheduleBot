package ua.khnu.commans;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class NonCommandCommand extends SimpleAnswerCommand {

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
        sendMessage(absSender, "Unsupported command", message.getChatId());
    }
}
