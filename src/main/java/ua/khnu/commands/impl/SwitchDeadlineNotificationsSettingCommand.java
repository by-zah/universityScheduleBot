package ua.khnu.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.AbstractSender;
import ua.khnu.commands.SafelyIBotCommand;
import ua.khnu.service.UserService;

@Component
public class SwitchDeadlineNotificationsSettingCommand extends AbstractSender implements SafelyIBotCommand {
    private static final String DEADLINE_NOTIFICATIONS_SUCCESSFULLY_TURNED = "Deadline notifications successfully turned";
    public static final String COMMAND_IDENTIFIER = "switchDeadlineNotificationsSetting";

    private final UserService userService;

    @Autowired
    public SwitchDeadlineNotificationsSettingCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] strings) {
        var userSettings = userService.switchDeadlineNotificationSetting(message.getFrom().getId());
        var messageText = userSettings.isDeadlineNotificationsEnabled()
                ? DEADLINE_NOTIFICATIONS_SUCCESSFULLY_TURNED + " on"
                : DEADLINE_NOTIFICATIONS_SUCCESSFULLY_TURNED + " off";
        sendMessage(absSender, message.getChatId(), messageText);
    }

    @Override
    public String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
