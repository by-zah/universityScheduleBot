package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.exception.BotException;
import ua.khnu.service.GroupService;

@Component
public class CreateNewGroupCommand extends SimpleAnswerCommand{
    private final GroupService groupService;

    @Autowired
    public CreateNewGroupCommand(GroupService groupService) {
        this.groupService = groupService;
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
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        try {
            groupService.createNewGroup(message.getChatId(),message.getText());
            sendMessage(absSender,"new group created",message.getChatId());
        } catch (BotException e){
            sendMessage(absSender,e.getMessage(),message.getChatId());
        }
    }
}
