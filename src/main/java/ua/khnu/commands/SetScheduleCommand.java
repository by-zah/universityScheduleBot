package ua.khnu.commands;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.khnu.repository.ScheduleRepository;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Predicate;

@Component
public class SetScheduleCommand extends SimpleAnswerCommand implements NonCommandCommand {
    private static final String JSON = "json";
    private static final Logger LOG = LogManager.getLogger(SetScheduleCommand.class);
    private final ScheduleRepository repository;
    private final Thread scheduleDemon;

    @Autowired
    public SetScheduleCommand(ScheduleRepository repository, Thread scheduleDemon) {
        this.repository = repository;
        this.scheduleDemon = scheduleDemon;
    }

    @Override
    public Predicate<Message> getCondition() {
        return (Message::hasDocument);
    }

    @Override
    public String getDescription() {
        return "You can send json file to set schedule";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        DefaultAbsSender defaultAbsSender = (DefaultAbsSender) absSender;
        GetFile getFile = new GetFile();
        getFile.setFileId(message.getDocument().getFileId());
        HttpURLConnection httpConn = null;
        try {
            File file = defaultAbsSender.execute(getFile);
            URL fileUrl = new URL(file.getFileUrl(defaultAbsSender.getBotToken()));
            httpConn = (HttpURLConnection) fileUrl.openConnection();
            try (InputStream inputStream = httpConn.getInputStream()) {
                byte[] output = IOUtils.toByteArray(inputStream);
                String fileName = file.getFilePath();
                String[] fileNameSlitted = fileName.split("\\.");
                String extension = fileNameSlitted[fileNameSlitted.length - 1];
                if (!JSON.equals(extension)) {
                    sendMessage(absSender, "Only json files supported", message.getChatId());
                }
                repository.setScheduleFromJSON(new String(output));
                scheduleDemon.interrupt();
                sendMessage(absSender, "New schedule is successfully set", message.getChatId());
            }
        } catch (TelegramApiException | IOException e) {
            LOG.error(e);
            sendMessage(absSender, "Error while file downloading", message.getChatId());
        } finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
    }
}
