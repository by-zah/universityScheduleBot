package ua.khnu.commans;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

public class UnSubscribeCommand extends SimpleAnswerIBotCommand {
    private final List<Long> subscribers;

    public UnSubscribeCommand(List<Long> subscribers) {
        this.subscribers = subscribers;
    }
    @Override
    public String getCommandIdentifier() {
        return "unSubscribe";
    }

    @Override
    public String getDescription() {
        return "Unsubscribe you to schedule updates";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        long chatId = message.getChatId();
        boolean containsId = subscribers.contains(chatId);
        if (containsId) {
            subscribers.remove(chatId);
        }
        String messageText = containsId ?
                "You are successfully unsubscribed" : "You were not subscribed";
        sendMessage(absSender,messageText,chatId);
    }
}