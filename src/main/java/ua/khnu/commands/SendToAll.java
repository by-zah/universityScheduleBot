package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.dto.MessageForQueue;
import ua.khnu.entity.User;
import ua.khnu.service.MailingService;
import ua.khnu.service.UserService;

import java.util.Optional;
import java.util.stream.Collectors;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class SendToAll implements IBotCommand {
    private final UserService userService;
    private final MailingService mailingService;

    @Autowired
    public SendToAll(UserService userService, MailingService mailingService) {
        this.userService = userService;
        this.mailingService = mailingService;
    }

    @Override
    public String getCommandIdentifier() {
        return "sendToAll";
    }

    @Override
    public String getDescription() {
        return "Send notification to all users";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        long chatId = message.getChatId();
        if (arguments.length < 1) {
            sendMessage(absSender, chatId, "Can`t send empty message");
            return;
        }
        int userId = message.getFrom().getId();
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty() || !userOpt.get().isSupper()) {
            sendMessage(absSender, chatId, "You are not allow to do it");
            return;
        }
        String messageText = String.join(" ", arguments);
        try {
            mailingService.sendMailingMessages(userService.getAllUsers().stream()
                    .map(user -> new MessageForQueue(messageText, user.getChatId()))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            sendMessage(absSender, chatId, "Error while perform mailing");
        }
    }
}
