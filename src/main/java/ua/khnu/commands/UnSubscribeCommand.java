package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.service.SubscriptionService;

@Component
public class UnSubscribeCommand extends SimpleAnswerCommand {

    private final SubscriptionService service;

    @Autowired
    public UnSubscribeCommand(SubscriptionService service) {
        this.service = service;
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
        service.unSubscribe(chatId, message.getText());
        sendMessage(absSender, "You are successfully unsubscribed", chatId);
    }
}
