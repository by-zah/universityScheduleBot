package ua.khnu.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.AbstractSender;
import ua.khnu.commands.CallBackCommand;
import ua.khnu.commands.SafelyIBotCommand;
import ua.khnu.entity.Deadline;
import ua.khnu.service.DeadlineService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ua.khnu.util.Constants.DATE_TIME_FORMATTER;
import static ua.khnu.util.KeyboardBuilder.buildInlineKeyboard;

@Component
public class RemoveDeadlineUserCreateCommand extends AbstractSender implements SafelyIBotCommand, CallBackCommand {
    private static final String COMMAND_IDENTIFIER = "removedeadline";
    private static final String DEADLINE_ROW_TEMPLATE = "%d. %s %s %s %s";
    private static final String YOU_DON_T_HAVE_ANY_ACTIVE_DEADLINE_YET = "You don't have any active deadline yet";
    private final DeadlineService deadlineService;

    @Autowired
    public RemoveDeadlineUserCreateCommand(DeadlineService deadlineService) {
        this.deadlineService = deadlineService;
    }

    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] strings) {
        var deadlinesUserCreate = deadlineService.getDeadlinesUserCreate(Math.toIntExact(message.getFrom().getId()));
        final var chatId = message.getChatId();
        if (deadlinesUserCreate.isEmpty()) {
            sendMessage(absSender, chatId, YOU_DON_T_HAVE_ANY_ACTIVE_DEADLINE_YET);
            return;
        }
        var messageText = buildMessage(deadlinesUserCreate);
        var keyboard = buildKeyboard(deadlinesUserCreate, getIdsList(deadlinesUserCreate));
        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(messageText);
        sendMessage.setReplyMarkup(keyboard);
        execute(absSender, sendMessage);
    }

    @Override
    public void processCallBackMessage(AbsSender absSender, CallbackQuery callbackQuery) {
        var deadlineId = Integer.parseInt(getArgumentByPositionAndSeparator(1, " ", callbackQuery.getData()));
        deadlineService.removeDeadline(deadlineId);
        final var chatId = callbackQuery.getMessage().getChatId();
        sendMessage(absSender, chatId, "The deadline has been deleted");

        var deadlinesUserCreate = deadlineService.getDeadlinesUserCreate(Math.toIntExact(callbackQuery.getFrom().getId()));
        var messageText = deadlinesUserCreate.isEmpty() ? YOU_DON_T_HAVE_ANY_ACTIVE_DEADLINE_YET : buildMessage(deadlinesUserCreate);
        var keyboard = buildKeyboard(deadlinesUserCreate, getIdsList(deadlinesUserCreate));
        var edit = new EditMessageText();
        edit.setChatId(String.valueOf(chatId));
        edit.setMessageId(callbackQuery.getMessage().getMessageId());
        edit.setText(messageText);
        edit.setReplyMarkup(keyboard);
        execute(absSender, edit);
    }

    @Override
    public String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return null;
    }

    private InlineKeyboardMarkup buildKeyboard(List<Deadline> deadlinesUserCreate, List<String> ids) {
        return buildInlineKeyboard("/" + getCommandIdentifier(), ids,
                IntStream.range(1, deadlinesUserCreate.size() + 1).boxed()
                        .map(Objects::toString)
                        .collect(Collectors.toList()), 3);
    }

    private List<String> getIdsList(List<Deadline> deadlinesUserCreate) {
        return deadlinesUserCreate.stream()
                .map(Deadline::getId)
                .map(Objects::toString)
                .collect(Collectors.toList());
    }

    private String buildMessage(java.util.List<Deadline> deadlinesUserCreate) {
        var messageText = "Choose deadline to delete and click on it number on keyboard\n";
        messageText += IntStream.range(0, deadlinesUserCreate.size()).boxed()
                .map(i -> {
                    var deadline = deadlinesUserCreate.get(i);
                    return String.format(DEADLINE_ROW_TEMPLATE, i + 1, deadline.getGroupName(), deadline.getClassName(),
                            DATE_TIME_FORMATTER.format(deadline.getDeadLineTime()), deadline.getTaskDescription());
                }).collect(Collectors.joining("\n"));
        return messageText;
    }
}
