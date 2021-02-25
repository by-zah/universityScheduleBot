package ua.khnu.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.exception.BotException;
import ua.khnu.service.ScheduleService;
import ua.khnu.util.FileUtil;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class UpdateScheduleCommand implements FileCommand {
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
    public void processFileMessage(AbsSender absSender, Message message) {
        try {
            var file = FileUtil.getFileContent(absSender, message, "json");
            service.updateScheduleFromJson(file.getContent(), message.getFrom().getId());
            scheduleDemon.interrupt();
            sendMessage(absSender, message.getChatId(), "New schedule is successfully set");
        } catch (BotException e) {
            LOG.error(e);
            sendMessage(absSender, message.getChatId(), e.getMessage());
        }
    }
}
