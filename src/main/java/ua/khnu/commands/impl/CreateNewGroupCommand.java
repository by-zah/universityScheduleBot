package ua.khnu.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.AbstractSender;
import ua.khnu.commands.SafelyIBotCommand;
import ua.khnu.service.GroupService;
import ua.khnu.service.UserService;

@Component
public class CreateNewGroupCommand extends AbstractSender implements SafelyIBotCommand {
    private final GroupService groupService;
    private final UserService userService;

    @Autowired
    public CreateNewGroupCommand(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    @Override
    public String getCommandIdentifier() {
        return "createNewGroup";
    }

    @Override
    public String getDescription() {
        return "allow everyone create group";
    }

    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] arguments) {
        var userId = message.getFrom().getId();
        userService.createOrUpdate(userId, message.getChatId());
        groupService.createNewGroup(userId, message.getText());
        sendMessage(absSender, message.getChatId(), "new group created");
    }
}
