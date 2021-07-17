package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.entity.Group;
import ua.khnu.service.GroupService;
import ua.khnu.service.SubscriptionService;
import ua.khnu.util.KeyboardBuilder;
import ua.khnu.util.MessageParser;

import java.util.List;
import java.util.stream.Collectors;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class UnSubscribeCommand implements SafelyIBotCommand, CallBackCommand {

    public static final String COMMAND_IDENTIFIER = "unsubscribe";
    private final SubscriptionService subscriptionService;
    private final GroupService groupService;

    @Autowired
    public UnSubscribeCommand(SubscriptionService subscriptionService, GroupService groupService) {
        this.subscriptionService = subscriptionService;
        this.groupService = groupService;
    }

    @Override
    public String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public void processCallBackMessage(AbsSender absSender, CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        String groupName = MessageParser.getArgumentByPositionAndSeparator(1, " ", callbackQuery.getData());
        subscriptionService.unSubscribe(chatId, groupName);
        sendMessage(absSender, chatId, "You are successfully unsubscribed");
    }

    @Override
    public String getDescription() {
        return "Unsubscribe you to schedule updates";
    }

    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] arguments) {
        List<Group> subscriptions = groupService.getUserGroups(message.getFrom().getId());
        if (subscriptions.isEmpty()) {
            sendMessage(absSender, message.getChatId(), "You are not subscribed to any of the groups");
            return;
        }
        SendMessage sendMessage = new SendMessage();
        InlineKeyboardMarkup inlineKeyboardMarkup = KeyboardBuilder
                .buildInlineKeyboard("/" + COMMAND_IDENTIFIER,
                        subscriptions.stream()
                                .map(Group::getName)
                                .collect(Collectors.toList()), 3);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage(absSender, message.getChatId(), sendMessage, "Select the group, you want to unsubscribe");
    }
}
