package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.exception.BotException;
import ua.khnu.service.PeriodService;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class ClearClassesCommand implements IBotCommand {
    private final PeriodService periodService;

    @Autowired
    public ClearClassesCommand(PeriodService periodService) {
        this.periodService = periodService;
    }

    @Override
    public String getCommandIdentifier() {
        return "clearClasses";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        try {
            periodService.removeAllClassesInGroupsUserOwn(message.getFrom().getId());
            sendMessage(absSender, message.getChatId(), "All classes has been removed");
        } catch (BotException e) {
            sendMessage(absSender, message.getChatId(), e.getMessage());
        }

    }
}
