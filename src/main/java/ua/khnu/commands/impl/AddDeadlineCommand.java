package ua.khnu.commands.impl;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.CallBackCommand;
import ua.khnu.commands.MultiCommand;
import ua.khnu.commands.SafelyIBotCommand;
import ua.khnu.dto.MultiCommandBuildersContainer;
import ua.khnu.dto.MultiCommandObjectBuilder;
import ua.khnu.entity.Deadline;
import ua.khnu.entity.Group;
import ua.khnu.exception.BotException;
import ua.khnu.service.DeadlineService;
import ua.khnu.service.GroupService;
import ua.khnu.service.PeriodService;
import ua.khnu.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ua.khnu.util.Constants.DATE_TIME_FORMATTER;
import static ua.khnu.util.KeyboardBuilder.buildInlineKeyboard;
import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class AddDeadlineCommand implements SafelyIBotCommand, CallBackCommand, MultiCommand {
    public static final String COMMAND_IDENTIFIER = "addDeadline";
    private static final String CLEAN = "clean";
    private static final String OR = "or use clean button to stop or restart deadline creation";
    private final MultiCommandBuildersContainer multiCommandBuilders;
    private final List<MessageAndKeyboard> messages;
    private final DeadlineService deadlineService;
    private final PeriodService periodService;
    private final UserService userService;

    @AllArgsConstructor
    @Getter
    private static class MessageAndKeyboard {
        private final String message;
        private final Supplier<InlineKeyboardMarkup> keyboardBuilder;
    }

    @Autowired
    public AddDeadlineCommand(MultiCommandBuildersContainer multiCommandBuilders, GroupService groupService, PeriodService periodService, DeadlineService deadlineService, UserService userService) {
        this.multiCommandBuilders = multiCommandBuilders;
        this.deadlineService = deadlineService;
        this.periodService = periodService;
        this.userService = userService;
        messages = ImmutableList.of(
                new MessageAndKeyboard("Select group name " + OR, () -> {
                    var groupNames = groupService.getAllGroups()
                            .stream()
                            .map(Group::getName)
                            .collect(Collectors.toList());
                    return buildInlineKeyboard("/" + getCommandIdentifier(), groupNames, 3);
                }),
                new MessageAndKeyboard("Select class name " + OR,
                        () -> {
                            final var allClassesNames = periodService.getAllClassesNames();
                            final var indexes = IntStream.range(0, allClassesNames.size()).boxed()
                                    .map(Objects::toString)
                                    .collect(Collectors.toList());
                            return buildInlineKeyboard("/" + getCommandIdentifier(), indexes, allClassesNames, 1);
                        }),
                new MessageAndKeyboard("Enter deadline date and time in following format " + DATE_TIME_FORMATTER.format(LocalDateTime.now()) + " " + OR, InlineKeyboardMarkup::new),
                new MessageAndKeyboard("Enter task description " + OR, InlineKeyboardMarkup::new)

        );
    }

    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] strings) {
        addNewDeadlineToContainer(message.getChatId());
        var sendMessage = new SendMessage();
        var messageAndKeyboard = messages.get(0);
        sendMessage.setReplyMarkup(addCleanButton(messageAndKeyboard));
        sendMessage(absSender, message.getChatId(), sendMessage, messageAndKeyboard.getMessage());
    }

    @Override
    public String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public void processMultiCommand(AbsSender absSender, long chatId, String message) {
        try {

            process(absSender, chatId, message);
        } catch (BotException e) {
            sendMessage(absSender, chatId, e.getMessage());
        }
    }

    @Override
    public void processCallBackMessage(AbsSender absSender, CallbackQuery callbackQuery) {
        var value = callbackQuery.getData().split(" ")[1];
        process(absSender, callbackQuery.getMessage().getChatId(), value);
    }

    @Override
    public String getDescription() {
        return null;
    }

    private void addNewDeadlineToContainer(long chatId) {
        multiCommandBuilders.getMultiCommandBuilders()
                .removeIf(multiCommandObjectBuilder -> multiCommandObjectBuilder.getRelatedChatId() == chatId);
        List<BiConsumer<Deadline, String>> setters = ImmutableList.of(
                Deadline::setGroupName,
                Deadline::setClassName,
                (d, s) -> d.setDeadLineTime(LocalDateTime.parse(s, DATE_TIME_FORMATTER)),
                Deadline::setTaskDescription
        );
        var multiObjectCommandBuilder =
                MultiCommandObjectBuilder.<Deadline>builder()
                        .object(new Deadline())
                        .setters(setters)
                        .relatedChatId(chatId)
                        .ownerIdentifier(getCommandIdentifier())
                        .build();
        multiCommandBuilders.getMultiCommandBuilders().add(multiObjectCommandBuilder);
    }

    private void process(AbsSender absSender, long chatId, String incomingMessage) {
        var objectBuilder = multiCommandBuilders.getMultiCommandBuilders().stream()
                .filter(multiCommandObjectBuilder -> multiCommandObjectBuilder.getRelatedChatId() == chatId)
                .findAny();
        if (objectBuilder.isEmpty()) {
            sendMessage(absSender, chatId, "To initialize deadline creation use /" + COMMAND_IDENTIFIER);
            return;
        }
        final MultiCommandObjectBuilder<Deadline> multiCommandObjectBuilder = (MultiCommandObjectBuilder<Deadline>) objectBuilder.get();
        if (CLEAN.equals(incomingMessage)) {
            multiCommandBuilders.getMultiCommandBuilders().remove(multiCommandObjectBuilder);
            sendMessage(absSender, chatId, "All data clear, if you want restart use /" + COMMAND_IDENTIFIER);
            return;
        }
        try {
            //TODO rewrite later
            if (multiCommandObjectBuilder.getOrder() == 1) {
                var index = Integer.parseInt(incomingMessage);
                multiCommandObjectBuilder.setNextValue(periodService.getAllClassesNames().get(index));
            } else {
                multiCommandObjectBuilder.setNextValue(incomingMessage);
            }
            if (multiCommandObjectBuilder.isDone()) {
                saveDeadline(multiCommandObjectBuilder.getObject(), chatId, (int) chatId);
                sendMessage(absSender, chatId, "Deadline successfully created!");
                return;
            }
            var sendMessage = new SendMessage();
            var messageAndKeyboard = messages.get(multiCommandObjectBuilder.getOrder());
            var keyboard = addCleanButton(messageAndKeyboard);
            sendMessage.setReplyMarkup(keyboard);
            sendMessage(absSender, chatId, sendMessage, messageAndKeyboard.getMessage());
        } catch (Exception e) {
            LOG.error(e);
            sendMessage(absSender, chatId, "Something went wrong, lets try again\n"
                    + messages.get(multiCommandObjectBuilder.getOrder()).getMessage());
        }
    }

    private InlineKeyboardMarkup addCleanButton(MessageAndKeyboard messageAndKeyboard) {
        var keyboard = messageAndKeyboard.getKeyboardBuilder().get();
        final var cleanButton = new InlineKeyboardButton();
        cleanButton.setCallbackData("/" + COMMAND_IDENTIFIER + " " + CLEAN);
        cleanButton.setText(CLEAN);
        var buttons = Optional.ofNullable(keyboard.getKeyboard())
                .orElse(new ArrayList<>());
        buttons.add(List.of(cleanButton));
        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private void saveDeadline(Deadline deadline, long chatId, int userId) {
        var user = userService.getUserById(userId).orElseGet(() -> userService.createUser(userId, chatId));
        deadline.setCreatedBy(user);
        deadlineService.createDeadline(deadline);
        multiCommandBuilders.getMultiCommandBuilders()
                .removeIf(multiCommandObjectBuilder -> chatId == multiCommandObjectBuilder.getRelatedChatId());
    }

}
