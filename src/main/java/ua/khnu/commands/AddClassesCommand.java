package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.exception.BotException;
import ua.khnu.service.PeriodService;
import ua.khnu.util.FileDownloader;

import static ua.khnu.util.MessageSender.sendMessage;

@Component
public class AddClassesCommand implements FileCommand {
    private final PeriodService service;

    @Autowired
    public AddClassesCommand(PeriodService service) {
        this.service = service;
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
            byte[] content = FileDownloader.getFileContent(absSender, message, "json");
            service.addAllFromJson(new String(content), message.getFrom().getId());
            sendMessage(absSender, "Classes added", chatId);
        } catch (BotException e) {
            sendMessage(absSender, e.getMessage(), chatId);
        }
    }
}
