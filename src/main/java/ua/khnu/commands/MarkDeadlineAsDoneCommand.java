package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.entity.pk.UserDeadlinePK;
import ua.khnu.service.DeadlineService;

import java.util.List;

import static ua.khnu.util.KeyboardBuilder.buildInlineKeyboard;
import static ua.khnu.util.MessageParser.getArgumentByPositionAndSeparator;
import static ua.khnu.util.MessageSender.execute;

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
        var newValue = deadlineService.changeDeadlineDoneStatus(new UserDeadlinePK(callbackQuery.getFrom().getId(), Integer.parseInt(deadlineId)));
        var buttonText = newValue ? "Mark as not done" : "Mark as done";
        var keyboard = buildInlineKeyboard("/" + getCommandIdentifier(), List.of(deadlineId), List.of(buttonText), 1);
        var editMessageReplyMarkup = new EditMessageReplyMarkup();
        var message = callbackQuery.getMessage();
        editMessageReplyMarkup.setChatId(String.valueOf(message.getChatId()));
        editMessageReplyMarkup.setMessageId(message.getMessageId());
        editMessageReplyMarkup.setReplyMarkup(keyboard);
        execute(absSender, editMessageReplyMarkup);
    }

}
