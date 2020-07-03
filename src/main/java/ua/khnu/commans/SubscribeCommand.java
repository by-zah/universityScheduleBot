package ua.khnu.commans;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

public class SubscribeCommand extends SimpleAnswerCommand {
    private final List<Long> subscribers;

    public SubscribeCommand(List<Long> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Subscribe you to schedule updates";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        long chatId = message.getChatId();
        boolean containsId = subscribers.contains(chatId);
        if (!containsId) {
            subscribers.add(chatId);
        }
        String messageText = containsId ?
                "You are already subscribed" : "You are successfully subscribed";
        sendMessage(absSender,messageText,chatId);
    }
}
