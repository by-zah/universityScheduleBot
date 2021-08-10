package ua.khnu.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.commands.AbstractSender;
import ua.khnu.commands.FileCommand;
import ua.khnu.exception.BotException;
import ua.khnu.service.PeriodService;
import ua.khnu.util.FileUtil;

import java.util.List;

@Component
public class AddClassesCommand extends AbstractSender implements FileCommand {
    private final PeriodService periodService;

    @Autowired
    public AddClassesCommand(PeriodService periodService) {
        this.periodService = periodService;
    }

    @Override
    public String getCommandIdentifier() {
        return "/addClasses";
    }

    @Override
    public String getDescription() {
        return "Send json to add new lessons";
    }

    @Override
    public void processFileMessage(AbsSender absSender, Message message) {
        long chatId = message.getChatId();
        try {
            var file = FileUtil.getFileContent(absSender, message, List.of("json", "csv"));
            periodService.addAll(file, message.getFrom().getId());
            sendMessage(absSender, chatId, "Classes added");
        } catch (BotException e) {
            sendMessage(absSender, chatId, e.getMessage());
        }
    }
}
