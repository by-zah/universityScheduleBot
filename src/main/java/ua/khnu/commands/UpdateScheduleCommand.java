package ua.khnu.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.exception.BotException;
import ua.khnu.service.ScheduleService;
import ua.khnu.util.FileDownloader;

@Component
public class UpdateScheduleCommand extends SimpleAnswerCommand {
    private static final Logger LOG = LogManager.getLogger(UpdateScheduleCommand.class);
    private final ScheduleService service;
    private final Thread scheduleDemon;

    @Autowired
    public UpdateScheduleCommand(ScheduleService service, Thread scheduleDemon) {
        this.service = service;
        this.scheduleDemon = scheduleDemon;
    }

    @Override
    public String getCommandIdentifier() {
        return "/updateSchedule";
    }

    @Override
    public String getDescription() {
        return "You can send json file to set schedule";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        try {
            byte[] content = FileDownloader.getFileContent(absSender, message, "json");
            service.updateScheduleFromJson(new String(content));
            scheduleDemon.interrupt();
            sendMessage(absSender, "New schedule is successfully set", message.getChatId());
        } catch (BotException e) {
            LOG.error(e);
            sendMessage(absSender, e.getMessage(), message.getChatId());
        }
    }
}