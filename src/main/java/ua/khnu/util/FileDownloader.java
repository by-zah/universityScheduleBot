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

public final class FileDownloader {
    private FileDownloader() {
    }

    public static byte[] getFileContent(AbsSender absSender, Message message, String expectedFileExtension) {
        HttpURLConnection httpConn = null;
        try {
            DefaultAbsSender defaultAbsSender = (DefaultAbsSender) absSender;
            GetFile getFile = new GetFile();
            getFile.setFileId(message.getDocument().getFileId());
            File file = defaultAbsSender.execute(getFile);
            String fileName = file.getFilePath();
            String[] fileNameSlitted = fileName.split("\\.");
            String extension = fileNameSlitted[fileNameSlitted.length - 1];
            if (expectedFileExtension != null && !expectedFileExtension.equals(extension)) {
                throw new BotException("Only " + expectedFileExtension + " files supported");
            }
            URL fileUrl = new URL(file.getFileUrl(defaultAbsSender.getBotToken()));
            httpConn = (HttpURLConnection) fileUrl.openConnection();
            try (InputStream inputStream = httpConn.getInputStream()) {
                return IOUtils.toByteArray(inputStream);
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
