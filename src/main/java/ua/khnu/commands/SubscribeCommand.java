package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.exception.BotException;
import ua.khnu.service.SubscriptionService;

@Component
public class SubscribeCommand extends SimpleAnswerCommand {
    public static final String YOU_ARE_SUCCESSFULLY_SUBSCRIBED = "You are successfully subscribed";

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscribeCommand(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
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
        try {
            subscriptionService.subscribe(chatId, message.getText());
            sendMessage(absSender, YOU_ARE_SUCCESSFULLY_SUBSCRIBED, chatId);
        } catch (BotException e) {
            sendMessage(absSender, e.getMessage(), chatId);
        }
    }
}
