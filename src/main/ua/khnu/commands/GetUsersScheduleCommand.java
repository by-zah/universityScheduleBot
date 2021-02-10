package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.entity.Group;
import ua.khnu.entity.Period;
import ua.khnu.service.PeriodService;
import ua.khnu.service.UserService;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class GetUsersScheduleCommand implements IBotCommand {
    private static final String MESSAGE_TEMPLATE = "Here are %s's %s classes:%n%s";
    private static final String CLASS_TEMPLATE = "%s. %s (%s)%n";
    private final UserService userService;
    private final PeriodService periodService;

    @Autowired
    public GetUsersScheduleCommand(UserService userService, PeriodService periodService) {
        this.userService = userService;
        this.periodService = periodService;
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
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        List<String> messageParts = new ArrayList<>();
        periodService.getUpcomingUserClasses(message.getFrom().getId()).stream()
                .collect(groupingBy(Period::getGroup))
                .forEach((group, classes) -> messageParts.add(buildMessage(group, classes)));
        sendMessage(absSender, message.getChatId(), messageParts.stream().collect(Collectors.joining(System.lineSeparator())));
    }

    private String buildMessage(Group group, List<Period> periods) {
        StringBuilder classesBuilder = new StringBuilder();
        var day = periods.get(0).getDay().getDisplayName(TextStyle.FULL, Locale.US).toLowerCase();
        periods.stream()
                .sorted(Comparator.comparing(Period::getIndex))
                .forEach(period -> classesBuilder.append(String.format(CLASS_TEMPLATE,
                        period.getIndex(), period.getName(), period.getScheduleUnit().toString())));
        return String.format(MESSAGE_TEMPLATE, day, group.getName(), classesBuilder.toString());
    }

}
