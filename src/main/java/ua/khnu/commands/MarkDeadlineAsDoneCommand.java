package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.entity.pk.UserDeadlinePK;
import ua.khnu.service.DeadlineService;

import static ua.khnu.util.MessageParser.getArgumentByPositionAndSeparator;
import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class MarkDeadlineAsDoneCommand implements CallBackCommand {
    public static final String COMMAND_IDENTIFIER = "markDeadlineAsDone";
    private final DeadlineService deadlineService;

    @Autowired
    public MarkDeadlineAsDoneCommand(DeadlineService deadlineService) {
        this.deadlineService = deadlineService;
    }

    @Override
    public String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public void processCallBackMessage(AbsSender absSender, CallbackQuery callbackQuery) {
        var deadlineId = getArgumentByPositionAndSeparator(1, " ", callbackQuery.getData());
        deadlineService.markUserDeadlineAsDone(new UserDeadlinePK(callbackQuery.getFrom().getId(), Integer.parseInt(deadlineId)));
        sendMessage(absSender, callbackQuery.getMessage().getChatId(), "Deadline marked as done");
    }

}
