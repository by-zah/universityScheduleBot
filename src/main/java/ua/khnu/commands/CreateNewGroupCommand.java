package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.exception.BotException;
import ua.khnu.service.GroupService;
import ua.khnu.service.UserService;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class CreateNewGroupCommand implements IBotCommand {
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
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        try {
            Integer userId = message.getFrom().getId();
            userService.createOrUpdate(userId,message.getChatId());
            groupService.createNewGroup(userId, message.getText());
            sendMessage(absSender, message.getChatId(), "new group created");
        } catch (BotException e) {
            sendMessage(absSender, message.getChatId(), e.getMessage());
        }
    }
}
