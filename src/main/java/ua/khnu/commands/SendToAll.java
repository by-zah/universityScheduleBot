package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.entity.User;
import ua.khnu.service.MailingService;
import ua.khnu.service.UserService;

import java.util.Optional;
import java.util.stream.Collectors;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class SendToAll implements SafelyIBotCommand {
    private static final String COMMAND_IDENTIFIER = "sendToAll";
    private static final int MESSAGE_OFFSET = COMMAND_IDENTIFIER.length() + 2;

    private final UserService userService;
    private final MailingService mailingService;

    @Autowired
    public SendToAll(UserService userService, MailingService mailingService) {
        this.userService = userService;
        this.mailingService = mailingService;
    }

    @Override
    public String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return "Send notification to all users";
    }

    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] arguments) {
        long chatId = message.getChatId();
        var messageText = message.getText().substring(MESSAGE_OFFSET);
        if (messageText.length() < 1) {
            sendMessage(absSender, chatId, "Can`t send empty message");
            return;
        }
        int userId = message.getFrom().getId();
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty() || !userOpt.get().isSupper()) {
            sendMessage(absSender, chatId, "You are not allow to do it");
            return;
        }
        mailingService.sendMailingMessages(userService.getAllUsers().stream()
                .map(user -> {
                    var sendMessage = new SendMessage();
                    sendMessage.setChatId(String.valueOf(user.getChatId()));
                    sendMessage.setText(messageText);
                    return sendMessage;
                })
                .collect(Collectors.toList()));
    }
}
