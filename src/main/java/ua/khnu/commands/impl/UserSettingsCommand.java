package ua.khnu.commands.impl;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.AbstractCommand;
import ua.khnu.commands.CallBackCommand;
import ua.khnu.commands.SafelyIBotCommand;
import ua.khnu.entity.UserSettings;
import ua.khnu.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static ua.khnu.util.KeyboardBuilder.buildInlineKeyboard;

@Component
public class UserSettingsCommand extends AbstractCommand implements SafelyIBotCommand, CallBackCommand {
    public static final String COMMAND_IDENTIFIER = "settings";
    private static final String SETTINGS_TEMPLATE = "Here is you settings:\n" +
            "Enable class notifications:%s\n" +
            "Enable deadline notifications:%s\n\n Use buttons below to change them";
    private static final String DEADLINE = "deadline";
    private static final String CLASS = "class";


    private final UserService userService;
    private final Map<String, Consumer<Long>> settingsUpdateFunctions;

    @Autowired
    public UserSettingsCommand(UserService userService) {
        this.userService = userService;
        settingsUpdateFunctions = ImmutableMap.<String, Consumer<Long>>builder()
                .put(CLASS, userService::switchClassNotificationSetting)
                .put(DEADLINE, userService::switchDeadlineNotificationSetting)
                .build();
    }

    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] strings) {
        final var userSettings = getUserSettings(message.getFrom().getId(), message.getChatId());
        final String messageText = getMessageText(userSettings);
        final var keyboard = getInlineKeyboardMarkup(userSettings);
        final var sendMessage = new SendMessage();
        sendMessage.setText(messageText);
        sendMessage.setReplyMarkup(keyboard);
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        execute(absSender, sendMessage);
    }

    @Override
    public void processCallBackMessage(AbsSender absSender, CallbackQuery callbackQuery) {
        final var message = callbackQuery.getMessage();
        final var chatId = message.getChatId();
        var settingName = getArgumentByPositionAndSeparator(1, " ", callbackQuery.getData());
        Objects.requireNonNull(settingsUpdateFunctions.get(settingName)).accept(callbackQuery.getFrom().getId());
        final var userSettings = getUserSettings(callbackQuery.getFrom().getId(), chatId);
        final var messageText = getMessageText(userSettings);
        final var messageId = message.getMessageId();

        var editMessageText = new EditMessageText();
        editMessageText.setText(messageText);
        editMessageText.setMessageId(messageId);
        editMessageText.setChatId(String.valueOf(chatId));
        execute(absSender, editMessageText);

        var editMessageKeyboard = new EditMessageReplyMarkup();
        editMessageKeyboard.setReplyMarkup(getInlineKeyboardMarkup(userSettings));
        editMessageKeyboard.setMessageId(messageId);
        editMessageKeyboard.setChatId(String.valueOf(chatId));
        execute(absSender, editMessageKeyboard);
    }

    @Override
    public String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return null;
    }

    private String getMessageText(UserSettings userSettings) {
        return String.format(SETTINGS_TEMPLATE,
                userSettings.isClassNotificationsEnabled(), userSettings.isDeadlineNotificationsEnabled());
    }

    private UserSettings getUserSettings(long userId, long chatId) {
        var user = userService.getUserById(userId)
                .orElseGet(() -> userService.createUser(userId, chatId));
        return user.getSettings();
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(ua.khnu.entity.UserSettings userSettings) {
        return buildInlineKeyboard("/" + getCommandIdentifier(),
                List.of(CLASS, DEADLINE),
                List.of(
                        settingsValueToString(userSettings.isClassNotificationsEnabled(), CLASS),
                        settingsValueToString(userSettings.isDeadlineNotificationsEnabled(), DEADLINE)
                ),
                1
        );
    }

    private String settingsValueToString(boolean value, String notificationName) {
        final var turnPart = value ? "Turn off " : "Turn on ";
        return turnPart + notificationName + " notifications";
    }
}
