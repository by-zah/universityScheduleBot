package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.service.DeadlineService;

import static ua.khnu.util.FileUtil.generateInputFile;
import static ua.khnu.util.MessageSender.sendFile;

@Component
public class GetDeadlinesFileCommand implements SafelyIBotCommand {
    public static final String COMMAND_IDENTIFIER = "getDeadlinesFile";
    private final DeadlineService deadlineService;

    @Autowired
    public GetDeadlinesFileCommand(DeadlineService deadlineService) {
        this.deadlineService = deadlineService;
    }

    @Override
    public void safelyProcessMessage(AbsSender absSender, Message message, String[] strings) {
        var inputFile = generateInputFile(deadlineService.getAllDeadlinesCsv(), "deadlines.csv");
        sendFile(absSender, inputFile, message.getChatId(), "Here is file with all deadlines");
    }

    @Override
    public String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
