package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.entity.Group;
import ua.khnu.service.PeriodService;
import ua.khnu.service.UserService;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.stream.Collectors;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class ClearClassesCommand implements IBotCommand {
    private final UserService userService;
    private final PeriodService periodService;

    @Autowired
    public ClearClassesCommand(UserService userService, PeriodService periodService) {
        this.userService = userService;
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
    @Transactional
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        userService.getUserById(message.getFrom().getId()).ifPresentOrElse(user -> {
                    var periods = user.getGroupsUserOwn().stream()
                            .map(Group::getPeriods)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());
                    if (periods.isEmpty()) {
                        sendMessage(absSender, message.getChatId(), "There aren't any class you are able to delete");
                        return;
                    }
                    periodService.removeAll(periods);
                    sendMessage(absSender, message.getChatId(), "All classes has been removed");
                }
                , () -> sendMessage(absSender, message.getChatId(), "You aren't registered"));
    }
}
