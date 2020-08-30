package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.entity.Group;
import ua.khnu.exception.BotException;
import ua.khnu.service.GroupService;
import ua.khnu.service.SubscriptionService;

import java.util.ArrayList;
import java.util.List;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class SubscribeCommand implements CallBackCommand, IBotCommand {
    public static final String YOU_ARE_SUCCESSFULLY_SUBSCRIBED = "You are successfully subscribed";

    private final SubscriptionService subscriptionService;
    private final GroupService groupService;

    @Autowired
    public SubscribeCommand(SubscriptionService subscriptionService, GroupService groupService) {
        this.subscriptionService = subscriptionService;
        this.groupService = groupService;
    }

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public void processCallBackMessage(AbsSender absSender, String callBackData, long chatId) {
        try {
            subscriptionService.subscribe(chatId, callBackData);
            sendMessage(absSender, YOU_ARE_SUCCESSFULLY_SUBSCRIBED, chatId);
        } catch (BotException e) {
            sendMessage(absSender, e.getMessage(), chatId);
        }
    }

    @Override
    public String getDescription() {
        return "Subscribe you to schedule updates";
    }


    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        List<Group> groups = groupService.getAllGroups();
        if (groups.isEmpty()) {
            sendMessage(absSender, "There isn`t any groups", message.getChatId());
            return;
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        SendMessage sendMessage = new SendMessage();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (Group group : groups) {
            row.add(new InlineKeyboardButton()
                    .setText(group.getName())
                    .setCallbackData("/subscribe " + group.getName()));
            if (row.size() > 2) {
                rowList.add(row);
                row = new ArrayList<>();
            }
        }
        rowList.add(row);

        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage(absSender, "Select the group, you want to subscribe", message.getChatId(), sendMessage);
    }
}
