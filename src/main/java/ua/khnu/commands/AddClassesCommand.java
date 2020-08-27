package ua.khnu.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ua.khnu.exception.BotException;
import ua.khnu.service.PeriodService;
import ua.khnu.util.FileDownloader;

@Component
public class AddClassesCommand extends SimpleAnswerCommand{
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
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        long chatId = message.getChatId();
        try {
            byte[] content = FileDownloader.getFileContent(absSender,message, "json");
            service.addAllFromJson(new String(content), chatId);
            sendMessage(absSender,"Classes added",chatId);
        }catch (BotException e){
            sendMessage(absSender, e.getMessage(), chatId);
        }
    }
}
