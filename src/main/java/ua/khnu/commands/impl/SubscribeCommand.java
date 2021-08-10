package ua.khnu.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.AbstractSender;
import ua.khnu.commands.CallBackCommand;
import ua.khnu.commands.SafelyIBotCommand;
import ua.khnu.entity.Group;
import ua.khnu.exception.BotException;
import ua.khnu.service.GroupService;
import ua.khnu.service.SubscriptionService;
import ua.khnu.service.UserService;
import ua.khnu.util.KeyboardBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SubscribeCommand extends AbstractSender implements CallBackCommand, SafelyIBotCommand {
    private static final String COMMAND_IDENTIFIER = "subscribe";
    private static final String SUCCESS_MESSAGE =
            String.format("You are successfully subscribed, use /%s to check out you schedule", GetUsersScheduleCommand.COMMAND_IDENTIFIER);

    private final SubscriptionService subscriptionService;
    private final GroupService groupService;
    private final UserService userService;

    @Autowired
    public SubscribeCommand(SubscriptionService subscriptionService, GroupService groupService, UserService userService) {
        this.subscriptionService = subscriptionService;
        this.groupService = groupService;
        this.userService = userService;
    }

    @Override
    public String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public void processCallBackMessage(AbsSender absSender, CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        long chatId = message.getChatId();
        try {
            var groupName = getArgumentByPositionAndSeparator(1, " ", callbackQuery.getData());
            subscriptionService.subscribe(chatId, groupName);
            sendMessage(absSender, chatId, SUCCESS_MESSAGE);
        } catch (BotException e) {
            sendMessage(absSender, chatId, e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "Subscribe you to schedule updates";
    }


    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] arguments) {
        userService.createOrUpdate(message.getFrom().getId(), message.getChatId());
        List<Group> groups = groupService.getAllGroups();
        if (groups.isEmpty()) {
            sendMessage(absSender, message.getChatId(), "There isn`t any groups");
            return;
        }
        SendMessage sendMessage = new SendMessage();
        InlineKeyboardMarkup inlineKeyboardMarkup = KeyboardBuilder
                .buildInlineKeyboard("/" + COMMAND_IDENTIFIER,
                        groups.stream()
                                .map(Group::getName)
                                .collect(Collectors.toList()), 3);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage(absSender, message.getChatId(), sendMessage, "Select the group, you want to subscribe");
    }
}
