package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.entity.Group;
import ua.khnu.entity.User;
import ua.khnu.service.SubscriptionService;
import ua.khnu.service.UserService;
import ua.khnu.util.KeyboardBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ua.khnu.util.MessageSender.sendCallBackAnswer;
import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class UnSubscribeCommand implements IBotCommand, CallBackCommand {

    public static final String COMMAND_IDENTIFIER = "unsubscribe";
    private final SubscriptionService subscriptionService;
    private final UserService userService;

    @Autowired
    public UnSubscribeCommand(SubscriptionService subscriptionService, UserService userService) {
        this.subscriptionService = subscriptionService;
        this.userService = userService;
    }

    @Override
    public String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public void processCallBackMessage(AbsSender absSender, CallbackQuery callbackQuery) {
        long chatId = callbackQuery.getMessage().getChatId();
        String callBackQueryId = callbackQuery.getId();
        subscriptionService.unSubscribe(chatId, callbackQuery.getData());
        sendMessage(absSender, chatId, "You are successfully unsubscribed");
        sendCallBackAnswer(absSender, callBackQueryId);
    }

    @Override
    public String getDescription() {
        return "Unsubscribe you to schedule updates";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Optional<User> user = userService.getUserById(message.getFrom().getId());
        if (!user.isPresent()) {
            sendMessage(absSender, message.getChatId(), "You aren't registered");
            return;
        }
        List<Group> subscriptions = user.get().getGroups();
        if (subscriptions.isEmpty()) {
            sendMessage(absSender, message.getChatId(), "You are not subscribed to any of the groups");
            return;
        }
        SendMessage sendMessage = new SendMessage();
        InlineKeyboardMarkup inlineKeyboardMarkup = KeyboardBuilder
                .buildInlineKeyboard("/" + COMMAND_IDENTIFIER,
                        subscriptions.stream()
                                .map(Group::getName)
                                .collect(Collectors.toList()));
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage(absSender, message.getChatId(), "Select the group, you want to unsubscribe", sendMessage);
    }
}
