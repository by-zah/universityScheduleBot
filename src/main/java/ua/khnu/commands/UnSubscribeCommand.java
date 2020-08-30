package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.entity.Subscription;
import ua.khnu.service.SubscriptionService;

import java.util.ArrayList;
import java.util.List;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class UnSubscribeCommand implements IBotCommand, CallBackCommand {

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
    public void processCallBackMessage(AbsSender absSender, String callBackData, long chatId) {
        service.unSubscribe(chatId, callBackData);
        sendMessage(absSender, "You are successfully unsubscribed", chatId);
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
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        SendMessage sendMessage = new SendMessage();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (Subscription subscription : subscriptions) {
            row.add(new InlineKeyboardButton()
                    .setText(subscription.getGroup())
                    .setCallbackData("/unSubscribe " + subscription.getGroup()));
            if (row.size() > 2) {
                rowList.add(row);
                row = new ArrayList<>();
            }
        }
        rowList.add(row);

        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage(absSender, "Select the group, you want to unsubscribe", message.getChatId(), sendMessage);
    }
}
