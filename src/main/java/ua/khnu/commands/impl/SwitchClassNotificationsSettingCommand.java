package ua.khnu.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.SafelyIBotCommand;
import ua.khnu.service.UserService;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class SwitchClassNotificationsSettingCommand implements SafelyIBotCommand {
    private static final String CLASS_NOTIFICATIONS_SUCCESSFULLY_TURNED = "Class notifications successfully turned";
    private final UserService userService;

    @Autowired
    public SwitchClassNotificationsSettingCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] strings) {
        var userSettings = userService.switchClassNotificationSetting(message.getFrom().getId());
        var messageText = userSettings.isClassNotificationsEnabled()
                ? CLASS_NOTIFICATIONS_SUCCESSFULLY_TURNED + " on"
                : CLASS_NOTIFICATIONS_SUCCESSFULLY_TURNED + " off";
        sendMessage(absSender, message.getChatId(), messageText);
    }

    @Override
    public String getCommandIdentifier() {
        return "switchClassNotificationsSetting";
    }

    @Override
    public String getDescription() {
        return null;
    }
}
