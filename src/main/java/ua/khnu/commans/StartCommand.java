package ua.khnu.commans;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class StartCommand extends SimpleAnswerIBotCommand{

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
        sendMessage(absSender
                ,"Bot is started, you can /subscribe or"
                + System.lineSeparator()
                + " /unSubscribe to receive or not university schedule updates"
                ,message.getChatId());
    }
}
