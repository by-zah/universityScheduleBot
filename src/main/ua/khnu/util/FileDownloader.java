package ua.khnu.util;

import org.apache.commons.io.IOUtils;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.khnu.exception.BotException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public final class FileDownloader {
    private FileDownloader() {
    }

    public static ua.khnu.dto.File getFileContent(AbsSender absSender, Message message, String expectedFileExtension) {
        return getFileContent(absSender, message, List.of(expectedFileExtension));
    }

    public static ua.khnu.dto.File getFileContent(AbsSender absSender, Message message, List<String> expectedFileExtensions) {
        HttpURLConnection httpConn = null;
        try {
            DefaultAbsSender defaultAbsSender = (DefaultAbsSender) absSender;
            GetFile getFile = new GetFile();
            getFile.setFileId(message.getDocument().getFileId());
            File file = defaultAbsSender.execute(getFile);
            String fileName = file.getFilePath();
            String[] fileNameSlitted = fileName.split("\\.");
            String extension = fileNameSlitted[fileNameSlitted.length - 1];
            if (expectedFileExtensions != null && !expectedFileExtensions.contains(extension)) {
                throw new BotException("Only following file extensions supported: " + String.join(",", expectedFileExtensions));
            }
            URL fileUrl = new URL(file.getFileUrl(defaultAbsSender.getBotToken()));
            httpConn = (HttpURLConnection) fileUrl.openConnection();
            try (InputStream inputStream = httpConn.getInputStream()) {
                return new ua.khnu.dto.File(IOUtils.toByteArray(inputStream), extension);
            }
        } catch (IOException | TelegramApiException e) {
            throw new BotException("Error while file downloading");
        } finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
    }
}
