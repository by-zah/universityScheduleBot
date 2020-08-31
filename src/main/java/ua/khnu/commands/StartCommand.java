package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.service.UserService;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class StartCommand implements IBotCommand {
    private final UserService userService;

    @Autowired
    public StartCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getCommandIdentifier() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Start bot command";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        userService.createOrUpdate(message.getFrom().getId(), message.getChatId());
        sendMessage(absSender
                , "Bot is started, you can /subscribe or"
                        + System.lineSeparator()
                        + " /unSubscribe to receive or not university schedule updates"
                , message.getChatId());
    }
}
