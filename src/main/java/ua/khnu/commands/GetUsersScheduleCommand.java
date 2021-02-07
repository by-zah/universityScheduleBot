package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.entity.Group;
import ua.khnu.entity.Period;
import ua.khnu.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ua.khnu.util.Constants.TIME_ZONE_ID;
import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class GetUsersScheduleCommand implements IBotCommand {
    private static final String MESSAGE_TEMPLATE = "Here are today's %s classes:%n%s";
    private static final String CLASS_TEMPLATE = "%s. %s (%s)%n";
    private final UserService userService;

    @Autowired
    public GetUsersScheduleCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getCommandIdentifier() {
        return "schedule";
    }

    @Override
    public String getDescription() {
        return "Returns current day schedule";
    }

    @Override
    @Transactional
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        var user = userService.getUserById(message.getFrom().getId());
        if (user.isEmpty()) {
            sendMessage(absSender, message.getChatId(), "You aren't registered");
            return;
        }
        var chatId = message.getChatId();
        List<String> messageParts = new ArrayList<>();
        var groups = user.get().getGroups();
        if (groups.isEmpty()) {
            sendMessage(absSender, chatId, "You don't subscribe to any group, use /subscribe to choose one or more");
            return;
        }
        groups.forEach(group -> {
            List<Period> periods = group.getPeriods().stream()
                    .filter(period -> Objects.equals(period.getDay(), LocalDate.now(ZoneId.of(TIME_ZONE_ID)).getDayOfWeek()))
                    .collect(Collectors.toList());
            if (!periods.isEmpty()) {
                messageParts.add(buildMessage(group, periods));
            }
        });
        if (messageParts.isEmpty()) {
            sendMessage(absSender, chatId, "There aren't any classes today");
        } else {
            sendMessage(absSender, chatId, messageParts.stream().collect(Collectors.joining(System.lineSeparator())));
        }
    }

    private String buildMessage(Group group, List<Period> periods) {
        StringBuilder classesBuilder = new StringBuilder();
        periods.stream()
                .sorted(Comparator.comparing(Period::getIndex))
                .forEach(period -> classesBuilder.append(String.format(CLASS_TEMPLATE,
                        period.getIndex(), period.getName(), period.getScheduleUnit().toString())));
        return String.format(MESSAGE_TEMPLATE, group.getName(), classesBuilder.toString());
    }

}
