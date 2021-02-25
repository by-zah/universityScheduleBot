package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.service.DeadlineService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

import static ua.khnu.util.Constants.DATE_TIME_FORMATTER;
import static ua.khnu.util.Constants.TIME_ZONE_ID;
import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class AddDeadlineCommand implements SafelyIBotCommand {
    private static final String COMMAND_SHOULD_HAVE_ARGS = "command should have 4 args (group name, class name, deadline time, description) splatted by \";\"";
    private final DeadlineService deadlineService;

    @Autowired
    public AddDeadlineCommand(DeadlineService deadlineService) {
        this.deadlineService = deadlineService;
    }

    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] strings) {
        final var chatId = message.getChatId();
        try {
            final var args = message.getText().substring(getCommandIdentifier().length() + 2).split(";");
            if (args.length < 4) {
                sendMessage(absSender, chatId, COMMAND_SHOULD_HAVE_ARGS);
                return;
            }

            final var localDateTime = LocalDateTime.parse(args[2], DATE_TIME_FORMATTER);
            deadlineService.createDeadline(args[0], args[1], localDateTime, args[3]);
            sendMessage(absSender, chatId, "Deadline created!");
        } catch (DateTimeParseException e) {
            sendMessage(absSender, chatId, "wrong time format, here is example of date-time format: "
                    + LocalDateTime.now(ZoneId.of(TIME_ZONE_ID)).format(DATE_TIME_FORMATTER));
        } catch (StringIndexOutOfBoundsException e) {
            sendMessage(absSender, chatId, COMMAND_SHOULD_HAVE_ARGS);
        }
    }

    @Override
    public String getCommandIdentifier() {
        return "addDeadline";
    }

    @Override
    public String getDescription() {
        return null;
    }
}
