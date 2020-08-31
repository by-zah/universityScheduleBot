package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.entity.Subscription;
import ua.khnu.service.SubscriptionService;
import ua.khnu.util.KeyboardBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static ua.khnu.util.MessageSender.sendCallBackAnswer;
import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class UnSubscribeCommand implements IBotCommand, CallBackCommand {

    public static final String COMMAND_IDENTIFIER = "unsubscribe";
    private final SubscriptionService service;

    @Autowired
    public UnSubscribeCommand(SubscriptionService service) {
        this.service = service;
    }

    @Override
    public String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public void processCallBackMessage(AbsSender absSender, CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        String callBackQueryId = callbackQuery.getId();
        service.unSubscribe(chatId, callbackQuery.getData());
        sendMessage(absSender, "You are successfully unsubscribed", chatId);
        sendCallBackAnswer(absSender, callBackQueryId);
    }

    @Override
    public String getDescription() {
        return "Unsubscribe you to schedule updates";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        List<Subscription> subscriptions = service.getAllUsersSubscriptions(message.getChatId());
        if (subscriptions.isEmpty()) {
            sendMessage(absSender, "You are not subscribed to any of the groups", message.getChatId());
            return;
        }
        SendMessage sendMessage = new SendMessage();
        InlineKeyboardMarkup inlineKeyboardMarkup = KeyboardBuilder
                .buildInlineKeyboard("/" + COMMAND_IDENTIFIER,
                        subscriptions.stream()
                                .map(Subscription::getGroup)
                                .collect(Collectors.toList()));
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage(absSender, "Select the group, you want to unsubscribe", message.getChatId(), sendMessage);
    }
}
